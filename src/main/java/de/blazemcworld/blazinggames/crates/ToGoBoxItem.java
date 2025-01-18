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

import de.blazemcworld.blazinggames.items.ContextlessItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import de.blazemcworld.blazinggames.BlazingGames;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

public class ToGoBoxItem extends ContextlessItem {
    @Override
    public @NotNull NamespacedKey getKey() {
        return BlazingGames.get().key("to_go_box");
    }

    @Override
    protected @NotNull ItemStack modifyMaterial(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.lore(List.of(
            Component.text("Right-click a crate to open it and store it inside of a bundle, for transportation.")
                .color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true)
        ));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    protected @NotNull Component itemName() {
        return Component.text("To-Go Box").color(NamedTextColor.GOLD);
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

        var out = new HashMap<NamespacedKey, Recipe>();
        out.put(getKey(), recipe);
        return out;
    }
}
