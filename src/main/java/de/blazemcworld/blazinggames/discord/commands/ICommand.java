package de.blazemcworld.blazinggames.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public interface ICommand {
    String name();
    String description();
    void handle(SlashCommandInteractionEvent event);
    OptionData[] getOptions();

    default void register(CommandListUpdateAction commandsAction) {
        SlashCommandData data = Commands.slash(name(), description()).setGuildOnly(true);
        for(OptionData option : getOptions()) {
            data.addOption(option.getType(), option.getName(), option.getDescription(), option.isRequired(), option.isAutoComplete());
        }
        commandsAction.addCommands(data);
    }
}
