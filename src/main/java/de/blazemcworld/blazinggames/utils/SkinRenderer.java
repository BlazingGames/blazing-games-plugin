/*
 * Copyright 2025 The Blazing Games Maintainers
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.blazemcworld.blazinggames.utils;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Iterator;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.gson.JsonObject;

import de.blazemcworld.blazinggames.BlazingAPI;
import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.computing.api.EndpointList;
import de.blazemcworld.blazinggames.players.FrontManager;
import de.blazemcworld.blazinggames.players.MemberData;
import de.blazemcworld.blazinggames.players.PlayerConfig;
import dev.ivycollective.datastorage.DataStorage;
import dev.ivycollective.datastorage.name.UUIDNameProvider;
import dev.ivycollective.datastorage.storage.BinaryStorageProvider;
import dev.ivycollective.datastorage.storage.RawTextStorageProvider;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SkinRenderer {
    private static final DataStorage<byte[], UUID> vanillaStorage = BlazingGames.dataStorageConfig().makeDataStorage(
        SkinRenderer.class,
        "vanilla",
        new BinaryStorageProvider(),
        new UUIDNameProvider()
    );

    private static final DataStorage<byte[], UUID> mineskinStorage = BlazingGames.dataStorageConfig().makeDataStorage(
        SkinRenderer.class,
        "mineskin",
        new BinaryStorageProvider(),
        new UUIDNameProvider()
    );

    private static final DataStorage<String, UUID> checksumStorage = BlazingGames.dataStorageConfig().makeDataStorage(
        SkinRenderer.class,
        "checksum",
        new RawTextStorageProvider("sha1"),
        new UUIDNameProvider()
    );

    private SkinRenderer() {}

    public static void updateVanilla(PlayerProfile profile) {
        ProfileProperty texture = null;
        Iterator<ProfileProperty> iterator = profile.getProperties().iterator();
        while (iterator.hasNext()) {
            ProfileProperty property = iterator.next();
            if (property.getName().equals("textures")) {
                texture = property;
                break;
            }
        }
        if (texture == null) {
            return;
        }

        final String textureString = texture.getValue();
        String checksum = checksum(textureString);
        String lastChecksum = checksumStorage.getData(profile.getId(), null);

        if (checksum != null && !checksum.equals(lastChecksum)) {
            final UUID profileUUID = profile.getId();
            checksumStorage.storeData(profileUUID, checksum);
            Bukkit.getAsyncScheduler().runNow(BlazingGames.get(), task -> {
                skinRenderJobAsync(profileUUID, textureString, false);
            });
        }
    }

    public static void updateMineskin(final UUID uuid, ProfileProperty texture) {
        if (!mineskinStorage.hasData(uuid)) {
            final String textureString = texture.getValue();
            Bukkit.getAsyncScheduler().runNow(BlazingGames.get(), task -> {
                skinRenderJobAsync(uuid, textureString, true);
            });
        }
    }

    public static String generateURL(UUID uuid, boolean isMineskin) {
        return BlazingAPI.getConfig().apiConfig().findAt() + EndpointList.SKINS.endpoint.path() + "?uuid=" + uuid + "&mode=" + (isMineskin ? "mineskin" : "vanilla");
    }

    public static String autoGenerateURL(Player player) {
        PlayerConfig config = PlayerConfig.forPlayer(player);
        String front = FrontManager.getFront(player.getUniqueId());
        if (config.isPlural() && front != null) {
            MemberData data = config.getPluralConfig().getMember(front);
            if (data.skin != null) return generateURL(data.skin, true);
        }
    
        return generateURL(player.getUniqueId(), false);
    }

    public static boolean hasSkin(UUID uuid, boolean isMineskin) {
        if (isMineskin) {
            return mineskinStorage.hasData(uuid);
        } else {
            return vanillaStorage.hasData(uuid);
        }
    }

    public static byte[] getSkin(UUID uuid, boolean isMineskin) {
        if (isMineskin) {
            return mineskinStorage.getData(uuid, null);
        } else {
            return vanillaStorage.getData(uuid, null);
        }
    }

    private static String checksum(String texture) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(texture.getBytes());
            byte[] digest = md.digest();

            StringBuilder out = new StringBuilder();
            for (byte b : digest) {
                out.append(String.format("%02x", b));
            }
            return out.toString();
        } catch (Exception e) {
            BlazingGames.get().log(e);
            return null;
        }
    }

    private static final OkHttpClient client = new OkHttpClient();
    private static void skinRenderJobAsync(UUID uuid, String texture, boolean isMineskin) {
        String uuidString = uuid.toString() + " (" + (isMineskin ? "mineskin" : "vanilla") + ")";
        BlazingGames.get().log("Rendering skin " + uuidString);

        // 1. find URL
        String decoded = new String(Base64.getDecoder().decode(texture));
        String url = null;
        try {
            JsonObject json = BlazingGames.gson.fromJson(decoded, JsonObject.class);
            JsonObject textures = json.get("textures").getAsJsonObject();
            JsonObject skin = textures.get("SKIN").getAsJsonObject();
            url = skin.get("url").getAsString();
        } catch (Exception e) {
            BlazingGames.get().log("Couldn't decode skin " + uuidString);
            BlazingGames.get().log(e);
            return;
        }

        // 2. download skin
        byte[] skin = null;
        Request request = new Request.Builder()
            .url(url)
            .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return;
            }
            skin = response.body().bytes();
        } catch (IOException e) {
            BlazingGames.get().log("Couldn't download skin " + uuidString);
            BlazingGames.get().log(e);
            return;
        }

        // 3. image manipulation
        ByteArrayInputStream stream = new ByteArrayInputStream(skin);
        BufferedImage headBase = null;
        BufferedImage headOverlay = null;
        try {
            BufferedImage image = ImageIO.read(stream);
            headBase = image.getSubimage(8, 8, 8, 8);
            headOverlay = image.getSubimage(40, 8, 8, 8);
        } catch (IOException e) {
            BlazingGames.get().log("Couldn't parse image of skin " + uuidString);
            BlazingGames.get().log(e);
            return;
        }
        BufferedImage head = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
        head.getGraphics().drawImage(headBase, 0, 0, null);
        head.getGraphics().drawImage(headOverlay, 0, 0, null);

        BufferedImage output = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
        AffineTransform transform = new AffineTransform();
        transform.scale(64, 64); // 8*64 = 512
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        output = op.filter(head, output);

        // 4. store skin
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(output, "png", out);
        } catch (IOException e) {
            BlazingGames.get().log("Couldn't write skin bytes for " + uuidString);
            BlazingGames.get().log(e);
            return;
        }

        BlazingGames.get().log("Saved skin: " + uuidString);
        if (isMineskin) {
            mineskinStorage.storeData(uuid, out.toByteArray());
        } else {
            vanillaStorage.storeData(uuid, out.toByteArray());
        }
    }
}