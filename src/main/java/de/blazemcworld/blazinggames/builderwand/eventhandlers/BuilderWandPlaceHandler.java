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

package de.blazemcworld.blazinggames.builderwand.eventhandlers;

import de.blazemcworld.blazinggames.builderwand.BuilderWand;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.items.CustomItem;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BuilderWandPlaceHandler extends BlazingEventHandler<PlayerInteractEvent> {
    @Override
    public boolean fitCriteria(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack eventItem = event.getItem();
        EquipmentSlot hand = event.getHand();
        Block block = event.getClickedBlock();
        BlockFace face = event.getBlockFace();
        Location interactionPoint = event.getInteractionPoint();
        Vector clampedInteractionPoint = null;
        if (interactionPoint != null && block != null) {
            clampedInteractionPoint = interactionPoint.clone().subtract(block.getLocation()).toVector();
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block != null && hand != null && eventItem != null) {
            if (CustomItem.getCustomItem(eventItem) instanceof BuilderWand wand) {
                if (!player.hasCooldown(eventItem)) {
                    int blocksUsed = wand.build(player, eventItem, block, face, clampedInteractionPoint);
                    return blocksUsed > 0;
                }
            }
        }
        return false;
    }

    @Override
    public void execute(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack eventItem = event.getItem();
        EquipmentSlot hand = event.getHand();
        if (hand == null) return;

        Block block = event.getClickedBlock();
        BlockFace face = event.getBlockFace();
        Location interactionPoint = event.getInteractionPoint();
        Vector clampedInteractionPoint = null;

        if (interactionPoint != null && block != null) {
            clampedInteractionPoint = interactionPoint.clone().subtract(block.getLocation()).toVector();
        }

        BuilderWand wand = (BuilderWand) CustomItem.getCustomItem(eventItem);
        if (wand == null) return;

        int blocksUsed = wand.build(player, eventItem, block, face, clampedInteractionPoint);
        player.damageItemStack(hand, blocksUsed);
        player.setCooldown(eventItem, 5);
        player.getWorld().playSound(player, Sound.ENTITY_CHICKEN_STEP, 1, 1.25f);
    }
}
