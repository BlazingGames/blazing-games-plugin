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
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.players.DisplayTag;
import de.blazemcworld.blazinggames.players.MemberData;
import de.blazemcworld.blazinggames.players.PlayerConfig;
import de.blazemcworld.blazinggames.players.PluralConfig;
import de.blazemcworld.blazinggames.utils.EmojiRegistry;
import de.blazemcworld.blazinggames.utils.TextUtils;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DiscordChatHandler extends BlazingEventHandler<AsyncChatEvent> {
    @Override
    public boolean fitCriteria(AsyncChatEvent event, boolean cancelled) {
        return true;
    }

    @Override
    public void execute(AsyncChatEvent event) {
        String message = PlainTextComponentSerializer.plainText().serialize(event.message());
        PlayerConfig config = PlayerConfig.forPlayer(event.getPlayer());
        if (config.isPlural()) {
            PluralConfig cfg = config.getPluralConfig();
            MemberData member = cfg.detectProxiedMember(message);
            if (member != null) {
                String proxyMessage = message.substring(member.proxyStart.length(), message.length() - member.proxyEnd.length());
                DisplayTag displayTag = cfg.toDisplayTag(member.name, config);
                event.renderer(new GenericRenderer(displayTag));
                event.message(parseGoodChat(proxyMessage));
                DiscordApp.messageHook(event.getPlayer(), proxyMessage, displayTag);
                return;
            }
        }

        DisplayTag displayTag = config.toDisplayTag(true);
        event.renderer(new GenericRenderer(displayTag));
        event.message(parseGoodChat(message));
        DiscordApp.messageHook(event.getPlayer(), message, displayTag);
    }

    public static class GenericRenderer implements ChatRenderer {
        public final DisplayTag tag;

        public GenericRenderer(DisplayTag tag) {
            this.tag = tag;
        }

        @Override
        public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component messageComponent, @NotNull Audience viewer) {
            return Component.text().append(tag.buildNameComponent()).append(Component.text(": ").color(NamedTextColor.WHITE))
                .append(messageComponent).build();
        }
    }

    public static Component parseGoodChat(String message) {
        return EmojiRegistry.parseEmoji(TextUtils.stringToMinimessage(message));
    }
}
