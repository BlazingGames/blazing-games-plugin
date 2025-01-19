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

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

public class BreakableItemPredicate implements ItemPredicate {
    private BreakableItemPredicate() {}

    public static BreakableItemPredicate instance = new BreakableItemPredicate();

    @Override
    public boolean matchItem(ItemStack stack) {
        if(stack == null) {
            return false;
        }

        if(!stack.hasData(DataComponentTypes.DAMAGE)) return false;
        if(!stack.hasData(DataComponentTypes.MAX_DAMAGE)) return false;

        return !stack.hasData(DataComponentTypes.UNBREAKABLE);
    }

    @Override
    public Component getDescription() {
        return Component.text("Any Breakable Item");
    }
}
