package de.blazemcworld.blazinggames.commands;

import de.blazemcworld.blazinggames.discord.DiscordApp;
import de.blazemcworld.blazinggames.discord.DiscordUser;
import de.blazemcworld.blazinggames.discord.WhitelistManagement;
import de.blazemcworld.blazinggames.discord.WhitelistedPlayer;
import de.blazemcworld.blazinggames.utils.PlayerConfig;
import de.blazemcworld.blazinggames.utils.PlayerInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DiscordWhitelistCommand implements CommandExecutor, TabCompleter {
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

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!DiscordApp.isWhitelistManaged()) {
            sender.sendMessage(Component.text("Whitelist is not managed, but /discordwhitelist was called")
                    .color(NamedTextColor.RED));
            return true;
        }

        WhitelistManagement whitelist = DiscordApp.getWhitelistManagement();

        if(args.length == 0) {
            CommandHelper.sendUsage(sender, command);
            return true;
        }

        switch (args[0]) {
            case "enforce" -> {
                if(args.length != 1) {
                    CommandHelper.sendUsage(sender, command);
                    return true;
                }
                sender.sendMessage(Component.text("Enforcing Whitelist...").color(NamedTextColor.YELLOW));
                enforceWhitelist();
            }
            case "list" -> {
                if(args.length != 1) {
                    CommandHelper.sendUsage(sender, command);
                    return true;
                }
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
                                .append(config.toDisplayTag(false).buildNameComponent());

                        if(user.favoriteAccount.equals(whitelistedPlayer.uuid)) {
                            displayName = displayName.appendSpace().append(
                                    Component.text("★").color(NamedTextColor.GOLD)
                                            .hoverEvent(HoverEvent.showText(Component.text("Primary Account")))
                            );
                        }

                        sender.sendMessage(displayName);
                    }
                }
            }
            case "remove" -> {
                if(args.length != 2) {
                    CommandHelper.sendUsage(sender, command);
                    return true;
                }

                WhitelistedPlayer whitelistedPlayer = whitelist.getWhitelistedPlayer(args[1]);

                if(whitelistedPlayer == null) {
                    sender.sendMessage(Component.text(args[1] + " has not been whitelisted using the discord whitelist!")
                            .color(NamedTextColor.RED));
                    return true;
                }

                whitelist.removePlayer(whitelistedPlayer);
                sender.sendMessage(Component.text("Successfully removed " + args[1] + " from the discord whitelist!")
                        .color(NamedTextColor.GREEN));
            }
            default -> CommandHelper.sendUsage(sender, command);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command,
                                                @NotNull String s, @NotNull String[] strings) {
        if(strings.length == 1) {
            return List.of("list", "remove", "enforce");
        }

        return List.of();
    }
}
