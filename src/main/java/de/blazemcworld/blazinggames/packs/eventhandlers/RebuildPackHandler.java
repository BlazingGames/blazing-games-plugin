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

package de.blazemcworld.blazinggames.packs.eventhandlers;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerQuitEvent;

public class RebuildPackHandler extends BlazingEventHandler<PlayerQuitEvent> {
    @Override
    public boolean fitCriteria(PlayerQuitEvent event, boolean cancelled) {
        return Bukkit.getOnlinePlayers().size() == 1 && BlazingGames.get().getPackConfig() != null;
    }

    @Override
    public void execute(PlayerQuitEvent event) {
        BlazingGames.get().rebuildPack();
    }
}
