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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

public class GuiElementsHook implements PackBuildHook {
    private static List<NamespacedKey> getGuiTextures() {
        BlazingGames bg = BlazingGames.get();
        return List.of(
                bg.key("blank")
        );
    }

    private static List<NamespacedKey> getGuiModels() {
        BlazingGames bg = BlazingGames.get();
        return List.of(
                bg.key("blank"),
                bg.key("tool_slot"),
                bg.key("material_slot"),
                bg.key("lapis_lazuli_slot")
        );
    }

    private static List<NamespacedKey> getGuiDefinitions() {
        BlazingGames bg = BlazingGames.get();
        return List.of(
                bg.key("tool_slot"),
                bg.key("material_slot"),
                bg.key("lapis_lazuli_slot")
        );
    }

    @Override
    public void runHook(Logger logger, HookContext context) {
        for (NamespacedKey texture : getGuiTextures()) {
            // install texture
            try (InputStream stream = BlazingGames.class.getResourceAsStream("/gui/" + texture.getKey() + ".png")) {
                if (stream != null) context.installTexture(texture, "item", stream.readAllBytes());
            } catch (IOException e) {
                BlazingGames.get().log(e);
            }

            // install animation options
            try (InputStream stream = BlazingGames.class.getResourceAsStream("/gui/" + texture.getKey() + ".png.mcmeta")) {
                if (stream != null) context.installTextureAnimationData(texture, "item", stream.readAllBytes());
            } catch (IOException e) {
                BlazingGames.get().log(e);
            }
        }

        for (NamespacedKey model : getGuiModels()) {
            // install model
            try (InputStream stream = BlazingGames.class.getResourceAsStream("/gui/" + model.getKey() + ".json")) {
                if (stream != null) context.installModel(model, stream.readAllBytes());
            } catch (IOException e) {
                BlazingGames.get().log(e);
            }
        }

        NamespacedKey blankKey = BlazingGames.get().key("blank");

        JsonObject blankRoot = new JsonObject();
        JsonObject blankModel = new JsonObject();
        blankModel.addProperty("type", "minecraft:model");
        blankModel.addProperty("model", blankKey.toString());
        blankRoot.add("model", blankModel);
        context.writeFile("/assets/" + blankKey.getNamespace() + "/items/" + blankKey.getKey() + ".json", blankRoot);

        for(NamespacedKey itemDefinition : getGuiDefinitions()) {
            // create items data
            JsonObject root = new JsonObject();
            JsonObject composite = new JsonObject();
            JsonArray models = new JsonArray();
            models.add(blankModel);
            JsonObject model = new JsonObject();
            model.addProperty("type", "minecraft:model");
            model.addProperty("model", itemDefinition.toString());
            models.add(model);
            composite.addProperty("type", "minecraft:composite");
            composite.add("models", models);
            root.add("model", composite);
            context.writeFile("/assets/" + itemDefinition.getNamespace() + "/items/" + itemDefinition.getKey() + ".json", root);
        }
    }
}