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
import de.blazemcworld.blazinggames.blocks.data.EmptyCustomBlockData;
import de.blazemcworld.blazinggames.blocks.wrappers.CustomBlockWrapper;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public abstract class DatalessCustomBlock extends CustomBlock<EmptyCustomBlockData> {
    public void createDisplay(Location owningLocation, Location displayLocation) {
        createDisplay(owningLocation, displayLocation, EmptyCustomBlockData.instance);
    }

    public CustomBlockWrapper<EmptyCustomBlockData> createWrapper() {
        return new CustomBlockWrapper<EmptyCustomBlockData>(this, EmptyCustomBlockData.instance);
    }

    @Override
    protected EmptyCustomBlockData deserializeData(JsonObject data) {
        return EmptyCustomBlockData.instance;
    }

    public final void onRightClick(PlayerInteractEvent event, @NotNull Location location, EmptyCustomBlockData blockData) {
        onRightClick(event, location);
    }

    public void onRightClick(PlayerInteractEvent event, @NotNull Location location) {
    }
}
