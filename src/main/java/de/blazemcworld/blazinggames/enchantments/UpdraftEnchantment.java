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
import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantmentTarget;
import de.blazemcworld.blazinggames.enchantments.sys.CustomTreasureEnchantment;
import de.blazemcworld.blazinggames.enchantments.sys.altar.AltarRecipe;
import de.blazemcworld.blazinggames.items.MaterialItemPredicate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class UpdraftEnchantment extends CustomTreasureEnchantment {
    @Override
    public @NotNull NamespacedKey getKey() {
        return BlazingGames.get().key("updraft");
    }

    @Override
    public ItemStack getPreIcon() {
        return new ItemStack(Material.CAMPFIRE);
    }

    @Override
    public AltarRecipe getRecipe(int level) {
        return new AltarRecipe(
                level, level * 8, 16, new MaterialItemPredicate(
                        level == 2 ? Material.SOUL_CAMPFIRE : Material.CAMPFIRE)
        );
    }

    @Override
    public @NotNull String getDisplayName() {
        return "Updraft";
    }

    public CustomEnchantmentTarget getItemTarget() {
        return BlazingEnchantmentTarget.ELYTRA;
    }

    @Override
    public int maxLevelAvailableInAltar(int altarTier) {
        if(altarTier <= 1) return 0;
        return altarTier - 1;
    }

    public int getMaxLevel() {
        return 2;
    }
}
