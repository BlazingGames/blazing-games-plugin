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

package de.blazemcworld.blazinggames.computing.eventhandlers;

import de.blazemcworld.blazinggames.computing.BootedComputer;
import de.blazemcworld.blazinggames.computing.ComputerEditor;
import de.blazemcworld.blazinggames.computing.ComputerMetadata;
import de.blazemcworld.blazinggames.computing.ComputerRegistry;
import de.blazemcworld.blazinggames.computing.upgrades.UpgradeItem;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ComputerUpgradeHandler extends BlazingEventHandler<PlayerInteractEvent> {
    @Override
    public boolean fitCriteria(PlayerInteractEvent event, boolean cancelled) {
        ItemStack eventItem = event.getItem();
        Block block = event.getClickedBlock();
        return block != null && block.getType() == Material.BARRIER && event.getAction() == Action.RIGHT_CLICK_BLOCK
                && eventItem != null && CustomItem.getCustomItem(eventItem) instanceof UpgradeItem
                && ComputerRegistry.getComputerByLocationRounded(block.getLocation()) != null;
    }

    @Override
    public void execute(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UpgradeItem upgrade = (UpgradeItem) CustomItem.getCustomItem(event.getItem());
        if (upgrade == null) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        EquipmentSlot hand = event.getHand();
        if (hand == null) return;

        // adding an upgrade
        BootedComputer computer = ComputerRegistry.getComputerByLocationRounded(block.getLocation());
        ComputerMetadata metadata = computer.getMetadata();
        int existingUpgradeCount = metadata.upgrades.size();
        if (existingUpgradeCount >= computer.getType().getType().getUpgradeSlots()) {
            // no more room
            metadata.location.getWorld().playSound(metadata.location, Sound.BLOCK_DISPENSER_FAIL, 1.0f, 1.0f);
        } else {
            // add it
            player.getInventory().getItem(hand).subtract(1);
            ComputerEditor.addUpgrade(computer.getId(), upgrade.type);
            metadata.location.getWorld().playSound(metadata.location, Sound.BLOCK_DISPENSER_DISPENSE, 1.0f, 1.0f);
        }
    }
}
