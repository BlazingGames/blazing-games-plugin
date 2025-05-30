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
package de.blazemcworld.blazinggames.enchantments.sys;

import de.blazemcworld.blazinggames.enchantments.sys.altar.AltarRecipe;
import de.blazemcworld.blazinggames.items.CustomItem;
import de.blazemcworld.blazinggames.items.predicates.ItemPredicate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class OverridableVanillaEnchantmentWrapper extends VanillaEnchantmentWrapper {
    public static class VanillaEnchantmentOverrides {
        private ItemPredicate target;
        private List<Enchantment> conflicts = null;

        public VanillaEnchantmentOverrides target(ItemPredicate target) {
            this.target = target;
            return this;
        }

        public VanillaEnchantmentOverrides conflicts(Enchantment... conflicts) {
            return conflicts(List.of(conflicts));
        }

        public VanillaEnchantmentOverrides conflicts(List<Enchantment> conflicts) {
            this.conflicts = new ArrayList<>(conflicts);
            return this;
        }
    }

    private final VanillaEnchantmentOverrides overrides;

    public OverridableVanillaEnchantmentWrapper(Enchantment enchantment, NamespacedKey model, List<Warning> warnings, VanillaEnchantmentOverrides overrides, AltarRecipe... recipes) {
        super(enchantment, model, warnings, recipes);
        this.overrides = overrides;
    }

    public OverridableVanillaEnchantmentWrapper(Enchantment enchantment, NamespacedKey model, VanillaEnchantmentOverrides overrides, AltarRecipe... recipes) {
        super(enchantment, model, recipes);
        this.overrides = overrides;
    }

    public OverridableVanillaEnchantmentWrapper(Enchantment enchantment, Material model, List<Warning> warnings, VanillaEnchantmentOverrides overrides, AltarRecipe... recipes) {
        super(enchantment, model, warnings, recipes);
        this.overrides = overrides;
    }

    public OverridableVanillaEnchantmentWrapper(Enchantment enchantment, Material model, VanillaEnchantmentOverrides overrides, AltarRecipe... recipes) {
        super(enchantment, model, recipes);
        this.overrides = overrides;
    }

    @Override
    public boolean canGoOnItem(ItemStack tool) {
        if(overrides.target != null) {
            if(!CustomItem.isCustomItem(tool)) {
                if(tool.getType() == Material.BOOK || tool.getType() == Material.ENCHANTED_BOOK) {
                    return true;
                }
            }
            return overrides.target.matchItem(tool);
        }

        return super.canGoOnItem(tool);
    }

    @Override
    public boolean conflictsWith(Enchantment other) {
        if(overrides.conflicts != null) {
            return overrides.conflicts.contains(other);
        }

        return super.conflictsWith(other);
    }
}
