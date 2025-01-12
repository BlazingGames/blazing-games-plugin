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
import de.blazemcworld.blazinggames.utils.PlayerConfig;
import de.blazemcworld.blazinggames.utils.TextUtils;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class ChatEventListener implements Listener, ChatRenderer {

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        event.renderer(this); // Tell the event to use our renderer
        DiscordApp.messageHook(event.getPlayer(), event.message());
    }

    @Override
    public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
        // format username
        Component username;
        PlayerConfig config = PlayerConfig.forPlayer(source.getUniqueId());
        if (config.getDisplayName() != null && !config.getDisplayName().equals(source.getName())) {
            username = Component.text(config.getDisplayName()).hoverEvent(HoverEvent.showText(Component.text("Real name: " + source.getName())));
        } else {
            username = Component.text(source.getName()).hoverEvent(HoverEvent.showText(Component.text("Real name: " + source.getName())));
        }

        if (config.getNameColor() != null) {
            username = username.color(config.getNameColor());
        }


        if (config.getPronouns() != null) {
            username = username.appendSpace().append(Component.text("(" + config.getPronouns() + ")")
                .color(NamedTextColor.GRAY).hoverEvent(HoverEvent.showText(Component.text("Pronouns"))));
        }

        if (source.isOp()) {
            username = username.appendSpace().append(Component.text("\u266E").color(NamedTextColor.RED)
                .hoverEvent(HoverEvent.showText(Component.text("Server Operator"))));
        }

        // custom formatting
        String rawMessage = TextUtils.componentToString(message);
        if (meFormat(rawMessage) != null) {
            // me when a oneliner needs to be multiline
            Component minimalUsername = Component.text(config.getDisplayName() != null ? config.getDisplayName() : source.getName())
                .color(config.getNameColor() != null ? config.getNameColor() : NamedTextColor.WHITE)
                .hoverEvent(HoverEvent.showText(Component.text("Real name: " + source.getName())
                    .appendNewline().append(Component.text("Pronouns: " + config.getPronouns() != null ? config.getPronouns() : "None specified"))
                    .appendNewline().append(Component.text("Server Operator: " + (source.isOp() ? "Yes" : "No")))));
            
            return Component.text("*").color(NamedTextColor.WHITE)
                .appendSpace()
                .append(minimalUsername)
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
