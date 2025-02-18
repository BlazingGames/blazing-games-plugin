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

import de.blazemcworld.blazinggames.crates.eventhandlers.MakeCrateHandler;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.events.base.BlazingEventListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathEventListener extends BlazingEventListener<PlayerDeathEvent> {
    public DeathEventListener() {
        this.handlers.add(new MakeCrateHandler());
    }

    @Override
    @EventHandler(ignoreCancelled = true)
    public void executeEvent(PlayerDeathEvent event) {
        for (BlazingEventHandler<PlayerDeathEvent> handler : handlers) {
            if (handler.fitCriteria(event)) handler.execute(event);
        }
    }
}
