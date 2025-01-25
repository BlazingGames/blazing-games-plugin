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

import com.google.gson.JsonObject;
import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.packs.HookContext;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Orientable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

import static de.blazemcworld.blazinggames.BlazingGames.gson;

public class CustomSlabs implements ItemProvider {
    private static final List<Material> blockedMaterials = List.of(
        Material.COMMAND_BLOCK,
        Material.CHAIN_COMMAND_BLOCK,
        Material.REPEATING_COMMAND_BLOCK,
        Material.SPAWNER,
        Material.TRIAL_SPAWNER,
        Material.BARRIER,
        Material.JIGSAW,
            Material.BEDROCK,
            Material.SUSPICIOUS_GRAVEL,
            Material.SUSPICIOUS_SAND,
            Material.REINFORCED_DEEPSLATE,

            // Temporarily Disabled
            Material.TARGET,
            Material.WAXED_EXPOSED_CHISELED_COPPER,
            Material.SNOW_BLOCK,
            Material.MANGROVE_ROOTS,
            Material.INFESTED_CHISELED_STONE_BRICKS,
            Material.SCULK_CATALYST,
            Material.WAXED_COPPER_BLOCK,
            Material.MYCELIUM,
            Material.WAXED_OXIDIZED_COPPER,
            Material.WAXED_OXIDIZED_CHISELED_COPPER,
            Material.INFESTED_MOSSY_STONE_BRICKS,
            Material.INFESTED_STONE,
            Material.INFESTED_STONE_BRICKS,
            Material.INFESTED_CRACKED_STONE_BRICKS,
            Material.MAGMA_BLOCK,
            Material.DRIED_KELP_BLOCK,
            Material.MELON,
            Material.WAXED_EXPOSED_COPPER,
            Material.ANCIENT_DEBRIS,
            Material.GRASS_BLOCK,
            Material.PODZOL,
            Material.QUARTZ_BLOCK,
            Material.WAXED_CHISELED_COPPER,
            Material.INFESTED_COBBLESTONE,
            Material.WAXED_WEATHERED_COPPER,
            Material.WAXED_WEATHERED_CHISELED_COPPER,
            Material.LODESTONE
    );

    public final ArrayList<CustomSlab> slabs = new ArrayList<>();

    public CustomSlabs() {
        List<String> materialNames = Arrays.stream(Material.values()).map(Material::name).toList();
        for (Material material : Material.values()) {
            if (!blockedMaterials.contains(material) && material.isBlock() && material.isItem() && !material.isInteractable() && material.isOccluding() && material.isCollidable() && material.isSolid() && !material.name().contains("_PLANKS") && !material.name().contains("_SLAB") && !materialNames.contains(material.name() + "_SLAB")) {
                BlockData blockData = material.createBlockData();

                //Temporarily Disabled
                if (!(blockData instanceof Directional) && !(blockData instanceof Orientable)) {
                    if (material.name().endsWith("S")) {
                        if (materialNames.contains(material.name().substring(0, material.name().length() - 1) + "_SLAB"))
                            continue;
                    }
                    slabs.add(new CustomSlab(material));
                }
            }
        }
    }

    @Override
    public Set<CustomItem<?>> getItems() {
        return new HashSet<>(slabs);
    }

    public static class CustomSlab extends ContextlessItem {
        public final Material material;
        public final String name;
        public final String camelName;

        private CustomSlab(Material material) {
            this.material = material;
            name = material.name().toLowerCase();
            camelName = formatMaterialName(name);
        }

        @Override
        public @NotNull NamespacedKey getKey() {
            return BlazingGames.get().key(name + "_slab");
        }

        @Override
        protected @NotNull Material baseMaterial() {
            return Material.REINFORCED_DEEPSLATE;
        }

        @Override
        protected @NotNull Component itemName() {
            return Component.text(camelName + " Slab");
        }

