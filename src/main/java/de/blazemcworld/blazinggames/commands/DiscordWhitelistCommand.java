package de.blazemcworld.blazinggames.commands;

import de.blazemcworld.blazinggames.discord.DiscordApp;
import de.blazemcworld.blazinggames.discord.DiscordUser;
import de.blazemcworld.blazinggames.discord.WhitelistManagement;
import de.blazemcworld.blazinggames.discord.WhitelistedPlayer;
import de.blazemcworld.blazinggames.utils.PlayerConfig;
import de.blazemcworld.blazinggames.utils.PlayerInfo;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;

import java.util.List;

public class DiscordWhitelistCommand {
    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("discordwhitelist")
            .requires(ctx -> ctx.getSender().hasPermission("blazinggames.discordwhitelist") && DiscordApp.isWhitelistManaged())
            .then(Commands.literal("enforce").executes(ctx -> {
                ctx.getSource().getSender().sendMessage(Component.text("Enforcing Whitelist...").color(NamedTextColor.YELLOW));
                enforceWhitelist();
                return Command.SINGLE_SUCCESS;
            }))
            .then(Commands.literal("list").executes(ctx -> {
                WhitelistManagement whitelist = DiscordApp.getWhitelistManagement();
                CommandSender sender = ctx.getSource().getSender();
                sender.sendMessage(Component.text("Discord Whitelisted Accounts:").color(NamedTextColor.YELLOW));
                for(DiscordUser user : whitelist.getAllKnownDiscordUsers()) {
                    List<WhitelistedPlayer> whitelistedPlayers = whitelist.getWhitelistedPlayersOfDiscordUser(user);

                    if(whitelistedPlayers.isEmpty()) {
                        continue;
                    }

                    sender.sendMessage(
                            Component.text(user.displayName).appendSpace()
                            .append(Component.text("("+user.username+")").color(NamedTextColor.DARK_GRAY))
                            .append(Component.text(":")).color(NamedTextColor.GRAY)
                    );

                    for(WhitelistedPlayer whitelistedPlayer : whitelistedPlayers) {
                        PlayerInfo info = PlayerInfo.fromWhitelistedPlayer(whitelistedPlayer);
                        PlayerConfig config = PlayerConfig.forPlayer(info);

                        Component displayName = Component.text(" - ").color(NamedTextColor.GRAY)
                                .append(config.buildNameComponent());

                        if(user.favoriteAccount.equals(whitelistedPlayer.uuid)) {
                            displayName = displayName.appendSpace().append(
                                    Component.text("★").color(NamedTextColor.GOLD)
                                            .hoverEvent(HoverEvent.showText(Component.text("Primary Account")))
                            );
                        }

                        sender.sendMessage(displayName);
                    }
                }
                return Command.SINGLE_SUCCESS;
            }))
            .then(Commands.literal("remove").then(Commands.argument("player", StringArgumentType.word()).executes(ctx -> {
                String player = StringArgumentType.getString(ctx, "player");
                WhitelistManagement whitelist = DiscordApp.getWhitelistManagement();
                CommandSender sender = ctx.getSource().getSender();

                WhitelistedPlayer whitelistedPlayer = whitelist.getWhitelistedPlayer(player);

                if(whitelistedPlayer == null) {
                    sender.sendMessage(Component.text(player + " has not been whitelisted using the discord whitelist!")
                            .color(NamedTextColor.RED));
                    return Command.SINGLE_SUCCESS;
                }

                whitelist.removePlayer(whitelistedPlayer);
                sender.sendMessage(Component.text("Successfully removed " + player + " from the discord whitelist!")
                        .color(NamedTextColor.GREEN));

                return Command.SINGLE_SUCCESS;
            })))
            .build();
    }

    public static void enforceWhitelist() {
        WhitelistManagement whitelist = DiscordApp.getWhitelistManagement();
        Bukkit.setWhitelist(true);
        Bukkit.setWhitelistEnforced(false);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isWhitelisted() || player.isOp()) {
                return;
            }

            if (whitelist != null && !whitelist.isWhitelisted(player.getUniqueId())) {
                player.kick(Component.text("You have been removed from the whitelist.").color(NamedTextColor.RED));
            }
        }
    }
}
