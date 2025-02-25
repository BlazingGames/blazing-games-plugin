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

import de.blazemcworld.blazinggames.discord.DiscordApp;
import de.blazemcworld.blazinggames.utils.DisplayTag;
import de.blazemcworld.blazinggames.utils.MemberData;
import de.blazemcworld.blazinggames.utils.Pair;
import de.blazemcworld.blazinggames.utils.PlayerConfig;
import de.blazemcworld.blazinggames.utils.PluralConfig;
import de.blazemcworld.blazinggames.utils.TextUtils;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class ChatEventListener implements Listener, ChatRenderer {

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        event.renderer(this); // Tell the event to use our renderer

        Pair<String, DisplayTag> pair = getShownMessageAndDisplayTag(TextUtils.componentToString(event.message()), event.getPlayer());
        DiscordApp.messageHook(event.getPlayer(), pair.left, pair.right);
    }

    private Pair<String, DisplayTag> getShownMessageAndDisplayTag(String message, Player speaker) {
        PlayerConfig config = PlayerConfig.forPlayer(speaker);
        if (config.isPlural()) {
            PluralConfig cfg = config.getPluralConfig();
            MemberData member = cfg.detectProxiedMember(message);
            if (member != null) {
                return new Pair<>(
                    message.substring(member.proxyStart.length(), message.length() - member.proxyEnd.length()),
                cfg.toDisplayTag(member.name, config));
            }
        }

        return new Pair<>(message, config.toDisplayTag(true));
    }

    @Override
    public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component messageComponent, @NotNull Audience viewer) {
        Pair<String, DisplayTag> pair = getShownMessageAndDisplayTag(TextUtils.componentToString(messageComponent), source);
        String message = pair.left;
        DisplayTag displayTag = pair.right;
        if (meFormat(message) != null) {
            // me when a oneliner needs to be multiline
            return Component.text("*").color(NamedTextColor.WHITE)
                .appendSpace()
                .append(displayTag.buildNameComponentShort())
                .appendSpace()
                .append(TextUtils.parseMinimessage(meFormat(message)).color(NamedTextColor.WHITE));
        } else {
            return Component.empty().append(displayTag.buildNameComponent()).append(Component.text(": ").color(NamedTextColor.WHITE))
                .append(TextUtils.parseMinimessage(message).color(NamedTextColor.WHITE));
        }
    }

    public static String meFormat(String existingContent) {
        if (existingContent.startsWith("* ")) {
            return existingContent.substring(1).trim();
        }
        return null;
    }
}
