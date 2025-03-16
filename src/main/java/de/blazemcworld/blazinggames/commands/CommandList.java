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

import java.util.List;
import java.util.function.Predicate;

import org.bukkit.configuration.file.FileConfiguration;

import com.mojang.brigadier.tree.LiteralCommandNode;

import de.blazemcworld.blazinggames.commands.plural.FrontCommand;
import de.blazemcworld.blazinggames.commands.plural.MemberCommand;
import de.blazemcworld.blazinggames.commands.plural.SystemCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;

public enum CommandList {
    // dev note: enum names don't matter
    // also keep these alphabetically sorted please :3

    CUSTOM_ENCHANT(CustomEnchantCommand.command(), "Enchants the main hand item with a specific custom enchantment."),
    CUSTOM_GIVE(CustomGiveCommand.command(), "Gives you a specific custom item with a specified count."),
    DISCORD_WHITELIST(DiscordWhitelistCommand.command(), "Modifies the discord whitelist at the admin level."),
    DISPLAY(DisplayCommand.command(), "Change nameplate display settings for your player.", "nick", "config"),
    KILLME(KillMeCommand.command(), "Kills you. Painfully.", "suicide"),
    PLAYTIME(PlaytimeCommand.command(), "See how much time you and your friends have wasted on this stupid server."),
    SET_ALTAR(SetAltarCommand.command(), "Set altar with specific level at current location."),
    UNLINK(UnlinkCommand.command(), "Unlinks your account from your discord account. This also removes you from the whitelist."),

    // plural package
    FRONT(FrontCommand.command(), "Change who is fronting. This is used for autoproxy."),
    MEMBER(MemberCommand.command(), "Manage members within your system."),
    SYSTEM(SystemCommand.command(), "Change settings about your system."),

    ;

    public final Predicate<FileConfiguration> defaultPredicate = obj -> true;
    public final LiteralCommandNode<CommandSourceStack> command;
    public final String description;
    public final List<String> aliases;
    CommandList(LiteralCommandNode<CommandSourceStack> command, String description, String... aliases) {
        this.command = command;
        this.description = description;
        this.aliases = List.of(aliases);
    }
}