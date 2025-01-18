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
package de.blazemcworld.blazinggames.crates;

import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.items.CustomItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

public class SkeletonKey extends CustomItem {
    @Override
    public @NotNull NamespacedKey getKey() {
        return BlazingGames.get().key("skeleton_key");
    }

    @Override
    protected @NotNull ItemStack material() {
        ItemStack item = new ItemStack(Material.SKELETON_SKULL);
        ItemMeta meta = item.getItemMeta();
        meta.itemName(Component.text("Skeleton Key").color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(Component.text("Used to open any crate that you've lost the key to.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true)));
        meta.setEquippable(null);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public Map<NamespacedKey, Recipe> getRecipes() {
        ShapedRecipe recipe = new ShapedRecipe(getKey(), create());
        recipe.shape(" D ", " B ", " B ");
        recipe.setIngredient('D', Material.DIAMOND);
        recipe.setIngredient('B', Material.BONE);
        return Map.of(getKey(), recipe);
    }
}
