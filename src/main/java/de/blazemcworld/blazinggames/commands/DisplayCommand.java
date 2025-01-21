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

public class DisplayCommand implements CommandExecutor, TabCompleter {
    public static final TextColor colorSuccess = TextColor.color(0xD1FCDF);
    public static final TextColor colorFailure = TextColor.color(0xFC9588);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command!")
                    .color(NamedTextColor.RED));
            return false;
        }
        PlayerConfig config = PlayerConfig.forPlayer(player.getUniqueId());
        player.sendMessage("");

        if (args.length < 1) {
            // show help text
            player.sendMessage(Component.text("Usage: " + command.getUsage()).color(colorSuccess));
            sendNameplates(player);
            return true;
        }

        String param = args[0];
        String valueStr = (args.length > 1) ? Arrays.stream(args).skip(1).collect(Collectors.joining(" ")) : null;
        String value = (valueStr == null || valueStr.isBlank()) ? null : valueStr;
        String pretty;
        switch (param) {
            case "name":
                if (value != null && (value.length() < 2 || value.length() > 40)) {
                    player.sendMessage(Component.text("Display name must be between 2 and 40 characters long.").color(colorFailure));
                    return true;
                }
                config.setDisplayName(value);
                pretty = "display name";
                break;
            case "pronouns":
                if (value != null && (value.length() < 2 || value.length() > 20)) {
                    player.sendMessage(Component.text("Pronouns must be between 2 and 20 characters long.").color(colorFailure));
                    return true;
                }
                config.setPronouns(value);
                pretty = "pronouns";
                break;
            case "color":
                if (value == null) {
                    config.setNameColor(null);
                    player.sendMessage(Component.text("Unset name color.").color(colorSuccess));
                    sendNameplates(player);
                    return true;
                } else if (value.length() != 6) {
                    player.sendMessage(Component.text("Colors must be a hex color without the first #. For example, \"ffffff\".").color(colorFailure));
                    return true;
                } else {
                    int realValue;
                    try {
                        realValue = Integer.parseInt(value, 16);
                    } catch (NumberFormatException e) {
                        player.sendMessage(Component.text("This isn't a valid color: #" + value).color(colorFailure));
                        return true;
                    }
                    config.setNameColor(TextColor.color(realValue));
                }   
                pretty = "name color";
                break;
            case "reset":
                if ("confirm".equals(value)) {
                    config.setDisplayName(null);
                    config.setPronouns(null);
                    config.setNameColor(null);
                    player.sendMessage(Component.text("Reset all settings successfully.").color(colorSuccess));
                    sendNameplates(player);
                } else {
                    player.sendMessage(Component.text("To reset all display settings, run ").color(colorSuccess)
                        .append(Component.text("/display reset confirm").color(colorFailure)));
                }
                return true;
            default:
                player.sendMessage(Component.text("Unknown parameter: " + param).color(colorFailure));
                return true;
        }

        if (value == null) {
            player.sendMessage(Component.text("Unset " + pretty + " successfully.").color(colorSuccess));
        } else {
            player.sendMessage(Component.text("Set " + pretty + " to ").color(colorSuccess)
                .append(Component.text(value).color(NamedTextColor.WHITE))
                .append(Component.text(" successfully.").color(colorSuccess)));
        }
        sendNameplates(player);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("name", "pronouns", "color");
        }
        return List.of();
    }

    private static void sendNameplates(Player player) {
        PlayerConfig config = PlayerConfig.forPlayer(player.getUniqueId());
        player.sendMessage(Component.text("Preview:").color(colorSuccess));
        player.sendMessage(Component.text("- Current nameplate: ").color(colorSuccess)
            .append(config.buildNameComponent(player.getName(), player.isOp())));
        player.sendMessage(Component.text("- Current nameplate (short): ").color(colorSuccess)
            .append(config.buildNameComponentShort(player.getName(), player.isOp())));
        player.sendMessage(Component.text("- Current discord name: ").color(colorSuccess)
            .append(Component.text(config.buildNameString(player.getName(), player.isOp())).color(NamedTextColor.WHITE)));
        player.sendMessage(Component.text("- Current discord name (short): ").color(colorSuccess)
            .append(Component.text(config.buildNameStringShort(player.getName())).color(NamedTextColor.WHITE)));
        config.updatePlayer(player);
    }
}
