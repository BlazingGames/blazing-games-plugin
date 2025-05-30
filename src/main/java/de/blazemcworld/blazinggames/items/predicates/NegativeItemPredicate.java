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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.inventory.ItemStack;

public class NegativeItemPredicate implements ItemPredicate {
    private final ItemPredicate predicate;

    public NegativeItemPredicate(ItemPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean matchItem(ItemStack stack) {
        return !predicate.matchItem(stack);
    }

    @Override
    public Component getDescription() {
        return Component.join(JoinConfiguration.spaces(), Component.text("No"), predicate.getDescription());
    }
}
