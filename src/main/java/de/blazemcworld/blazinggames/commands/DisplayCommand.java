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

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;

import de.blazemcworld.blazinggames.commands.boilerplate.CommandHelper;
import de.blazemcworld.blazinggames.commands.finalizers.ShowNameplatesFinalizer;
import de.blazemcworld.blazinggames.commands.middleware.EmptyMessageMiddleware;
import de.blazemcworld.blazinggames.commands.middleware.NoFrontMiddleware;
import de.blazemcworld.blazinggames.players.PlayerConfig;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class DisplayCommand {
    public static final TextColor colorSuccess = TextColor.color(0xD1FCDF);
    public static final TextColor colorFailure = TextColor.color(0xFC9588);

    public static final CommandHelper helper = CommandHelper.builder()
        .middleware(new NoFrontMiddleware(colorFailure))
        .middleware(new EmptyMessageMiddleware())
        .finalizer(new ShowNameplatesFinalizer(false, colorSuccess))
        .ignoreExecutor(true)
        .build();

    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("display")

            // help text
            .executes(helper.requirePlayer((ctx, player) -> {
                player.sendMessage(Component.text("Usage: /display [name|pronouns|color] [value]").color(colorSuccess));
            }))

            // reset commands
            .then(Commands.literal("reset")
                
                // reset help text
                .executes(helper.requirePlayer((ctx, player) -> {
                    player.sendMessage(Component.text("To reset all display settings, run ").color(colorSuccess)
                        .append(Component.text("/display reset confirm").color(colorFailure)));
                }))

                // reset confirmation
                .then(Commands.literal("confirm").executes(helper.requirePlayer((ctx, player) -> {
                    PlayerConfig config = PlayerConfig.forPlayer(player);
                    config.setDisplayName(null);
                    config.setPronouns(null);
                    config.setNameColor(null);
                    player.sendMessage(Component.text("Reset all settings successfully.").color(colorSuccess));
                }))))

            // display name
            .then(Commands.literal("name")
                .executes(helper.requirePlayer((ctx, player) -> handle(ctx, player, PropertyType.DISPLAY_NAME, true)))
                .then(Commands.argument("value", StringArgumentType.greedyString())
                    .executes(helper.requirePlayer((ctx, player) -> handle(ctx, player, PropertyType.DISPLAY_NAME, false)))))

            // pronouns
            .then(Commands.literal("pronouns")
                .executes(helper.requirePlayer((ctx, player) -> handle(ctx, player, PropertyType.PRONOUNS, true)))
                .then(Commands.argument("value", StringArgumentType.greedyString())
                    .executes(helper.requirePlayer((ctx, player) -> handle(ctx, player, PropertyType.PRONOUNS, false)))))
            
            // color
            .then(Commands.literal("color")
                .executes(helper.requirePlayer((ctx, player) -> handle(ctx, player, PropertyType.COLOR, true)))
                .then(Commands.argument("value", StringArgumentType.greedyString())
                    .executes(helper.requirePlayer((ctx, player) -> handle(ctx, player, PropertyType.COLOR, false)))))

            .build();
    }

    public static void handle(CommandContext<CommandSourceStack> ctx, Player player, PropertyType type, boolean clear) {
        String value;
        if (clear) {
            value = null;
        } else {
            value = StringArgumentType.getString(ctx, "value");
        }

        String result = setProperty(PlayerConfig.forPlayer(player), type, value);
        if (result != null) {
            ctx.getSource().getSender().sendMessage(Component.text("Failed to change " + type.pretty + ": " + result, colorFailure));
            return;
        }

        String keyword = clear ? "Cleared " : "Set ";
        ctx.getSource().getSender().sendMessage(Component.text(keyword + type.pretty + " successfully.", colorSuccess));
    }

    public static enum PropertyType {
        DISPLAY_NAME("display name"),
        PRONOUNS("pronouns"),
        COLOR("name color"),

        ;

        public final String pretty;
        PropertyType(String pretty) {
            this.pretty = pretty;
        }
    }

    public static String setProperty(PlayerConfig config, PropertyType type, String value) {
        if (value == null) {
            switch (type) {
                case DISPLAY_NAME:
                    config.setDisplayName(null);
                    return null;
                case PRONOUNS:
                    config.setPronouns(null);
                    return null;
                case COLOR:
                    config.setNameColor(null);
                    return null;
            }
            return null;
        }

        switch (type) {
            case DISPLAY_NAME:
                if (value.length() < 2 || value.length() > 40) {
                    return "display names must be between 2 and 40 characters long.";
                }
                config.setDisplayName(value);
                return null;
            case PRONOUNS:
                if (value.length() < 2 || value.length() > 30) {
                    return "pronouns must be between 2 and 30 characters long.";
                }
                config.setPronouns(value);
                return null;
            case COLOR:
                if (value.length() != 6) {
                    return "colors must be a hex color without the first #. For example, \"ffffff\".";
                }
                int intValue;
                try {
                    intValue = Integer.parseInt(value, 16);
                } catch (NumberFormatException e) {
                    return "#" + value + " is not a valid color.";
                }
                config.setNameColor(TextColor.color(intValue));
                return null;
            default:
                return "Unknown property type. This is a bug.";
        }
    }
}
