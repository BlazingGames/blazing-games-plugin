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
import de.blazemcworld.blazinggames.computing.ComputerRegistry;
import de.blazemcworld.blazinggames.computing.types.ComputerTypes;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.utils.TextLocation;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class ComputerPlaceHandler extends BlazingEventHandler<BlockPlaceEvent> {
    @Override
    public boolean fitCriteria(BlockPlaceEvent event, boolean cancelled) {
        ItemStack handItem = event.getItemInHand();
        if (handItem.hasItemMeta()) {
            PersistentDataContainer container = handItem.getItemMeta().getPersistentDataContainer();
            String computerTypeString = container.getOrDefault(ComputerRegistry.NAMESPACEDKEY_COMPUTER_TYPE, PersistentDataType.STRING, "");
            return !computerTypeString.isEmpty();
        }
        return false;
    }

    @Override
    public void execute(BlockPlaceEvent event) {
        ItemStack handItem = event.getItemInHand();
        PersistentDataContainer container = handItem.getItemMeta().getPersistentDataContainer();
        String computerTypeString = container.getOrDefault(ComputerRegistry.NAMESPACEDKEY_COMPUTER_TYPE, PersistentDataType.STRING, "");
        event.setCancelled(true);

        ComputerTypes computerTypes;
        try {
            computerTypes = ComputerTypes.valueOf(computerTypeString);
        } catch (IllegalArgumentException ignored) {
            event.getPlayer().sendMessage("This computer type doesn't exist?");
            return;
        }

        String computerId = container.getOrDefault(ComputerRegistry.NAMESPACEDKEY_COMPUTER_ID, PersistentDataType.STRING, "");
        Location placeLocation = event.getBlockPlaced().getLocation();
        UUID uuid = event.getPlayer().getUniqueId();
        if (event.getItemInHand().getAmount() > 1) {
            ItemStack newStack = event.getItemInHand();
            newStack.setAmount(event.getItemInHand().getAmount() - 1);
            event.getPlayer().getInventory().setItem(event.getHand(), newStack);
        } else {
            event.getPlayer().getInventory().setItem(event.getHand(), new ItemStack(Material.AIR));
        }
        if (computerId.isEmpty()) {
            ComputerRegistry.placeNewComputer(
                    placeLocation,
                    computerTypes,
                    uuid,
                    computer -> {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player != null) {
                            if (computer == null) {
                                player.sendActionBar(Component.text("Failed to create a computer, please tell a developer to check the console."));
                            } else {
                                player.sendActionBar(
                                        Component.text("Your new computer (%s) has been created with id %s!".formatted(computer.getMetadata().name, computer.getId()))
                                );
                            }
                        }
                    }
            );
        } else {
            ComputerRegistry.placeComputer(
                    computerId,
                    placeLocation,
                    (result, computer) -> {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player != null) {
                            if (result && computer != null) {
                                player.sendActionBar(
                                        Component.text("The computer (%s) has been placed (id: %s)!".formatted(computer.getMetadata().name, computer.getId()))
                                );
                            }

                            if (result && computer == null) {
                                player.sendActionBar(Component.text("fear me."));
                            }

                            if (!result && computer != null) {
                                if (ComputerRegistry.getComputerById(computerId) != null) {
                                    BootedComputer dupe = ComputerRegistry.getComputerById(computerId);
                                    player.sendMessage(
                                            "Duplicate computer has an ID of %s and is at [ %s ]."
                                                    .formatted(dupe.getId(), TextLocation.serialize(dupe.getMetadata().location))
                                    );
                                    player.sendActionBar(Component.text("A computer with this ID is already present in the world. See chat for details."));
                                } else {
                                    player.sendActionBar(Component.text("A computer with this location is already present here."));
                                }
                            }

                            if (!result && computer == null) {
                                player.sendActionBar(Component.text("Failed to place that computer, please tell a developer to check the console."));
                            }
                        }
                    }
            );
        }
    }
}
