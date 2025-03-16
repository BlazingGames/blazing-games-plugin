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
import dev.ivycollective.datastorage.DataStorage;
import dev.ivycollective.datastorage.name.UUIDNameProvider;
import dev.ivycollective.datastorage.storage.PropertiesStorageProvider;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
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
            Component name = toDisplayTag(true).buildNameComponent();
            player.displayName(name);
            player.playerListName(name);
        }
    }

    public PlayerInfo playerInfo() {
        return playerInfo;
    }



    public DisplayTag toDisplayTag(boolean useMember) {
        if (!isPlural() || !useMember) {
            return new DisplayTag(
                playerInfo.getUUID(),
                playerInfo.getUsername(),
                playerInfo.isOperator(),
                getDisplayName(),
                getPronouns(),
                getNameColor(),
                false, getSystemName(), getSystemTag()
            );
        }

        String front = FrontManager.getFront(playerInfo.getUUID());
        PluralConfig cfg = getPluralConfig();
        
        if (front != null && useMember) {
            DisplayTag tag = cfg.toDisplayTag(front, this);
            if (tag != null) return tag;
        }

        // this always triggers the first if statement, so it should be safe
        return toDisplayTag(false);
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



    public boolean isPlural() {
        return Boolean.parseBoolean(props.getProperty("plural", "false"));
    }

    public void setPlural(boolean plural) {
        if (!plural) props.remove("plural");
        props.setProperty("plural", String.valueOf(plural));
        write();
    }

    public PluralConfig getPluralConfig() {
        return new PluralConfig(playerInfo.getUUID());
    }



    public String getSystemName() {
        String value = props.getProperty("system.name", null);
        if (value == null || value.isBlank()) return null;
        return value;
    }

    public void setSystemName(String systemName) {
        if (systemName == null || systemName.isBlank()) props.remove("system.name");
        else props.setProperty("system.name", systemName);
        write();
    }



    public String getSystemTag() {
        String value = props.getProperty("system.tag", null);
        if (value == null || value.isBlank()) return null;
        return value;
    }

    public void setSystemTag(String systemTag) {
        if (systemTag == null || systemTag.isBlank()) props.remove("system.tag");
        else props.setProperty("system.tag", systemTag);
        write();
    }
}
