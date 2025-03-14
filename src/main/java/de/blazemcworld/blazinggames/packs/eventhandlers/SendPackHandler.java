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

package de.blazemcworld.blazinggames.packs.eventhandlers;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.computing.api.BlazingAPI;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.packs.ResourcePackManager;
import net.kyori.adventure.text.Component;
import org.bukkit.event.player.PlayerJoinEvent;

public class SendPackHandler extends BlazingEventHandler<PlayerJoinEvent> {
    @Override
    public boolean fitCriteria(PlayerJoinEvent event, boolean cancelled) {
        return BlazingGames.get().getPackConfig() != null;
    }

    @Override
    public void execute(PlayerJoinEvent event) {
        ResourcePackManager.PackConfig config = BlazingGames.get().getPackConfig();
        byte[] sha1 = BlazingGames.get().getPackSha1();
        event.getPlayer().setResourcePack(
                config.uuid(),
                BlazingAPI.getConfig().apiConfig().findAt() + "/pack.zip",
                sha1,
                Component.text("Custom features require this resource pack."),
                true
        );
    }
}
