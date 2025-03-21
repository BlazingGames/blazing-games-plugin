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

import de.blazemcworld.blazinggames.computing.eventhandlers.ComputerPlaceHandler;
import de.blazemcworld.blazinggames.events.base.BlazingEventListener;
import de.blazemcworld.blazinggames.events.handlers.slabs.CustomSlabPlaceHandler;
import de.blazemcworld.blazinggames.events.handlers.spawners.SpawnerPlaceHandler;
import de.blazemcworld.blazinggames.events.handlers.tome_altars.TomeAltarPlaceHandler;
import de.blazemcworld.blazinggames.items.eventhandlers.CustomItemPlaceHandler;
import de.blazemcworld.blazinggames.warpstones.handlers.WarpstonePlaceHandler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.List;

public class BlockPlaceEventListener extends BlazingEventListener<BlockPlaceEvent> {
    public BlockPlaceEventListener() {
        this.handlers.addAll(List.of(
                new CustomItemPlaceHandler(),
                new SpawnerPlaceHandler(),
                new TomeAltarPlaceHandler(),
                new CustomSlabPlaceHandler(),
                new ComputerPlaceHandler(),
                new WarpstonePlaceHandler()
        ));
    }

    @EventHandler
    public void event(BlockPlaceEvent event) {
        executeEvent(event);
    }
}
