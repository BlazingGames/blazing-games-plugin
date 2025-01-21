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
package de.blazemcworld.blazinggames.discord;

import de.blazemcworld.blazinggames.utils.PlayerConfig;
import de.blazemcworld.blazinggames.utils.TextUtils;
import io.papermc.paper.advancement.AdvancementDisplay;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.awt.Color;

public record DiscordNotification(
        Player player,
        String title,
        String description,
        Color color,
        String iconUrl
) {
    public static DiscordNotification serverStartup() {
        return new DiscordNotification(
                null, "Server Started", null, Color.GREEN, null
        );
    }

    public static DiscordNotification serverShutdown() {
        return new DiscordNotification(
                null, "Server Stopped", null, Color.RED, null
        );
    }

    public static DiscordNotification playerJoin(Player player) {
        int players = Bukkit.getOnlinePlayers().size();
        String verbText = players == 1 ? "is" : "are";
        String playersText = players == 1 ? "player" : "players";
        return new DiscordNotification(
                player, "Joined the game",
                "There " + verbText + " now " + players + " " + playersText + " online",
                Color.GREEN, null
        );
    }

    public static DiscordNotification playerLeave(Player player) {
        int players = Bukkit.getOnlinePlayers().size() - 1;
        String verbText = players == 1 ? "is" : "are";
        String playersText = players == 1 ? "player" : "players";
        return new DiscordNotification(
                player, "Left the game",
                "There " + verbText + " now " + players + " " + playersText + " online",
                Color.RED, null
        );
    }

    public static DiscordNotification playerAdvancement(Player player, AdvancementDisplay advancement) {
        return new DiscordNotification(
                player, "Obtained " + TextUtils.componentToString(advancement.title()),
                TextUtils.componentToString(advancement.description()), Color.YELLOW,
                "https://raw.githubusercontent.com/Owen1212055/mc-assets/main/assets/"
                        + advancement.icon().getType() + ".png"
        );
    }

    public static DiscordNotification playerDeath(Player player, String deathMessage) {
        return new DiscordNotification(
                player, "Died",
                deathMessage, Color.ORANGE, null
        );
    }

    public MessageEmbed toEmbed() {
        EmbedBuilder builder = new EmbedBuilder();

        // defaults
        builder.setTitle("Notification");
        builder.setColor(Color.ORANGE);

        if (player != null) {
            PlayerConfig config = PlayerConfig.forPlayer(player.getUniqueId());
            builder.setAuthor(
                    config.buildNameString(player.getName(), player.isOp()), null,
                    "http://cravatar.eu/helmhead/" + player.getUniqueId() + "/128.png"
            );
        }
        if (title != null) builder.setTitle(title);
        if (description != null) builder.setDescription(description);
        if (color != null) builder.setColor(color);
        if (iconUrl != null) builder.setThumbnail(iconUrl);
        return builder.build();
    }
}
