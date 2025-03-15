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

package de.blazemcworld.blazinggames.events.handlers.blueprint;

import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.items.CustomItems;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class BlueprintEnchantingTableInteractHandler extends BlazingEventHandler<PlayerInteractEvent> {
    @Override
    public boolean fitCriteria(PlayerInteractEvent event, boolean cancelled) {
        Block block = event.getClickedBlock();
        ItemStack eventItem = event.getItem();
        EquipmentSlot hand = event.getHand();
        return block != null && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.useInteractedBlock() != Event.Result.DENY && block.getType() == Material.ENCHANTING_TABLE && CustomItems.BLUEPRINT.matchItem(eventItem) && hand == EquipmentSlot.HAND;
    }

    @Override
    public void execute(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) return;

        ItemStack eventItem = event.getItem();
        if (eventItem == null) return;

        Player player = event.getPlayer();
        event.setCancelled(true);
        if (!player.hasCooldown(eventItem)) {
            CustomItems.BLUEPRINT.outputMultiBlockProgress(player, block.getLocation());
            player.setCooldown(eventItem, 40);
        }
    }
}
