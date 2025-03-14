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

package de.blazemcworld.blazinggames.discord.eventhandlers;

import de.blazemcworld.blazinggames.discord.DiscordApp;
import de.blazemcworld.blazinggames.discord.DiscordNotification;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.utils.TextUtils;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DiscordDeathEventHandler extends BlazingEventHandler<PlayerDeathEvent> {
    @Override
    public boolean fitCriteria(PlayerDeathEvent event, boolean cancelled) {
        return DiscordApp.isEnabled();
    }

    @Override
    public void execute(PlayerDeathEvent event) {
        DiscordApp.send(DiscordNotification.playerDeath(event.getPlayer(), TextUtils.componentToString(event.deathMessage())));
    }
}
