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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CustomEnchantCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command,
                             @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player p)) {
            commandSender.sendMessage(Component.text("Only players can use this command!")
                    .color(NamedTextColor.RED));
            return true;
        }

        if(strings.length < 1) {
            CommandHelper.sendUsage(commandSender, command);
            return true;
        }

        if(strings.length > 2) {
            CommandHelper.sendUsage(commandSender, command);
            return true;
        }

        CustomEnchantment enchantment = CustomEnchantments.getByKey(BlazingGames.get().key(strings[0]));
        int level = 1;

        if(enchantment == null)
        {
            commandSender.sendMessage(Component.text("Unknown custom enchantment: " + strings[0] + "!"));
            return true;
        }

        if(strings.length > 1) {
            level = Integer.parseInt(strings[1]);
        }

        ItemStack tool = EnchantmentHelper.setCustomEnchantment(p.getInventory().getItemInMainHand(), enchantment, level);
        p.getInventory().setItemInMainHand(tool);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command,
                                                @NotNull String s, @NotNull String[] strings) {
        List<String> tabs = new ArrayList<>();

        if(strings.length == 1) {
            CustomEnchantments.list().forEach(enchantment -> tabs.add(enchantment.getKey().getKey()));
        }

        return tabs;
    }
}