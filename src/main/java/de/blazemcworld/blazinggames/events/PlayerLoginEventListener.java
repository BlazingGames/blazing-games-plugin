package de.blazemcworld.blazinggames.events;

import de.blazemcworld.blazinggames.discord.eventhandlers.DiscordLoginHandler;
import de.blazemcworld.blazinggames.events.base.BlazingEventListener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerLoginEventListener extends BlazingEventListener<PlayerLoginEvent> {
    public PlayerLoginEventListener() {
        this.handlers.add(new DiscordLoginHandler());
    }
}
