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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.commands.boilerplate.CommandHelper;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentHelper;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentWrapper;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentWrappers;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CustomEnchantCommand {
    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("customenchant")
            .requires(ctx -> ctx.getSender().hasPermission("blazinggames.customenchant"))
            .then(Commands.argument("enchantment", StringArgumentType.word())
                .executes(CommandHelper.getDefault().requirePlayer((ctx, player) -> enchant(ctx.getSource().getSender(), player, StringArgumentType.getString(ctx, "enchantment"), 1)))
                .then(Commands.argument("level", IntegerArgumentType.integer(1, Short.MAX_VALUE))
                    .executes(CommandHelper.getDefault().requirePlayer((ctx, player) -> enchant(ctx.getSource().getSender(), player, StringArgumentType.getString(ctx, "enchantment"), IntegerArgumentType.getInteger(ctx, "level"))))
        )).build();
    }

    public static void enchant(CommandSender sender, Player player, String enchantmentStr, int level) {
        EnchantmentWrapper enchantment = EnchantmentWrappers.instance.getByKey(BlazingGames.get().key(enchantmentStr));

        if (enchantment == null) {
            sender.sendMessage(Component.text("Unknown custom enchantment: " + enchantmentStr + "!").color(NamedTextColor.RED));
            return;
        }

        ItemStack tool = EnchantmentHelper.enchantTool(player.getInventory().getItemInMainHand(), enchantment, level);
        player.getInventory().setItemInMainHand(tool);
    }
}