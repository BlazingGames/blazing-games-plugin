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

public class SaddleRecipe implements RecipeProvider {
    @Override
    public Map<NamespacedKey, Recipe> getRecipes() {
        NamespacedKey key = BlazingGames.get().key("saddle");

        ShapedRecipe saddleRecipe = new ShapedRecipe(key, new ItemStack(Material.SADDLE));
        saddleRecipe.shape(
                "LLL",
                "LSL",
                " I "
        );
        saddleRecipe.setIngredient('L', Material.LEATHER);
        saddleRecipe.setIngredient('S', Material.STRING);
        saddleRecipe.setIngredient('I', Material.IRON_INGOT);

        return Map.of(
                key, saddleRecipe
        );
    }
}
