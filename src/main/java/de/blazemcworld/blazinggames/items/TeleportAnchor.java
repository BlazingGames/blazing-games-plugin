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
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class TeleportAnchor extends ContextlessItem {
    @Override
    public @NotNull NamespacedKey getKey() {
        return BlazingGames.get().key("teleport_anchor");
    }

    @Override
    protected @NotNull Component itemName() {
        return Component.text("Teleport Anchor", TextColor.color(0x8E6AFC)).decoration(TextDecoration.ITALIC, false);
    }

    @Override
    protected int stackSize() {
        return 1;
    }

    @Override
    public List<Component> lore(ItemStack stack) {
        return List.of(Component.text("Click to show discovered warpstones.").color(TextColor.color(0xCCCCCC)).decoration(TextDecoration.ITALIC, false));
    }

    public Map<NamespacedKey, Recipe> getRecipes() {
        ShapedRecipe teleportAnchorRecipe = new ShapedRecipe(getKey(), create());
        teleportAnchorRecipe.shape(
                "EEE",
                "ECE",
                "EEE"
        );
        teleportAnchorRecipe.setIngredient('C', Material.COMPASS);
        teleportAnchorRecipe.setIngredient('E', Material.ENDER_PEARL);

        return Map.of(
                getKey(), teleportAnchorRecipe
        );
    }
}
