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
package de.blazemcworld.blazinggames.multiblocks;

import de.blazemcworld.blazinggames.utils.NumberUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class MultiLevelBlockStructure implements MultiBlockStructureMetadatable {
    List<MultiBlockStructure> levels = new ArrayList<>();

    public MultiLevelBlockStructure add(MultiBlockStructure... structure) {
        levels.addAll(List.of(structure));
        return this;
    }

    public MultiLevelBlockStructure(MultiBlockStructure... structure) {
        add(structure);
    }

    @Override
    public int match(Location location, int direction) {
        for(int i = 0; i < levels.size(); i++) {
            if(levels.get(i).match(location, direction) <= 0) {
                return i;
            }
        }
        return levels.size();
    }

    @Override
    public boolean matchTarget(Location location, int direction) {
        for (MultiBlockStructure level : levels) {
            if (level.matchTarget(location, direction)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Component> getProgress(Location loc) {
        int matched = match(loc);

        String tier = NumberUtils.getRomanNumber(matched) + "/" + NumberUtils.getRomanNumber(levels.size());

        TextColor color = NamedTextColor.GREEN;
        if(matched == 0) {
            color = NamedTextColor.RED;
        }
        if(matched < levels.size()) {
            color = NamedTextColor.YELLOW;
        }

        Component component = Component.text(tier)
                .color(color);

        return List.of(component);
    }

    public List<MultiBlockStructure> getLevels() {
        return levels;
    }
}

