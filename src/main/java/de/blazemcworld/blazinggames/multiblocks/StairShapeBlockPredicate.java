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
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Stairs;

public enum StairShapeBlockPredicate implements BlockPredicate {
    STRAIGHT_NORTH(true, false, false, false, false),
    STRAIGHT_SOUTH(false, true, false, false, false),
    STRAIGHT_EAST(false, false, true, false, false),
    STRAIGHT_WEST(false, false, false, true, false),
    INNER_NORTH_WEST(true, false, false, true, false),
    INNER_NORTH_EAST(true, false, true, false, false),
    INNER_SOUTH_WEST(false, true, false, true, false),
    INNER_SOUTH_EAST(false, true, true, false, false),
    OUTER_NORTH_WEST(true, false, false, true, true),
    OUTER_NORTH_EAST(true, false, true, false, true),
    OUTER_SOUTH_WEST(false, true, false, true, true),
    OUTER_SOUTH_EAST(false, true, true, false, true);

    private final boolean north, south, east, west, outer;

    StairShapeBlockPredicate(boolean north, boolean south, boolean east, boolean west, boolean outer) {
        this.north = north;
        this.south = south;
        this.east = east;
        this.west = west;
        this.outer = outer;
    }

    @Override
    public boolean matchBlock(BlockState block, int direction) {
        BlockData data = block.getBlockData();

        if(data instanceof Stairs stairs) {
            boolean north = false;
            boolean south = false;
            boolean east = false;
            boolean west = false;
            boolean outer = stairs.getShape() == Stairs.Shape.OUTER_LEFT || stairs.getShape() == Stairs.Shape.OUTER_RIGHT;

            switch(stairs.getFacing()) {
                case EAST -> east = true;
                case SOUTH -> south = true;
                case WEST -> west = true;
                case NORTH -> north = true;
            }

            switch(stairs.getShape()) {
                case OUTER_LEFT, INNER_LEFT -> {
                    switch(stairs.getFacing()) {
                        case EAST -> north = true;
                        case SOUTH -> east = true;
                        case WEST -> south = true;
                        case NORTH -> west = true;
                    }
                }
                case OUTER_RIGHT, INNER_RIGHT -> {
                    switch(stairs.getFacing()) {
                        case EAST -> south = true;
                        case SOUTH -> west = true;
                        case WEST -> north = true;
                        case NORTH -> east = true;
                    }
                }
            }

            for(int i = 0; i < direction; i++) {
                boolean temp = east;
                east = north;
                north = west;
                west = south;
                south = temp;
            }

            if(this.east != east) return false;
            if(this.west != west) return false;
            if(this.north != north) return false;
            if(this.south != south) return false;
            return this.outer == outer;
        }

        return true;
    }

    @Override
    public String toString() {
        return name();
    }
}
