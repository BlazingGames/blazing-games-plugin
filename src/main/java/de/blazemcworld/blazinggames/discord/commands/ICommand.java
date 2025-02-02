package de.blazemcworld.blazinggames.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public interface ICommand {
    String name();
    String description();
    void handle(SlashCommandInteractionEvent event);
    OptionData[] getOptions();
}
