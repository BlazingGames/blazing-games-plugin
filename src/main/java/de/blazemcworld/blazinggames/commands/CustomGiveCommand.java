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

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.items.CustomItem;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import de.blazemcworld.blazinggames.items.ItemProviders;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;

import java.text.ParseException;

public class CustomGiveCommand {
    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("customgive")
            .requires(ctx -> ctx.getSender().hasPermission("blazinggames.customgive"))
            .then(Commands.argument("item", StringArgumentType.word())
                .executes(ctx -> {
                    give(
                        ctx.getSource().getSender(), ctx.getSource().getExecutor(),
                        StringArgumentType.getString(ctx, "item"),
                        1, ""
                    );
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.argument("count", IntegerArgumentType.integer(1, 64))
                    .executes(ctx -> {
                        give(
                            ctx.getSource().getSender(), ctx.getSource().getExecutor(),
                            StringArgumentType.getString(ctx, "item"),
                            IntegerArgumentType.getInteger(ctx, "count"),
                            ""
                        );
                        return Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("context", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            give(
                                ctx.getSource().getSender(), ctx.getSource().getExecutor(),
                                StringArgumentType.getString(ctx, "item"),
                                IntegerArgumentType.getInteger(ctx, "count"),
                                StringArgumentType.getString(ctx, "context")
                            );
                            return Command.SINGLE_SUCCESS;
                        })
        ))).build();
    }

    public static void give(CommandSender sender, Entity executor, String id, int count, String rawContext) {
        if (executor == null || !(executor instanceof Player player)) {
            sender.sendRichMessage("<red>The executor is not a player!");
            return;
        }
        
        CustomItem<?> itemType = ItemProviders.instance.getByKey(BlazingGames.get().key(id));

        if(itemType == null) {
            sender.sendMessage(Component.text("Unknown custom item: " + id + "!").color(NamedTextColor.RED));
            return;
        }

        try {
            ItemStack item = itemType.createWithRawContext(player, rawContext);
            item.setAmount(count);

            player.getInventory().addItem(item);
        } catch (ParseException parsingException) {
            sender.sendMessage(Component.text("Parsing Exception: "
                            + parsingException.getMessage()
                            + " at " + parsingException.getErrorOffset())
                    .color(NamedTextColor.RED));
            BlazingGames.get().debugLog(parsingException);
        } catch(Exception exception) {
            sender.sendMessage(Component.text(exception.getClass().getName() + ": "
                            + exception.getMessage())
                    .color(NamedTextColor.RED));
            BlazingGames.get().debugLog(exception);
        }
    }
}