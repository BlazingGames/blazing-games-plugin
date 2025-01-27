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
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ComplexItemPredicate implements ItemPredicate {
    List<ItemPredicate> predicates = new ArrayList<>();
    String description;

    public ComplexItemPredicate add(ItemPredicate... predicate) {
        predicates.addAll(List.of(predicate));
        return this;
    }

    public ComplexItemPredicate(String description, ItemPredicate... predicate) {
        this.description = description;
        add(predicate);
    }

    public List<ItemPredicate> getPredicates() {
        return predicates;
    }

    @Override
    public boolean matchItem(ItemStack stack) {
        for(ItemPredicate predicate : predicates) {
            if(!predicate.matchItem(stack)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return predicates.toString();
    }

    @Override
    public Component getDescription() {
        return Component.text(description);
    }
}