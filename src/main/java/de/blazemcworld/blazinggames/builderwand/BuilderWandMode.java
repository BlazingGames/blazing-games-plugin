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

import com.google.common.collect.ImmutableList;
import de.blazemcworld.blazinggames.utils.EnumDataType;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public enum BuilderWandMode {
    NO_LOCK("No Lock", (m) -> {
        m.allowedFaces.add(BlockFace.NORTH);
        m.allowedFaces.add(BlockFace.SOUTH);
        m.allowedFaces.add(BlockFace.EAST);
        m.allowedFaces.add(BlockFace.WEST);
        m.allowedFaces.add(BlockFace.UP);
        m.allowedFaces.add(BlockFace.DOWN);

        m.buildDirections.add(BlockFace.NORTH);
        m.buildDirections.add(BlockFace.SOUTH);
        m.buildDirections.add(BlockFace.EAST);
        m.buildDirections.add(BlockFace.WEST);
        m.buildDirections.add(BlockFace.UP);
        m.buildDirections.add(BlockFace.DOWN);
    }),
    HORIZONTAL("Horizontal", (m) -> {
        m.allowedFaces.add(BlockFace.NORTH);
        m.allowedFaces.add(BlockFace.SOUTH);
        m.allowedFaces.add(BlockFace.EAST);
        m.allowedFaces.add(BlockFace.WEST);

        m.buildDirections.add(BlockFace.NORTH);
        m.buildDirections.add(BlockFace.SOUTH);
        m.buildDirections.add(BlockFace.EAST);
        m.buildDirections.add(BlockFace.WEST);
    }),
    VERTICAL("Vertical", (m) -> {
        m.allowedFaces.add(BlockFace.NORTH);
        m.allowedFaces.add(BlockFace.SOUTH);
        m.allowedFaces.add(BlockFace.EAST);
        m.allowedFaces.add(BlockFace.WEST);

        m.buildDirections.add(BlockFace.UP);
        m.buildDirections.add(BlockFace.DOWN);
    }),
    NORTH_SOUTH("North-South", (m) -> {
        m.allowedFaces.add(BlockFace.EAST);
        m.allowedFaces.add(BlockFace.WEST);
        m.allowedFaces.add(BlockFace.UP);
        m.allowedFaces.add(BlockFace.DOWN);

        m.buildDirections.add(BlockFace.NORTH);
        m.buildDirections.add(BlockFace.SOUTH);
    }),
    NORTH_SOUTH_VERTICAL("North-South (+ Vertical)", (m) -> {
        m.allowedFaces.add(BlockFace.EAST);
        m.allowedFaces.add(BlockFace.WEST);
        m.allowedFaces.add(BlockFace.UP);
        m.allowedFaces.add(BlockFace.DOWN);

        m.buildDirections.add(BlockFace.NORTH);
        m.buildDirections.add(BlockFace.SOUTH);
        m.buildDirections.add(BlockFace.UP);
        m.buildDirections.add(BlockFace.DOWN);
    }),
    EAST_WEST("East-West", (m) -> {
        m.allowedFaces.add(BlockFace.NORTH);
        m.allowedFaces.add(BlockFace.SOUTH);
        m.allowedFaces.add(BlockFace.UP);
        m.allowedFaces.add(BlockFace.DOWN);

        m.buildDirections.add(BlockFace.EAST);
        m.buildDirections.add(BlockFace.WEST);
    }),
    EAST_WEST_VERTICAL("East-West (+ Vertical)", (m) -> {
        m.allowedFaces.add(BlockFace.NORTH);
        m.allowedFaces.add(BlockFace.SOUTH);
        m.allowedFaces.add(BlockFace.UP);
        m.allowedFaces.add(BlockFace.DOWN);

        m.buildDirections.add(BlockFace.EAST);
        m.buildDirections.add(BlockFace.WEST);
        m.buildDirections.add(BlockFace.UP);
        m.buildDirections.add(BlockFace.DOWN);
    });

    public static final EnumDataType<BuilderWandMode> persistentType = new EnumDataType<>(BuilderWandMode.class);

    private final String modeText;
    private final List<BlockFace> allowedFaces = new ArrayList<>();
    private final List<BlockFace> buildDirections = new ArrayList<>();

    BuilderWandMode(String modeText, Consumer<BuilderWandMode> builder) {
        this.modeText = modeText;
        builder.accept(this);
    }

    public boolean canBuildOnFace(BlockFace face) {
        return allowedFaces.contains(face);
    }

    public List<BlockFace> getBuildDirections() {
        ImmutableList.Builder<BlockFace> faces = new ImmutableList.Builder<>();
        faces.addAll(buildDirections);
        return faces.build();
    }

    public String getModeText() {
        return modeText;
    }

    public BuilderWandMode getNextMode() {
        return switch(this) {
            case NO_LOCK -> HORIZONTAL;
            case HORIZONTAL -> VERTICAL;
            case VERTICAL -> NORTH_SOUTH;
            case NORTH_SOUTH -> NORTH_SOUTH_VERTICAL;
            case NORTH_SOUTH_VERTICAL -> EAST_WEST;
            case EAST_WEST -> EAST_WEST_VERTICAL;
            case EAST_WEST_VERTICAL -> NO_LOCK;
        };
    }
}