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

import com.google.common.collect.ImmutableList;
import de.blazemcworld.blazinggames.utils.providers.ValueProvider;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;

import java.util.List;
import java.util.Map;

public interface RecipeProvider extends ValueProvider<RecipeWrapper> {
    default List<? extends RecipeWrapper> list() {
        ImmutableList.Builder<RecipeWrapper> list = new ImmutableList.Builder<>();
        for(Map.Entry<NamespacedKey, Recipe> recipe : getRecipes().entrySet()) {
            list.add(new RecipeWrapper(recipe.getKey(), recipe.getValue()));
        }
        return list.build();
    }

    default Map<NamespacedKey, Recipe> getRecipes() {
        return Map.of();
    }
}
