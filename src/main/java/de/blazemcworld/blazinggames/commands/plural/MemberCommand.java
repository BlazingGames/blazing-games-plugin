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
import com.mojang.brigadier.tree.LiteralCommandNode;

import de.blazemcworld.blazinggames.commands.boilerplate.CommandHelper;
import de.blazemcworld.blazinggames.commands.finalizers.ShowNameplatesFinalizer;
import de.blazemcworld.blazinggames.commands.middleware.RequireMemberMiddleware;
import de.blazemcworld.blazinggames.commands.middleware.RequireSystemMiddleware;
import de.blazemcworld.blazinggames.commands.templates.DisplayCommandBuilder;
import de.blazemcworld.blazinggames.players.DisplayTag;
import de.blazemcworld.blazinggames.players.FrontManager;
import de.blazemcworld.blazinggames.players.MemberData;
import de.blazemcworld.blazinggames.players.PlayerConfig;
import de.blazemcworld.blazinggames.players.PluralConfig;
import de.blazemcworld.blazinggames.players.ServerPlayerConfig;
import de.blazemcworld.blazinggames.utils.Pair;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class MemberCommand {
    public static final TextColor color = TextColor.color(0xEDC4DF);
    public static final CommandHelper helper = CommandHelper.builder()
        .middleware(new RequireSystemMiddleware(color))
        .ignoreExecutor(true)
        .build();
    public static final CommandHelper configHelper = CommandHelper.builder()
        .middleware(new RequireSystemMiddleware(color))
        .middleware(new RequireMemberMiddleware("name", color))
        .ignoreExecutor(true)
        .build();

    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("member").then(Commands.literal("list").executes(helper.requirePlayer((ctx, player) -> {
            PluralConfig pluralConfig = PlayerConfig.forPlayer(player).getPluralConfig();
            if (pluralConfig.getMembers().isEmpty()) {
                player.sendMessage(Component.text("No members found.", color));
            } else {
                player.sendMessage(Component.text("Members:", color));
                for (MemberData member : pluralConfig.getMembers()) {
                    player.sendMessage(Component.text("- " + member.name, color));
                }
            }
        })))
        .then(Commands.argument("name", StringArgumentType.string())



            .then(Commands.literal("create").executes(helper.requirePlayer((ctx, player) -> {
                String name = StringArgumentType.getString(ctx, "name");
                if (!ServerPlayerConfig.isLengthValid(name)) {
                    player.sendMessage(Component.text("Member names must be between " + ServerPlayerConfig.minLength() + " and " + ServerPlayerConfig.maxLength() + " characters long.", color));
                }

                PluralConfig cfg = PlayerConfig.forPlayer(player).getPluralConfig();
                if (cfg.getMember(name) != null) {
                    player.sendMessage(Component.text("A member with this name already exists.", color));
                } else {
                    cfg.addMember(name);
                    player.sendMessage(Component.text("Created a member with this name successfully.", color));
                }
            })))



            .then(Commands.literal("delete").executes(configHelper.requirePlayer((ctx, player) -> {
                String name = StringArgumentType.getString(ctx, "name");
                PlayerConfig.forPlayer(player).getPluralConfig().removeMember(name);
                if (name.equals(FrontManager.getFront(player.getUniqueId()))) {
                    FrontManager.clearFront(player.getUniqueId());
                    PlayerConfig.forPlayer(player).updatePlayer();
                }
                player.sendMessage(Component.text("Deleted the member with this name successfully.", color));
            })))



            .then(Commands.literal("rename").then(Commands.argument("newName", StringArgumentType.string()).executes(configHelper.requirePlayer((ctx, player) -> {
                String name = StringArgumentType.getString(ctx, "name");
                String newName = StringArgumentType.getString(ctx, "newName");

                if (!ServerPlayerConfig.isLengthValid(newName)) {
                    player.sendMessage(Component.text("New name must be between " + ServerPlayerConfig.minLength() + " and " + ServerPlayerConfig.maxLength() + " characters long.", color));
                }

                PlayerConfig config = PlayerConfig.forPlayer(player);
                PluralConfig cfg = config.getPluralConfig();
                if (cfg.getMember(newName) != null) {
                    player.sendMessage(Component.text("A member with this name already exists.", color));
                } else {
                    cfg.rename(name, newName);
                    if (name.equals(FrontManager.getFront(player.getUniqueId()))) {
                        FrontManager.updateFront(player.getUniqueId(), newName);
                    }
                    config.updatePlayer();
                    player.sendMessage(Component.text("Renamed that member successfully.", color));
                }
            }))))



            .then(Commands.literal("proxy").then(Commands.argument("tag", StringArgumentType.greedyString()).executes(configHelper.requirePlayer((ctx, player) -> {
                String name = StringArgumentType.getString(ctx, "name");
                String proxyTag = StringArgumentType.getString(ctx, "tag");

                Pair<String, String> pair = PluralConfig.proxyParse(proxyTag);
                if (pair == null) {
                    player.sendMessage(Component.text("Invalid proxy tags. Make sure the string contains \"" + PluralConfig.proxySplit + "\" somewhere.", color));
                    return;
                }

                PlayerConfig.forPlayer(player).getPluralConfig().setMemberProxy(name, pair.left, pair.right);
                player.sendMessage(Component.text("Changed that member's proxy tags successfully.", color));
            }))))


            
            // display commands
            .then(DisplayCommandBuilder.tree(color, color, (ctx, player) -> {
                String name = StringArgumentType.getString(ctx, "name");
                return PlayerConfig.forPlayer(player).getPluralConfig().toDisplayConfigurationEditor(name);
            }, new ShowNameplatesFinalizer("name", color), new RequireSystemMiddleware(color), new RequireMemberMiddleware("name", color))
        )).build();
    }

    public static void renderNameplates(Player player, PluralConfig config, String name) {
        DisplayTag tag = config.toDisplayTag(name, PlayerConfig.forPlayer(player));
        if (tag == null) return;
        tag.sendPreviews(player, color);
    }
}