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

package de.blazemcworld.blazinggames.events.handlers.containers;

import de.blazemcworld.blazinggames.events.BlazingBlockDropEvent;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.utils.ItemUtils;
import io.papermc.paper.block.TileStateInventoryHolder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class ContainerDropHandler extends BlazingEventHandler<BlazingBlockDropEvent> {
    @Override
    public boolean fitCriteria(BlazingBlockDropEvent event, boolean cancelled) {
        Block block = event.getBlock();

        return block.getState() instanceof TileStateInventoryHolder &&
                ItemUtils.getUncoloredType(block) != Material.SHULKER_BOX;
    }

    @Override
    public void execute(BlazingBlockDropEvent event) {
        ItemStack[] contents = ((TileStateInventoryHolder) event.getBlock().getState()).getInventory().getContents();
        for (ItemStack item : contents) {
            if (item != null && !item.isEmpty()) {
                event.getDrops().add(item);
            }
        }
    }
}
