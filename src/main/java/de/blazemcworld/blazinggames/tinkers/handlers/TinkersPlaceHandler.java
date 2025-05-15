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
package de.blazemcworld.blazinggames.tinkers.handlers;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.items.ContextlessItem;
import de.blazemcworld.blazinggames.items.CustomItems;
import de.blazemcworld.blazinggames.tinkers.items.Compound;
import de.blazemcworld.blazinggames.utils.TextUtils;
import de.blazemcworld.blazinggames.utils.TomeAltarStorage;
import de.blazemcworld.blazinggames.warpstones.WarpstoneStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TinkersPlaceHandler extends BlazingEventHandler<BlockPlaceEvent> {
    List<ContextlessItem> items = List.of(
            CustomItems.COMPOUND
    );

    @Override
    public boolean fitCriteria(BlockPlaceEvent event, boolean cancelled) {
        return items.stream().anyMatch(item -> item.matchItem(event.getItemInHand()));
    }

    @Override
    public void execute(BlockPlaceEvent event) {
        event.setCancelled(true);
        ItemStack handItem = event.getItemInHand().clone();
        event.getItemInHand().setAmount(event.getItemInHand().getAmount() - 1);

        Bukkit.getScheduler().runTask(BlazingGames.get(), () -> {
            event.getBlock().getLocation().getBlock().setType(Material.BARRIER);

            Location loc = event.getBlock().getLocation().toCenterLocation();

            ItemDisplay display = (ItemDisplay) event.getBlock().getWorld().spawnEntity(loc, EntityType.ITEM_DISPLAY);

            for (ContextlessItem item : items) {
                if (item.matchItem(handItem)) {
                    display.setItemStack(item.create());
                    break;
                }
            }
        });
    }
}
