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
package de.blazemcworld.blazinggames.utils.persistentDataTypes;

import de.blazemcworld.blazinggames.utils.TextLocation;
import org.bukkit.Location;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class BlockLocationDataType implements PersistentDataType<String, Location> {
    public static final BlockLocationDataType instance = new BlockLocationDataType();

    private BlockLocationDataType() {

    }

    @Override
    public @NotNull Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public @NotNull Class<Location> getComplexType() {
        return Location.class;
    }

    @Override
    public @NotNull String toPrimitive(@NotNull Location complex, @NotNull PersistentDataAdapterContext context) {
        return TextLocation.serialize(complex.toBlockLocation());
    }

    @Override
    public @NotNull Location fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
        return TextLocation.deserialize(primitive);
    }
}
