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
package de.blazemcworld.blazinggames.packs.hooks;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.packs.HookContext;
import de.blazemcworld.blazinggames.packs.PackBuildHook;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class LockedBlockStylesHook implements PackBuildHook {
    public enum LockedBlockStyle
    {
        PADLOCK("padlock", "locked_block_side", "locked_block_top");

        final NamespacedKey key, sideTexture, topTexture;

        LockedBlockStyle(NamespacedKey key, NamespacedKey sideTexture, NamespacedKey topTexture) {
            this.key = key;
            this.sideTexture = sideTexture;
            this.topTexture = topTexture;
        }

        LockedBlockStyle(String key, String sideTexture, String topTexture)
        {
            this(BlazingGames.get().key(key), BlazingGames.get().key(sideTexture), BlazingGames.get().key(topTexture));
        }

        public NamespacedKey getKey() {
            return key;
        }

        public static LockedBlockStyle getByKey(NamespacedKey styleKey) {
            for(LockedBlockStyle style : values()) {
                if(style.getKey().equals(styleKey)) {
                    return style;
                }
            }
            return null;
        }
    }

    private static final NamespacedKey baseModel = BlazingGames.get().key("locked_block_base");

    private static String convertNamespacedKeyToStringWithFolder(NamespacedKey key, String folder) {
        return key.getNamespace() + ":" + folder + "/" + key.getKey();
    }

    @Override
    public void runHook(Logger logger, HookContext context) {
        Set<NamespacedKey> textures = new HashSet<>();

        for (LockedBlockStyle style : LockedBlockStyle.values()) {
            textures.add(style.sideTexture);
            textures.add(style.topTexture);

            // create model
            JsonObject modelData = new JsonObject();
            modelData.addProperty("parent", baseModel.toString());

            JsonObject texturesData = new JsonObject();
            texturesData.addProperty("side", convertNamespacedKeyToStringWithFolder(style.sideTexture, "item"));
            texturesData.addProperty("top", convertNamespacedKeyToStringWithFolder(style.topTexture, "item"));

            modelData.add("textures", texturesData);

            context.installModel(style.key, modelData);

            // create items definition
            JsonObject styleRoot = getItemDefinitionForStyle(style);
            context.writeFile("/assets/" + style.key.getNamespace() + "/items/" + style.key.getKey() + ".json", styleRoot);
        }

        for (NamespacedKey texture : textures) {
            // install texture
            try (InputStream stream = BlazingGames.class.getResourceAsStream("/lockedblocks/" + texture.getKey() + ".png")) {
                if (stream != null) context.installTexture(texture, "item", stream.readAllBytes());
            } catch (IOException e) {
                BlazingGames.get().log(e);
            }

            // install animation options
            try (InputStream stream = BlazingGames.class.getResourceAsStream("/lockedblocks/" + texture.getKey() + ".png.mcmeta")) {
                if (stream != null) context.installTextureAnimationData(texture, "item", stream.readAllBytes());
            } catch (IOException e) {
                BlazingGames.get().log(e);
            }
        }

        // install base model
        try (InputStream stream = BlazingGames.class.getResourceAsStream("/lockedblocks/" + baseModel.getKey() + ".json")) {
            if (stream != null) context.installModel(baseModel, stream.readAllBytes());
        } catch (IOException e) {
            BlazingGames.get().log(e);
        }
    }

    private static @NotNull JsonObject getItemDefinitionForStyle(LockedBlockStyle style) {
        JsonObject styleRoot = new JsonObject();
        JsonObject styleModel = new JsonObject();
        JsonArray tints = new JsonArray();
        JsonObject tint = new JsonObject();
        JsonArray defaultColor = new JsonArray();
        defaultColor.add(255);
        defaultColor.add(255);
        defaultColor.add(255);
        tint.addProperty("type", "minecraft:custom_model_data");
        tint.addProperty("index", 0);
        tint.add("default", defaultColor);
        tints.add(tint);
        styleModel.addProperty("type", "minecraft:model");
        styleModel.addProperty("model", style.key.toString());
        styleModel.add("tints", tints);
        styleRoot.add("model", styleModel);
        return styleRoot;
    }
}