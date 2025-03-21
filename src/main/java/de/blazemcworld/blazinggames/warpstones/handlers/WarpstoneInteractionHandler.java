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

import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.items.CustomItems;
import de.blazemcworld.blazinggames.utils.InventoryUtils;
import de.blazemcworld.blazinggames.warpstones.WarpstoneStorage;

public class WarpstoneInteractionHandler extends BlazingEventHandler<PlayerInteractEvent> {
    @Override
    public boolean fitCriteria(PlayerInteractEvent event, boolean cancelled) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.BARRIER && event.getAction() == Action.LEFT_CLICK_BLOCK) {
            return WarpstoneStorage.isWarpstone(event.getClickedBlock().getLocation());
        }
        return false;
    }

    @Override
    public void execute(PlayerInteractEvent event) {
        event.setCancelled(true);
        event.getClickedBlock().setType(Material.AIR);
        WarpstoneStorage.breakWarpstone(event.getClickedBlock().getLocation());
        InventoryUtils.collectableDrop(event.getPlayer(), event.getClickedBlock().getLocation(), CustomItems.WARPSTONE.create());
    }
}
