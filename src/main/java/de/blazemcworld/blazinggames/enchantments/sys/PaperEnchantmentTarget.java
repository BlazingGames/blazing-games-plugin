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

import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentTarget;
import org.jetbrains.annotations.NotNull;

public enum PaperEnchantmentTarget implements CustomEnchantmentTarget {
    ALL(EnchantmentTarget.ALL),
    ARMOR(EnchantmentTarget.ARMOR),
    ARMOR_FFET(EnchantmentTarget.ARMOR_FEET),
    ARMOR_HEAD(EnchantmentTarget.ARMOR_HEAD),
    ARMOR_LEGS(EnchantmentTarget.ARMOR_LEGS),
    ARMOR_TORSO(EnchantmentTarget.ARMOR_TORSO),
    TOOL(EnchantmentTarget.TOOL),
    BOW(EnchantmentTarget.BOW),
    BREAKABLE(EnchantmentTarget.BREAKABLE),
    CROSSBOW(EnchantmentTarget.CROSSBOW),
    FISHING_ROD(EnchantmentTarget.FISHING_ROD),
    TRIDENT(EnchantmentTarget.TRIDENT),
    VANISHABLE(EnchantmentTarget.VANISHABLE),
    WEAPON(EnchantmentTarget.WEAPON),
    WEARABLE(EnchantmentTarget.WEARABLE);

    final EnchantmentTarget paperTarget;

    PaperEnchantmentTarget(EnchantmentTarget target) {
        paperTarget = target;
    }

    public boolean includes(@NotNull Material item) {
        return paperTarget.includes(item);
    }
}
