package de.blazemcworld.blazinggames.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import de.blazemcworld.blazinggames.discord.DiscordApp;
import de.blazemcworld.blazinggames.discord.WhitelistManagement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class EnforceWhitelistCommand implements CommandExecutor {
    public static void enforceWhitelist() {
        WhitelistManagement whitelist = DiscordApp.getWhitelistManagement();
        Bukkit.setWhitelist(true);
        Bukkit.setWhitelistEnforced(false);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isWhitelisted() || player.isOp()) {
                return;
            }

            if (!whitelist.isWhitelisted(player.getUniqueId())) {
                player.kick(Component.text("fuck you").color(NamedTextColor.RED));
            }
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage(Component.text("Enforcing whitelist..."));
        enforceWhitelist();
        return true;
    }
}
