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

import de.blazemcworld.blazinggames.discord.WhitelistedPlayer;
import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.naming.NameNotFoundException;
import java.util.UUID;

public class PlayerInfo {
    private final UUID uuid;
    private final String username;
    private final boolean isOperator;

    private PlayerInfo(UUID uuid, String username, boolean isOperator) {
        this.uuid = uuid;
        this.username = username;
        this.isOperator = isOperator;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public boolean isOperator() {
        return isOperator;
    }

    public static PlayerInfo fromOfflinePlayer(OfflinePlayer player) throws NameNotFoundException {
        if(player == null) {
            throw new NullArgumentException("Player cannot be null!");
        }

        String username = player.getName();

        if(username == null) {
            throw new NameNotFoundException("Player's name is not known!");
        }

        return new PlayerInfo(player.getUniqueId(), username, player.isOp());
    }

    public static PlayerInfo fromOnlinePlayer(Player player) {
        if(player == null) {
            throw new NullArgumentException("Player cannot be null!");
        }

        String username = player.getName();
        return new PlayerInfo(player.getUniqueId(), username, player.isOp());
    }

    public static PlayerInfo fromUsernameUUID(UUID uuid, String username) {
        return new PlayerInfo(uuid, username, false);
    }

    public static PlayerInfo fromUUID(UUID uuid) throws NameNotFoundException {
        return fromOfflinePlayer(Bukkit.getOfflinePlayer(uuid));
    }

    public static PlayerInfo fromWhitelistedPlayer(WhitelistedPlayer player) {
        if(player == null) {
            return null;
        }

        try {
            return PlayerInfo.fromUUID(player.uuid);
        } catch (NameNotFoundException e) {
            return PlayerInfo.fromUsernameUUID(player.uuid, player.lastKnownName);
        }
    }
}
