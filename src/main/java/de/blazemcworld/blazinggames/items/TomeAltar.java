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
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class TomeAltar extends CustomItem {
    @Override
    public @NotNull NamespacedKey getKey() {
        return BlazingGames.get().key("tome_altar");
    }

    @Override
    protected @NotNull ItemStack material() {
        ItemStack item = new ItemStack(Material.BLACKSTONE_WALL);

        ItemMeta meta = item.getItemMeta();
        meta.setEnchantmentGlintOverride(true);
        meta.itemName(Component.text("Tome Altar").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
        item.setItemMeta(meta);

        return item;
    }

    public Map<NamespacedKey, Recipe> getRecipes() {
        ShapelessRecipe portableCraftingTableRecipe = new ShapelessRecipe(getKey(), create());
        portableCraftingTableRecipe.addIngredient(Material.ITEM_FRAME);
        portableCraftingTableRecipe.addIngredient(Material.BLACKSTONE);

        return Map.of(
                getKey(), portableCraftingTableRecipe
        );
    }
}
