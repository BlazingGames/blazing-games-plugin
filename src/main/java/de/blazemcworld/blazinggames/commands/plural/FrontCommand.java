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
import de.blazemcworld.blazinggames.commands.middleware.RequireMemberMiddleware;
import de.blazemcworld.blazinggames.commands.middleware.RequireSystemMiddleware;
import de.blazemcworld.blazinggames.players.FrontManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class FrontCommand {
    public static final TextColor color = TextColor.color(0xEDC4DF);
    public static final CommandHelper clearHelper = CommandHelper.builder()
        .middleware(new RequireSystemMiddleware(color))
        .ignoreExecutor(true)
        .build();
    public static final CommandHelper setHelper = CommandHelper.builder()
        .middleware(new RequireSystemMiddleware(color))
        .middleware(new RequireMemberMiddleware("member", color))
        .ignoreExecutor(true)
        .build();

    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("front")
            .executes(clearHelper.requirePlayer(FrontCommand::handleClear))
            .then(Commands.argument("member", StringArgumentType.greedyString())
                .executes(setHelper.requirePlayer(FrontCommand::handleSet))).build();
    }

    public static void handleSet(CommandContext<CommandSourceStack> ctx, Player player) {
        String member = StringArgumentType.getString(ctx, "member");
        FrontManager.updateFront(player, member);
        player.sendMessage(Component.text("Set front to \"" + member + "\" successfully.", color));
    }

    public static void handleClear(CommandContext<CommandSourceStack> ctx, Player player) {
        FrontManager.clearFront(player);
        player.sendMessage(Component.text("Cleared front successfully.", color));
    }
}