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
import de.blazemcworld.blazinggames.items.ItemProviders;
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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CustomGiveCommand implements CommandExecutor, TabCompleter {

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

        CustomItem<?> itemType = ItemProviders.instance.getByKey(BlazingGames.get().key(strings[0]));
        int count = 1;

        if(itemType == null)
        {
            commandSender.sendMessage(Component.text("Unknown custom item: " + strings[0] + "!").color(NamedTextColor.RED));
            return true;
        }

        if(strings.length > 1) {
            count = Integer.parseInt(strings[1]);
        }

        String rawContext = "";

        if(strings.length > 2)
        {
            List<String> contextStrings = new ArrayList<>(List.of(strings));
            contextStrings.removeFirst();
            contextStrings.removeFirst();

            rawContext = String.join(" ", contextStrings);
        }

        try {
            ItemStack item = itemType.createWithRawContext(p, rawContext);
            item.setAmount(count);

            p.getInventory().addItem(item);
        }
        catch(ParseException parsingException) {
            commandSender.sendMessage(Component.text("Parsing Exception: "
                            + parsingException.getMessage()
                            + " at " + parsingException.getErrorOffset())
                    .color(NamedTextColor.RED));
            BlazingGames.get().debugLog(parsingException);
        }
        catch(Exception exception) {
            commandSender.sendMessage(Component.text(exception.getClass().getName() + ": "
                            + exception.getMessage())
                    .color(NamedTextColor.RED));
            BlazingGames.get().debugLog(exception);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command,
                                                @NotNull String s, @NotNull String[] strings) {
        List<String> tabs = new ArrayList<>();

        if(strings.length == 1) {
            ItemProviders.instance.list().forEach(itemType -> tabs.add(itemType.getKey().getKey()));
        }

        return tabs;
    }
}