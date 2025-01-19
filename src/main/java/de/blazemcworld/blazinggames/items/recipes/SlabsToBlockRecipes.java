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
package de.blazemcworld.blazinggames.items.recipes;

import de.blazemcworld.blazinggames.BlazingGames;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;
import java.util.Map;

public class SlabsToBlockRecipes implements RecipeProvider {
    @Override
    public Map<NamespacedKey, Recipe> getRecipes() {
        Map<NamespacedKey, Recipe> recipes = new HashMap<>();
        for (Material mat : Material.values()) {
            if (mat.name().endsWith("_SLAB")) {
                String slabName = mat.name().substring(0, mat.name().length() - 5);
                Material block;
                try {
                    block = Material.valueOf(slabName);
                } catch (IllegalArgumentException err) {
                    try {
                        block = Material.valueOf(slabName + "S");
                    } catch (IllegalArgumentException err2) {
                        try {
                            block = Material.valueOf(slabName + "_PLANKS");
                        } catch (IllegalArgumentException err3) {
                            continue;
                        }
                    }
                }

                ShapedRecipe recipe = new ShapedRecipe(BlazingGames.get().key(slabName), new ItemStack(block));
                recipe.shape(
                        "   ",
                        "  M",
                        "  M"
                );
                recipe.setIngredient('M', new ItemStack(mat));
                recipes.put(BlazingGames.get().key(slabName), recipe);
            }
        }
        return recipes;
    }
}
