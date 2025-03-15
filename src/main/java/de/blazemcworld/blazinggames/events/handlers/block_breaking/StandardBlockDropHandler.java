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

package de.blazemcworld.blazinggames.events.handlers.block_breaking;

import de.blazemcworld.blazinggames.events.BlazingBlockDropEvent;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class StandardBlockDropHandler extends BlazingEventHandler<BlazingBlockDropEvent> {
    @Override
    public boolean fitCriteria(BlazingBlockDropEvent event, boolean cancelled) {
        return true;
    }

    @Override
    public void execute(BlazingBlockDropEvent event) {
        event.getDrops().addAll(event.getBlock().getDrops(event.getTool()));
        event.getDrops().addExperience(getDroppedExp(event.getBlock(), event.getTool()));
    }

    private static int getDroppedExp(Block block, ItemStack tool) {
        if (!(block instanceof CraftBlock craftBlock)) return 0;

        net.minecraft.world.level.block.state.BlockState nmsBlockState = craftBlock.getNMS();
        BlockPos pos = craftBlock.getPosition();
        ServerLevel level = craftBlock.getCraftWorld().getHandle();

        return nmsBlockState.getBlock().getExpDrop(nmsBlockState, level, pos, CraftItemStack.asNMSCopy(tool), true);
    }
}
