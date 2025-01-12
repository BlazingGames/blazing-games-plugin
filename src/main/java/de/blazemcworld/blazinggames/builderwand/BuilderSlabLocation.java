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
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.Player;

public class BuilderSlabLocation extends BuilderLocation {
    public enum BuilderSlabHalf {
        TOP,
        BOTTOM
    }

    BuilderSlabHalf half;

    public BuilderSlabLocation(Location location, BuilderSlabHalf half) {
        super(location);
        this.half = half;
    }

    public BuilderSlabLocation(BuilderSlabLocation other) {
        super(other);
        this.half = other.half;
    }

    // returns a COPY of the BuilderLocation offset by a trio of integers
    public BuilderSlabLocation add(int modX, int modY, int modZ) {
        BuilderSlabLocation result = new BuilderSlabLocation(this);

        int yMov = 0;

        if(modY != 0) {
            boolean inverted = modY < 0;
            if(inverted) modY = -modY;

            for(int i = 0; i < modY; i++) {
                if(result.half == BuilderSlabHalf.BOTTOM) {
                    if(inverted) {
                        yMov--;
                    }
                    result.half = BuilderSlabHalf.TOP;
                }
                else {
                    if(!inverted) {
                        yMov++;
                    }
                    result.half = BuilderSlabHalf.BOTTOM;
                }
            }
        }

        result.location.add(modX, yMov, modZ);

        return result;
    }

    public boolean canPlaceOnLocation(Player player, Material stack) {
        Block block = this.location.getBlock();

        if(block.getType() != stack) {
            return false;
        }

        if(block.getBlockData() instanceof Slab slab) {
            if(slab.getPlacementMaterial() != stack) {
                return false;
            }

            if(half == BuilderSlabHalf.TOP) {
                return slab.getType() != Slab.Type.BOTTOM;
            }
            else {
                return slab.getType() != Slab.Type.TOP;
            }
        }

        return true;
    }

    public boolean canPlaceAtLocation(Player player, Material stack) {
        Block block = this.location.getBlock();

        if(block.getBlockData() instanceof Slab slab) {
            if(slab.getPlacementMaterial() != stack) {
                return false;
            }

            if(half == BuilderSlabHalf.TOP) {
                return slab.getType() == Slab.Type.BOTTOM;
            }
            else {
                return slab.getType() == Slab.Type.TOP;
            }
        }

        return block.isEmpty() || block.isReplaceable();
    }

    public boolean placeBlock(Player player, Material stack) {
        if(stack.isBlock() && canPlaceAtLocation(player, stack)) {
            if(location.getBlock().getBlockData() instanceof Slab slab) {
                if(location.getBlock().getType() != stack) {
                    return false;
                }

                if(slab.getType() == Slab.Type.TOP) {
                    if(half == BuilderSlabHalf.BOTTOM) {
                        slab.setType(Slab.Type.DOUBLE);
                        location.getBlock().setBlockData(slab);
                        return true;
                    }
                }
                else if(slab.getType() == Slab.Type.BOTTOM) {
                    if(half == BuilderSlabHalf.TOP) {
                        slab.setType(Slab.Type.DOUBLE);
                        location.getBlock().setBlockData(slab);
                        return true;
                    }
                }
                return false;
            }

            BlockData data = stack.createBlockData();

            if(data instanceof Slab slab) {
                if(half == BuilderSlabHalf.TOP) {
                    slab.setType(Slab.Type.TOP);
                }
                else if(half == BuilderSlabHalf.BOTTOM) {
                    slab.setType(Slab.Type.BOTTOM);
                }
            }

            location.getBlock().setBlockData(data);

            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + ":" + half.name();
    }

    @Override
    public boolean equals(Object other) {
        if(!super.equals(other)) {
            return false;
        }

        if(other instanceof BuilderSlabLocation loc) {
            return half == loc.half;
        }

        return false;
    }
}
