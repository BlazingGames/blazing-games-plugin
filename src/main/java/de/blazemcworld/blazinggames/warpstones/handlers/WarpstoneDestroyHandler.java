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

package de.blazemcworld.blazinggames.warpstones.handlers;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.warpstones.WarpstoneStorage;

import org.bukkit.Material;

public class WarpstoneDestroyHandler extends BlazingEventHandler<BlockDestroyEvent> {
    @Override
    public boolean fitCriteria(BlockDestroyEvent event, boolean cancelled) {
        if (!Material.BARRIER.equals(event.getBlock().getType())) {
            return false;
        }
        return WarpstoneStorage.isWarpstone(event.getBlock().getLocation());
    }

    @Override
    public void execute(BlockDestroyEvent event) {
        WarpstoneStorage.breakWarpstone(event.getBlock().getLocation());
    }
}
