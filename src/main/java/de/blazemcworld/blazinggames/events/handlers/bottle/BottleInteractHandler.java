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

package de.blazemcworld.blazinggames.events.handlers.bottle;

import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class BottleInteractHandler extends BlazingEventHandler<PlayerInteractEvent> {
    @Override
    public boolean fitCriteria(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack eventItem = event.getItem();
        EquipmentSlot hand = event.getHand();
        if (player.isSneaking() && eventItem != null && hand != null && eventItem.getType() == Material.GLASS_BOTTLE) {
            Block target = event.getPlayer().getTargetBlockExact(5, FluidCollisionMode.ALWAYS);
            if (target != null && target.getType() == Material.WATER) return false;
            return player.getLevel() > 0;
        }
        return false;
    }

    @Override
    public void execute(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        EquipmentSlot hand = event.getHand();
        if (hand == null) return;

        event.setCancelled(true);
        player.setLevel(player.getLevel() - 1);
        if (player.getInventory().getItem(hand).getAmount() == 1) {
            player.getInventory().setItem(hand, new ItemStack(Material.EXPERIENCE_BOTTLE, 1));
        } else {
            player.getInventory().getItem(hand).subtract(1);
            HashMap<Integer, ItemStack> remaining = event.getPlayer().getInventory().addItem(new ItemStack(Material.EXPERIENCE_BOTTLE));
            for (ItemStack item : remaining.values()) {
                event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), item);
            }
        }
        event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1);
    }
}
