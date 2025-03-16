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
package de.blazemcworld.blazinggames.commands.templates;

import org.bukkit.entity.Player;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import de.blazemcworld.blazinggames.commands.boilerplate.CommandHelper;
import de.blazemcworld.blazinggames.commands.boilerplate.CommandHelperBuilder;
import de.blazemcworld.blazinggames.commands.boilerplate.MiddlewareFunction;
import de.blazemcworld.blazinggames.commands.finalizers.ShowNameplatesFinalizer;
import de.blazemcworld.blazinggames.commands.middleware.EmptyMessageMiddleware;
import de.blazemcworld.blazinggames.players.DisplayConfigurationEditor;
import de.blazemcworld.blazinggames.players.DisplayTagProperty;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class DisplayCommandBuilder {
    public static LiteralArgumentBuilder<CommandSourceStack> tree(
        final TextColor colorSuccess,
        final TextColor colorFailure,
        final DisplayCommandBuilderFunction function,
        final ShowNameplatesFinalizer finalizer,
        final MiddlewareFunction... middleware
    ) {
        CommandHelperBuilder builder = CommandHelper.builder();
        for (MiddlewareFunction middlewareFunction : middleware) {
            builder = builder.middleware(middlewareFunction);
        }
        final CommandHelper helper = builder
            .middleware(new EmptyMessageMiddleware())
            .finalizer(finalizer)
            .ignoreExecutor(true)
            .build();

        return Commands.literal("display")
            // reset commands
            .then(Commands.literal("reset")
                
                // reset help text
                .executes(helper.requirePlayer((ctx, player) -> {
                    player.sendMessage(Component.text("To reset all display settings, run ").color(colorSuccess)
                        .append(Component.text("/display reset confirm").color(colorFailure)));
                }))

                // reset confirmation
                .then(Commands.literal("confirm").executes(helper.requirePlayer((ctx, player) -> {
                    DisplayConfigurationEditor editor = function.apply(ctx, player);
                    if (editor == null) {
                        return;
                    }

                    for (DisplayTagProperty property : DisplayTagProperty.values()) {
                        try {
                            editor.setProperty(property, null);
                        } catch (IllegalArgumentException e) {
                            player.sendMessage(Component.text("Failed to reset " + property.pretty + ": " + e.getMessage(), colorFailure));
                            return;
                        }
                    }

                    player.sendMessage(Component.text("Reset all settings successfully.").color(colorSuccess));
                }))))

            // display name
            .then(Commands.literal("name")
                .executes(helper.requirePlayer((ctx, player) -> handle(colorSuccess, colorFailure, function, ctx, player, DisplayTagProperty.DISPLAY_NAME, true)))
                .then(Commands.argument("value", StringArgumentType.greedyString())
                    .executes(helper.requirePlayer((ctx, player) -> handle(colorSuccess, colorFailure, function, ctx, player, DisplayTagProperty.DISPLAY_NAME, false)))))

            // pronouns
            .then(Commands.literal("pronouns")
                .executes(helper.requirePlayer((ctx, player) -> handle(colorSuccess, colorFailure, function, ctx, player, DisplayTagProperty.PRONOUNS, true)))
                .then(Commands.argument("value", StringArgumentType.greedyString())
                    .executes(helper.requirePlayer((ctx, player) -> handle(colorSuccess, colorFailure, function, ctx, player, DisplayTagProperty.PRONOUNS, false)))))
            
            // color
            .then(Commands.literal("color")
                .executes(helper.requirePlayer((ctx, player) -> handle(colorSuccess, colorFailure, function, ctx, player, DisplayTagProperty.NAME_COLOR, true)))
                .then(Commands.argument("value", StringArgumentType.greedyString())
                    .executes(helper.requirePlayer((ctx, player) -> handle(colorSuccess, colorFailure, function, ctx, player, DisplayTagProperty.NAME_COLOR, false)))));
    }

    public static void handle(TextColor colorSuccess, TextColor colorFailure, DisplayCommandBuilderFunction function, CommandContext<CommandSourceStack> ctx, Player player, DisplayTagProperty type, boolean clear) {
        DisplayConfigurationEditor editor = function.apply(ctx, player);
        if (editor == null) {
            return;
        }

        String value;
        if (clear) {
            value = null;
        } else {
            value = StringArgumentType.getString(ctx, "value");
        }

        try {
            editor.setProperty(type, value);
        } catch (IllegalArgumentException e) {
            ctx.getSource().getSender().sendMessage(Component.text("Failed to change " + type.pretty + ": " + e.getMessage(), colorFailure));
            return;
        }

        String keyword = clear ? "Cleared " : "Set ";
        ctx.getSource().getSender().sendMessage(Component.text(keyword + type.pretty + " successfully.", colorSuccess));
    }

    public static interface DisplayCommandBuilderFunction {
        DisplayConfigurationEditor apply(CommandContext<CommandSourceStack> ctx, Player player);
    }
}
