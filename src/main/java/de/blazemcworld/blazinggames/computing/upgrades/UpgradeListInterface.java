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
package de.blazemcworld.blazinggames.computing.upgrades;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.computing.ComputerEditor;
import de.blazemcworld.blazinggames.computing.ComputerMetadata;
import de.blazemcworld.blazinggames.userinterfaces.StaticUserInterfaceSlot;
import de.blazemcworld.blazinggames.userinterfaces.UserInterface;

import java.util.ArrayList;

public class UpgradeListInterface extends UserInterface {
    private static final int rows = 3;

    public final String computerId;
    public final int maxSlots;
    final ArrayList<UpgradeType> slots;
    public UpgradeListInterface(BlazingGames plugin, String computerId, int maxSlots) {
        super(plugin, "Installed Upgrades (" + maxSlots + " slots)", rows);
        this.computerId = computerId;
        this.slots = new ArrayList<>();
        this.maxSlots = maxSlots;
        preload();
        refresh();
    }
    
    @Override
    protected void preload() {
        for (int i = 0; i < rows * 9; i++) {
            if (i >= 13 - Math.floor(maxSlots / 2.0) && i <= 13 + Math.floor(maxSlots / 2.0))
                addSlot(i, new UpgradeListSlot());
            else addSlot(i, StaticUserInterfaceSlot.blank);
        }
    }

    @Override
    protected void reload() {
        refresh();
        super.reload();
    }

    public void refresh() {
        slots.clear();
        ComputerMetadata metadata = ComputerEditor.getMetadata(computerId);
        if (metadata != null) {
            slots.addAll(metadata.upgrades);
        }
    }
}