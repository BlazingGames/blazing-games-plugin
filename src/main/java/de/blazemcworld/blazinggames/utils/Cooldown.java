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
package de.blazemcworld.blazinggames.utils;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class Cooldown {
    private final HashMap<Player, Integer> cooldown = new HashMap<>();

    public Cooldown(Plugin plugin) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::updateCooldowns, 1, 1);
    }

    public void setCooldown(Player p, int ticks) {
        cooldown.put(p, ticks);
    }

    public void updateCooldowns() {
        for(Player p : cooldown.keySet()) {
            int ticks = cooldown.get(p);
            ticks--;
            if(ticks <= 0) {
                cooldown.remove(p);
            }
            else {
                cooldown.put(p, ticks);
            }
        }
    }

    public boolean onCooldown(Player p) {
        return cooldown.getOrDefault(p, 0) > 0;
    }
}
