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
package de.blazemcworld.blazinggames.commands.plural;

import org.bukkit.entity.Player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;

import de.blazemcworld.blazinggames.utils.FrontManager;
import de.blazemcworld.blazinggames.utils.PlayerConfig;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class SystemCommand {
    public static final TextColor color = TextColor.color(0xEDC4DF);
    public static final String resetHint = " Run this command with \"unset\" to reset it.";

    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("system")
            .then(Commands.literal("enable").executes(ctx -> {
                if (!(ctx.getSource().getSender() instanceof Player player)) {
                    ctx.getSource().getSender().sendMessage("You must be a player to use this command!");
                    return Command.SINGLE_SUCCESS;
                }

                PlayerConfig config = PlayerConfig.forPlayer(player);
                if (!config.isPlural()) {
                    config.setPlural(true);
                    player.sendMessage(Component.text("Marked this account as a plural system.", color));
                } else {
                    player.sendMessage(Component.text("This account is already marked as a plural system.", color));
                }
                return Command.SINGLE_SUCCESS;
            }))



            .then(Commands.literal("disable").executes(ctx -> {
                if (!(ctx.getSource().getSender() instanceof Player player)) {
                    ctx.getSource().getSender().sendMessage("You must be a player to use this command!");
                    return Command.SINGLE_SUCCESS;
                }

                PlayerConfig config = PlayerConfig.forPlayer(player);
                if (config.isPlural()) {
                    config.setPlural(false);
                    FrontManager.clearFront(player.getUniqueId());
                    player.sendMessage(Component.text("Unmarked this account as a plural system.", color));
                } else {
                    player.sendMessage(Component.text("This account is already not marked as a plural system.", color));
                }
                return Command.SINGLE_SUCCESS;
            }))



            .then(Commands.literal("name").then(Commands.argument("name", StringArgumentType.greedyString()).executes(ctx -> {
                if (!(ctx.getSource().getSender() instanceof Player player)) {
                    ctx.getSource().getSender().sendMessage("You must be a player to use this command!");
                    return Command.SINGLE_SUCCESS;
                }

                String argument = StringArgumentType.getString(ctx, "name");
                PlayerConfig config = PlayerConfig.forPlayer(player);
                if (!argument.equals("unset")) {
                    config.setSystemName(argument);
                    player.sendMessage(Component.text("Set this system name to \"" + argument + "\"." + resetHint, color));
                } else {
                    config.setSystemName(null);
                    player.sendMessage(Component.text("Cleared this system name.", color));
                }
                return Command.SINGLE_SUCCESS;
            })))



            .then(Commands.literal("tag").then(Commands.argument("tag", StringArgumentType.greedyString()).executes(ctx -> {
                if (!(ctx.getSource().getSender() instanceof Player player)) {
                    ctx.getSource().getSender().sendMessage("You must be a player to use this command!");
                    return Command.SINGLE_SUCCESS;
                }

                String argument = StringArgumentType.getString(ctx, "tag");
                PlayerConfig config = PlayerConfig.forPlayer(player);
                if (!argument.equals("unset")) {
                    config.setSystemTag(argument);
                    player.sendMessage(Component.text("Set this system tag to \"" + argument + "\"." + resetHint, color));
                } else {
                    config.setSystemTag(null);
                    player.sendMessage(Component.text("Cleared this system tag.", color));
                }
                return Command.SINGLE_SUCCESS;
            }))).build();
    }
}