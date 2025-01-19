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
import de.blazemcworld.blazinggames.enchantments.sys.PaperEnchantmentTarget;
import de.blazemcworld.blazinggames.enchantments.sys.altar.AltarRecipe;
import de.blazemcworld.blazinggames.items.predicates.ItemPredicate;
import de.blazemcworld.blazinggames.items.predicates.MaterialItemPredicate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Illager;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BaneOfIllagersEnchantment extends CustomEnchantment {
    @Override
    public @NotNull NamespacedKey getKey() {
        return BlazingGames.get().key("bane_of_illagers");
    }

    @Override
    public @NotNull String getDisplayName() {
        return "Bane of Illagers";
    }

    public ItemPredicate getItemTarget() {
        return PaperEnchantmentTarget.WEAPON;
    }

    public int getMaxLevel() {
        return 5;
    }

    public double getDamageIncrease(Entity victim, int level) {
        if(victim instanceof Illager) {
            return level*2.5;
        }
        return 0;
    }

    @Override
    public int maxLevelAvailableInAltar(int altarTier) {
        return altarTier + 1;
    }

    @Override
    public ItemStack getPreIcon() {
        return new ItemStack(Material.IRON_AXE);
    }

    @Override
    public AltarRecipe getRecipe(int level) {
        return new AltarRecipe(
                level, level, switch(level) {
                    case 3, 4 -> 16;
                    default -> 1;
                }, switch(level) {
                    case 2 -> new MaterialItemPredicate(Material.IRON_AXE);
                    case 3 -> new MaterialItemPredicate(Material.EMERALD);
                    case 4 -> new MaterialItemPredicate(Material.EMERALD_BLOCK);
                    case 5 -> new MaterialItemPredicate(Material.TOTEM_OF_UNDYING);
                    default -> new MaterialItemPredicate(Material.SADDLE);
                }
        );
    }
}
