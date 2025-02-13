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
package de.blazemcworld.blazinggames.events;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantment;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentHelper;
import de.blazemcworld.blazinggames.items.CustomItems;
import de.blazemcworld.blazinggames.items.predicates.ItemPredicates;
import de.blazemcworld.blazinggames.userinterfaces.UserInterface;
import de.blazemcworld.blazinggames.utils.InventoryUtils;
import de.blazemcworld.blazinggames.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class ClickInventorySlotEventListener implements Listener {
    @EventHandler
    public void onClickSlot(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();

        if(inventory == null) {
            return;
        }

        if(event.getInventory().getHolder() instanceof UserInterface ui && event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
            event.setCancelled(!ui.onShiftClick(event.getCursor(), event.getAction(), event));
            return;
        }

        if(inventory.getHolder() instanceof UserInterface ui) {
            event.setCancelled(!ui.onClick(event.getCurrentItem(), event.getCursor(), event.getSlot(), event.getAction(), event));
            return;
        }

        if (inventory instanceof PlayerInventory) {
            if (event.getClick() == ClickType.RIGHT && CustomItems.PORTABLE_CRAFTING_TABLE.matchItem(event.getCurrentItem())) {
                event.setCancelled(true);
                Bukkit.getScheduler().runTask(BlazingGames.get(), () -> event.getWhoClicked().openWorkbench(null, true));
                return;
            }

            if(event.isShiftClick()) {
                if(event.getInventory().getHolder() instanceof UserInterface ui) {
                    event.setCancelled(!ui.onShiftClick(event.getCurrentItem(), event.getAction(), event));
                    return;
                }

                if(event.getInventory() instanceof GrindstoneInventory grindstone) {
                    ItemStack movedItem = event.getCurrentItem();

                    if(movedItem != null && ItemPredicates.grindstoneHandler.matchItem(movedItem)) {
                        for(int i = 0; i < 2; i++) {
                            ItemStack current = grindstone.getItem(i);

                            if(current == null || current.isEmpty()) {
                                int available = getGrindstoneDragMax(movedItem);
                                if(available > movedItem.getAmount()) {
                                    available = movedItem.getAmount();
                                }
                                grindstone.setItem(i, movedItem.asQuantity(available));
                                movedItem.subtract(available);
                            }
                            else if(current.isSimilar(movedItem)) {
                                int available = getGrindstoneDragMax(movedItem) - current.getAmount();
                                if(available > movedItem.getAmount()) {
                                    available = movedItem.getAmount();
                                }
                                current.add(available);
                                movedItem.subtract(available);
                            }
                        }

                        event.setCancelled(true);
                    }
                }
                return;
            }
        }

        if(inventory instanceof GrindstoneInventory grindstone) {
            ItemStack cursorItem = event.getCursor();
            if (event.getSlot() != 2) {
                ItemStack eventItem = grindstone.getItem(event.getSlot());
                switch (event.getAction()) {
                    case PLACE_ONE, PLACE_SOME, PLACE_ALL -> {
                        if (ItemPredicates.grindstoneHandler.matchItem(cursorItem)) {
                            int amount = cursorItem.getAmount();

                            if (event.getAction() == InventoryAction.PLACE_ONE) {
                                amount = 1;
                            }

                            if (eventItem == null || eventItem.isEmpty()) {
                                int available = getGrindstoneDragMax(cursorItem);
                                if (available > amount) {
                                    available = amount;
                                }

                                grindstone.setItem(event.getSlot(), cursorItem.asQuantity(available));
                                cursorItem.subtract(available);
                                event.setCancelled(true);
                            } else if (eventItem.isSimilar(cursorItem)) {
                                int available = getGrindstoneDragMax(eventItem) - eventItem.getAmount();
                                if (available > amount) {
                                    available = amount;
                                }

                                eventItem.add(available);
                                cursorItem.subtract(available);
                                event.setCancelled(true);
                            }
                        }
                    }
                    case SWAP_WITH_CURSOR -> {
                        if (ItemPredicates.grindstoneHandler.matchItem(cursorItem)) {
                            if (cursorItem.getAmount() <= getGrindstoneDragMax(cursorItem)) {
                                event.getWhoClicked().setItemOnCursor(eventItem);
                                grindstone.setItem(event.getSlot(), cursorItem);
                            }

                            event.setCancelled(true);
                        }
                    }
                    case HOTBAR_SWAP -> {
                        if (event.getHotbarButton() >= 0) {
                            ItemStack hotbar = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());

                            if (hotbar == null || hotbar.isEmpty()) {
                                inventory.setItem(event.getSlot(), ItemStack.empty());
                                event.getWhoClicked().getInventory().setItem(event.getHotbarButton(), eventItem);
                            } else if (ItemPredicates.grindstoneHandler.matchItem(hotbar) && hotbar.getAmount() <= getGrindstoneDragMax(hotbar)) {
                                inventory.setItem(event.getSlot(), hotbar);
                                event.getWhoClicked().getInventory().setItem(event.getHotbarButton(), eventItem);
                            }
                        } else {
                            ItemStack hotbar = event.getWhoClicked().getInventory().getItemInOffHand();

                            if(hotbar.isEmpty()) {
                                inventory.setItem(event.getSlot(), ItemStack.empty());
                                event.getWhoClicked().getInventory().setItemInOffHand(eventItem);
                            }
                            else if (ItemPredicates.grindstoneHandler.matchItem(hotbar) && hotbar.getAmount() <= getGrindstoneDragMax(hotbar)) {
                                inventory.setItem(event.getSlot(), event.getWhoClicked().getInventory().getItemInOffHand());
                                event.getWhoClicked().getInventory().setItemInOffHand(eventItem);
                            }
                        }
                    }
                }

            }
            else {
                ItemStack up = grindstone.getUpperItem();
                ItemStack down = grindstone.getLowerItem();
                ItemStack result = grindstone.getResult();

                if(result != null && !result.isEmpty()) {
                    if(up != null && !up.isEmpty()) {
                        if(down != null && !down.isEmpty()) {
                            if(ItemPredicates.grindstoneScrubber.matchItem(up)) {
                                ItemStack book = scrubResultClick(down, up);

                                if(book == null) {
                                    return;
                                }

                                event.setCancelled(true);

                                if(giveItemStack(event, result)) {
                                    grindstone.setLowerItem(book);
                                }

                                return;
                            }
                            else if(ItemPredicates.grindstoneScrubber.matchItem(down)) {
                                ItemStack book = scrubResultClick(up, down);

                                if(book == null) {
                                    return;
                                }

                                event.setCancelled(true);

                                if(giveItemStack(event, result)) {
                                    grindstone.setUpperItem(book);
                                }
                            }
                        }
                    }
                }
            }
            return;
        }
    }

    private ItemStack scrubResultClick(ItemStack up, ItemStack down) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);

        if(down.getType() == Material.SPONGE) {
            Pair<Enchantment, Integer> enchantment = EnchantmentHelper.getEnchantmentEntryByIndex(up, down.getAmount());

            if(enchantment == null) {
                return null;
            }

            if(book.getItemMeta() instanceof EnchantmentStorageMeta meta) {
                meta.addStoredEnchant(enchantment.left, enchantment.right, true);
                book.setItemMeta(meta);
            }
            else {
                return null;
            }
        }
        if(down.getType() == Material.WET_SPONGE) {
            Pair<CustomEnchantment, Integer> enchantment
                    = EnchantmentHelper.getCustomEnchantmentEntryByIndex(up, down.getAmount());

            if(enchantment == null) {
                return null;
            }

            book = enchantment.left.apply(book, enchantment.right);
        }

        return book;
    }

    private int getGrindstoneDragMax(ItemStack item) {
        if(ItemPredicates.grindstoneScrubber.matchItem(item)) {
            return item.getMaxStackSize();
        }
        return 1;
    }

    private boolean giveItemStack(InventoryClickEvent event, ItemStack stack) {
        stack = stack.clone();

        if(event.isShiftClick()) {
            if(InventoryUtils.canFitItem(event.getWhoClicked().getInventory(), stack)) {
                InventoryUtils.giveItem(event.getWhoClicked().getInventory(), stack);
                return true;
            }
            return false;
        }
        else if(event.getClick().isKeyboardClick()) {
            ItemStack shouldBeAir = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());

            if(shouldBeAir == null || shouldBeAir.isEmpty()) {
                event.getWhoClicked().getInventory().setItem(event.getHotbarButton(), stack);
                return true;
            }
            return false;
        }
        else {
            if(event.getCursor().isEmpty()) {
                event.getWhoClicked().setItemOnCursor(stack);
                return true;
            }
            if(event.getCursor().isSimilar(stack)) {
                int total = event.getCursor().getAmount() + stack.getAmount();
                if(total <= stack.getMaxStackSize()) {
                    stack.setAmount(total);
                    event.getWhoClicked().setItemOnCursor(stack);
                    return true;
                }
            }
            return false;
        }
    }
}


