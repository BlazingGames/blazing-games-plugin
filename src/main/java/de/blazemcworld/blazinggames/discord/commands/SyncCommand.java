package de.blazemcworld.blazinggames.discord.commands;

import de.blazemcworld.blazinggames.discord.DiscordApp;
import de.blazemcworld.blazinggames.discord.WhitelistManagement;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class SyncCommand implements ICommand {
    @Override
    public String name() {
        return "sync";
    }
    
    @Override
    public String description() {
        return "Updates the data that the server and the bot know about you.";
    }

    @SuppressWarnings("null")
    @Override
    public void handle(SlashCommandInteractionEvent event) {
        if (!DiscordApp.isWhitelistManaged()) throw new IllegalStateException("Whitelist is not managed, but /sync was called");
        WhitelistManagement whitelist = DiscordApp.getWhitelistManagement();

        whitelist.updateUser(event.getUser());

        event.reply("Successfully updated the data that the server and the bot know about you!").setEphemeral(true).queue();
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[] {};
    }
}
