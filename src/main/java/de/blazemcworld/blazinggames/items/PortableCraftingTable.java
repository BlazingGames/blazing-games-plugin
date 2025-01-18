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
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class PortableCraftingTable extends ContextlessItem {
    @Override
    public @NotNull NamespacedKey getKey() {
        return BlazingGames.get().key("portable_crafting_table");
    }

    @Override
    protected @NotNull Component itemName() {
        return Component.text("Portable Crafting Table");
    }

    public Map<NamespacedKey, Recipe> getRecipes() {
        ShapelessRecipe portableCraftingTableRecipe = new ShapelessRecipe(getKey(), create());
        portableCraftingTableRecipe.addIngredient(Material.STICK);
        portableCraftingTableRecipe.addIngredient(Material.CRAFTING_TABLE);

        return Map.of(
                getKey(), portableCraftingTableRecipe
        );
    }
}
