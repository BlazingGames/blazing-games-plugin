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

package de.blazemcworld.blazinggames.items.eventhandlers;

import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class CustomItemPlaceHandler extends BlazingEventHandler<BlockPlaceEvent> {
    @Override
    public boolean fitCriteria(BlockPlaceEvent event, boolean cancelled) {
        ItemStack item = event.getItemInHand();
        return CustomItem.isCustomItem(item) && item.getType() == Material.STRUCTURE_BLOCK;
    }

    @Override
    public void execute(BlockPlaceEvent event) {
        event.setCancelled(true);
    }
}
