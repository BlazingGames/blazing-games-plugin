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
package de.blazemcworld.blazinggames.events.handlers.plural;

import org.bukkit.event.player.PlayerJoinEvent;

import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.utils.PlayerConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class PluralFrontReminderJoinHandler extends BlazingEventHandler<PlayerJoinEvent> {
    public static final TextColor reminderColor = TextColor.color(0xFCB279);

    @Override
    public boolean fitCriteria(PlayerJoinEvent event, boolean cancelled) {
        return PlayerConfig.forPlayer(event.getPlayer()).isPlural();
    }

    @Override
    public void execute(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(Component.text("Reminder: set a front with /front to autoproxy your messages", reminderColor));
    }
    
}