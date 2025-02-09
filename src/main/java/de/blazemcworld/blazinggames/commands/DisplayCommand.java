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

import org.bukkit.entity.Player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;

import de.blazemcworld.blazinggames.utils.PlayerConfig;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class DisplayCommand {
    public static final TextColor colorSuccess = TextColor.color(0xD1FCDF);
    public static final TextColor colorFailure = TextColor.color(0xFC9588);

    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("display")
            .executes(ctx -> {
                Player player = requirePlayer(ctx);
                if (player == null) return Command.SINGLE_SUCCESS;

                // help text
                player.sendMessage(Component.text("Usage: /display [name|pronouns|color] [value]").color(colorSuccess));
                sendNameplates(player);
                return Command.SINGLE_SUCCESS;
            })
            .then(Commands.literal("reset")
                .executes(ctx -> {
                    Player player = requirePlayer(ctx);
                    if (player == null) return Command.SINGLE_SUCCESS;

                    // reset help text
                    player.sendMessage(Component.text("To reset all display settings, run ").color(colorSuccess)
                        .append(Component.text("/display reset confirm").color(colorFailure)));
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.literal("confirm").executes(ctx -> {
                    Player player = requirePlayer(ctx);
                    if (player == null) return Command.SINGLE_SUCCESS;

                    // reset confirmation
                    PlayerConfig config = PlayerConfig.forPlayer(player);
                    config.setDisplayName(null);
                    config.setPronouns(null);
                    config.setNameColor(null);
                    player.sendMessage(Component.text("Reset all settings successfully.").color(colorSuccess));
                    sendNameplates(player);
                    return Command.SINGLE_SUCCESS;
                })))
            .then(Commands.literal("name")
                .executes(ctx -> {
                    Player player = requirePlayer(ctx);
                    if (player == null) return Command.SINGLE_SUCCESS;

                    // clear name
                    PlayerConfig.forPlayer(player).setDisplayName(null);
                    player.sendMessage(Component.text("Unset display name successfully.", colorSuccess));
                    sendNameplates(player);
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.argument("value", StringArgumentType.greedyString()).executes(ctx -> {
                    Player player = requirePlayer(ctx);
                    if (player == null) return Command.SINGLE_SUCCESS;
                    String value = StringArgumentType.getString(ctx, "value");

                    // update display name
                    if (value.length() < 2 || value.length() > 40) {
                        player.sendMessage(Component.text("Display name must be between 2 and 40 characters long.").color(colorFailure));
                        return Command.SINGLE_SUCCESS;
                    }
                    PlayerConfig.forPlayer(player).setDisplayName(value);
                    player.sendMessage(Component.text("Set display name to %s successfully.".formatted(value), colorSuccess));
                    sendNameplates(player);
                    return Command.SINGLE_SUCCESS;
                })))
            .then(Commands.literal("pronouns")
                .executes(ctx -> {
                    Player player = requirePlayer(ctx);
                    if (player == null) return Command.SINGLE_SUCCESS;

                    // clear pronouns
                    PlayerConfig.forPlayer(player).setPronouns(null);
                    player.sendMessage(Component.text("Unset pronouns successfully.", colorSuccess));
                    sendNameplates(player);
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.argument("value", StringArgumentType.greedyString()).executes(ctx -> {
                    Player player = requirePlayer(ctx);
                    if (player == null) return Command.SINGLE_SUCCESS;
                    String value = StringArgumentType.getString(ctx, "value");

                    // update pronouns
                    if (value.length() < 2 || value.length() > 20) {
                        player.sendMessage(Component.text("Pronouns must be between 2 and 20 characters long.").color(colorFailure));
                        return Command.SINGLE_SUCCESS;
                    }
                    PlayerConfig.forPlayer(player).setPronouns(value);
                    player.sendMessage(Component.text("Set pronouns to %s successfully.".formatted(value), colorSuccess));
                    sendNameplates(player);
                    return Command.SINGLE_SUCCESS;
                })))
            .then(Commands.literal("color")
                .executes(ctx -> {
                    Player player = requirePlayer(ctx);
                    if (player == null) return Command.SINGLE_SUCCESS;

                    // clear color
                    PlayerConfig.forPlayer(player).setNameColor(null);
                    player.sendMessage(Component.text("Unset name color successfully.", colorSuccess));
                    sendNameplates(player);
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.argument("value", StringArgumentType.word()).executes(ctx -> {
                    Player player = requirePlayer(ctx);
                    if (player == null) return Command.SINGLE_SUCCESS;
                    String value = StringArgumentType.getString(ctx, "value");

                    // update color
                    if (value.length() != 6) {
                        player.sendMessage(Component.text("Colors must be a hex color without the first #. For example, \"ffffff\".").color(colorFailure));
                        return Command.SINGLE_SUCCESS;
                    } else {
                        int realValue;
                        try {
                            realValue = Integer.parseInt(value, 16);
                        } catch (NumberFormatException e) {
                            player.sendMessage(Component.text("This isn't a valid color: #" + value).color(colorFailure));
                            return Command.SINGLE_SUCCESS;
                        }
                        PlayerConfig.forPlayer(player).setNameColor(TextColor.color(realValue));
                        player.sendMessage(Component.text("Set pronouns to %s successfully.".formatted(value), colorSuccess));
                        sendNameplates(player);
                    }
                    return Command.SINGLE_SUCCESS;
                })))
            .build();
    }

    public static Player requirePlayer(CommandContext<CommandSourceStack> ctx) {
        if (ctx.getSource().getExecutor() == null || !(ctx.getSource().getExecutor() instanceof Player player)) {
            ctx.getSource().getSender().sendRichMessage("<red>Executor must be a player!");
            return null;
        }
        player.sendMessage("");
        return player;
    }

    public static void sendNameplates(Player player) {
        PlayerConfig config = PlayerConfig.forPlayer(player);
        player.sendMessage(Component.text("Preview:").color(colorSuccess));
        player.sendMessage(Component.text("- Current nameplate: ").color(colorSuccess)
                .append(config.buildNameComponent()));
        player.sendMessage(Component.text("- Current nameplate (short): ").color(colorSuccess)
                .append(config.buildNameComponentShort()));
        player.sendMessage(Component.text("- Current discord name: ").color(colorSuccess)
                .append(Component.text(config.buildNameString()).color(NamedTextColor.WHITE)));
        player.sendMessage(Component.text("- Current discord name (short): ").color(colorSuccess)
                .append(Component.text(config.buildNameStringShort()).color(NamedTextColor.WHITE)));
        config.updatePlayer();
    }
}
