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

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

public class RecipeWrapper implements Keyed {
    private final NamespacedKey key;
    private final Recipe recipe;

    public RecipeWrapper(NamespacedKey key, Recipe recipe) {
        this.key = key;
        this.recipe = recipe;
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    public Recipe getRecipe() {
        return recipe;
    }
}
