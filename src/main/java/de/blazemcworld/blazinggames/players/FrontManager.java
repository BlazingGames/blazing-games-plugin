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
package de.blazemcworld.blazinggames.players;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.utils.SkinLoader;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class FrontManager {
    private FrontManager() {}
    private static final HashMap<UUID, String> frontMap = new HashMap<>();
    private static final HashMap<UUID, UUID> currentSkinMap = new HashMap<>();
    private static final HashMap<UUID, ArrayList<ProfileProperty>> defaultProfileMap = new HashMap<>();

    public static void updateFront(Player player, String front) {
        frontMap.put(player.getUniqueId(), front);
        updatePlayer(player);
    }

    public static String getFront(UUID player) {
        return frontMap.get(player);
    }

    public static void clearFront(Player player) {
        frontMap.remove(player.getUniqueId());
        updatePlayer(player);
    }

    public static void clearFrontLogout(Player player) {
        frontMap.remove(player.getUniqueId());
        currentSkinMap.remove(player.getUniqueId());
        defaultProfileMap.remove(player.getUniqueId());
    }

    public static boolean hasFront(UUID uuid) {
        return frontMap.containsKey(uuid);
    }

    public static void updatePlayer(Player player) {
        PlayerConfig.forPlayer(player).updatePlayer();

        if (!defaultProfileMap.containsKey(player.getUniqueId())) {
            defaultProfileMap.put(player.getUniqueId(), new ArrayList<>(player.getPlayerProfile().getProperties()));
        }

        String currentFront = frontMap.get(player.getUniqueId());
        UUID currentSkin = currentSkinMap.getOrDefault(player.getUniqueId(), null);
        UUID frontSkin = currentFront == null ? null : PlayerConfig.forPlayer(player).getPluralConfig().getMember(currentFront).skin;

        if (!Objects.equals(currentSkin, frontSkin)) {
            currentSkinMap.put(player.getUniqueId(), frontSkin);

            if (frontSkin == null) {
                PlayerProfile profile = player.getPlayerProfile();
                profile.clearProperties();
                profile.setProperties(defaultProfileMap.get(player.getUniqueId()));
                player.setPlayerProfile(profile);
                return;
            }
            
            final CompletableFuture<ProfileProperty> future = SkinLoader.getProfile(frontSkin);
            final UUID playerUUID = player.getUniqueId();
            Bukkit.getAsyncScheduler().runNow(BlazingGames.get(), task -> {
                try {
                    final ProfileProperty newProfile = future.get(30, TimeUnit.SECONDS);
                    applyProfileSync(playerUUID, newProfile);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    BlazingGames.get().log(e);
                    sendErrorMessage(playerUUID, e.getClass().getName() + ": " + e.getMessage());
                }
            });
        }
    }

    private static void applyProfileSync(final UUID playerUUID, final ProfileProperty prop) {
        Bukkit.getScheduler().runTask(BlazingGames.get(), () -> {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                PlayerProfile profile = player.getPlayerProfile();
                profile.clearProperties();
                profile.setProperty(prop);
                player.setPlayerProfile(profile);
            }
        });
    }

    private static void sendErrorMessage(final UUID playerUUID, final String message) {
        Bukkit.getScheduler().runTask(BlazingGames.get(), () -> {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                player.sendMessage(Component.text("Error when loading your skin: " + message, NamedTextColor.RED));
            }
        });
    }
}