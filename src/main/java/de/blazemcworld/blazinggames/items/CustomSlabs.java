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

import de.blazemcworld.blazinggames.BlazingGames;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CustomSlabs {
    private final List<Material> blockedMaterials = List.of(
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
            Material.REINFORCED_DEEPSLATE
    );

    public final ArrayList<CustomSlab> slabs = new ArrayList<>();

    public CustomSlabs() {
        List<String> materialNames = Arrays.stream(Material.values()).map(Material::name).toList();
        for (Material material : Material.values()) {
            if (!blockedMaterials.contains(material) && material.isBlock() && material.isItem() && !material.isInteractable() && material.isOccluding() && material.isCollidable() && material.isSolid() && !material.name().contains("_PLANKS") && !material.name().contains("_SLAB") && !materialNames.contains(material.name() + "_SLAB")) {
                if (material.name().endsWith("S")) {
                    if (materialNames.contains(material.name().substring(0, material.name().length() - 1) + "_SLAB"))
                        continue;
                }
                slabs.add(new CustomSlab(material));
            }
        }
    }

    public static class CustomSlab extends CustomItem {
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
        protected @NotNull ItemStack material() {
            ItemStack item = new ItemStack(material);

            ItemMeta meta = item.getItemMeta();
            meta.setEnchantmentGlintOverride(true);
            meta.itemName(Component.text(camelName + " Slab").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
            item.setItemMeta(meta);

            return item;
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
                if (word.length() > 0) {
                    formattedName.append(Character.toUpperCase(word.charAt(0))); // Capitalize first letter
                    formattedName.append(word.substring(1).toLowerCase()); // Append the rest in lowercase
                    formattedName.append(" "); // Add a space after each word
                }
            }

            // Remove the trailing space and return the result
            return formattedName.toString().trim();
        }
    }
}
