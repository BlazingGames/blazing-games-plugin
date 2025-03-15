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
import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantmentType;
import de.blazemcworld.blazinggames.enchantments.sys.CustomTreasureSingleLeveledEnchantment;
import de.blazemcworld.blazinggames.enchantments.sys.altar.AltarRecipe;
import de.blazemcworld.blazinggames.items.predicates.ItemPredicate;
import de.blazemcworld.blazinggames.items.predicates.ItemPredicates;
import de.blazemcworld.blazinggames.items.predicates.MaterialItemPredicate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class UnshinyEnchantment extends CustomTreasureSingleLeveledEnchantment {
    @Override
    public @NotNull NamespacedKey getKey() {
        return BlazingGames.get().key("unshiny");
    }

    @Override
    public NamespacedKey getModel() {
        return Material.INK_SAC.getKey();
    }

    @Override
    public AltarRecipe getRecipe() {
        return new AltarRecipe(1, 1, 0, new MaterialItemPredicate(Material.INK_SAC));
    }

    @Override
    public @NotNull String getDisplayName() {
        return "Unshiny";
    }

    public ItemPredicate getItemTarget() {
        return ItemPredicates.enchantability;
    }

    public @NotNull CustomEnchantmentType getEnchantmentType() {
        return CustomEnchantmentType.COSMETIC;
    }
}
