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

import org.bukkit.block.BlockState;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;

public enum BisectedBlockPredicate implements BlockPredicate {
    TOP(Bisected.Half.TOP),
    BOTTOM(Bisected.Half.BOTTOM);

    private final Bisected.Half half;

    BisectedBlockPredicate(Bisected.Half half) {
        this.half = half;
    }

    @Override
    public boolean matchBlock(BlockState block, int direction) {
        BlockData data = block.getBlockData();

        if(data instanceof Bisected bi) {
            return bi.getHalf() == half;
        }

        return true;
    }

    @Override
    public String toString() {
        return name();
    }
}
