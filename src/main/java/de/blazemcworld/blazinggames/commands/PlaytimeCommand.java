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

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import java.util.ArrayList;
import java.util.List;

public class PlaytimeCommand {
    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("playtime")
            .executes(ctx -> {
                CommandSender sender = ctx.getSource().getSender();
                if (ctx.getSource().getExecutor() == null || !(ctx.getSource().getExecutor() instanceof Player player)) {
                    sender.sendRichMessage("<red>The executor is not a player!");
                    return Command.SINGLE_SUCCESS;
                }
                
                String playtime = getPlaytime(player);

                if (playtime == null) {
                    sender.sendRichMessage("<red>what.");
                } else {
                    sender.sendRichMessage("<yellow>" + playtime);
                }

                return Command.SINGLE_SUCCESS;
            })
            .then(Commands.argument("player", StringArgumentType.word())
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();
                    String username = StringArgumentType.getString(ctx, "player");
                    OfflinePlayer player = Bukkit.getOfflinePlayer(username);

                    String playtime = getPlaytime(player);

                    if (playtime == null) {
                        sender.sendRichMessage("<red>This player hasn't played before.");
                    } else {
                        sender.sendRichMessage("<yellow>" + playtime);
                    }

                    return Command.SINGLE_SUCCESS;
                }))
            .build();
    }

    public static String getPlaytime(OfflinePlayer player) {
        int playtime = player.getStatistic(Statistic.PLAY_ONE_MINUTE);

        if(playtime <= 0) {
            return null;
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

        return player.getName() + " has been wasting time on this server for " + timeText + "!";
    }
}