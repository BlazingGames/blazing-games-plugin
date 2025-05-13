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

package de.blazemcworld.blazinggames.blocks.eventhandlers;

import de.blazemcworld.blazinggames.blocks.wrappers.BlockWrapper;
import de.blazemcworld.blazinggames.blocks.wrappers.CustomBlockWrapper;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class CustomBlockInteractHandler extends BlazingEventHandler<PlayerInteractEvent> {
    @Override
    public boolean fitCriteria(PlayerInteractEvent event, boolean cancelled) {
        Block block = event.getClickedBlock();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block != null) {
            return CustomBlockWrapper.isCustomBlock(block.getLocation());
        }
        return false;
    }

    @Override
    public void execute(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) return;

        BlockWrapper wrapper = BlockWrapper.fromLocation(block.getLocation());

        if(wrapper instanceof CustomBlockWrapper<?> cbw) {
            cbw.onRightClick(event, block.getLocation());
        }
    }
}
