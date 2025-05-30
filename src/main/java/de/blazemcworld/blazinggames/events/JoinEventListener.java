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

import de.blazemcworld.blazinggames.discord.eventhandlers.DiscordJoinHandler;
import de.blazemcworld.blazinggames.events.base.BlazingEventListener;
import de.blazemcworld.blazinggames.events.handlers.player.PlayerJoinHandler;
import de.blazemcworld.blazinggames.events.handlers.plural.PluralFrontReminderJoinHandler;
import de.blazemcworld.blazinggames.packs.eventhandlers.SendPackHandler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class JoinEventListener extends BlazingEventListener<PlayerJoinEvent> {
    public JoinEventListener() {
        this.handlers.addAll(List.of(
                new PlayerJoinHandler(),
                new DiscordJoinHandler(),
                new SendPackHandler(),
                new PluralFrontReminderJoinHandler()
        ));
    }

    @EventHandler
    public void event(PlayerJoinEvent event) {
        executeEvent(event);
    }
}
