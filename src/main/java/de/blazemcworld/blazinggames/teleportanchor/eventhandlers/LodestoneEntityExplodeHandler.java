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

package de.blazemcworld.blazinggames.teleportanchor.eventhandlers;

import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.teleportanchor.LodestoneStorage;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.List;

public class LodestoneEntityExplodeHandler extends BlazingEventHandler<EntityExplodeEvent> {
    @Override
    public boolean fitCriteria(EntityExplodeEvent event) {
        List<Block> blocks = event.blockList();
        blocks = blocks.stream().filter(block -> block.getType() == Material.LODESTONE).toList();
        return !blocks.isEmpty();
    }

    @Override
    public void execute(EntityExplodeEvent event) {
        List<Block> blocks = event.blockList();
        blocks = blocks.stream().filter(block -> block.getType() == Material.LODESTONE).toList();
        for (Block block : blocks) {
            LodestoneStorage.destroyLodestone(block.getLocation());
            LodestoneStorage.refreshAllInventories();
        }
    }
}
