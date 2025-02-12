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
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TreeFellerEnchantment extends CustomEnchantment {
    @Override
    public @NotNull NamespacedKey getKey() {
        return BlazingGames.get().key("tree_feller");
    }

    @Override
    public @NotNull String getDisplayName() {
        return "Tree Feller";
    }

    public ItemPredicate getItemTarget() {
        return BlazingEnchantmentTarget.AXE;
    }

    public boolean conflictsWith(@NotNull CustomEnchantment enchantment) {
        return enchantment == CustomEnchantments.PATTERN;
    }

    @Override
    public NamespacedKey getModel() {
        return Material.OAK_SAPLING.getKey();
    }

    @Override
    public List<AltarRecipe> getRecipes() {
        return List.of(
                new AltarRecipe(1, 1, 3, 32, new MaterialItemPredicate(Material.OAK_LOG)),
                new AltarRecipe(2, 2, 6, 32, new MaterialItemPredicate(Material.JUNGLE_LOG)),
                new AltarRecipe(3, 3, 9, 32, new MaterialItemPredicate(Material.CHERRY_LOG)),
                new AltarRecipe(4, 4, 12, 32, new MaterialItemPredicate(Material.WARPED_STEM)),
                new AltarRecipe(4, 5, 15, 32, new MaterialItemPredicate(Material.CHORUS_FLOWER))
        );
    }
}
