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

package de.blazemcworld.blazinggames.items.predicates;

import de.blazemcworld.blazinggames.enchantments.sys.BlazingEnchantmentTarget;
import de.blazemcworld.blazinggames.enchantments.sys.PaperEnchantmentTarget;
import org.bukkit.Material;

public class ItemPredicates {
    public static final ItemPredicate enchantability = new AlternativeItemPredicate("Any Enchantable Item",
            BreakableItemPredicate.instance, PaperEnchantmentTarget.ALL, BlazingEnchantmentTarget.BOW_ROCKET
    );

    public static final ItemPredicate grindstoneScrubber = new AlternativeItemPredicate("Sponge or Wet Sponge",
            new MaterialItemPredicate(Material.SPONGE), new MaterialItemPredicate(Material.WET_SPONGE));
    // Controls whether the special code regarding the grindstone inventory is run.
    public static final ItemPredicate grindstoneHandler = new AlternativeItemPredicate("Any Item that can be put in a grindstone",
            new ComplexItemPredicate("N/A", new NegativeItemPredicate(BreakableItemPredicate.instance), enchantability),
            grindstoneScrubber
    );
}
