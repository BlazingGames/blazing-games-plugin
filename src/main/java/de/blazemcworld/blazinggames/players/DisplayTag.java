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
package de.blazemcworld.blazinggames.players;

import java.util.UUID;

import org.bukkit.entity.Player;

import de.blazemcworld.blazinggames.discord.DiscordApp;
import de.blazemcworld.blazinggames.discord.DiscordUser;
import de.blazemcworld.blazinggames.discord.WhitelistManagement;
import de.blazemcworld.blazinggames.discord.WhitelistedPlayer;
import de.blazemcworld.blazinggames.utils.EmojiRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public record DisplayTag(
    // player data
    UUID uuid,
    String username,
    boolean isOperator,

    // display data
    String displayName,
    String pronouns,
    TextColor color,

    // plural data
    boolean isPluralMember,
    String systemName,
    String systemTag
) {
    public void sendPreviews(Player player, TextColor color) {
        player.sendMessage(Component.text("Preview:", color));
        player.sendMessage(Component.text("- Current nameplate: ").color(color)
            .append(buildNameComponent()));
        player.sendMessage(Component.text("- Current nameplate (short): ").color(color)
            .append(buildNameComponentShort()));
        player.sendMessage(Component.text("- Current discord name: ").color(color)
            .append(Component.text(buildNameString()).color(NamedTextColor.WHITE)));
        player.sendMessage(Component.text("- Current discord name (short): ").color(color)
            .append(Component.text(buildNameStringShort()).color(NamedTextColor.WHITE)));
    }

    public Component buildNameComponent() {
        String nameHoverString = "Real Name: " + username();

        if(DiscordApp.isWhitelistManaged()) {
            WhitelistManagement whitelist = DiscordApp.getWhitelistManagement();

            WhitelistedPlayer whitelistedPlayer = whitelist.getWhitelistedPlayer(uuid());

            if(whitelistedPlayer == null) {
                nameHoverString += "\nDiscord User not found";
            }
            else {
                DiscordUser user = whitelist.getDiscordUser(whitelistedPlayer.discordUser);
                if(user == null) {
                    nameHoverString += "\nDiscord User not found";
                }
                else {
                    nameHoverString += "\nDiscord Display Name: " + user.displayName;
                    nameHoverString += "\nDiscord Username: " + user.username;
                }
            }
        }

        HoverEvent<Component> nameHover = Component.text(nameHoverString).asHoverEvent();

        Component tagComponent;
        if (isPluralMember() && systemTag() != null) {
            Component subTagComponent = Component.text(systemTag());
            if (systemName() != null) {
                subTagComponent = subTagComponent.hoverEvent(Component.text("System tag, part of " + systemName()).asHoverEvent());
            } else {
                subTagComponent = subTagComponent.hoverEvent(Component.text("System tag").asHoverEvent());
            }
            tagComponent = Component.space().append(subTagComponent);
        } else if (isPluralMember() && systemName() != null && systemTag() == null) {
            tagComponent = Component.space().append(Component.text("(" + systemName() + ")").hoverEvent(Component.text("System name").asHoverEvent()));
        } else {
            tagComponent = Component.empty();
        }

        Component builder;
        if (displayName() != null && !displayName().equals(username())) {
            builder = Component.text(displayName()).hoverEvent(nameHover).append(tagComponent);
        } else {
            builder = Component.text(username()).hoverEvent(nameHover).append(tagComponent);
        }

        if (color() != null) {
            builder = builder.color(color());
        }

        if (pronouns() != null) {
            builder = builder.appendSpace().append(Component.text("(" + pronouns() + ")")
                .color(NamedTextColor.GRAY).hoverEvent(HoverEvent.showText(Component.text("Pronouns"))));
        }

        if (isOperator()) {
            builder = builder.appendSpace().append(Component.text("♮").color(NamedTextColor.RED)
                .hoverEvent(HoverEvent.showText(Component.text("Server Operator"))));
        }

        return EmojiRegistry.parseEmoji(builder);
    }

    public Component buildNameComponentShort() {
        String shownName = displayName() != null ? displayName() : username();

        if (isPluralMember() && systemTag() != null) {
            shownName += (" " + systemTag());
        }

        Component event = Component.text("Real name: " + username())
            .appendNewline().append(Component.text("Pronouns: " + (pronouns() != null ? pronouns() : "None specified")))
            .appendNewline().append(Component.text("Server Operator: " + (isOperator() ? "Yes" : "No")));

        if (isPluralMember()) {
            event = event.appendNewline()
                .appendNewline().append(Component.text("Plural system member"))
                .appendNewline().append(Component.text("System name: " + (systemName() != null ? systemName() : "Undefined")))
                .appendNewline().append(Component.text("System tag: " + (systemTag() != null ? systemTag() : "Undefined")));
        }

        Component name = Component.text(shownName)
            .color(color() != null ? color() : NamedTextColor.WHITE)
            .hoverEvent(event.asHoverEvent());

        return EmojiRegistry.parseEmoji(name);
    }

    public String buildNameString() {
        StringBuilder builder = new StringBuilder();
        
        if (displayName() != null) {
            builder.append(displayName());
        } else {
            builder.append(username());
        }

        if (isPluralMember() && systemTag() != null) {
            builder.append(" ").append(systemTag());
        } else if (isPluralMember() && systemTag() == null && systemName() != null) {
            builder.append(" (").append(systemName()).append(")");
        }

        if (!builder.toString().equals(username())) {
            builder.append(" [aka ").append(username()).append("]");
        }

        if (pronouns() != null) {
            builder.append(" (").append(pronouns()).append(")");
        }
        if (isOperator()) {
            builder.append(" ♮");
        }

        return builder.toString();
    }

    public String buildNameStringShort() {
        if (isPluralMember()) {
            return displayName() + " " + systemTag(); // plural members always have a display tag
        }

        return displayName() != null ? displayName() : username();
    }
}