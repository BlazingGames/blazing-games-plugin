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
import de.blazemcworld.blazinggames.enchantments.sys.CustomSingleLeveledEnchantment;
import de.blazemcworld.blazinggames.enchantments.sys.altar.AltarRecipe;
import de.blazemcworld.blazinggames.items.MaterialItemPredicate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NatureBlessingEnchantment extends CustomSingleLeveledEnchantment {
    @Override
    public @NotNull NamespacedKey getKey() {
        return BlazingGames.get().key("nature_blessing");
    }

    @Override
    public ItemStack getPreIcon() {
        return new ItemStack(Material.WHEAT);
    }

    @Override
    public AltarRecipe getRecipe(int level) {
        return new AltarRecipe(
                1, 4, 32, new MaterialItemPredicate(Material.BONE_BLOCK)
        );
    }

    @Override
    public @NotNull String getDisplayName() {
        return "Nature's Blessing";
    }

    public CustomEnchantmentTarget getItemTarget() {
        return BlazingEnchantmentTarget.HOE;
    }

    @Override
    protected boolean allowAltarTier(int altarTier) {
        return true;
    }
}
