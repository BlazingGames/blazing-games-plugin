package de.blazemcworld.blazinggames.commands;

import de.blazemcworld.blazinggames.discord.DiscordApp;
import de.blazemcworld.blazinggames.discord.DiscordUser;
import de.blazemcworld.blazinggames.discord.WhitelistManagement;
import de.blazemcworld.blazinggames.discord.WhitelistedPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UnlinkCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage(Component.text("Only players can use this command!")
                    .color(NamedTextColor.RED));
            return true;
        }
        if(!DiscordApp.isWhitelistManaged()) {
            sender.sendMessage(Component.text("Whitelist is not managed, but /whitelist was called")
                    .color(NamedTextColor.RED));
            return true;
        }

        WhitelistManagement whitelist = DiscordApp.getWhitelistManagement();

        WhitelistedPlayer whitelistedPlayer = whitelist.getWhitelistedPlayer(p.getUniqueId());

        if(whitelistedPlayer == null) {
            sender.sendMessage(Component.text("You have not been whitelisted on this server by discord!").appendNewline()
                    .append(Component.text("(you probably could join because of the vanilla whitelist or " +
                            "because you are a server operator)"))
                    .color(NamedTextColor.RED));
            return true;
        }

        boolean removingPrimary = whitelist.removePlayer(whitelistedPlayer, false);

        StringBuilder embedDescription = new StringBuilder();

        DiscordUser user = whitelist.getDiscordUser(whitelistedPlayer.discordUser);

        boolean noMorePrimary = user == null || user.favoriteAccount == null;

        embedDescription.append("You have been removed from the whitelist and unlinked from your discord account.\n");
        if(removingPrimary) {
            if(noMorePrimary) {
                embedDescription.append("You no longer have any players linked to this discord account.\n");
                embedDescription.append("This means that in order to continue playing, you need to link a new account");
            }
            else {
                embedDescription.append("This account was your primary account up to now. Sending messages in the chat link channel will now send as ").append(whitelist.getWhitelistedPlayer(user.favoriteAccount).lastKnownName);
            }
        }
        else if(noMorePrimary) {
            embedDescription.append("You never had a primary account in the first place. How.");
        }
        else {
            embedDescription.append("Your primary account was unaffected. Sending messages in the chat link channel will still send as ").append(whitelist.getWhitelistedPlayer(user.favoriteAccount).lastKnownName);
        }
        embedDescription.append(".\n");
        embedDescription.append("To change your primary account, run `/setprimary` (in discord).\n");
        embedDescription.append("To link a new player, run `/whitelist` (in discord) with a link code obtained by joining the server with the desired account.\n");
        embedDescription.append("To view a list of all linked minecraft accounts, run `/links` (in discord).");

        p.kick(Component.text(embedDescription.toString()).color(NamedTextColor.RED));

        return true;
    }
}
