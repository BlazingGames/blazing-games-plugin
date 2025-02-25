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

package de.blazemcworld.blazinggames.events.handlers.firework;

import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkInteractHandler extends BlazingEventHandler<PlayerInteractEvent> {
    @Override
    public boolean fitCriteria(PlayerInteractEvent event) {
        ItemStack eventItem = event.getItem();

        if (event.getAction().isRightClick() && eventItem != null) {
            if (eventItem.getType() == Material.FIREWORK_ROCKET) {
                return eventItem.getEnchantmentLevel(Enchantment.INFINITY) > 0;
            }
        }
        return false;
    }

    @Override
    public void execute(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Location interactionPoint = event.getInteractionPoint();

        ItemStack eventItem = event.getItem();
        if (eventItem == null) return;

        event.setUseInteractedBlock(Event.Result.ALLOW);
        event.setUseItemInHand(Event.Result.DENY);

        if (player.isGliding()) {
            player.fireworkBoost(eventItem);
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (interactionPoint != null && eventItem.getItemMeta() instanceof FireworkMeta fire) {
                Firework work = interactionPoint.getWorld().spawn(interactionPoint, Firework.class);
                work.setFireworkMeta(fire);
                work.setShooter(player);
            }
        }
    }
}
