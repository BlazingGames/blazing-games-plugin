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

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;

import java.util.HashMap;
import java.util.Map;

public class CustomRecipes implements RecipeProvider {
    private static final Map<NamespacedKey, Recipe> registeredRecipes = new HashMap<>();

    public static void loadRecipes() {
        Map<NamespacedKey, Recipe> recipes = RecipeProviders.instance.getRecipes();

        for(Map.Entry<NamespacedKey, Recipe> recipe : recipes.entrySet()) {
            registeredRecipes.put(recipe.getKey(), recipe.getValue());
            try {
                Bukkit.addRecipe(recipe.getValue());
            } catch (IllegalStateException err) {
                Bukkit.removeRecipe(recipe.getKey());
                Bukkit.addRecipe(recipe.getValue());
            }
        }
    }

    public static void unloadRecipes() {
        for(NamespacedKey recipe : registeredRecipes.keySet()) {
            try {
                Bukkit.removeRecipe(recipe);
            } catch (IllegalStateException ignored) {}
        }
        registeredRecipes.clear();
    }

    public Map<NamespacedKey, Recipe> getRecipes() {
        return Map.of();
    }
}
