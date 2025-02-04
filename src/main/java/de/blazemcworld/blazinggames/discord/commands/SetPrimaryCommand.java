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

import java.util.UUID;

public class SetPrimaryCommand implements ICommand {
    @Override
    public String name() {
        return "setprimary";
    }
    
    @Override
    public String description() {
        return "Sets a whitelisted account as your primary account.";
    }

    @SuppressWarnings("null")
    @Override
    public void handle(SlashCommandInteractionEvent event) {
        if (!DiscordApp.isWhitelistManaged()) throw new IllegalStateException("Whitelist is not managed, but /setprimary was called");
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

        DiscordUser user = whitelist.updateUser(event.getUser());

        if(whitelistedPlayer.uuid.equals(user.favoriteAccount)) {
            event.reply("The specified player is already your primary account.").setEphemeral(true).queue();
            return;
        }

        UUID previousPrimary = user.favoriteAccount;
        WhitelistedPlayer previousPrimaryPlayer = whitelist.getWhitelistedPlayer(previousPrimary);

        whitelist.updateUser(event.getUser(), whitelistedPlayer.uuid);

        StringBuilder embedDescription = new StringBuilder();

        embedDescription.append("The player ").append(username).append(" has been set as the primary account for your discord account.\n\n");

        if(previousPrimaryPlayer == null) {
            embedDescription.append("You didn't have a primary account up to now. Sending messages in the chat link channel will now send as ").append(username);
        }
        else {
            embedDescription.append("The player ").append(previousPrimaryPlayer.lastKnownName).append(" was your primary account up to now. Sending messages in the chat link channel will now send as ").append(username);
        }
        embedDescription.append(".\n\n");
        embedDescription.append("* To unlink a minecraft account, run `/unlink` (in discord or in game).\n");
        embedDescription.append("* To link a new player, run `/whitelist` (in discord) with a link code obtained by joining the server with the desired account.\n");
        embedDescription.append("* To view a list of all linked minecraft accounts, run `/links` (in discord).");

        MessageEmbed embed = new EmbedBuilder()
                .setTitle("Whitelist")
                .setColor(0xDFE378)
                .setDescription(embedDescription)
                .build();
        event.replyEmbeds(embed).setEphemeral(true).queue();
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[] {
            new OptionData(OptionType.STRING, "username", "Username of the player you want to set as primary", true)
        };
    }
}
