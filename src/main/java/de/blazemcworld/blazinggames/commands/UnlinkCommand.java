package de.blazemcworld.blazinggames.commands;

import de.blazemcworld.blazinggames.commands.boilerplate.CommandHelper;
import de.blazemcworld.blazinggames.discord.DiscordApp;
import de.blazemcworld.blazinggames.discord.DiscordUser;
import de.blazemcworld.blazinggames.discord.WhitelistManagement;
import de.blazemcworld.blazinggames.discord.WhitelistedPlayer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class UnlinkCommand {
    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("unlink")
            .requires(ctx -> DiscordApp.isWhitelistManaged())
            .executes(CommandHelper.getDefault().requirePlayer(UnlinkCommand::handle))
            .build();
    }

    public static void handle(CommandContext<CommandSourceStack> ctx, Player player) {
        if (!DiscordApp.isWhitelistManaged()) {
            ctx.getSource().getSender().sendRichMessage("<red>Whitelist is not managed, but a whitelist command was called (report this as a bug!)");
            return;
        }

        WhitelistManagement whitelist = DiscordApp.getWhitelistManagement();
        WhitelistedPlayer whitelistedPlayer = whitelist.getWhitelistedPlayer(player.getUniqueId());
        if (whitelistedPlayer == null) {
            player.sendRichMessage("<red>You have not been whitelisted on this server by discord!"
                + "<newline>(you probably joined because of the vanilla whitelist or because you are a server operator)");
            return;
        }

        boolean removingPrimary = whitelist.removePlayer(whitelistedPlayer, false);

        StringBuilder embedDescription = new StringBuilder();

        DiscordUser user = whitelist.getDiscordUser(whitelistedPlayer.discordUser);

        // nothing screams "I copied this from another class" more than embedDescription being used for a kick message
        embedDescription.append("You have been removed from the whitelist and unlinked from your discord account.\n");
        if(removingPrimary) {
            if(user == null || user.favoriteAccount == null) {
                embedDescription.append("You no longer have any players linked to this discord account.\n");
                embedDescription.append("This means that in order to continue playing, you need to link a new account");
            }
            else {
                embedDescription.append("This account was your primary account up to now. Sending messages in the chat link channel will now send as ").append(whitelist.getWhitelistedPlayer(user.favoriteAccount).lastKnownName);
            }
        }
        else if(user == null || user.favoriteAccount == null) {
            embedDescription.append("You never had a primary account in the first place. How.");
        }
        else {
            embedDescription.append("Your primary account was unaffected. Sending messages in the chat link channel will still send as ").append(whitelist.getWhitelistedPlayer(user.favoriteAccount).lastKnownName);
        }
        embedDescription.append(".\n");
        embedDescription.append("To change your primary account, run `/setprimary` (in discord).\n");
        embedDescription.append("To link a new player, run `/whitelist` (in discord) with a link code obtained by joining the server with the desired account.\n");
        embedDescription.append("To view a list of all linked minecraft accounts, run `/links` (in discord).");

        player.kick(Component.text(embedDescription.toString()).color(NamedTextColor.RED));
    }
}
