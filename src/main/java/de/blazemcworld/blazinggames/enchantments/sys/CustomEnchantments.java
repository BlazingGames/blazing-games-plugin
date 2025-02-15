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

import de.blazemcworld.blazinggames.enchantments.*;
import java.util.List;

public class CustomEnchantments implements EnchantmentWrapperProvider {
    public static final CustomEnchantments instance = new CustomEnchantments();

    public static final CustomEnchantment COLLECTABLE = new CollectableEnchantment();
    public static final CustomEnchantment PATTERN = new PatternEnchantment();
    public static final CustomEnchantment TREE_FELLER = new TreeFellerEnchantment();
    public static final CustomEnchantment CAPTURING = new CapturingEnchantment();
    public static final CustomEnchantment NATURE_BLESSING = new NatureBlessingEnchantment();
    public static final CustomEnchantment REFLECTIVE_DEFENSES = new ReflectiveDefensesEnchantment();
    public static final CustomEnchantment BANE_OF_ILLAGERS = new BaneOfIllagersEnchantment();
    public static final CustomEnchantment SEA_INFUSION = new SeaInfusionEnchantment();
    public static final CustomEnchantment HELL_INFUSION = new HellInfusionEnchantment();
    public static final CustomEnchantment UPDRAFT = new UpdraftEnchantment();
    public static final CustomEnchantment FLAME_TOUCH = new FlameTouchEnchantment();
    public static final CustomEnchantment UNSHINY = new UnshinyEnchantment();
    public static final CustomEnchantment SCAVENGER = new ScavengerEnchantment();

    private CustomEnchantments() {}

    public List<CustomEnchantment> list() {
        return List.of(
                COLLECTABLE,
                PATTERN,
                TREE_FELLER,
                CAPTURING,
                NATURE_BLESSING,
                REFLECTIVE_DEFENSES,
                BANE_OF_ILLAGERS,
                SEA_INFUSION,
                HELL_INFUSION,
                UPDRAFT,
                FLAME_TOUCH,
                UNSHINY,
                SCAVENGER
        );
    }
}
