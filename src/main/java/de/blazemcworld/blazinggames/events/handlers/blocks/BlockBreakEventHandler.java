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

package de.blazemcworld.blazinggames.events.handlers.blocks;

import de.blazemcworld.blazinggames.events.BreakBlockEventListener;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakEventHandler extends BlazingEventHandler<BlockBreakEvent> {
    @Override
    public boolean fitCriteria(BlockBreakEvent event) {
        return true;
    }

    @Override
    public void execute(BlockBreakEvent event) {
        Player player = event.getPlayer();

        event.setDropItems(false);
        event.setExpToDrop(0);

        BreakBlockEventListener.fakeBreakBlock(player, event.getBlock(), false);
    }
}
