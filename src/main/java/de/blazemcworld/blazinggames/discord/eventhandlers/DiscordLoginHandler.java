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

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.discord.DiscordApp;
import de.blazemcworld.blazinggames.discord.DiscordUser;
import de.blazemcworld.blazinggames.discord.WhitelistManagement;
import de.blazemcworld.blazinggames.discord.WhitelistedPlayer;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.UUID;

public class DiscordLoginHandler extends BlazingEventHandler<PlayerLoginEvent> {
    @Override
    public boolean fitCriteria(PlayerLoginEvent event, boolean cancelled) {
        return DiscordApp.isWhitelistManaged() && (event.getResult() == PlayerLoginEvent.Result.KICK_WHITELIST || event.getResult() == PlayerLoginEvent.Result.ALLOWED);
    }

    @Override
    public void execute(PlayerLoginEvent event) {
        WhitelistManagement whitelist = DiscordApp.getWhitelistManagement();
        Player player = event.getPlayer();
        if (player.isOp()) {
            sendMessageLater(player.getUniqueId(), "Hello, " + player.getName()
                    + "! (whitelisted via operator)");
            event.allow();
            return;
        }

        if (player.isWhitelisted()) {
            sendMessageLater(player.getUniqueId(), "Hello, " + player.getName()
                    + "! (whitelisted via vanilla whitelist)");
            event.allow();
            return;
        }

        if (whitelist.isWhitelisted(player.getUniqueId())) {
            whitelist.updatePlayerLastKnownName(player.getUniqueId(), player.getName());
            WhitelistedPlayer whitelistedPlayer = whitelist.getWhitelistedPlayer(player.getUniqueId());
            DiscordUser linkedAccount = whitelist.getDiscordUser(whitelistedPlayer.discordUser);
            sendMessageLater(player.getUniqueId(), "Hello, " + linkedAccount.displayName + "! ("
                    + linkedAccount.username + ")");
            event.allow();
            return;
        }

        String linkCode = DiscordApp.getWhitelistManagement().createLinkCode(event.getPlayer().getName(), event.getPlayer().getUniqueId());

        event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, Component.text(
                "You are not whitelisted. Please use /whitelist on discord to link your account first.\nLink Code: " + linkCode
        ).color(NamedTextColor.RED));
    }

    private void sendMessageLater(final UUID whom, final String message) {
        Bukkit.getScheduler().runTaskLater(BlazingGames.get(), () -> {
            if (Bukkit.getPlayer(whom) != null) {
                Bukkit.getPlayer(whom).sendMessage(Component.text(message).color(NamedTextColor.YELLOW));
            }
        }, 20L);
    }
}
