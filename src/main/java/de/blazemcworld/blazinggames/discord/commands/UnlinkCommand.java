package de.blazemcworld.blazinggames.discord.commands;

import de.blazemcworld.blazinggames.discord.DiscordApp;
import de.blazemcworld.blazinggames.discord.DiscordUser;
import de.blazemcworld.blazinggames.discord.WhitelistManagement;
import de.blazemcworld.blazinggames.discord.WhitelistedPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class UnlinkCommand implements ICommand {
    @Override
    public String name() {
        return "unlink";
    }
    
    @Override
    public String description() {
        return "Unlink your account(s) from the minecraft whitelist.";
    }

    @SuppressWarnings("null")
    @Override
    public void handle(SlashCommandInteractionEvent event) {
        if (!DiscordApp.isWhitelistManaged()) throw new IllegalStateException("Whitelist is not managed, but /unlink was called");
        WhitelistManagement whitelist = DiscordApp.getWhitelistManagement();

        String username = event.getOption("username").getAsString();
        WhitelistedPlayer whitelistedPlayer = whitelist.getWhitelistedPlayer(username);

        if(whitelistedPlayer == null) {
            event.reply("The specified player has not been whitelisted on this server. To view a list of linked players, use /links").setEphemeral(true).queue();
            return;
        }

        if(whitelistedPlayer.discordUser != event.getUser().getIdLong()) {
            event.reply("The specified player is not linked to your discord account. To view a list of linked players, use /links").setEphemeral(true).queue();
            return;
        }

        username = whitelistedPlayer.lastKnownName;

        boolean removingPrimary = whitelist.removePlayer(whitelistedPlayer);

        StringBuilder embedDescription = new StringBuilder();

        DiscordUser user = whitelist.updateUser(event.getUser());
        boolean noMorePrimary = user == null || user.favoriteAccount == null;

        embedDescription.append("The player ").append(username).append(" has been removed from the whitelist and unlinked from your discord account.\n\n");
        if(removingPrimary) {
            if(noMorePrimary) {
                embedDescription.append("You no longer have any players linked to this discord account. ");
                embedDescription.append("This means that in order to continue playing, you need to link a new account");
            }
            else {
                embedDescription.append("The player ").append(username).append(" was your primary account up to now. Sending messages in the chat link channel will now send as ").append(whitelist.getWhitelistedPlayer(user.favoriteAccount).lastKnownName);
            }
        }
        else if(noMorePrimary) {
            embedDescription.append("You never had a primary account in the first place. How.");
        }
        else {
            embedDescription.append("Your primary account was unaffected. Sending messages in the chat link channel will still send as ").append(whitelist.getWhitelistedPlayer(user.favoriteAccount).lastKnownName);
        }
        embedDescription.append(".\n\n");
        embedDescription.append("* To change your primary account, run `/setprimary` (in discord).\n");
        embedDescription.append("* To link a new player, run `/whitelist` (in discord) with a link code obtained by joining the server with the desired account.\n");
        embedDescription.append("* To view a list of all linked minecraft accounts, run `/links` (in discord).");

        MessageEmbed embed = new EmbedBuilder()
                .setTitle("Whitelist")
                .setColor(0xF27B7B)
                .setDescription(embedDescription)
                .build();
        event.replyEmbeds(embed).setEphemeral(true).queue();
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[] {
            new OptionData(OptionType.STRING, "username", "Username of the player you want to unlink", true)
        };
    }
}
