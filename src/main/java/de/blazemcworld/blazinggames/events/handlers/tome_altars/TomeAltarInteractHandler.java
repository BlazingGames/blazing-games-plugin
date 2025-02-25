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
import de.blazemcworld.blazinggames.utils.TomeAltarStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TomeAltarInteractHandler extends BlazingEventHandler<PlayerInteractEvent> {
    @Override
    public boolean fitCriteria(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block != null && block.getType() == Material.BARRIER) {
            if (!BlazingGames.get().interactCooldown.onCooldown(player)) {
                return TomeAltarStorage.isTomeAltar(block.getLocation());
            }
        }
        return false;
    }

    @Override
    public void execute(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Block block = event.getClickedBlock();
        if (block == null) return;

        BlazingGames.get().interactCooldown.setCooldown(player, 4);

        event.setCancelled(true);

        ItemStack giveItemNullable = TomeAltarStorage.getItem(block.getLocation());
        final ItemStack giveItem = giveItemNullable == null ? new ItemStack(Material.AIR) : giveItemNullable;

        ItemStack item = event.getItem();
        if (item == null) item = new ItemStack(Material.AIR);

        if (!giveItem.isSimilar(item)) {
            ItemStack setItem = item.clone();
            item.setAmount(item.getAmount() - 1);

            setItem.setAmount(1);
            TomeAltarStorage.setItem(block.getLocation(), setItem);

            Location bLoc = block.getLocation().toCenterLocation();
            ItemDisplay display = null;
            for (Entity e : player.getWorld().getEntities()) {
                Location eLoc = e.getLocation().toCenterLocation();
                if (eLoc.getX() == bLoc.getX() && eLoc.getY() == bLoc.getY() && eLoc.getZ() == bLoc.getZ() && e.getType() == EntityType.ITEM_DISPLAY) {
                    PersistentDataContainer container = e.getPersistentDataContainer();
                    if (container.has(BlazingGames.get().key("spin"), PersistentDataType.BOOLEAN)) {
                        display = (ItemDisplay) e;
                        break;
                    }
                }
            }
            if (display == null) {
                Location loc = block.getLocation().toCenterLocation();
                loc.setY(loc.getY() + 0.375);
                loc.setX(loc.getX());
                loc.setZ(loc.getZ());

                display = (ItemDisplay) block.getWorld().spawnEntity(loc, EntityType.ITEM_DISPLAY);
                PersistentDataContainer container = display.getPersistentDataContainer();
                container.set(BlazingGames.get().key("spin"), PersistentDataType.BOOLEAN, true);
                display.setItemStack(setItem);
                Transformation transformation = new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(0.25f, 0.25f, 0.25f), new Quaternionf());
                display.setTransformation(transformation);
            } else {
                display.setItemStack(setItem);
            }

            Bukkit.getScheduler().runTaskLater(BlazingGames.get(), () -> {
                if (!player.getInventory().addItem(giveItem).values().isEmpty()) {
                    player.getWorld().dropItem(player.getLocation(), giveItem);
                }
            }, 1);
        }
    }
}
