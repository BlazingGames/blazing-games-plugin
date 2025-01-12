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
package de.blazemcworld.blazinggames.utils;

import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class Box {
    private final Face top, bottom, left, right, front, back;

    public Box(Face top, Face bottom, Face left, Face right, Face front, Face back) {
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
        this.front = front;
        this.back = back;
    }

    public Vector getDirection(Vector v) {
        if (top.contains(v)) return new Vector(0, 1, 0);
        if (bottom.contains(v)) return new Vector(0, -1, 0);
        if (left.contains(v)) return new Vector(-1, 0, 0);
        if (right.contains(v)) return new Vector(1, 0, 0);
        if (front.contains(v)) return new Vector(0, 0, 1);
        if (back.contains(v)) return new Vector(0, 0, -1);
        return null;
    }

    public BlockFace getFace(Vector v) {
        if (top.contains(v)) return BlockFace.UP;
        if (bottom.contains(v)) return BlockFace.DOWN;
        if (left.contains(v)) return BlockFace.WEST;
        if (right.contains(v)) return BlockFace.EAST;
        if (front.contains(v)) return BlockFace.NORTH;
        if (back.contains(v)) return BlockFace.SOUTH;
        return null;
    }
}
