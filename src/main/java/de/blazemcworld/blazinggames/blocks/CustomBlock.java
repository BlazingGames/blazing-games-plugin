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

package de.blazemcworld.blazinggames.blocks;

import com.google.gson.JsonObject;
import de.blazemcworld.blazinggames.blocks.data.CustomBlockData;
import de.blazemcworld.blazinggames.blocks.wrappers.CustomBlockWrapper;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public abstract class CustomBlock<T extends CustomBlockData> implements Keyed {
    public Material getBaseMaterial() {
        return Material.BARRIER;
    }

    public abstract @NotNull NamespacedKey getKey();

    public abstract void createDisplay(Location owningLocation, Location displayLocation, T blockData);

    public CustomBlockWrapper<T> createWrapper(T blockData) {
        return new CustomBlockWrapper<>(this, blockData);
    }

    protected abstract T deserializeData(JsonObject data);

    public CustomBlockWrapper<T> deserializeAndCreateWrapper(JsonObject data) {
        return createWrapper(deserializeData(data));
    }

    public void onRightClick(PlayerInteractEvent event, @NotNull Location location, T blockData) {

    }
}
