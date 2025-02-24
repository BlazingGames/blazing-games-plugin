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

import com.google.common.collect.ImmutableSet;
import de.blazemcworld.blazinggames.items.CustomItems;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CustomRecipes implements RecipeProvider {
    private static final Map<NamespacedKey, Recipe> registeredRecipes = new HashMap<>();

    private static Set<RecipeProvider> getRecipeProviders() {
        ImmutableSet.Builder<RecipeProvider> providers = new ImmutableSet.Builder<>();
        providers.addAll(CustomItems.getAllItems().stream().toList());

        providers.add(new SlabsToBlockRecipes());
        providers.add(new NametagRecipe());
        providers.add(new SaddleRecipe());
        providers.add(new HorseArmorRecipes());
        providers.add(new CustomRecipes());

        return providers.build();
    }

    public static Map<NamespacedKey, Recipe> getAllRecipes() {
        Map<NamespacedKey, Recipe> recipes = new HashMap<>();

        for(RecipeProvider provider : getRecipeProviders()) {
            recipes.putAll(provider.getRecipes());
        }

        return recipes;
    }

    public static void loadRecipes() {
        Map<NamespacedKey, Recipe> recipes = getAllRecipes();

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
