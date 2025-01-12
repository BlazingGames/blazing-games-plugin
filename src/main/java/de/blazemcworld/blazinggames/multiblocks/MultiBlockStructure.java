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

import de.blazemcworld.blazinggames.BlazingGames;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiBlockStructure implements MultiBlockStructureMetadatable {
    Map<Vector, BlockPredicate> predicates = new HashMap<>();

    public MultiBlockStructure add(Vector vec, BlockPredicate predicate) {
        if(!predicates.containsKey(vec)) {
            predicates.put(vec, predicate);
        }
        return this;
    }

    public MultiBlockStructure addArea(Vector vec1, Vector vec2, BlockPredicate predicate) {
        int x1 = vec1.getBlockX();
        int x2 = vec2.getBlockX();
        int y1 = vec1.getBlockY();
        int y2 = vec2.getBlockY();
        int z1 = vec1.getBlockZ();
        int z2 = vec2.getBlockZ();

        if(x1 > x2) {
            int temp = x1;
            x1 = x2;
            x2 = temp;
        }
        if(y1 > y2) {
            int temp = y1;
            y1 = y2;
            y2 = temp;
        }
        if(z1 > z2) {
            int temp = z1;
            z1 = z2;
            z2 = temp;
        }

        for(int x = x1; x <= x2; x++) {
            for(int y = y1; y <= y2; y++) {
                for(int z = z1; z <= z2; z++) {
                    add(new Vector(x, y, z), predicate);
                }
            }
        }

        return this;
    }

    @Override
    public int match(Location location, int direction) {
        boolean matched = true;

        for(Map.Entry<Vector, BlockPredicate> entry : predicates.entrySet()) {
            Vector vec = entry.getKey().clone().rotateAroundAxis(new Vector(0, 1, 0), direction * Math.PI/2);

            Location loc = location.toBlockLocation().add(vec).toBlockLocation();

            BlockState state = loc.getBlock().getState();

            if(!entry.getValue().matchBlock(state, direction)) {
                BlazingGames.get().debugLog("Failed block predicate! (Direction: "+direction+") " + loc + "=" + entry.getValue());

                matched = false;
                break;
            }
        }

        if(matched) return 1;

        return 0;
    }

    @Override
    public boolean matchTarget(Location location, int direction) {
        Vector target = new Vector(0,0,0);

        for(Map.Entry<Vector, BlockPredicate> predicate : predicates.entrySet()) {
            if(predicate.getKey().equals(target)) {
                if(predicate.getValue().matchBlock(location.toBlockLocation().getBlock().getState(), direction)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public List<Component> getProgress(Location loc) {
        boolean matched = match(loc.toBlockLocation()) > 0;

        Component component = Component.text(matched ? "VALID" : "INVALID")
                                       .color(matched ? NamedTextColor.GREEN : NamedTextColor.RED);

        return List.of(component);
    }

    public Map<Vector, BlockPredicate> getPredicates() { return predicates; }
}

