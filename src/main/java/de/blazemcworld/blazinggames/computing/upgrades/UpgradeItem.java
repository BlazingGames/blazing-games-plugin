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
package de.blazemcworld.blazinggames.computing.upgrades;

import java.util.List;
import java.util.Map;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.items.ContextlessItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class UpgradeItem extends ContextlessItem {
    public final UpgradeType type;
    public UpgradeItem(UpgradeType type) {
        this.type = type;
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return BlazingGames.get().key(type.name().toLowerCase());
    }

    @Override
    protected @NotNull Component itemName() {
        return Component.text(type.displayName, type.color).decoration(TextDecoration.ITALIC, false);
    }

    @Override
    public List<Component> lore(ItemStack stack) {
        if (type.description == null) return List.of();
        return List.of(
            Component.text(type.description, NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true)
        );
    }

    @Override
    public Map<NamespacedKey, Recipe> getRecipes() {
        if (type.recipe == null) return Map.of();
        return Map.of(getKey(), type.recipe.apply(create(), getKey()));
    }
}
