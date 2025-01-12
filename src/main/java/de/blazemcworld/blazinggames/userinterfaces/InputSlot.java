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
package de.blazemcworld.blazinggames.userinterfaces;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InputSlot extends UsableInterfaceSlot {
    @Override
    public final boolean onClick(UserInterface inventory, ItemStack current, ItemStack cursor, int slot, InventoryAction action, boolean isShiftClick, InventoryClickEvent event) {
        return switch(action) {
            case PICKUP_ALL, PICKUP_SOME, PICKUP_HALF, PICKUP_ONE, COLLECT_TO_CURSOR -> {
                int pickupAmount = current.getAmount();

                switch(action) {
                    case PICKUP_HALF -> pickupAmount /= 2;
                    case PICKUP_ONE -> pickupAmount = 1;
                }

                if(cursor.isSimilar(current)) {
                    int available = cursor.getMaxStackSize() - cursor.getAmount();

                    if(available > pickupAmount) {
                        available = pickupAmount;
                    }

                    cursor.add(available);
                    current.subtract(available);
                }
                else {
                    if(!isShiftClick) {
                        event.getWhoClicked().setItemOnCursor(current.clone());
                        current.subtract(current.getAmount());
                    }
                }

                yield false;
            }
            case PLACE_ALL, PLACE_SOME, PLACE_ONE -> {
                int placeAmount = cursor.getAmount();

                if (action == InventoryAction.PLACE_ONE) {
                    placeAmount = 1;
                }

                if(current.isEmpty() && filterItem(cursor)) {
                    int available = getDragMax(cursor);
                    if(available > placeAmount) {
                        available = placeAmount;
                    }

                    inventory.setItem(slot, cursor.asQuantity(available));
                    cursor.subtract(available);
                }
                else if(cursor.isSimilar(current)) {
                    int available = getDragMax(cursor) - current.getAmount();
                    if(available > placeAmount) {
                        available = placeAmount;
                    }

                    current.add(available);
                    cursor.subtract(available);
                }

                yield false;
            }
            case SWAP_WITH_CURSOR -> {
                if(cursor.getAmount() <= getDragMax(cursor) && filterItem(cursor)) {
                    event.getWhoClicked().setItemOnCursor(current);
                    inventory.setItem(slot, cursor);
                }

                yield false;
            }
            case DROP_ALL_SLOT, DROP_ONE_SLOT, CLONE_STACK -> true;
            case MOVE_TO_OTHER_INVENTORY -> {
                if(isShiftClick) {
                    if(current.isEmpty() && filterItem(cursor)) {
                        int available = getDragMax(cursor);
                        if(available > cursor.getAmount()) {
                            available = cursor.getAmount();
                        }

                        inventory.setItem(slot, cursor.asQuantity(available));
                        cursor.subtract(available);
                    }
                    else if(current.isSimilar(cursor)) {
                        int available = getDragMax(cursor) - current.getAmount();
                        if(available > cursor.getAmount()) {
                            available = cursor.getAmount();
                        }

                        current.add(available);
                        cursor.subtract(available);
                    }
                    yield false;
                }
                yield true;
            }
            case HOTBAR_SWAP, HOTBAR_MOVE_AND_READD -> {
                if(event.getHotbarButton() >= 0) {
                    ItemStack hotbar = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());

                    if(hotbar == null) {
                        inventory.setItem(slot, ItemStack.empty());
                        event.getWhoClicked().getInventory().setItem(event.getHotbarButton(), current);
                    }
                    else if(hotbar.getAmount() <= getDragMax(hotbar) && filterItem(hotbar)) {
                        inventory.setItem(slot, hotbar);
                        event.getWhoClicked().getInventory().setItem(event.getHotbarButton(), current);
                    }
                }
                else {
                    ItemStack hotbar = event.getWhoClicked().getInventory().getItemInOffHand();

                    if(hotbar.getAmount() <= getDragMax(hotbar) && filterItem(hotbar)) {
                        inventory.setItem(slot, event.getWhoClicked().getInventory().getItemInOffHand());
                        event.getWhoClicked().getInventory().setItemInOffHand(current);
                    }
                }

                yield false;
            }
            default -> false;
        };
    }

    @Override
    public int getDragMax(ItemStack value) {
        if(value.isEmpty()) return 64;
        return value.getMaxStackSize();
    }

    public boolean filterItem(ItemStack stack) {
        return true;
    }
}
