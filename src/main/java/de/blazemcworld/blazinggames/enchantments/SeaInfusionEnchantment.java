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
import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantments;
import de.blazemcworld.blazinggames.enchantments.sys.altar.AltarRecipe;
import de.blazemcworld.blazinggames.items.predicates.ItemPredicate;
import de.blazemcworld.blazinggames.items.predicates.MaterialItemPredicate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SeaInfusionEnchantment extends CustomEnchantment {
    @Override
    public @NotNull NamespacedKey getKey() {
        return BlazingGames.get().key("sea_infusion");
    }

    @Override
    public @NotNull String getDisplayName() {
        return "Sea Infusion";
    }

    public ItemPredicate getItemTarget() {
        return BlazingEnchantmentTarget.WEAPON_TRIDENT;
    }

    public int getMaxLevel() {
        return 3;
    }

    public double getDamageIncrease(Entity victim, int level) {
        if(victim.isInWaterOrRainOrBubbleColumn()) {
            return level*level*0.5;
        }
        return 0;
    }

    public boolean conflictsWith(Enchantment enchantment) {
        return enchantment == Enchantment.SHARPNESS
                || enchantment == Enchantment.SMITE
                || enchantment == Enchantment.BANE_OF_ARTHROPODS;
    }

    public boolean conflictsWith(@NotNull CustomEnchantment enchantment) {
        return enchantment == CustomEnchantments.HELL_INFUSION
                || enchantment == CustomEnchantments.BANE_OF_ILLAGERS;
    }

    @Override
    public int maxLevelAvailableInAltar(int altarTier) {
        if(altarTier <= 1) return 0;
        return altarTier - 1;
    }

    @Override
    public ItemStack getPreIcon() {
        return new ItemStack(Material.TUBE_CORAL_BLOCK);
    }

    @Override
    public AltarRecipe getRecipe(int level) {
        return new AltarRecipe(
                level, level * 4, level == 3 ? 32 : 16, switch(level) {
                    case 2 -> new MaterialItemPredicate(Material.TUBE_CORAL_BLOCK);
                    case 3 -> new MaterialItemPredicate(Material.SPONGE);
                    default -> new MaterialItemPredicate(Material.TUBE_CORAL);
                }
        );
    }
}
