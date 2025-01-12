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

import org.bukkit.enchantments.Enchantment;

import java.util.List;

public class EnchantmentOrder {
    public static List<Enchantment> order() {
        return List.of(
                Enchantment.BINDING_CURSE,
                Enchantment.VANISHING_CURSE,
                Enchantment.RIPTIDE,
                Enchantment.CHANNELING,
                Enchantment.FROST_WALKER,
                Enchantment.SHARPNESS,
                Enchantment.SMITE,
                Enchantment.BANE_OF_ARTHROPODS,
                Enchantment.IMPALING,
                Enchantment.POWER,
                Enchantment.PIERCING,
                Enchantment.SWEEPING_EDGE,
                Enchantment.MULTISHOT,
                Enchantment.FIRE_ASPECT,
                Enchantment.FLAME,
                Enchantment.KNOCKBACK,
                Enchantment.PUNCH,
                Enchantment.PROTECTION,
                Enchantment.BLAST_PROTECTION,
                Enchantment.FIRE_PROTECTION,
                Enchantment.PROJECTILE_PROTECTION,
                Enchantment.FEATHER_FALLING,
                Enchantment.FORTUNE,
                Enchantment.LOOTING,
                Enchantment.SILK_TOUCH,
                Enchantment.LUCK_OF_THE_SEA,
                Enchantment.EFFICIENCY,
                Enchantment.QUICK_CHARGE,
                Enchantment.LURE,
                Enchantment.RESPIRATION,
                Enchantment.AQUA_AFFINITY,
                Enchantment.SOUL_SPEED,
                Enchantment.SWIFT_SNEAK,
                Enchantment.DEPTH_STRIDER,
                Enchantment.THORNS,
                Enchantment.LOYALTY,
                Enchantment.UNBREAKING,
                Enchantment.INFINITY,
                Enchantment.MENDING
        );
    }
}
