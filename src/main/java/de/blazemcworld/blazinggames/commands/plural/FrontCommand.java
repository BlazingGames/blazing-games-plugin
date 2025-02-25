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

public class FrontCommand {
    public static final TextColor color = TextColor.color(0xEDC4DF);

    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("front").executes(ctx -> {
            if (!(ctx.getSource().getSender() instanceof Player player)) {
                ctx.getSource().getSender().sendMessage("You must be a player to use this command!");
                return Command.SINGLE_SUCCESS;
            }

            PlayerConfig config = PlayerConfig.forPlayer(player);
            if (!config.isPlural()) {
                player.sendMessage(Component.text("This account is not a plural system.", color));
                return Command.SINGLE_SUCCESS;
            }

            FrontManager.clearFront(player.getUniqueId());
            player.sendMessage(Component.text("Cleared front successfully.", color));
            return Command.SINGLE_SUCCESS;
        }).then(Commands.argument("member", StringArgumentType.greedyString()).executes(ctx -> {
            String member = StringArgumentType.getString(ctx, "member");
            if (!(ctx.getSource().getSender() instanceof Player player)) {
                ctx.getSource().getSender().sendMessage("You must be a player to use this command!");
                return Command.SINGLE_SUCCESS;
            }

            PlayerConfig config = PlayerConfig.forPlayer(player);
            if (!config.isPlural()) {
                player.sendMessage(Component.text("This account is not a plural system.", color));
                return Command.SINGLE_SUCCESS;
            }

            if (config.getPluralConfig().getMember(member) == null) {
                player.sendMessage(Component.text("There is no member with this name.", color));
                return Command.SINGLE_SUCCESS;
            }

            FrontManager.updateFront(player.getUniqueId(), member);
            player.sendMessage(Component.text("Set front to \"" + member + "\" successfully.", color));
            return Command.SINGLE_SUCCESS;
        })).build();
    }
}