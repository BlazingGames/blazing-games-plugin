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

import java.util.Map;

public class HorseArmorRecipes implements RecipeProvider {
    @Override
    public Map<NamespacedKey, Recipe> getRecipes() {
        Map<NamespacedKey, Recipe> recipes = new java.util.HashMap<>(Map.of());
        Map<Material, Material> armor = Map.of(
                Material.IRON_HORSE_ARMOR, Material.IRON_INGOT,
                Material.GOLDEN_HORSE_ARMOR, Material.GOLD_INGOT,
                Material.DIAMOND_HORSE_ARMOR, Material.DIAMOND
        );

        for (Map.Entry<Material, Material> entry : armor.entrySet()) {
            Material armorMaterial = entry.getKey();
            Material itemMaterial = entry.getValue();
            NamespacedKey key = BlazingGames.get().key(itemMaterial.name().toLowerCase().replaceAll(" ", "_") + "_horse_armor");

            ShapedRecipe recipe = new ShapedRecipe(key, new ItemStack(armorMaterial));
            recipe.shape(
                    "I I",
                    "III",
                    "I I"
            );
            recipe.setIngredient('I', itemMaterial);

            recipes.put(key, recipe);
        }

        return recipes;
    }
}
