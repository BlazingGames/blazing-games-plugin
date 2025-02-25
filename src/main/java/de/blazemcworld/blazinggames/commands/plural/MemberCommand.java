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

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.utils.FrontManager;
import de.blazemcworld.blazinggames.utils.MemberData;
import de.blazemcworld.blazinggames.utils.Pair;
import de.blazemcworld.blazinggames.utils.PlayerConfig;
import de.blazemcworld.blazinggames.utils.PluralConfig;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class MemberCommand {
    public static final TextColor color = TextColor.color(0xEDC4DF);

    public static String basicChecks(Player player, String name) {
        if (name == null) {
            return "Name must not be null..?";
        }

        if (name.length() < 3 || name.length() > 40) {
            return "Name must be between 3 and 40 characters.";
        }

        PlayerConfig config = PlayerConfig.forPlayer(player);
        if (!config.isPlural()) {
            return "Enable plurality with \"/system enable\" first.";
        }

        return null;
    }

    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("member").then(Commands.literal("list").executes(ctx -> {
            if (!(ctx.getSource().getSender() instanceof Player player)) {
                ctx.getSource().getSender().sendMessage("You must be a player to use this command!");
                return Command.SINGLE_SUCCESS;
            }

            PlayerConfig config = PlayerConfig.forPlayer(player);
            if (!config.isPlural()) {
                player.sendMessage(Component.text("Enable plurality with \"/system enable\" first.", color));
                return Command.SINGLE_SUCCESS;
            }

            PluralConfig pluralConfig = config.getPluralConfig();
            if (pluralConfig.getMembers().isEmpty()) {
                player.sendMessage(Component.text("No members found.", color));
                return Command.SINGLE_SUCCESS;
            }

            player.sendMessage(Component.text("Members:", color));
            for (MemberData member : pluralConfig.getMembers()) {
                player.sendMessage(Component.text("- " + member.name, color));
            }

            return Command.SINGLE_SUCCESS;
        }))
        
        
        
        .then(Commands.argument("name", StringArgumentType.string())



            .then(Commands.literal("create").executes(ctx -> {
                String name = StringArgumentType.getString(ctx, "name");
                if (!(ctx.getSource().getSender() instanceof Player player)) {
                    ctx.getSource().getSender().sendMessage("You must be a player to use this command!");
                    return Command.SINGLE_SUCCESS;
                }

                String res = basicChecks(player, name);
                if (res != null) { player.sendMessage(Component.text(res, color)); return Command.SINGLE_SUCCESS; }

                PluralConfig cfg = PlayerConfig.forPlayer(player).getPluralConfig();
                if (cfg.getMember(name) != null) {
                    player.sendMessage(Component.text("A member with this name already exists.", color));
                } else {
                    cfg.addMember(name);
                    player.sendMessage(Component.text("Created a member with this name successfully.", color));
                }
                return Command.SINGLE_SUCCESS;
            }))



            .then(Commands.literal("delete").executes(ctx -> {
                String name = StringArgumentType.getString(ctx, "name");
                if (!(ctx.getSource().getSender() instanceof Player player)) {
                    ctx.getSource().getSender().sendMessage("You must be a player to use this command!");
                    return Command.SINGLE_SUCCESS;
                }

                String res = basicChecks(player, name);
                if (res != null) { player.sendMessage(Component.text(res, color)); return Command.SINGLE_SUCCESS; }

                PluralConfig cfg = PlayerConfig.forPlayer(player).getPluralConfig();
                if (cfg.getMember(name) == null) {
                    player.sendMessage(Component.text("Can't find any member with this name.", color));
                } else {
                    cfg.removeMember(name);
                    if (name.equals(FrontManager.getFront(player.getUniqueId()))) {
                        FrontManager.clearFront(player.getUniqueId());
                    }
                    player.sendMessage(Component.text("Deleted the member with this name successfully.", color));
                }
                return Command.SINGLE_SUCCESS;
            }))



            .then(Commands.literal("rename").then(Commands.argument("newName", StringArgumentType.string()).executes(ctx -> {
                String name = StringArgumentType.getString(ctx, "name");
                String newName = StringArgumentType.getString(ctx, "newName");
                if (!(ctx.getSource().getSender() instanceof Player player)) {
                    ctx.getSource().getSender().sendMessage("You must be a player to use this command!");
                    return Command.SINGLE_SUCCESS;
                }

                String res1 = basicChecks(player, name);
                if (res1 != null) { player.sendMessage(Component.text(res1, color)); return Command.SINGLE_SUCCESS; }
                String res2 = basicChecks(player, name);
                if (res2 != null) { player.sendMessage(Component.text(res2, color)); return Command.SINGLE_SUCCESS; }

                PlayerConfig config = PlayerConfig.forPlayer(player);
                PluralConfig cfg = config.getPluralConfig();
                if (cfg.getMember(name) == null) {
                    player.sendMessage(Component.text("Can't find any member with this name.", color));
                } else if (cfg.getMember(newName) != null) {
                    player.sendMessage(Component.text("A member with this name already exists.", color));
                } else {
                    cfg.rename(name, newName);
                    if (name.equals(FrontManager.getFront(player.getUniqueId()))) {
                        FrontManager.updateFront(player.getUniqueId(), newName);
                    }
                    config.updatePlayer();
                    player.sendMessage(Component.text("Renamed that member successfully.", color));
                }
                return Command.SINGLE_SUCCESS;
            })))



            .then(Commands.literal("proxy").then(Commands.argument("tag", StringArgumentType.greedyString()).executes(ctx -> {
                String name = StringArgumentType.getString(ctx, "name");
                String proxyTag = StringArgumentType.getString(ctx, "tag");
                if (!(ctx.getSource().getSender() instanceof Player player)) {
                    ctx.getSource().getSender().sendMessage("You must be a player to use this command!");
                    return Command.SINGLE_SUCCESS;
                }

                String res = basicChecks(player, name);
                if (res != null) { player.sendMessage(Component.text(res, color)); return Command.SINGLE_SUCCESS; }

                Pair<String, String> pair = PluralConfig.proxyParse(proxyTag);
                if (pair == null) {
                    player.sendMessage(Component.text("Invalid proxy tags. Make sure the string contains \"" + PluralConfig.proxySplit + "\" somewhere.", color));
                    return Command.SINGLE_SUCCESS;
                }

                PluralConfig cfg = PlayerConfig.forPlayer(player).getPluralConfig();
                if (cfg.getMember(name) == null) {
                    player.sendMessage(Component.text("Can't find any member with this name.", color));
                } else {
                    cfg.setProxy(name, pair.left, pair.right);
                    player.sendMessage(Component.text("Changed that member's proxy tags successfully.", color));
                }
                return Command.SINGLE_SUCCESS;
            })))


            
            .then(Commands.literal("display")



                .then(Commands.literal("pronouns").then(Commands.argument("pronouns", StringArgumentType.greedyString()).executes(ctx -> {
                    String name = StringArgumentType.getString(ctx, "name");
                    String pronouns = StringArgumentType.getString(ctx, "pronouns");
                    if (!(ctx.getSource().getSender() instanceof Player player)) {
                        ctx.getSource().getSender().sendMessage("You must be a player to use this command!");
                        return Command.SINGLE_SUCCESS;
                    }

                    String res = basicChecks(player, name);
                    if (res != null) { player.sendMessage(Component.text(res, color)); return Command.SINGLE_SUCCESS; }

                    if ("unset".equals(pronouns)) {
                        pronouns = null;
                    }

                    if (pronouns != null && (pronouns.length() < 2 || pronouns.length() > 30)) {
                        player.sendMessage(Component.text("Pronouns must be between 2 and 30 characters long.", color));
                        return Command.SINGLE_SUCCESS;
                    }

                    PlayerConfig config = PlayerConfig.forPlayer(player);
                    PluralConfig cfg = config.getPluralConfig();
                    if (cfg.getMember(name) == null) {
                        player.sendMessage(Component.text("Can't find any member with this name.", color));
                    } else {
                        cfg.setPronouns(name, pronouns);
                        config.updatePlayer();
                        player.sendMessage(Component.text((pronouns == null ? "Cleared" : "Changed") + " that member's pronouns successfully.", color));
                    }
                    return Command.SINGLE_SUCCESS;
                })))



                .then(Commands.literal("color").then(Commands.argument("color", StringArgumentType.word()).executes(ctx -> {
                    String name = StringArgumentType.getString(ctx, "name");
                    String rawColor = StringArgumentType.getString(ctx, "color");
                    if (!(ctx.getSource().getSender() instanceof Player player)) {
                        ctx.getSource().getSender().sendMessage("You must be a player to use this command!");
                        return Command.SINGLE_SUCCESS;
                    }

                    String res = basicChecks(player, name);
                    if (res != null) { player.sendMessage(Component.text(res, color)); return Command.SINGLE_SUCCESS; }

                    Integer colorCode;
                    try {
                        if ("unset".equals(rawColor)) {
                            colorCode = null;
                        } else {
                            if (rawColor.length() != 6) throw new NumberFormatException("Bad length");
                            colorCode = Integer.parseInt(rawColor, 16);
                        }
                    } catch (NumberFormatException e) {
                        BlazingGames.get().debugLog(e);
                        player.sendMessage(Component.text("This is not a valid hex color code.", color));
                        return Command.SINGLE_SUCCESS;
                    }

                    PlayerConfig config = PlayerConfig.forPlayer(player);
                    PluralConfig cfg = config.getPluralConfig();
                    if (cfg.getMember(name) == null) {
                        player.sendMessage(Component.text("Can't find any member with this name.", color));
                    } else {
                        cfg.setColor(name, colorCode == null ? null : TextColor.color(colorCode));
                        config.updatePlayer();
                        player.sendMessage(Component.text((colorCode == null ? "Cleared" : "Changed") + " that member's color successfully.", color));
                    }
                    return Command.SINGLE_SUCCESS;
                })))
        )).build();
    }
}