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

package de.blazemcworld.blazinggames.items;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gson.JsonObject;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.packs.HookContext;
import de.blazemcworld.blazinggames.packs.PackBuildHook;

public interface ItemProvider extends PackBuildHook {
    default Set<CustomItem<?>> getItems() {
        return Set.of();
    }

    @Override
    default void runHook(Logger logger, HookContext context) {
        installItems(getClass().getSimpleName(), this, logger, context);
    }

    public static void installItems(String directoryName, ItemProvider provider, Logger logger, HookContext context) {
        String directory = "/" + directoryName + "/";
        for (CustomItem<?> item : provider.getItems()) {
            // install texture
            try (InputStream stream = item.getClass().getResourceAsStream(directory + item.getKey().getKey() + ".png")) {
                if (stream != null) context.installTexture(item.getKey(), "item", stream.readAllBytes());
            } catch (IOException e) {
                BlazingGames.get().log(e);
            }

            // install model
            try (InputStream stream = item.getClass().getResourceAsStream(directory + item.getKey().getKey() + ".json")) {
                if (stream != null) context.installModel(item.getKey(), stream.readAllBytes());
            } catch (IOException e) {
                BlazingGames.get().log(e);
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
