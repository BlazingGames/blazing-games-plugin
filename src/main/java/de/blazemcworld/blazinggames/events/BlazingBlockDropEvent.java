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

import de.blazemcworld.blazinggames.utils.Drops;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BlazingBlockDropEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Drops drops;
    private final ItemStack tool;
    private final Block block;

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public BlazingBlockDropEvent(ItemStack tool, Block block, Drops drops) {
        this.tool = tool;
        this.block = block;
        this.drops = drops;
    }

    public Drops getDrops() {
        return drops;
    }

    public ItemStack getTool() {
        return tool;
    }

    public Block getBlock() {
        return block;
    }
}
