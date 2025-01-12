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
package de.blazemcworld.blazinggames.builderwand;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class BuilderLocation {
    protected final Location location;

    public BuilderLocation(Location location) {
        this.location = location.clone();
    }

    public BuilderLocation(BuilderLocation other) {
        this.location = other.location.clone();
    }

    // returns a COPY of the BuilderLocation offset by a block face
    public BuilderLocation add(BlockFace face) {
        return add(face.getDirection());
    }

    // returns a COPY of the BuilderLocation offset by a vector
    public BuilderLocation add(Vector vector) {
        return add(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    // returns a COPY of the BuilderLocation offset by a trio of integers
    public BuilderLocation add(int modX, int modY, int modZ) {
        BuilderLocation result = new BuilderLocation(this);

        result.location.add(modX, modY, modZ);

        return result;
    }

    public boolean canPlaceOnLocation(Player player, Material stack) {
        return location.getBlock().getType() == stack;
    }

    public boolean placeBlock(Player player, Material stack) {
        if(stack.isBlock() && canPlaceAtLocation(player, stack)) {
            location.getBlock().setBlockData(stack.createBlockData());
            return true;
        }
        return false;
    }

    public boolean canPlaceAtLocation(Player player, Material stack) {
        Block block = this.location.getBlock();

        BlockData data = stack.createBlockData();
        if(location.getBlock().canPlace(data)) {
            return block.isEmpty() || block.isReplaceable();
        }

        return false;
    }

    @Override
    public String toString() {
        return location.toString();
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof BuilderLocation loc) {
            return location.equals(loc.location);
        }

        return false;
    }
}
