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

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.discord.DiscordApp;
import de.blazemcworld.blazinggames.discord.DiscordNotification;
import de.blazemcworld.blazinggames.utils.PlayerConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitEventListener implements Listener {
    public static final TextColor color = TextColor.color(0xF99490);

    @EventHandler
    public void join(PlayerQuitEvent event) {
        DiscordApp.send(DiscordNotification.playerLeave(event.getPlayer()));

        if (Bukkit.getOnlinePlayers().size() == 1 && BlazingGames.get().getPackConfig() != null) {
            BlazingGames.get().rebuildPack();
        }

        Component name = PlayerConfig.forPlayer(event.getPlayer())
            .buildNameComponent();
        event.quitMessage(Component.empty().append(name).append(Component.text(" left the game").color(color)));
    }
}
