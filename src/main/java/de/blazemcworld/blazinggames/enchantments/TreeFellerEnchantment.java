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
import de.blazemcworld.blazinggames.enchantments.sys.BlazingEnchantmentTarget;
import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantment;
import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantmentTarget;
import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantments;
import de.blazemcworld.blazinggames.enchantments.sys.altar.AltarRecipe;
import de.blazemcworld.blazinggames.items.MaterialItemPredicate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TreeFellerEnchantment extends CustomEnchantment {
    @Override
    public @NotNull NamespacedKey getKey() {
        return BlazingGames.get().key("tree_feller");
    }

    @Override
    public @NotNull String getDisplayName() {
        return "Tree Feller";
    }

    public CustomEnchantmentTarget getItemTarget() {
        return BlazingEnchantmentTarget.AXE;
    }

    public int getMaxLevel() {
        return 5;
    }

    public boolean conflictsWith(@NotNull CustomEnchantment enchantment) {
        return enchantment == CustomEnchantments.PATTERN;
    }

    @Override
    public int maxLevelAvailableInAltar(int altarTier) {
        if(altarTier >= 4) return 5;

        return altarTier;
    }

    @Override
    public ItemStack getPreIcon() {
        return new ItemStack(Material.OAK_SAPLING);
    }

    @Override
    public AltarRecipe getRecipe(int level) {
        return new AltarRecipe(
                level,
                level * 3,
                32,
                switch(level) {
                    case 2 -> new MaterialItemPredicate(Material.JUNGLE_LOG);
                    case 3 -> new MaterialItemPredicate(Material.CHERRY_LOG);
                    case 4 -> new MaterialItemPredicate(Material.WARPED_STEM);
                    case 5 -> new MaterialItemPredicate(Material.CHORUS_FLOWER);
                    default -> new MaterialItemPredicate(Material.OAK_LOG);
                }
        );
    }
}
