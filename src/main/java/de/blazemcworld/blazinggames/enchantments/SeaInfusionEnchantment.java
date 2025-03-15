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
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
    public NamespacedKey getModel() {
        return Material.TUBE_CORAL_BLOCK.getKey();
    }

    @Override
    public List<AltarRecipe> getRecipes() {
        return List.of(
                new AltarRecipe(2, 1, 4, 16, new MaterialItemPredicate(Material.TUBE_CORAL)),
                new AltarRecipe(3, 2, 8, 16, new MaterialItemPredicate(Material.TUBE_CORAL_BLOCK)),
                new AltarRecipe(4, 3, 12, 32, new MaterialItemPredicate(Material.SPONGE))
        );
    }
}
