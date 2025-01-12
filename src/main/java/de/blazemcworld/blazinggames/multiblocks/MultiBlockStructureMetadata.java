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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MultiBlockStructureMetadata implements MultiBlockStructureMatcher, Keyed {
    private final MultiBlockStructureMetadatable structure;
    private final NamespacedKey key;
    private final String name;
    private final Style style;

    public MultiBlockStructureMetadata(NamespacedKey key, String name, Style style, MultiBlockStructureMetadatable structure) {
        this.structure = structure;
        this.key = key;
        this.name = name;
        this.style = style;
    }

    @Override
    public List<Component> getProgress(Location loc) {
        ArrayList<Component> components = new ArrayList<>(structure.getProgress(loc));

        Component title = Component.text(name + ": ").style(style);

        if(components.isEmpty()) {
            components.add(Component.text("No response...").color(NamedTextColor.DARK_GRAY).decorate(TextDecoration.ITALIC));
        }

        Component component = Component.join(JoinConfiguration.builder().build(), title, components.get(0));

        components.set(0, component);

        return components;
    }

    @Override
    public int match(Location location) {
        return structure.match(location);
    }

    @Override
    public int match(Location location, int direction) {
        return structure.match(location, direction);
    }

    @Override
    public boolean matchTarget(Location location) {
        return structure.matchTarget(location);
    }

    @Override
    public boolean matchTarget(Location location, int direction) {
        return structure.matchTarget(location, direction);
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    public MultiBlockStructureMetadatable getStructure() {
        return structure;
    }
}
