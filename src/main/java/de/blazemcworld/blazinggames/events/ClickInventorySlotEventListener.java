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
import de.blazemcworld.blazinggames.userinterfaces.UserInterface;
import de.blazemcworld.blazinggames.utils.InventoryUtils;
import de.blazemcworld.blazinggames.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
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
            }

            if(!event.isCancelled() && event.getInventory().getHolder() instanceof UserInterface ui && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                event.setCancelled(!ui.onShiftClick(event.getCurrentItem(), event.getAction(), event));
                return;
            }
        }

        if(inventory instanceof GrindstoneInventory grindstone) {
            ItemStack cursorItem = event.getCursor().clone();
            if(cursorItem.getType() == Material.SPONGE || cursorItem.getType() == Material.WET_SPONGE) {
                if(event.getSlot() != 2) {
                    ItemStack eventItem;

                    if(event.getSlot() == 0) {
                        eventItem = grindstone.getUpperItem();
                    }
                    else if(event.getSlot() == 1) {
                        eventItem = grindstone.getLowerItem();
                    }
                    else {
                        return;
                    }

                    if(eventItem == null) {
                        eventItem = ItemStack.empty();
                    }
                    else {
                        eventItem = eventItem.clone();
                    }

                    BlazingGames.get().debugLog(cursorItem.toString());
                    BlazingGames.get().debugLog(eventItem.toString());

                    if(event.getClick() == ClickType.LEFT) {
                        if(eventItem.isSimilar(cursorItem)) {
                            int total = eventItem.getAmount() + cursorItem.getAmount();
                            if(total > eventItem.getMaxStackSize()) {
                                cursorItem.setAmount(total - eventItem.getMaxStackSize());
                                eventItem.setAmount(eventItem.getMaxStackSize());
                            }
                            else {
                                cursorItem.subtract(cursorItem.getAmount());
                                eventItem.setAmount(total);
                            }
                        }
                        else {
                            ItemStack swap = cursorItem;
                            cursorItem = eventItem;
                            eventItem = swap;
                        }
                    }
                    else if(event.getClick() == ClickType.RIGHT) {
                        if(eventItem.isSimilar(cursorItem)) {
                            if(eventItem.getAmount() < eventItem.getMaxStackSize()) {
                                eventItem.add();
                                cursorItem.subtract();
                            }
                        } else {
                            if (eventItem == null || eventItem.isEmpty()) {
                                eventItem = cursorItem.asOne();
                                cursorItem.subtract();
                            }
                        }
                    }

                    BlazingGames.get().log(cursorItem.toString());
                    BlazingGames.get().log(eventItem.toString());

                    if(event.getSlot() == 0) {
                        grindstone.setUpperItem(eventItem);
                    }
                    else if(event.getSlot() == 1) {
                        grindstone.setLowerItem(eventItem);
                    }
                    event.getWhoClicked().setItemOnCursor(cursorItem);

                    event.setCancelled(true);
                    return;
                }
            }
            if(event.getSlot() == 2) {
                ItemStack up = grindstone.getUpperItem();
                ItemStack down = grindstone.getLowerItem();
                ItemStack result = grindstone.getResult();

                if(result != null && !result.isEmpty()) {
                    if(up != null && !up.isEmpty()) {
                        if(down != null && !down.isEmpty()) {
                            if(up.getType() == Material.SPONGE || up.getType() == Material.WET_SPONGE) {
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
                            else if(down.getType() == Material.SPONGE || down.getType() == Material.WET_SPONGE) {
                                ItemStack book = scrubResultClick(up, down);

                                if(book == null) {
                                    return;
                                }

                                event.setCancelled(true);

                                if(giveItemStack(event, result)) {
                                    grindstone.setUpperItem(book);
                                }

                                return;
                            }
                        }
                    }
                }
            }
        }

        if(inventory instanceof AnvilInventory anvil) {
            if(event.getSlotType() == InventoryType.SlotType.RESULT) {
                ItemStack result = anvil.getResult();
                ItemStack book = anvil.getSecondItem();

                if(book != null && book.getType() == Material.ENCHANTED_BOOK) {
                    if(book.getItemMeta() instanceof EnchantmentStorageMeta esm) {
                        if(esm.hasStoredEnchant(Enchantment.INFINITY)) {
                            if(result != null && result.getType() == Material.FIREWORK_ROCKET
                                    && result.getEnchantmentLevel(Enchantment.INFINITY) > 0) {
                                if(giveItemStack(event, result)) {
                                    anvil.setFirstItem(ItemStack.empty());
                                    anvil.setSecondItem(ItemStack.empty());
                                    anvil.setResult(ItemStack.empty());

                                    HumanEntity p = event.getWhoClicked();

                                    p.getWorld().playSound(p, Sound.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1, 1);
                                }
                            }
                        }
                    }
                }
            }
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

            book = EnchantmentHelper.setCustomEnchantment(book, enchantment.left, enchantment.right);
        }

        return book;
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


