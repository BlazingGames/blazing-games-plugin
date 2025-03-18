/*
 * Copyright 2025 The Blazing Games Maintainers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.blazemcworld.blazinggames.events.handlers.player;

import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.items.recipes.RecipeProviders;
import de.blazemcworld.blazinggames.players.PlayerConfig;
import de.blazemcworld.blazinggames.utils.SkinRenderer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinHandler extends BlazingEventHandler<PlayerJoinEvent> {
    public static final TextColor color = TextColor.color(0xD1F990);

    @Override
    public boolean fitCriteria(PlayerJoinEvent event, boolean cancelled) {
        return true;
    }

    @Override
    public void execute(PlayerJoinEvent event) {
        event.getPlayer().discoverRecipes(RecipeProviders.instance.getRecipes().keySet());

        PlayerConfig config = PlayerConfig.forPlayer(event.getPlayer());
        config.updatePlayer();

        Component name = config.toDisplayTag(false).buildNameComponent();
        event.joinMessage(Component.empty().append(name).append(Component.text(" joined the game").color(color)));
        SkinRenderer.updateVanilla(event.getPlayer().getPlayerProfile());
    }
}
