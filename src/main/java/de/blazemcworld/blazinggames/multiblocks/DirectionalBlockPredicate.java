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

import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;

public enum DirectionalBlockPredicate implements BlockPredicate {
    WEST(-1, 0, 0),
    EAST(1, 0, 0),
    NORTH(0, 0, -1),
    SOUTH(0, 0, 1);

    private final int forward, vertical, right;

    DirectionalBlockPredicate(int forward, int vertical, int right) {
        this.forward = forward;
        this.vertical = vertical;
        this.right = right;
    }

    @Override
    public boolean matchBlock(BlockState block, int direction) {
        BlockData data = block.getBlockData();

        if(data instanceof Directional dir) {
            BlockFace face = dir.getFacing();

            int forward = face.getModX();
            int vertical = face.getModY();
            int right = face.getModZ();

            for(int i = 0; i < direction; i++) {
                int temp = right;
                right = -forward;
                forward = temp;
            }

            if(forward != this.forward) return false;
            if(vertical != this.vertical) return false;
            return right == this.right;
        }

        return true;
    }

    @Override
    public String toString() {
        return name();
    }
}
