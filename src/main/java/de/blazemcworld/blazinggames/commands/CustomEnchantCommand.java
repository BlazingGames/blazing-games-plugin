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
import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantment;
import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantments;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentHelper;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
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

public class CustomEnchantCommand {
    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("customenchant")
            .requires(ctx -> ctx.getSender().hasPermission("blazinggames.customenchant"))
            .then(Commands.argument("enchantment", StringArgumentType.word())
                .executes(ctx -> {
                    enchant(ctx.getSource().getSender(), ctx.getSource().getExecutor(), StringArgumentType.getString(ctx, "enchantment"), 1);
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.argument("level", IntegerArgumentType.integer(1, Short.MAX_VALUE))
                    .executes(ctx -> {
                        enchant(ctx.getSource().getSender(), ctx.getSource().getExecutor(), StringArgumentType.getString(ctx, "enchantment"), IntegerArgumentType.getInteger(ctx, "level"));
                        return Command.SINGLE_SUCCESS;
                    })
        )).build();
    }

    public static void enchant(CommandSender sender, Entity executor, String enchantmentStr, int level) {
        if (executor == null || !(executor instanceof Player player)) {
            sender.sendRichMessage("<red>The executor is not a player!");
            return;
        }

        CustomEnchantment enchantment = CustomEnchantments.getByKey(BlazingGames.get().key(enchantmentStr));

        if (enchantment == null) {
            sender.sendMessage(Component.text("Unknown custom enchantment: " + enchantmentStr + "!").color(NamedTextColor.RED));
            return;
        }

        ItemStack tool = EnchantmentHelper.setCustomEnchantment(player.getInventory().getItemInMainHand(), enchantment, level);
        player.getInventory().setItemInMainHand(tool);
    }
}