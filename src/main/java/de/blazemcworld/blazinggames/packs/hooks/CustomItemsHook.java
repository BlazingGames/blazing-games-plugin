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

import com.google.gson.JsonObject;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.items.CustomItem;
import de.blazemcworld.blazinggames.items.CustomItems;
import de.blazemcworld.blazinggames.packs.HookContext;
import de.blazemcworld.blazinggames.packs.PackBuildHook;

public class CustomItemsHook extends PackBuildHook {
    @Override
    public void run(HookContext context) {
        for (CustomItem item : CustomItems.list()) {
            // install texture
            try (InputStream stream = item.getClass().getResourceAsStream("/customitems/" + item.getKey().getKey() + ".png")) {
                if (stream == null) continue;
                context.installTexture(item.getKey(), stream.readAllBytes());
            } catch (IOException e) {
                BlazingGames.get().log(e);
                continue;
            }

            // install model
            try (InputStream stream = item.getClass().getResourceAsStream("/customitems/" + item.getKey().getKey() + ".json")) {
                if (stream == null) continue;
                context.installModel(item.getKey(), stream.readAllBytes());
            } catch (IOException e) {
                BlazingGames.get().log(e);
                continue;
            }

            // create items data
            JsonObject root = new JsonObject();
            JsonObject model = new JsonObject();
            model.addProperty("type", "minecraft:model");
            model.addProperty("model", item.getKey().toString());
            root.add("model", model);
            context.writeFile("/assets/" + item.getKey().getNamespace() + "/items/" + item.getKey().getKey() + ".json", root);
        }
    }
}