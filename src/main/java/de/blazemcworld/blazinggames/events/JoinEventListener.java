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
import de.blazemcworld.blazinggames.computing.api.BlazingAPI;
import de.blazemcworld.blazinggames.discord.DiscordApp;
import de.blazemcworld.blazinggames.discord.DiscordNotification;
import de.blazemcworld.blazinggames.packs.ResourcePackManager.PackConfig;
import de.blazemcworld.blazinggames.items.recipes.CustomRecipes;
import net.kyori.adventure.text.Component;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEventListener implements Listener {
    @EventHandler
    public void join(PlayerJoinEvent event) {
        event.getPlayer().discoverRecipes(CustomRecipes.getAllRecipes().keySet());
        DiscordApp.send(DiscordNotification.playerJoin(event.getPlayer()));

        if (BlazingGames.get().getPackConfig() != null) {
            PackConfig config = BlazingGames.get().getPackConfig();
            byte[] sha1 = BlazingGames.get().getPackSha1();
            event.getPlayer().setResourcePack(
                config.uuid(),
                BlazingAPI.getConfig().apiConfig().findAt() + "/pack.zip",
                sha1,
                Component.text("Custom features require this resource pack."),
                true
            );
        }
    }
}
