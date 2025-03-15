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

import org.bukkit.event.block.BlockPlaceEvent;

import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.items.CustomItems;
import de.blazemcworld.blazinggames.utils.TextUtils;
import de.blazemcworld.blazinggames.warpstones.WarpstoneStorage;

public class WarpstonePlaceHandler extends BlazingEventHandler<BlockPlaceEvent> {
    @Override
    public boolean fitCriteria(BlockPlaceEvent event) {
        return CustomItems.WARPSTONE.matchItem(event.getItemInHand());
    }

    @Override
    public void execute(BlockPlaceEvent event) {
        String name = TextUtils.componentToString(event.getItemInHand().getItemMeta().displayName());
        event.getItemInHand().subtract();
        WarpstoneStorage.placeWarpstone(event.getBlockPlaced().getLocation(), event.getPlayer(), name);
    }
}
