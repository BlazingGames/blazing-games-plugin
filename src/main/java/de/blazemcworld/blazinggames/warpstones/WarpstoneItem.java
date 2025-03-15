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
package de.blazemcworld.blazinggames.warpstones;

import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.items.ContextlessItem;
import de.blazemcworld.blazinggames.items.CustomItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class WarpstoneItem extends ContextlessItem {
    @Override
    public @NotNull NamespacedKey getKey() {
        return BlazingGames.get().key("warpstone");
    }

    @Override
    protected @NotNull Component itemName() {
        return Component.text("Warpstone", TextColor.color(0xB873E2));
    }

    @Override
    protected @NotNull Material baseMaterial() {
        return Material.BARRIER;
    }

    @Override
    public List<Component> lore(ItemStack stack) {
        TextColor color = TextColor.color(0xCCCCCC);
        return List.of(
            Component.text("Place in the world and use a Teleport", color),
            Component.text("Anchor to save this warpstone. Then,", color),
            Component.text("use the Teleport Anchor to teleport", color),
            Component.text("back to this warpstone anytime.", color)
        );
    }

    @Override
    public Map<NamespacedKey, Recipe> getRecipes() {
        ShapedRecipe recipe = new ShapedRecipe(getKey(), create());
        recipe.shape(
                "PNP",
                "PLP",
                "PPP"
        );
        recipe.setIngredient('P', Material.POPPED_CHORUS_FRUIT);
        recipe.setIngredient('N', CustomItems.NETHER_STAR_CHUNK.create());
        recipe.setIngredient('L', Material.LODESTONE);
        return Map.of(getKey(), recipe);
    }
}