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

import com.mojang.brigadier.tree.LiteralCommandNode;

import de.blazemcworld.blazinggames.commands.boilerplate.CommandHelper;
import de.blazemcworld.blazinggames.commands.finalizers.ShowNameplatesFinalizer;
import de.blazemcworld.blazinggames.commands.middleware.EmptyMessageMiddleware;
import de.blazemcworld.blazinggames.commands.middleware.NoFrontMiddleware;
import de.blazemcworld.blazinggames.commands.templates.DisplayCommandBuilder;
import de.blazemcworld.blazinggames.players.PlayerConfig;
import io.papermc.paper.command.brigadier.CommandSourceStack;
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
        return DisplayCommandBuilder.tree(
            "/",
            colorSuccess,
            colorFailure,
            (ctx, player) -> PlayerConfig.forPlayer(player),
            new ShowNameplatesFinalizer(false, colorSuccess),
            new NoFrontMiddleware(colorFailure)
        ).executes(helper.requirePlayer((ctx, player) -> {
            player.sendMessage(Component.text("Usage: /display [name|pronouns|color] [value]").color(colorSuccess));
        })).build();
    }
}
