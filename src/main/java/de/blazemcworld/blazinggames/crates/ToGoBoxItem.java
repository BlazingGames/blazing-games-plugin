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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.items.CustomItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

public class ToGoBoxItem extends CustomItem {
    @Override
    public @NotNull NamespacedKey getKey() {
        return BlazingGames.get().key("to_go_box");
    }

    @Override
    protected @NotNull ItemStack material() {
        ItemStack item = new ItemStack(Material.NETHERITE_SHOVEL);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("To-Go Box").color(NamedTextColor.GOLD)
            .decoration(TextDecoration.ITALIC, false));
        
        meta.lore(List.of(
            Component.text("Right-click a crate to open it and store it inside of a bundle, for transportation.")
                .color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true)
        ));

        item.setItemMeta(meta);
        return item;
    }

    @Override
    public Map<NamespacedKey, Recipe> getRecipes() {
        ShapedRecipe recipe = new ShapedRecipe(getKey(), create());
        recipe.shape(
                "DBD",
                "DED",
                " D "
        );
        recipe.setIngredient('D', new ItemStack(Material.DIAMOND));
        recipe.setIngredient('E', new ItemStack(Material.ENDER_CHEST));
        recipe.setIngredient('B', new ItemStack(Material.BUNDLE));

        ShapelessRecipe bundleRecipe = new ShapelessRecipe(BlazingGames.get().key("bundle"), new ItemStack(Material.BUNDLE));
        bundleRecipe.addIngredient(new ItemStack(Material.LEATHER));
        bundleRecipe.addIngredient(new ItemStack(Material.STRING));

        var out = new HashMap<NamespacedKey, Recipe>();
        out.put(getKey(), recipe);
        out.put(BlazingGames.get().key("bundle"), bundleRecipe);
        return out;
    }
}
