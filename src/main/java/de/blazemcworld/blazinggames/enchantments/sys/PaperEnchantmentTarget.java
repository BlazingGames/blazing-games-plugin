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

import de.blazemcworld.blazinggames.items.CustomItem;
import de.blazemcworld.blazinggames.items.predicates.ItemPredicate;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public enum PaperEnchantmentTarget implements ItemPredicate {
    ALL("\"All\"", EnchantmentTarget.ALL),
    ARMOR("Armor", EnchantmentTarget.ARMOR),
    ARMOR_FFET("Boots", EnchantmentTarget.ARMOR_FEET),
    ARMOR_HEAD("Head", EnchantmentTarget.ARMOR_HEAD),
    ARMOR_LEGS("Legs", EnchantmentTarget.ARMOR_LEGS),
    ARMOR_TORSO("Torso", EnchantmentTarget.ARMOR_TORSO),
    TOOL("Tools", EnchantmentTarget.TOOL),
    BOW("Bows", EnchantmentTarget.BOW),
    BREAKABLE("Breakable", EnchantmentTarget.BREAKABLE),
    CROSSBOW("Crossbows", EnchantmentTarget.CROSSBOW),
    FISHING_ROD("Fishing Rods", EnchantmentTarget.FISHING_ROD),
    TRIDENT("Tridents", EnchantmentTarget.TRIDENT),
    VANISHABLE("Vanishable", EnchantmentTarget.VANISHABLE),
    WEAPON("Weapons", EnchantmentTarget.WEAPON),
    WEARABLE("Wearable", EnchantmentTarget.WEARABLE);

    final EnchantmentTarget paperTarget;
    final String description;

    PaperEnchantmentTarget(String description, EnchantmentTarget target) {
        paperTarget = target;
        this.description = description;
    }

    public boolean matchItem(@NotNull Material item) {
        return paperTarget.includes(item);
    }

    @Override
    public boolean matchItem(ItemStack stack) {
        if(CustomItem.isCustomItem(stack)) return false;

        return matchItem(stack.getType());
    }

    @Override
    public Component getDescription() {
        return Component.text(description);
    }
}
