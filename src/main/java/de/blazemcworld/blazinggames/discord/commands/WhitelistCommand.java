package de.blazemcworld.blazinggames.discord.commands;

import java.util.UUID;

import de.blazemcworld.blazinggames.discord.DiscordApp;
import de.blazemcworld.blazinggames.discord.WhitelistManagement;
import de.blazemcworld.blazinggames.utils.Pair;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class WhitelistCommand implements ICommand {
    @Override
    public String name() {
        return "whitelist";
    }
    
    @Override
    public String description() {
        return "Add your account(s) to the minecraft whitelist.";
    }

    @SuppressWarnings("null")
    @Override
    public void handle(SlashCommandInteractionEvent event) {
        if (!DiscordApp.isWhitelistManaged()) throw new IllegalStateException("Whitelist is not managed, but /whitelist was called");
        WhitelistManagement whitelist = DiscordApp.getWhitelistManagement();

        String code = event.getOption("code").getAsString();
        Pair<String, UUID> user = whitelist.getLinkCodeUser(code);
        if (user == null) {
            event.reply("Invalid link code. If you don't have one, please join the server and your kick message will include one. It also may be expired.").setEphemeral(true).queue();
            return;
        }
        String username = user.left;
        UUID uuid = user.right;

        boolean isNewPrimary;

        

        if (whitelist.getDiscordUser(event.getMember().getIdLong()) == null) {
            isNewPrimary = true;
        } else if (event.getOption("primary") != null) {
            isNewPrimary = event.getOption("primary").getAsBoolean();
        } else {
            isNewPrimary = false;
        }

        StringBuilder embedDescription = new StringBuilder();
        embedDescription.append("The player ").append(username).append(" has been whitelisted and linked to your discord account.\n\n");
        if (isNewPrimary) {
            embedDescription.append("This is your new primary account. You may send messages in the chat link channel with this account and they will send as ").append(username);
        } else {
            embedDescription.append("Your primary account was unaffected. Sending messages in the chat link channel will still send as ").append(whitelist.getWhitelistedPlayer(whitelist.getDiscordUser(event.getMember().getIdLong()).favoriteAccount).lastKnownName);
        }
        embedDescription.append(".\n\n");
        embedDescription.append("* To change your primary account, run `/setprimary` (in discord).\n");
        embedDescription.append("* To unlink a minecraft account, run `/unlink` (in discord or in game).");

        MessageEmbed embed = new EmbedBuilder()
            .setTitle("Whitelist")
            .setColor(0x7BF283)
            .setDescription(embedDescription)
            .setImage("https://crafatar.com/renders/head/" + uuid + ".png?overlay")
            .build();
        event.replyEmbeds(embed).setEphemeral(true).queue();
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[] {
            new OptionData(OptionType.STRING, "code", "Link code (join the server to see this)", true),
            new OptionData(OptionType.BOOLEAN, "primary", "Use this account to proxy messages for discord to minecraft?", false)
        };
    }
}
