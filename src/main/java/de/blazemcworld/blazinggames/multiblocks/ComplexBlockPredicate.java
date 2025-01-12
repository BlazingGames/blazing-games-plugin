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
package de.blazemcworld.blazinggames.multiblocks;

import de.blazemcworld.blazinggames.BlazingGames;
import org.bukkit.block.BlockState;

import java.util.ArrayList;
import java.util.List;

public class ComplexBlockPredicate implements BlockPredicate {
    List<BlockPredicate> predicates = new ArrayList<>();

    public ComplexBlockPredicate add(BlockPredicate... predicate) {
        predicates.addAll(List.of(predicate));
        return this;
    }

    public ComplexBlockPredicate(BlockPredicate... predicate) {
        add(predicate);
    }

    public List<BlockPredicate> getPredicates() {
        return predicates;
    }

    @Override
    public boolean matchBlock(BlockState block, int direction) {
        for(BlockPredicate predicate : predicates) {
            if(!predicate.matchBlock(block, direction)) {
                BlazingGames.get().debugLog("Complex Predicate Failed. " + predicate);
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return predicates.toString();
    }
}
