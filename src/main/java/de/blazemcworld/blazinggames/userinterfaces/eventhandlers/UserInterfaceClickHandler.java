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

package de.blazemcworld.blazinggames.userinterfaces.eventhandlers;

import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.userinterfaces.UserInterface;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class UserInterfaceClickHandler extends BlazingEventHandler<InventoryClickEvent> {
    @Override
    public boolean fitCriteria(InventoryClickEvent event, boolean cancelled) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) return false;

        return inventory.getHolder() instanceof UserInterface;
    }

    @Override
    public void execute(InventoryClickEvent event) {
        UserInterface ui = (UserInterface) event.getInventory().getHolder();
        if (ui == null) return;

        event.setCancelled(!ui.onClick(event.getCurrentItem(), event.getCursor(), event.getSlot(), event.getAction(), event));
    }
}
