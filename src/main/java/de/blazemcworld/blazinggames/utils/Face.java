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

import org.bukkit.util.Vector;

public class Face {
    private final Vector v1, v2;

    public Face(Vector v1, Vector v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public boolean contains(Vector v) {
        double minX = Math.min(v1.getX(), v2.getX());
        double maxX = Math.max(v1.getX(), v2.getX());
        double minY = Math.min(v1.getY(), v2.getY());
        double maxY = Math.max(v1.getY(), v2.getY());
        double minZ = Math.min(v1.getZ(), v2.getZ());
        double maxZ = Math.max(v1.getZ(), v2.getZ());

        return (v.getX() >= minX && v.getX() <= maxX) &&
                (v.getY() >= minY && v.getY() <= maxY) &&
                (v.getZ() >= minZ && v.getZ() <= maxZ);
    }
}
