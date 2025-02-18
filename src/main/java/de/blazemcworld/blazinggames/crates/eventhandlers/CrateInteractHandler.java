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

package de.blazemcworld.blazinggames.crates.eventhandlers;

import de.blazemcworld.blazinggames.crates.CrateData;
import de.blazemcworld.blazinggames.crates.CrateManager;
import de.blazemcworld.blazinggames.crates.DeathCrateKey;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.items.CustomItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BundleMeta;

import java.util.Objects;

public class CrateInteractHandler extends BlazingEventHandler<PlayerInteractEvent> {
    @Override
    public boolean fitCriteria(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        return block != null && block.getType() == Material.END_PORTAL_FRAME && event.getAction() == Action.RIGHT_CLICK_BLOCK;
    }

    @Override
    public void execute(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        ItemStack eventItem = event.getItem();
        EquipmentSlot hand = event.getHand();

        String crateId = CrateManager.getKeyULID(block.getLocation());

        boolean allowOpening = false;

        if (crateId != null) {
            if (CustomItems.DEATH_CRATE_KEY.matchItem(eventItem)) {
                String keyId = DeathCrateKey.getKeyULID(eventItem);

                if (Objects.equals(crateId, keyId)) {
                    allowOpening = true;
                }
            }

            if (CustomItems.SKELETON_KEY.matchItem(eventItem) || CustomItems.TO_GO_BOX.matchItem(eventItem)) {
                assert eventItem != null;

                if (!player.hasCooldown(eventItem)) {
                    allowOpening = true;
                    player.setCooldown(eventItem, 200);
                }
            }
        }

        if (allowOpening) {
            CrateData data = CrateManager.readCrate(crateId);
            Location crateLocation = data.location;

            if (CustomItems.TO_GO_BOX.matchItem(eventItem)) {
                crateLocation.getBlock().breakNaturally();
                ItemStack filledToGoBox = new ItemStack(Material.BUNDLE);
                BundleMeta bundleMeta = (BundleMeta) filledToGoBox.getItemMeta();

                if (data.helmet != null) bundleMeta.addItem(data.helmet);
                if (data.chestplate != null) bundleMeta.addItem(data.chestplate);
                if (data.leggings != null) bundleMeta.addItem(data.leggings);
                if (data.boots != null) bundleMeta.addItem(data.boots);
                if (data.offhand != null) bundleMeta.addItem(data.offhand);
                for (ItemStack itemStack : data.hotbarItems) {
                    if (itemStack == null) continue;
                    bundleMeta.addItem(itemStack);
                }
                for (ItemStack itemStack : data.inventoryItems) {
                    if (itemStack == null) continue;
                    bundleMeta.addItem(itemStack);
                }

                filledToGoBox.setItemMeta(bundleMeta);
                player.getInventory().getItem(hand).subtract(1);
                if (player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(filledToGoBox);
                } else {
                    player.getWorld().dropItemNaturally(player.getLocation(), filledToGoBox);
                }
                player.giveExp(data.exp);
                CrateManager.deleteCrate(crateId);
                return;
            } else {
                PlayerInventory inventory = player.getInventory();

                if (
                        (data.offhand != null && inventory.getItemInOffHand() != null && !inventory.getItemInOffHand().isEmpty()) ||
                                (data.helmet != null && inventory.getHelmet() != null && !inventory.getHelmet().isEmpty()) ||
                                (data.chestplate != null && inventory.getChestplate() != null && !inventory.getChestplate().isEmpty()) ||
                                (data.leggings != null && inventory.getLeggings() != null && !inventory.getLeggings().isEmpty()) ||
                                (data.boots != null && inventory.getBoots() != null && !inventory.getBoots().isEmpty())
                ) {
                    player.sendActionBar(Component.text("Move your offhand/armor into your inventory to open").color(NamedTextColor.RED));
                    return;
                }

                player.getInventory().getItem(hand).subtract(1);
                crateLocation.getBlock().breakNaturally(true);

                if (data.offhand != null) inventory.setItemInOffHand(data.offhand);
                if (data.helmet != null) inventory.setHelmet(data.helmet);
                if (data.chestplate != null) inventory.setChestplate(data.chestplate);
                if (data.leggings != null) inventory.setLeggings(data.leggings);
                if (data.boots != null) inventory.setBoots(data.boots);

                int hotbarIndex = -1;
                for (ItemStack hotbarItem : data.hotbarItems) {
                    hotbarIndex++;
                    if (hotbarItem == null) continue;
                    if (inventory.getItem(hotbarIndex) == null) {
                        inventory.setItem(hotbarIndex, hotbarItem);
                    } else {
                        if (inventory.firstEmpty() == -1) {
                            crateLocation.getWorld().dropItemNaturally(crateLocation, hotbarItem);
                        }
                        inventory.addItem(hotbarItem);
                    }
                }

                int inventoryIndex = 8;
                for (ItemStack inventoryItem : data.inventoryItems) {
                    inventoryIndex++;
                    if (inventoryItem == null) continue;
                    if (inventory.getItem(inventoryIndex) == null) {
                        inventory.setItem(inventoryIndex, inventoryItem);
                    } else {
                        if (inventory.firstEmpty() == -1) {
                            crateLocation.getWorld().dropItemNaturally(crateLocation, inventoryItem);
                        } else {
                            inventory.addItem(inventoryItem);
                        }
                    }
                }

                player.giveExp(data.exp);

                CrateManager.deleteCrate(crateId);
            }
        }
    }
}
