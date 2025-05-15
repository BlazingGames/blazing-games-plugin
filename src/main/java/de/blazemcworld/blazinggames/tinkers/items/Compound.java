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

package de.blazemcworld.blazinggames.tinkers.items;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.items.ContextlessItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;
import java.util.Map;

public class Compound extends ContextlessItem {
    @Override
    public @NotNull NamespacedKey getKey() {
        return BlazingGames.get().key("compound");
    }

    @Override
    protected @NotNull Component itemName() {
        return Component.text("Compound");
    }

    @Override
    protected @NotNull Material baseMaterial() {
        return Material.REINFORCED_DEEPSLATE;
    }

    @Override
    public Map<NamespacedKey, Recipe> getRecipes() {
        ItemStack result = create();
        result.setAmount(16);
        ShapedRecipe recipe = new ShapedRecipe(getKey(), result);
        recipe.shape(
                "SSG",
                "SCG",
                "SGG"
        );
        recipe.setIngredient('S', Material.SAND);
        recipe.setIngredient('C', Material.CLAY);
        recipe.setIngredient('G', Material.GRAVEL);
        return Map.of(getKey(), recipe);
    }
}
