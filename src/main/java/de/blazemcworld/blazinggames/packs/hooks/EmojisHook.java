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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.NamespacedKey;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.packs.HookContext;
import de.blazemcworld.blazinggames.packs.PackBuildHook;
import de.blazemcworld.blazinggames.utils.EmojiRegistry;

public class EmojisHook implements PackBuildHook {
    @Override
    public void runHook(Logger logger, HookContext context) {
        logger.info("Initializing emojis..");
        EmojiRegistry.init();
        logger.info("Found " + EmojiRegistry.emojiMap.size() + " emojis");

        JsonArray array = new JsonArray();
        for (Map.Entry<String, EmojiRegistry.EmojiProperties> entry : EmojiRegistry.emojiMap.entrySet()) {
            String emojiName = entry.getKey();
            Character emojiChar = entry.getValue().character;

            // install texture
            try (InputStream stream = BlazingGames.class.getResourceAsStream("/" + EmojiRegistry.emojiDir + "/" + emojiName + ".png")) {
                if (stream != null) context.installTexture(new NamespacedKey(EmojiRegistry.namespace, emojiName), EmojiRegistry.emojiDir, stream.readAllBytes());
            } catch (IOException e) {
                BlazingGames.get().log(e);
            }

            // add to json
            JsonArray chars = new JsonArray();
            chars.add(emojiChar.toString());
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "bitmap");
            obj.addProperty("file", EmojiRegistry.namespace + ":" + EmojiRegistry.emojiDir + "/" + emojiName + ".png");
            obj.addProperty("height", 8);
            obj.addProperty("ascent", 7);
            obj.add("chars", chars);
            array.add(obj);
        }

        JsonObject root = new JsonObject();
        root.add("providers", array);
        context.writeFile("assets/" + EmojiRegistry.namespace + "/font/" + EmojiRegistry.namespace + ".json", root);
    }
}