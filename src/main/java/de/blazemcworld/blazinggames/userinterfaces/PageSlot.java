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

import de.blazemcworld.blazinggames.BlazingGames;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class PageSlot implements UserInterfaceSlot {
    public enum Arrow {
        LEFT(BlazingGames.get().key("left_arrow")),
        RIGHT(BlazingGames.get().key("right_arrow")),
        UP(BlazingGames.get().key("up_arrow")),
        DOWN(BlazingGames.get().key("down_arrow"));

        private final NamespacedKey model;

        Arrow(NamespacedKey model) {
            this.model = model;
        }
    }
    public enum Direction {
        FORWARD(1), BACKWARD(-1);

        private final int pageIncrement;

        Direction(int pageIncrement) {
            this.pageIncrement = pageIncrement;
        }
    }

    private final Arrow arrow;
    private final Direction direction;

    public PageSlot(Arrow arrow, Direction direction) {
        this.arrow = arrow;
        this.direction = direction;
    }

    @Override
    public void onUpdate(UserInterface inventory, int slot) {
        NamespacedKey model = StaticUserInterfaceSlot.blankModel;

        if(inventory instanceof PagedUserInterface paged) {
            int newPage = paged.getCurrentPage() + this.direction.pageIncrement;

            if(paged.isWithinBounds(newPage)) {
                model = arrow.model;
            }
        }

        inventory.setItem(slot, StaticUserInterfaceSlot.getGuiItem(model));
    }

    @Override
    public boolean onClick(UserInterface inventory, ItemStack current, ItemStack cursor, int slot, InventoryAction action, boolean isShiftClick, InventoryClickEvent event) {
        if(inventory instanceof PagedUserInterface paged) {
            int newPage = paged.getCurrentPage() + this.direction.pageIncrement;
            paged.changePage(newPage);
        }

        return false;
    }
}
