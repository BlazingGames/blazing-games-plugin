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

package de.blazemcworld.blazinggames.events.handlers.tome_altars;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.items.CustomItems;
import de.blazemcworld.blazinggames.utils.InventoryUtils;
import de.blazemcworld.blazinggames.utils.TomeAltarStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TomeAltarBreakHandler extends BlazingEventHandler<PlayerInteractEvent> {
    @Override
    public boolean fitCriteria(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK && block != null && block.getType() == Material.BARRIER) {
            return TomeAltarStorage.isTomeAltar(block.getLocation());
        }
        return false;
    }

    @Override
    public void execute(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Block block = event.getClickedBlock();
        if (block == null) return;

        event.setCancelled(true);
        ItemStack tomeItem = TomeAltarStorage.getItem(block.getLocation());
        if (tomeItem == null) tomeItem = new ItemStack(Material.AIR);
        ItemStack finalTomeItem = tomeItem;
        TomeAltarStorage.removeTomeAltar(block.getLocation());
        Location bLoc = block.getLocation().toCenterLocation();
        List<Entity> entities = new ArrayList<>();
        for (Entity e : player.getWorld().getEntities()) {
            Location eLoc = e.getLocation().toCenterLocation();
            if (eLoc.getX() == bLoc.getX() && eLoc.getY() == bLoc.getY() && eLoc.getZ() == bLoc.getZ()) {
                entities.add(e);
            }
        }
        for (Entity e : entities) {
            e.remove();
        }
        player.getWorld().setBlockData(bLoc, Material.AIR.createBlockData());
        Bukkit.getScheduler().runTaskLater(BlazingGames.get(), () -> InventoryUtils.collectableDrop(player, bLoc, CustomItems.TOME_ALTAR.create(), finalTomeItem), 1);
    }
}
