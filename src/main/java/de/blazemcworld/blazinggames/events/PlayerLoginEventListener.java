package de.blazemcworld.blazinggames.events;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.discord.DiscordApp;
import de.blazemcworld.blazinggames.discord.DiscordUser;
import de.blazemcworld.blazinggames.discord.WhitelistManagement;
import de.blazemcworld.blazinggames.discord.WhitelistedPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class PlayerLoginEventListener implements Listener {
    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        if (DiscordApp.isWhitelistManaged() && (
            event.getResult() == Result.KICK_WHITELIST ||
            event.getResult() == Result.ALLOWED
        )) {
            WhitelistManagement whitelist = DiscordApp.getWhitelistManagement();
            Player player = event.getPlayer();
            if (player.isOp()) {
                sendMessageLater(player.getUniqueId(),"Hello, " + player.getName()
                    + "! (whitelisted via operator)");
                event.allow();
                return;
            }

            if (player.isWhitelisted()) {
                sendMessageLater(player.getUniqueId(),"Hello, " + player.getName()
                    + "! (whitelisted via vanilla whitelist)");
                event.allow();
                return;
            }

            if (whitelist.isWhitelisted(player.getUniqueId())) {
                WhitelistedPlayer whitelistedPlayer = whitelist.getWhitelistedPlayer(player.getUniqueId());
                DiscordUser linkedAccount = whitelist.getDiscordUser(whitelistedPlayer.discordUser);
                sendMessageLater(player.getUniqueId(), "Hello, " + linkedAccount.displayName + "! ("
                    + linkedAccount.username + "#" + linkedAccount.descriminator + ")");
                event.allow();
                return;
            }

            event.disallow(Result.KICK_WHITELIST, Component.text(
                "You are not whitelisted. Please use /whitelist on discord to link your account first."
            ).color(NamedTextColor.RED));
        }
    }

    private void sendMessageLater(final UUID whom, final String message) {
        Bukkit.getScheduler().runTaskLater(BlazingGames.get(), () -> {
            if (Bukkit.getPlayer(whom) != null) {
                Bukkit.getPlayer(whom).sendMessage(Component.text(message).color(NamedTextColor.YELLOW));
            }
        }, 20L);
    }
}
