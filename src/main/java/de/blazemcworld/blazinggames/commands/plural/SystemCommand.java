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

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;

import de.blazemcworld.blazinggames.commands.boilerplate.CommandHelper;
import de.blazemcworld.blazinggames.commands.finalizers.ShowNameplatesFinalizer;
import de.blazemcworld.blazinggames.commands.middleware.EmptyMessageMiddleware;
import de.blazemcworld.blazinggames.commands.middleware.RequireSystemMiddleware;
import de.blazemcworld.blazinggames.players.FrontManager;
import de.blazemcworld.blazinggames.players.PlayerConfig;
import de.blazemcworld.blazinggames.players.ServerPlayerConfig;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class SystemCommand {
    public static final TextColor color = TextColor.color(0xEDC4DF);
    public static final CommandHelper configHelper = CommandHelper.builder()
        .middleware(new RequireSystemMiddleware(color))
        .middleware(new EmptyMessageMiddleware())
        .finalizer(new ShowNameplatesFinalizer(true, color))
        .ignoreExecutor(true)
        .build();

    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("system")
            .then(Commands.literal("enable").executes(CommandHelper.getDefaultIgnoreExecutor().requirePlayer((ctx, player) -> handleToggle(player, true))))
            .then(Commands.literal("disable").executes(CommandHelper.getDefaultIgnoreExecutor().requirePlayer((ctx, player) -> handleToggle(player, false))))

            .then(Commands.literal("name")
                .executes(configHelper.requirePlayer((ctx, player) -> handleConfig(ctx, player, true, false)))
                .then(Commands.argument("value", StringArgumentType.greedyString()).executes(configHelper.requirePlayer((ctx, player) -> handleConfig(ctx, player, false, false)))))
            .then(Commands.literal("tag")
                .executes(configHelper.requirePlayer((ctx, player) -> handleConfig(ctx, player, true, true)))
                .then(Commands.argument("value", StringArgumentType.greedyString()).executes(configHelper.requirePlayer((ctx, player) -> handleConfig(ctx, player, false, true)))))
            .build();
    }

    public static void handleToggle(Player player, boolean enable) {
        PlayerConfig config = PlayerConfig.forPlayer(player);
        if (config.isPlural() != enable) {
            if (enable) {
                config.setPlural(true);
                player.sendMessage(Component.text("Marked this account as a plural system.", color));
            } else {
                config.setPlural(false);
                FrontManager.clearFront(player.getUniqueId());
                PlayerConfig.forPlayer(player).updatePlayer();
                player.sendMessage(Component.text("Unmarked this account as a plural system.", color));
            }
        } else {
            player.sendMessage(Component.text("This account is already " + (enable ? "marked" : "not marked") + " as a plural system.", color));
        }
    }

    public static void handleConfig(CommandContext<CommandSourceStack> ctx, Player player, boolean clear, boolean isTag) {
        String value = clear ? null : StringArgumentType.getString(ctx, "value");
        PlayerConfig config = PlayerConfig.forPlayer(player);

        if (value == null) {
            if (isTag) {
                config.setSystemTag(null);
                PlayerConfig.forPlayer(player).updatePlayer();
                player.sendMessage(Component.text("Cleared this system tag successfully.", color));
            } else {
                config.setSystemName(null);
                PlayerConfig.forPlayer(player).updatePlayer();
                player.sendMessage(Component.text("Cleared this system name successfully.", color));
            }
        } else {
            if (!ServerPlayerConfig.isLengthValid(value)) {
                player.sendMessage(Component.text((isTag ? "System tags " : "System names ") + "must be between " + ServerPlayerConfig.minLength() + " and " + ServerPlayerConfig.maxLength() + " characters long.", color));
                return;
            }

            if (isTag) {
                config.setSystemTag(value);
                PlayerConfig.forPlayer(player).updatePlayer();
                player.sendMessage(Component.text("Set this system tag to \"" + value + "\".", color));
            } else {
                config.setSystemName(value);
                PlayerConfig.forPlayer(player).updatePlayer();
                player.sendMessage(Component.text("Set this system name to \"" + value + "\".", color));
            }
        }
    }
}