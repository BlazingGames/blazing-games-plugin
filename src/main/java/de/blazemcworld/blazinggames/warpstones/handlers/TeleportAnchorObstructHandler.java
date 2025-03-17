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

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;

import de.blazemcworld.blazinggames.events.BlazingBlockDisappearEvent;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.warpstones.WarpstoneStorage;

public class TeleportAnchorObstructHandler {
    private static void genericHandle(Block block) {
        Location rel = WarpstoneStorage.findNearbyWarpstone(block);
        if (rel != null) {
            WarpstoneStorage.reloadGuis(rel);
        }
    }

    public static class DestroyHandler extends BlazingEventHandler<BlockDestroyEvent> {
        @Override
        public void execute(BlockDestroyEvent event) {
            genericHandle(event.getBlock());
        }

        public boolean fitCriteria(BlockDestroyEvent event, boolean cancelled) { return true; }
    }

    public static class DisappearHandler extends BlazingEventHandler<BlazingBlockDisappearEvent> {
        @Override
        public void execute(BlazingBlockDisappearEvent event) {
            genericHandle(event.getBlock());
        }

        public boolean fitCriteria(BlazingBlockDisappearEvent event, boolean cancelled) { return true; }
    }

    public static class EntityExplodeHandler extends BlazingEventHandler<EntityExplodeEvent> {
        @Override
        public void execute(EntityExplodeEvent event) {
            for (Block block : event.blockList()) {
                genericHandle(block);
            }
        }

        public boolean fitCriteria(EntityExplodeEvent event, boolean cancelled) { return true; }
    }

    public static class ExplodeHandler extends BlazingEventHandler<BlockExplodeEvent> {
        @Override
        public void execute(BlockExplodeEvent event) {
            for (Block block : event.blockList()) {
                genericHandle(block);
            }
        }

        public boolean fitCriteria(BlockExplodeEvent event, boolean cancelled) { return true; }
    }

    public static class InteractHandler extends BlazingEventHandler<PlayerInteractEvent> {
        @Override
        public void execute(PlayerInteractEvent event) {
            genericHandle(event.getClickedBlock());
        }

        public boolean fitCriteria(PlayerInteractEvent event, boolean cancelled) { return true; }
    }

    public static class PlaceHandler extends BlazingEventHandler<BlockPlaceEvent> {
        @Override
        public void execute(BlockPlaceEvent event) {
            genericHandle(event.getBlock());
        }

        public boolean fitCriteria(BlockPlaceEvent event, boolean cancelled) { return true; }
    }
}