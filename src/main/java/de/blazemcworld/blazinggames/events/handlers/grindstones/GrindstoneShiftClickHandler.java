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

import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.items.predicates.ItemPredicates;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class GrindstoneShiftClickHandler extends BlazingEventHandler<InventoryClickEvent> {
    @Override
    public boolean fitCriteria(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) return false;

        if (inventory instanceof PlayerInventory) {
            if (event.isShiftClick()) {
                if (event.getInventory() instanceof GrindstoneInventory) {
                    ItemStack movedItem = event.getCurrentItem();
                    return movedItem != null && ItemPredicates.grindstoneHandler.matchItem(movedItem);
                }
            }
        }
        return false;
    }

    @Override
    public void execute(InventoryClickEvent event) {
        GrindstoneInventory grindstone = (GrindstoneInventory) event.getInventory();
        ItemStack movedItem = event.getCurrentItem();
        if (movedItem == null) return;

        for (int i = 0; i < 2; i++) {
            ItemStack current = grindstone.getItem(i);

            if (current == null || current.isEmpty()) {
                int available = getGrindstoneDragMax(movedItem);
                if (available > movedItem.getAmount()) {
                    available = movedItem.getAmount();
                }
                grindstone.setItem(i, movedItem.asQuantity(available));
                movedItem.subtract(available);
            } else if (current.isSimilar(movedItem)) {
                int available = getGrindstoneDragMax(movedItem) - current.getAmount();
                if (available > movedItem.getAmount()) {
                    available = movedItem.getAmount();
                }
                current.add(available);
                movedItem.subtract(available);
            }
        }

        event.setCancelled(true);
    }

    private int getGrindstoneDragMax(ItemStack item) {
        if (ItemPredicates.grindstoneScrubber.matchItem(item)) {
            return item.getMaxStackSize();
        }
        return 1;
    }
}
