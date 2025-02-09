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
package de.blazemcworld.blazinggames.utils;

import java.util.Properties;
import java.util.UUID;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.discord.DiscordApp;
import de.blazemcworld.blazinggames.discord.DiscordUser;
import de.blazemcworld.blazinggames.discord.WhitelistManagement;
import de.blazemcworld.blazinggames.discord.WhitelistedPlayer;
import dev.ivycollective.datastorage.DataStorage;
import dev.ivycollective.datastorage.name.UUIDNameProvider;
import dev.ivycollective.datastorage.storage.PropertiesStorageProvider;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import javax.naming.NameNotFoundException;

public class PlayerConfig {
    private static final DataStorage<Properties, UUID> dataStorage = BlazingGames.dataStorageConfig().makeDataStorage(
        PlayerConfig.class, null,
        new PropertiesStorageProvider(), new UUIDNameProvider()
    );

    public static PlayerConfig forPlayer(UUID uuid) throws NameNotFoundException {
        return forPlayer(PlayerInfo.fromUUID(uuid));
    }

    public static PlayerConfig forPlayer(Player player) {
        return forPlayer(PlayerInfo.fromOnlinePlayer(player));
    }

    public static PlayerConfig forPlayer(PlayerInfo info) {
        if (dataStorage.getData(info.getUUID()) == null) dataStorage.storeData(info.getUUID(), new Properties());
        return new PlayerConfig(dataStorage.getData(info.getUUID()), info);
    }

    private final Properties props;
    private final PlayerInfo playerInfo;
    private PlayerConfig(Properties props, PlayerInfo playerInfo) {
        this.props = props;
        this.playerInfo = playerInfo;
    }

    private void write() {
        dataStorage.storeData(playerInfo.getUUID(), props);
    }

    public void updatePlayer() {
        Player player = Bukkit.getPlayer(playerInfo.getUUID());

        if(player != null) {
            Component name = buildNameComponent();
            player.displayName(name);
            player.playerListName(name);
        }
    }

    public Component buildNameComponent() {
        String playerName = playerInfo.getUsername();

        String nameHoverString = "Real Name: " + playerName;

        if(DiscordApp.isWhitelistManaged()) {
            WhitelistManagement whitelist = DiscordApp.getWhitelistManagement();

            WhitelistedPlayer whitelistedPlayer = whitelist.getWhitelistedPlayer(playerInfo.getUUID());

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

        Component username;
        if (getDisplayName() != null && !getDisplayName().equals(playerName)) {
            username = Component.text(getDisplayName()).hoverEvent(nameHover);
        } else {
            username = Component.text(playerName).hoverEvent(nameHover);
        }

        if (getNameColor() != null) {
            username = username.color(getNameColor());
        }

        if (getPronouns() != null) {
            username = username.appendSpace().append(Component.text("(" + getPronouns() + ")")
                .color(NamedTextColor.GRAY).hoverEvent(HoverEvent.showText(Component.text("Pronouns"))));
        }

        if (playerInfo.isOperator()) {
            username = username.appendSpace().append(Component.text("♮").color(NamedTextColor.RED)
                .hoverEvent(HoverEvent.showText(Component.text("Server Operator"))));
        }

        return username;
    }

    public Component buildNameComponentShort() {
        return Component.text(getDisplayName() != null ? getDisplayName() : playerInfo.getUsername())
            .color(getNameColor() != null ? getNameColor() : NamedTextColor.WHITE)
            .hoverEvent(HoverEvent.showText(Component.text("Real name: " + playerInfo.getUsername())
            .appendNewline().append(Component.text("Pronouns: " + (getPronouns() != null ? getPronouns() : "None specified")))
            .appendNewline().append(Component.text("Server Operator: " + (playerInfo.isOperator() ? "Yes" : "No")))));
    }

    public String buildNameString() {
        String playerName = playerInfo.getUsername();

        StringBuilder username = new StringBuilder();
        if (getDisplayName() != null && !getDisplayName().equals(playerName)) {
            username.append(getDisplayName()).append(" [aka ").append(playerName).append("]");
        } else {
            username.append(playerName);
        }
        if (getPronouns() != null) {
            username.append(" (").append(getPronouns()).append(")");
        }
        if (playerInfo.isOperator()) {
            username.append(" ♮");
        }

        return username.toString();
    }

    public String buildNameStringShort() {
        return getDisplayName() != null ? getDisplayName() : playerInfo.getUsername();
    }



    public String getDisplayName() {
        String value = props.getProperty("displayname", null);
        if (value == null || value.isBlank()) return null;
        return value;
    }

    public void setDisplayName(String name) {
        if (name == null || name.isBlank()) props.remove("displayname");
        else props.setProperty("displayname", name);
        write();
    }



    public String getPronouns() {
        String value = props.getProperty("pronouns", null);
        if (value == null || value.isBlank()) return null;
        return value;
    }

    public void setPronouns(String pronouns) {
        if (pronouns == null || pronouns.isBlank()) props.remove("pronouns");
        else props.setProperty("pronouns", pronouns);
        write();
    }



    public TextColor getNameColor() {
        return TextColor.color(Integer.valueOf(props.getProperty("namecolor", "16777215")));
    }

    public void setNameColor(TextColor color) {
        if (color == null) props.remove("namecolor");
        else props.setProperty("namecolor", String.valueOf(color.value()));
        write();
    }
}
