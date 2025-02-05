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
package de.blazemcworld.blazinggames.computing.upgrades;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import de.blazemcworld.blazinggames.computing.ComputerEditor;
import de.blazemcworld.blazinggames.userinterfaces.UserInterface;
import de.blazemcworld.blazinggames.userinterfaces.UserInterfaceSlot;

public class UpgradeListSlot implements UserInterfaceSlot {
    @Override
    public void onUpdate(UserInterface inventory, int slot) {
        if (!(inventory instanceof UpgradeListInterface parent)) {
            inventory.setItem(slot, ItemStack.empty());
            return;
        }

        if (parent.slots.size() > (slot + 1)) {
            inventory.setItem(slot, new UpgradeItem(parent.slots.get(slot)).create());
        }
    }

    @Override
    public boolean onClick(UserInterface inventory, ItemStack current, ItemStack cursor, int slot,
        InventoryAction action, boolean isShiftClick, InventoryClickEvent event
    ) {
        if (!(inventory instanceof UpgradeListInterface parent)) {
            return false;
        }

        if (parent.slots.size() > (slot + 1)) {
            UpgradeType type = parent.slots.get(slot);
            if (type != null) {
                ComputerEditor.removeUpgrade(parent.computerId, type);
                event.getWhoClicked().getInventory().addItem(new UpgradeItem(type).create());
                parent.reload();
            }
        }

        return false;
    }
}