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
import de.blazemcworld.blazinggames.enchantments.sys.altar.AltarRecipe;
import de.blazemcworld.blazinggames.items.predicates.ItemPredicate;
import de.blazemcworld.blazinggames.items.predicates.MaterialItemPredicate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ReflectiveDefensesEnchantment extends CustomEnchantment {
    @Override
    public @NotNull NamespacedKey getKey() {
        return BlazingGames.get().key("reflective_defenses");
    }

    @Override
    public @NotNull String getDisplayName() {
        return "Reflective Defenses";
    }

    public ItemPredicate getItemTarget() {
        return BlazingEnchantmentTarget.SHIELD;
    }

    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int maxLevelAvailableInAltar(int altarTier) {
        return altarTier + 1;
    }

    @Override
    public ItemStack getPreIcon() {
        return new ItemStack(Material.SHIELD);
    }

    @Override
    public AltarRecipe getRecipe(int level) {
        return new AltarRecipe(
                level,
                level * 2,
                level == 5 ? 1 : 16,
                switch(level) {
                    case 2 -> new MaterialItemPredicate(Material.SWEET_BERRIES);
                    case 3 -> new MaterialItemPredicate(Material.PRISMARINE_SHARD);
                    case 4 -> new MaterialItemPredicate(Material.PRISMARINE_CRYSTALS);
                    case 5 -> new MaterialItemPredicate(Material.DIAMOND_BLOCK);
                    default -> new MaterialItemPredicate(Material.CACTUS);
                }
        );
    }
}
