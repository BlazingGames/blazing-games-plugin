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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.blazemcworld.blazinggames.utils.PlayerConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class ConfigCommand implements CommandExecutor, TabCompleter {
    String[] values = {"display", "pronouns", "color"};

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(Component.text("Only players can use this command!")
                    .color(NamedTextColor.RED));
            return false;
        }

        if (args.length < 1) {
            return false;
        }

        String param = args[0];
        String value = (args.length > 1) ? Arrays.stream(args).skip(1).collect(Collectors.joining(" "))
            : null;

        PlayerConfig config = PlayerConfig.forPlayer(p.getUniqueId());
        switch (param) {
            case "display":
                if (!enforceParam(sender, param, value, 1, 36)) return false;
                config.setDisplayName(value);
                break;
            case "pronouns":
                if (!enforceParam(sender, param, value, 1, 16)) return false;
                config.setPronouns(value);
                break;
            case "color":
                if (!enforceParam(sender, param, value, 6, 6)) return false;
                if (value == null || value.isBlank()) {
                    config.setNameColor(null);
                } else {
                    int realValue;
                    try {
                        realValue = Integer.parseInt(value, 16);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(Component.text("Invalid color: #" + value).color(NamedTextColor.RED));
                        return false;
                    }
                    config.setNameColor(TextColor.color(realValue));
                }
                break;
            default:
                sender.sendMessage(Component.text("Unknown parameter: " + param).color(NamedTextColor.RED));
                return false;
        }
        sendSuccess(sender, param, value);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList(values);
        }
        return List.of();
    }

    private static void sendSuccess(CommandSender sender, String param, String value) {
        if (value == null || value.isBlank()) {
            sender.sendMessage(Component.text("Cleared " + param).color(NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("Set " + param + " to " + value).color(NamedTextColor.GREEN));
        }
    }

    private static boolean enforceParam(CommandSender sender, String param, String value, int minChars, int maxChars) {
        if (value == null || value.isBlank()) {
            // return for unset
            return true;
        }

        if (value.length() < minChars || value.length() > maxChars) {
            if (minChars == maxChars) {
                sender.sendMessage(Component.text("Parameter " + param + " must be exactly " + minChars + " chars long!").color(NamedTextColor.RED));
            } else {
                sender.sendMessage(Component.text("Parameter " + param + " must be between " + minChars + " and " + maxChars + " chars long!").color(NamedTextColor.RED));
            }
            return false;
        }
        return true;

    }
}
