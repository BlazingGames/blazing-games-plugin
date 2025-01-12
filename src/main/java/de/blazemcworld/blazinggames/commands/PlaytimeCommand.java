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
package de.blazemcworld.blazinggames.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PlaytimeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command,
                             @NotNull String s, @NotNull String[] strings) {
        if(strings.length > 1) {
            CommandHelper.sendUsage(commandSender, command);
            return true;
        }

        OfflinePlayer player;
        if(strings.length > 0) {
            player = Bukkit.getOfflinePlayer(strings[0]);
        }
        else {
            if(commandSender instanceof Player p) {
                player = p;
            }
            else {
                commandSender.sendMessage(Component.text("Only players can use this command without providing a player!")
                        .color(NamedTextColor.RED));
                return true;
            }
        }

        int playtime = player.getStatistic(Statistic.PLAY_ONE_MINUTE);

        if(playtime <= 0) {
            commandSender.sendMessage(Component.text(player.getName() + " has not been on this server ever before!")
                                        .color(NamedTextColor.RED));
            return true;
        }

        int seconds = playtime / 20 % 60;
        int minutes = playtime / 20 / 60 % 60;
        int hours = playtime / 20 / 60 / 60 % 24;
        int days = playtime / 20 / 60 / 60 / 24;

        List<String> time = new ArrayList<>();

        if(days > 0) time.add(days+" days");
        if(hours > 0) time.add(hours+" hours");
        if(minutes > 0) time.add(minutes+" minutes");
        if(seconds > 0) time.add(seconds+" seconds");

        StringBuilder timeText = new StringBuilder();

        timeText.append(time.get(0));

        if(time.size() > 1) {
            for(int i = 1; i < time.size()-1; i++) {
                timeText.append(", ").append(time.get(i));
            }
            timeText.append(" and ").append(time.get(time.size()-1));
        }

        commandSender.sendMessage(Component.text(player.getName() + " has been wasting time on this server for " + timeText + "!")
                .color(NamedTextColor.YELLOW));

        return true;
    }
}