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
import org.bukkit.Statistic;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;

import de.blazemcworld.blazinggames.commands.boilerplate.CommandHelper;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.PlayerProfileListResolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PlaytimeCommand {
    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("playtime")
            // no arguments, current player
            .executes(CommandHelper.getDefault().requirePlayer((ctx, p) -> handle(ctx, p.getPlayerProfile())))

            // all players selected as argument
            .then(Commands.argument("player", ArgumentTypes.playerProfiles())
                .executes(CommandHelper.getDefault().wrap(ctx -> {
                    PlayerProfileListResolver resolver = ctx.getArgument("player", PlayerProfileListResolver.class);
                    Collection<PlayerProfile> profiles = resolver.resolve(ctx.getSource());
                    handle(ctx, profiles.toArray(PlayerProfile[]::new));
                })))
            .build();
    }

    public static void handle(CommandContext<CommandSourceStack> ctx, PlayerProfile... profiles) {
        if (profiles.length == 0) {
            ctx.getSource().getSender().sendRichMessage("<red>No players provided!");
            return;
        }

        for (PlayerProfile profile : profiles) {
            String playtime = getPlaytime(profile.getName(), profile.getId());

            if (playtime == null) {
                ctx.getSource().getSender().sendRichMessage("<red>Couldn't find any playtime for " + profile.getName());
            } else {
                ctx.getSource().getSender().sendRichMessage("<yellow>" + playtime);
            }
        }
    }

    public static String getPlaytime(String username, UUID uuid) {
        int playtime = Bukkit.getOfflinePlayer(uuid).getStatistic(Statistic.PLAY_ONE_MINUTE);

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

        return username + " has been wasting time on this server for " + timeText + "!";
    }
}