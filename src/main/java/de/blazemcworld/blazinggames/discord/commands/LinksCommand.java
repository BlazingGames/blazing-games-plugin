package de.blazemcworld.blazinggames.discord.commands;

import de.blazemcworld.blazinggames.discord.DiscordApp;
import de.blazemcworld.blazinggames.discord.DiscordUser;
import de.blazemcworld.blazinggames.discord.WhitelistManagement;
import de.blazemcworld.blazinggames.discord.WhitelistedPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class LinksCommand implements ICommand {
    @Override
    public String name() {
        return "links";
    }
    
    @Override
    public String description() {
        return "View all of your linked minecraft accounts.";
    }

    @SuppressWarnings("null")
    @Override
    public void handle(SlashCommandInteractionEvent event) {
        if (!DiscordApp.isWhitelistManaged()) throw new IllegalStateException("Whitelist is not managed, but /links was called");
        WhitelistManagement whitelist = DiscordApp.getWhitelistManagement();

        DiscordUser user = whitelist.updateUser(event.getUser());

        List<WhitelistedPlayer> whitelistedPlayers = whitelist.getWhitelistedPlayersOfDiscordUser(user);

        StringBuilder embedDescription = new StringBuilder();

        embedDescription.append("* To change your primary account, run `/setprimary` (in discord).\n");
        embedDescription.append("* To unlink a minecraft account, run `/unlink` (in discord or in game).\n");
        embedDescription.append("* To link a new player, run `/whitelist` (in discord) with a link code obtained by joining the server with the desired account.");

        EmbedBuilder b = new EmbedBuilder()
                .setTitle("Linked Players")
                .setColor(0x78A6E3)
                .setDescription(embedDescription);

        if(whitelistedPlayers.isEmpty()) {
            b.addField(
                    "No linked players!", "", true
            );
        }
        else {
            for(WhitelistedPlayer whitelistedPlayer : whitelistedPlayers) {
                b.addField(
                        whitelistedPlayer.lastKnownName, "Primary: " +
                                (whitelistedPlayer.uuid.equals(user.favoriteAccount) ? "Yes": "No"), true
                );
            }
        }

        MessageEmbed embed = b.build();
        event.replyEmbeds(embed).setEphemeral(true).queue();
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[] {};
    }
}
