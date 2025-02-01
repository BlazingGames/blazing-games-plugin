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
package de.blazemcworld.blazinggames.enchantments.sys.altar;

import de.blazemcworld.blazinggames.items.predicates.EmptyItemPredicate;
import de.blazemcworld.blazinggames.items.predicates.ItemPredicate;
import org.bukkit.inventory.ItemStack;

public record AltarRecipe(int tier, int lapisAmount, int expAmount, int itemAmount, ItemPredicate itemRequirement) {
    public AltarRecipe(int tier, int lapisAmount, int expAmount, ItemPredicate itemRequirement) {
        this(tier, lapisAmount, expAmount, (itemRequirement instanceof EmptyItemPredicate) ? 0 : 1, itemRequirement);
    }

    public boolean matchMaterial(ItemStack material) {
        if(!itemRequirement.matchItem(material)) {
            return false;
        }

        return material.getAmount() >= itemAmount;
    }
}
