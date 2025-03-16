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

package de.blazemcworld.blazinggames.warpstones;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.userinterfaces.PageSlot;
import de.blazemcworld.blazinggames.userinterfaces.PagedUserInterface;
import de.blazemcworld.blazinggames.userinterfaces.StaticUserInterfaceSlot;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TeleportAnchorInterface extends PagedUserInterface {
    private static final int rows = 3; // number of rows to show warpstones. 1 is added for controls
    private final Player player;
    private final Map<Location, WarpstoneOverrideDetails> warpstones = new HashMap<>();

    public TeleportAnchorInterface(BlazingGames plugin, Player player) {
        super(plugin, "Teleport Anchor", rows + 1);
        this.player = player;
    }

    private static final PageSlot next = new PageSlot(PageSlot.Arrow.RIGHT, PageSlot.Direction.FORWARD);
    private static final PageSlot back = new PageSlot(PageSlot.Arrow.LEFT, PageSlot.Direction.BACKWARD);

    public Player getPlayer() {
        return player;
    }

    public Map.Entry<Location, WarpstoneOverrideDetails> getWarpstone(int index) {
        int current = 0;
        for(Map.Entry<Location, WarpstoneOverrideDetails> warpstone : warpstones.entrySet()) {
            if(current == index) {
                return warpstone;
            }
            current++;
        }

        return null;
    }

    @Override
    protected void preload() {
        for(int i = 0; i < rows*9; i++) {
            addSlot(i, new TeleportAnchorSlot(i));
        }

        addSlot(0, rows, StaticUserInterfaceSlot.blank);
        addSlot(1, rows, StaticUserInterfaceSlot.blank);
        addSlot(2, rows, StaticUserInterfaceSlot.blank);
        addSlot(3, rows, back);
        addSlot(4, rows, StaticUserInterfaceSlot.blank);
        addSlot(5, rows, next);
        addSlot(6, rows, StaticUserInterfaceSlot.blank);
        addSlot(7, rows, StaticUserInterfaceSlot.blank);
        addSlot(8, rows, StaticUserInterfaceSlot.blank);
    }

    @Override
    public void reload() {
        reloadWarpstones();

        super.reload();
    }

    private void reloadWarpstones() {
        this.warpstones.clear();
        this.warpstones.putAll(WarpstoneStorage.getSavedWarpstones(player));
    }

    @Override
    public int getMaxPage() {
        int len = warpstones.size();

        if (len == 0) {
            return 0;
        }

        return (len - 1) / indicesPerPage();
    }

    @Override
    public int indicesPerPage() {
        return rows * 9;
    }
}
