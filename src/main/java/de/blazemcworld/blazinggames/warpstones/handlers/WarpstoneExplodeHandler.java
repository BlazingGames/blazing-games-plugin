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

import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.warpstones.WarpstoneStorage;

import org.bukkit.Material;
import org.bukkit.event.block.BlockExplodeEvent;

public class WarpstoneExplodeHandler extends BlazingEventHandler<BlockExplodeEvent> {
    @Override
    public boolean fitCriteria(BlockExplodeEvent event) {
        return true;
    }

    @Override
    public void execute(BlockExplodeEvent event) {
        event.blockList().stream().filter(block -> {
            if (!block.getType().equals(Material.BARRIER)) {
                return false;
            }
            return WarpstoneStorage.isWarpstone(block.getLocation());
        }).forEach(block -> {
            WarpstoneStorage.breakWarpstone(block.getLocation());
        });
    }
}
