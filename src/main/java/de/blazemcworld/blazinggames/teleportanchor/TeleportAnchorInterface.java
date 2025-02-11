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

package de.blazemcworld.blazinggames.teleportanchor;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.userinterfaces.UserInterface;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TeleportAnchorInterface extends UserInterface {
    private final Player player;
    private final Map<Location, String> lodestones = new HashMap<>();

    public TeleportAnchorInterface(BlazingGames plugin, Player player) {
        super(plugin, "Teleport Anchor", 6);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public Map.Entry<Location, String> getLodestone(int index) {
        int current = 0;
        for(Map.Entry<Location, String> lodestone : lodestones.entrySet()) {
            if(current == index) {
                return lodestone;
            }
            current++;
        }

        return null;
    }

    @Override
    protected void preload() {
        for(int i = 0; i < 6*9; i++) {
            addSlot(i, new LodestoneSlot(i));
        }
    }

    @Override
    public void reload() {
        reloadLodestones();

        super.reload();
    }

    private void reloadLodestones() {
        this.lodestones.clear();

        this.lodestones.putAll(LodestoneStorage.getSavedLodestones(player.getUniqueId()));
    }
}