        public Map<NamespacedKey, Recipe> getRecipes() {
            ItemStack item = create();
            item.setAmount(6);
            ShapedRecipe recipe = new ShapedRecipe(getKey(), item);
            recipe.shape(
                    "   ",
                    "   ",
                    "MMM"
            );
            recipe.setIngredient('M', new ItemStack(material));

            ShapedRecipe recipe2 = new ShapedRecipe(BlazingGames.get().key(name), new ItemStack(material));
            recipe2.shape(
                    "   ",
                    "  M",
                    "  M"
            );
            recipe2.setIngredient('M', create());

            return Map.of(
                    getKey(), recipe,
                    BlazingGames.get().key(name), recipe2
            );
        }

        private String formatMaterialName(String materialName) {
            // Split the string by underscores
            String[] words = materialName.split("_");

            // Use a StringBuilder to construct the final result
            StringBuilder formattedName = new StringBuilder();

            for (String word : words) {
                // Capitalize the first letter and append the rest of the word
                if (!word.isEmpty()) {
                    formattedName.append(Character.toUpperCase(word.charAt(0))); // Capitalize first letter
                    formattedName.append(word.substring(1).toLowerCase()); // Append the rest in lowercase
                    formattedName.append(" "); // Add a space after each word
                }
            }

            // Remove the trailing space and return the result
            return formattedName.toString().trim();
        }
    }

    @Override
    public void runHook(Logger logger, HookContext context) {
        String customSlab = null, customTopSlab = null, customBottomSlab = null;
        try {
            InputStream customSlabStream = this.getClass().getResourceAsStream("/customitems/custom_slab.json");
            customSlab = new String(customSlabStream.readAllBytes(), StandardCharsets.UTF_8);
            customSlabStream.close();

            InputStream customTopSlabStream = this.getClass().getResourceAsStream("/customitems/custom_slab_top.json");
            customTopSlab = new String(customTopSlabStream.readAllBytes(), StandardCharsets.UTF_8);
            customTopSlabStream.close();

            InputStream customBottomSlabStream = this.getClass().getResourceAsStream("/customitems/custom_slab_bottom.json");
            customBottomSlab = new String(customBottomSlabStream.readAllBytes(), StandardCharsets.UTF_8);
            customBottomSlabStream.close();
        } catch (IOException e) {
            BlazingGames.get().log(e);
        }
        if (customSlab == null || customTopSlab == null || customBottomSlab == null) {
            logger.warning("Failed to load custom slab models");
            return;
        }
        for (CustomItem<?> item : getItems()) {
            CustomSlab slab = (CustomSlab) item;
            // install slab models
            String[] jsons = new String[]{
                    customBottomSlab,
                    customTopSlab
            };
            for (int i = 0; i < jsons.length; i++) {
                String json = jsons[i];

                JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

                JsonObject textures = jsonObject.get("textures").getAsJsonObject();
                textures.addProperty("bottom", "minecraft:block/" + slab.material.name().toLowerCase());
                textures.addProperty("side", "minecraft:block/" + slab.material.name().toLowerCase());
                textures.addProperty("top", "minecraft:block/" + slab.material.name().toLowerCase());

                json = gson.toJson(jsonObject);

                byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

                context.installModel(BlazingGames.get().key(slab.name + "_slab" + (i == 0 ? "_bottom" : "_top")), bytes);
            }

            //create item data
            JsonObject jsonObject = gson.fromJson(customSlab, JsonObject.class);

            JsonObject model = jsonObject.get("model").getAsJsonObject();

            JsonObject onTrue = new JsonObject();
            onTrue.addProperty("type", "minecraft:model");
            onTrue.addProperty("model", "blazinggames:" + slab.material.name().toLowerCase() + "_slab_top");
            model.add("on_true", onTrue);

            JsonObject onFalse = new JsonObject();
            onFalse.addProperty("type", "minecraft:model");
            onFalse.addProperty("model", "blazinggames:" + slab.material.name().toLowerCase() + "_slab_bottom");
            model.add("on_false", onFalse);

            context.writeFile("/assets/" + item.getKey().getNamespace() + "/items/" + item.getKey().getKey() + ".json", jsonObject);
        }
    }
}
