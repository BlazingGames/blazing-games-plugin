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
import de.blazemcworld.blazinggames.utils.PlayerConfig;
import de.blazemcworld.blazinggames.utils.TextUtils;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DiscordChatHandler extends BlazingEventHandler<AsyncChatEvent> implements ChatRenderer {
    @Override
    public boolean fitCriteria(AsyncChatEvent event, boolean cancelled) {
        return true;
    }

    @Override
    public void execute(AsyncChatEvent event) {
        event.renderer(this); // Tell the event to use our renderer
        DiscordApp.messageHook(event.getPlayer(), event.message());
    }

    @Override
    public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
        PlayerConfig config = PlayerConfig.forPlayer(source);
        Component username = config.buildNameComponent();
        String rawMessage = TextUtils.componentToString(message);
        if (meFormat(rawMessage) != null) {
            // me when a oneliner needs to be multiline
            return Component.text("*").color(NamedTextColor.WHITE)
                    .appendSpace()
                    .append(config.buildNameComponentShort())
                    .appendSpace()
                    .append(TextUtils.colorCodeParser(TextUtils.stringToComponent(meFormat(rawMessage))).color(NamedTextColor.WHITE));
        } else if (greentextFormat(rawMessage) != null) {
            Component txt = Component.empty().append(username).append(Component.text(": ").color(NamedTextColor.WHITE));
            String[] parts = greentextFormat(rawMessage);
            for (String part : parts) {
                txt = txt.appendNewline().append(Component.text("    > ").color(NamedTextColor.WHITE))
                        .append(TextUtils.colorCodeParser(TextUtils.stringToComponent(part)).color(NamedTextColor.WHITE));
            }
            return txt;
        } else {
            return Component.empty().append(username).append(Component.text(": ").color(NamedTextColor.WHITE))
                    .append(TextUtils.colorCodeParser(message).color(NamedTextColor.WHITE));
        }
    }

    public static String meFormat(String existingContent) {
        if (existingContent.startsWith("* ")) {
            return existingContent.substring(1).trim();
        }
        return null;
    }

    public static String[] greentextFormat(String existingContent) {
        if (existingContent.startsWith(">")) {
            String[] parts = existingContent.split(">");
            if (parts.length > 1) {
                ArrayList<String> output = new ArrayList<>(List.of(parts));
                output.remove(0);
                return output.stream().map(String::trim).toArray(String[]::new);
            }
        }
        return null;
    }
}
