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

package de.blazemcworld.blazinggames.events.handlers.grindstones;

import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantment;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentHelper;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentWrapper;
import de.blazemcworld.blazinggames.enchantments.sys.VanillaEnchantmentWrapper;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.items.CustomItem;
import de.blazemcworld.blazinggames.items.predicates.ItemPredicates;
import de.blazemcworld.blazinggames.utils.InventoryUtils;
import de.blazemcworld.blazinggames.utils.Pair;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Predicate;

public class GrindstoneSlotHandler extends BlazingEventHandler<InventoryClickEvent> {
    @Override
    public boolean fitCriteria(InventoryClickEvent event, boolean cancelled) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) return false;

        return inventory instanceof GrindstoneInventory;
    }

    @Override
    public void execute(InventoryClickEvent event) {
        ItemStack cursorItem = event.getCursor();
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) return;

        GrindstoneInventory grindstone = (GrindstoneInventory) event.getInventory();

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

                        if (hotbar.isEmpty()) {
                            inventory.setItem(event.getSlot(), ItemStack.empty());
                            event.getWhoClicked().getInventory().setItemInOffHand(eventItem);
                        } else if (ItemPredicates.grindstoneHandler.matchItem(hotbar) && hotbar.getAmount() <= getGrindstoneDragMax(hotbar)) {
                            inventory.setItem(event.getSlot(), event.getWhoClicked().getInventory().getItemInOffHand());
                            event.getWhoClicked().getInventory().setItemInOffHand(eventItem);
                        }
                    }
                }
            }

        } else {
            ItemStack up = grindstone.getUpperItem();
            ItemStack down = grindstone.getLowerItem();
            ItemStack result = grindstone.getResult();

            if (result != null && !result.isEmpty()) {
                if (up != null && !up.isEmpty()) {
                    if (down != null && !down.isEmpty()) {
                        if (ItemPredicates.grindstoneScrubber.matchItem(up)) {
                            ItemStack book = scrubResultClick(down, up);

                            if (book == null) {
                                return;
                            }

                            event.setCancelled(true);

                            if (giveItemStack(event, result)) {
                                grindstone.setLowerItem(book);
                            }
                        } else if (ItemPredicates.grindstoneScrubber.matchItem(down)) {
                            ItemStack book = scrubResultClick(up, down);

                            if (book == null) {
                                return;
                            }

                            event.setCancelled(true);

                            if (giveItemStack(event, result)) {
                                grindstone.setUpperItem(book);
                            }
                        }
                    }
                }
            }
        }
    }

    private ItemStack scrubResultClick(ItemStack up, ItemStack down) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);

        Predicate<EnchantmentWrapper> filter = null;

        if(!CustomItem.isCustomItem(down)) {
            if(down.getType() == Material.SPONGE) {
                filter = (wrapper) -> wrapper instanceof VanillaEnchantmentWrapper;
            }
            else if(down.getType() == Material.WET_SPONGE) {
                filter = (wrapper) -> wrapper instanceof CustomEnchantment;
            }
        }

        if(filter == null) {
            return null;
        }

        Pair<EnchantmentWrapper, Integer> entry = EnchantmentHelper.getEnchantmentWrapperEntryByIndex(up, down.getAmount(),
                filter.and(EnchantmentWrapper::canBeRemoved));

        if(entry != null) {
            book = entry.left.apply(book, entry.right);
        }

        return book;
    }

    private boolean giveItemStack(InventoryClickEvent event, ItemStack stack) {
        stack = stack.clone();

        if (event.isShiftClick()) {
            if (InventoryUtils.canFitItem(event.getWhoClicked().getInventory(), stack)) {
                InventoryUtils.giveItem(event.getWhoClicked().getInventory(), stack);
                return true;
            }
            return false;
        } else if (event.getClick().isKeyboardClick()) {
            ItemStack shouldBeAir = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());

            if (shouldBeAir == null || shouldBeAir.isEmpty()) {
                event.getWhoClicked().getInventory().setItem(event.getHotbarButton(), stack);
                return true;
            }
            return false;
        } else {
            if (event.getCursor().isEmpty()) {
                event.getWhoClicked().setItemOnCursor(stack);
                return true;
            }
            if (event.getCursor().isSimilar(stack)) {
                int total = event.getCursor().getAmount() + stack.getAmount();
                if (total <= stack.getMaxStackSize()) {
                    stack.setAmount(total);
                    event.getWhoClicked().setItemOnCursor(stack);
                    return true;
                }
            }
            return false;
        }
    }

    private int getGrindstoneDragMax(ItemStack item) {
        if (ItemPredicates.grindstoneScrubber.matchItem(item)) {
            return item.getMaxStackSize();
        }
        return 1;
    }
}
