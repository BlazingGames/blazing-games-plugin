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
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;

import de.blazemcworld.blazinggames.commands.boilerplate.CommandHelper;
import de.blazemcworld.blazinggames.commands.finalizers.ShowNameplatesFinalizer;
import de.blazemcworld.blazinggames.commands.middleware.EmptyMessageMiddleware;
import de.blazemcworld.blazinggames.commands.middleware.RequireMemberMiddleware;
import de.blazemcworld.blazinggames.commands.middleware.RequireSystemMiddleware;
import de.blazemcworld.blazinggames.players.DisplayTag;
import de.blazemcworld.blazinggames.players.FrontManager;
import de.blazemcworld.blazinggames.players.MemberData;
import de.blazemcworld.blazinggames.players.PlayerConfig;
import de.blazemcworld.blazinggames.players.PluralConfig;
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
    public static final CommandHelper displayHelper = CommandHelper.builder()
        .middleware(new RequireSystemMiddleware(color))
        .middleware(new EmptyMessageMiddleware())
        .middleware(new RequireMemberMiddleware("name", color))
        .finalizer(new ShowNameplatesFinalizer("name", color))
        .ignoreExecutor(true)
        .build();

    public static String basicChecks(Player player, String name) {
        player.sendMessage(Component.empty());

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
                if (name.length() < 3 || name.length() > 40) {
                    player.sendMessage(Component.text("Name must be between 3 and 40 characters.", color));
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
                }
                player.sendMessage(Component.text("Deleted the member with this name successfully.", color));
            })))



            .then(Commands.literal("rename").then(Commands.argument("newName", StringArgumentType.string()).executes(configHelper.requirePlayer((ctx, player) -> {
                String name = StringArgumentType.getString(ctx, "name");
                String newName = StringArgumentType.getString(ctx, "newName");

                if (newName.length() < 3 || newName.length() > 40) {
                    player.sendMessage(Component.text("New name must be between 3 and 40 characters.", color));
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

                PlayerConfig.forPlayer(player).getPluralConfig().setProxy(name, pair.left, pair.right);
                player.sendMessage(Component.text("Changed that member's proxy tags successfully.", color));
            }))))


            
            .then(Commands.literal("display")
                .executes(displayHelper.requirePlayer((ctx, player) -> {})) // let middleware/finalizers handle this


                // display name
                .then(Commands.literal("name")
                    .executes(helper.requirePlayer((ctx, player) -> handleDisplay(ctx, player, PropertyType.DISPLAY_NAME, true)))
                    .then(Commands.argument("value", StringArgumentType.greedyString())
                        .executes(helper.requirePlayer((ctx, player) -> handleDisplay(ctx, player, PropertyType.DISPLAY_NAME, false)))))

            // pronouns
            .then(Commands.literal("pronouns")
                .executes(helper.requirePlayer((ctx, player) -> handleDisplay(ctx, player, PropertyType.PRONOUNS, true)))
                .then(Commands.argument("value", StringArgumentType.greedyString())
                    .executes(helper.requirePlayer((ctx, player) -> handleDisplay(ctx, player, PropertyType.PRONOUNS, false)))))
            
            // color
            .then(Commands.literal("color")
                .executes(helper.requirePlayer((ctx, player) -> handleDisplay(ctx, player, PropertyType.COLOR, true)))
                .then(Commands.argument("value", StringArgumentType.greedyString())
                    .executes(helper.requirePlayer((ctx, player) -> handleDisplay(ctx, player, PropertyType.COLOR, false)))))
        )).build();
    }

    public static void renderNameplates(Player player, PluralConfig config, String name) {
        DisplayTag tag = config.toDisplayTag(name, PlayerConfig.forPlayer(player));
        if (tag == null) return;
        tag.sendPreviews(player, color);
    }

    // copied from DisplayCommand because they're really similar

    public static void handleDisplay(CommandContext<CommandSourceStack> ctx, Player player, PropertyType type, boolean clear) {
        String member = StringArgumentType.getString(ctx, "name");
        String value;
        if (clear) {
            value = null;
        } else {
            value = StringArgumentType.getString(ctx, "value");
        }

        String result = setProperty(PlayerConfig.forPlayer(player).getPluralConfig(), member, type, value);
        if (result != null) {
            ctx.getSource().getSender().sendMessage(Component.text("Failed to change " + type.pretty + ": " + result, color));
            return;
        }

        String keyword = clear ? "Cleared " : "Set ";
        ctx.getSource().getSender().sendMessage(Component.text(keyword + type.pretty + " successfully.", color));
    }

    public static enum PropertyType {
        DISPLAY_NAME("display name"),
        PRONOUNS("pronouns"),
        COLOR("name color"),

        ;

        public final String pretty;
        PropertyType(String pretty) {
            this.pretty = pretty;
        }
    }

    public static String setProperty(PluralConfig config, String member, PropertyType type, String value) {
        if (value == null) {
            switch (type) {
                case DISPLAY_NAME:
                    config.setDisplayName(member, null);
                    return null;
                case PRONOUNS:
                    config.setPronouns(member, null);
                    return null;
                case COLOR:
                    config.setNameColor(member, null);
                    return null;
            }
            return null;
        }

        switch (type) {
            case DISPLAY_NAME:
                if (value.length() < 2 || value.length() > 40) {
                    return "display names must be between 2 and 40 characters long.";
                }
                config.setDisplayName(member, value);
                return null;
            case PRONOUNS:
                if (value.length() < 2 || value.length() > 30) {
                    return "pronouns must be between 2 and 30 characters long.";
                }
                config.setPronouns(member, value);
                return null;
            case COLOR:
                if (value.length() != 6) {
                    return "colors must be a hex color without the first #. For example, \"ffffff\".";
                }
                int intValue;
                try {
                    intValue = Integer.parseInt(value, 16);
                } catch (NumberFormatException e) {
                    return "#" + value + " is not a valid color.";
                }
                config.setNameColor(member, TextColor.color(intValue));
                return null;
            default:
                return "Unknown property type. This is a bug.";
        }
    }
}