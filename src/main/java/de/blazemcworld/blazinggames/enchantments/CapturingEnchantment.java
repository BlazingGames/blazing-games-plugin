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
package de.blazemcworld.blazinggames.enchantments;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.enchantments.sys.CustomTreasureEnchantment;
import de.blazemcworld.blazinggames.enchantments.sys.PaperEnchantmentTarget;
import de.blazemcworld.blazinggames.enchantments.sys.altar.AltarRecipe;
import de.blazemcworld.blazinggames.items.predicates.ItemPredicate;
import de.blazemcworld.blazinggames.items.predicates.MaterialItemPredicate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CapturingEnchantment extends CustomTreasureEnchantment {
    @Override
    public @NotNull NamespacedKey getKey() {
        return BlazingGames.get().key("capturing");
    }

    @Override
    public ItemStack getPreIcon() {
        return new ItemStack(Material.CREEPER_SPAWN_EGG);
    }

    @Override
    public AltarRecipe getRecipe(int level) {
        return new AltarRecipe(
                level, level*4, 16, switch(level) {
                    case 2 -> new MaterialItemPredicate(Material.SPIDER_EYE);
                    case 3 -> new MaterialItemPredicate(Material.EGG);
                    default -> new MaterialItemPredicate(Material.GUNPOWDER);
                }
        );
    }

    @Override
    public @NotNull String getDisplayName() {
        return "Capturing";
    }

    public ItemPredicate getItemTarget() {
        return PaperEnchantmentTarget.WEAPON;
    }

    @Override
    public int maxLevelAvailableInAltar(int altarTier) {
        if(altarTier <= 2) return 0;
        if (altarTier == 4) return 3;
        return altarTier - 1;
    }

    public int getMaxLevel() {
        return 3;
    }
}
