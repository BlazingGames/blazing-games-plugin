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
import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantment;
import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantmentTarget;
import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantments;
import de.blazemcworld.blazinggames.enchantments.sys.PaperEnchantmentTarget;
import de.blazemcworld.blazinggames.enchantments.sys.altar.AltarRecipe;
import de.blazemcworld.blazinggames.items.MaterialItemPredicate;
import de.blazemcworld.blazinggames.utils.Triple;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PatternEnchantment extends CustomEnchantment {
    public static List<Triple<Integer, Integer, Material>> dimensions = List.of(
            new Triple<>(1,2, Material.WOODEN_PICKAXE),
            new Triple<>(2,2, Material.STONE_PICKAXE),
            new Triple<>(3,3, Material.IRON_PICKAXE)
    );

    @Override
    public @NotNull NamespacedKey getKey() {
        return BlazingGames.get().key("pattern");
    }

    @Override
    public ItemStack getPreIcon() {
        return new ItemStack(Material.IRON_PICKAXE);
    }

    @Override
    public AltarRecipe getRecipe(int level) {
        if(level <= 0) {
            return getRecipe(1);
        }

        if(level > dimensions.size()) {
            return getRecipe(dimensions.size());
        }

        return new AltarRecipe(level, level * 4, new MaterialItemPredicate(dimensions.get(level-1).right));
    }

    @Override
    public @NotNull String getDisplayName() {
        return "Pattern";
    }

    public @NotNull String getDisplayLevel(int level) {
        if(level <= 0 || level > dimensions.size()) {
            return super.getDisplayLevel(level);
        }

        return dimensions.get(level-1).left + "x" + dimensions.get(level-1).middle;
    }

    public CustomEnchantmentTarget getItemTarget() {
        return PaperEnchantmentTarget.TOOL;
    }

    public int getMaxLevel() {
        return 3;
    }

    public boolean conflictsWith(@NotNull CustomEnchantment enchantment) {
        return enchantment == CustomEnchantments.TREE_FELLER;
    }

    @Override
    public boolean canUpgradeLevel(int currentLevel) {
        return false;
    }
}
