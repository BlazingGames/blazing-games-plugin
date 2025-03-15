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

package de.blazemcworld.blazinggames.userinterfaces;

import de.blazemcworld.blazinggames.BlazingGames;
import net.kyori.adventure.text.Component;

public abstract class PagedUserInterface extends UserInterface {
    private int page = 0;

    public PagedUserInterface(BlazingGames plugin, Component title, int rows) {
        super(plugin, title, rows);
    }

    public PagedUserInterface(BlazingGames plugin, String title, int rows) {
        super(plugin, title, rows);
    }

    public final int getCurrentPage() {
        return page;
    }

    // The return value needs to be non-negative. The max page is inclusive.
    public abstract int getMaxPage();

    public abstract int indicesPerPage();

    public final int getUnpagedIndex(int pagedIndex) {
        return pagedIndex + indicesPerPage() * getCurrentPage();
    }

    public final boolean changePage(int newPage) {
        if(!isWithinBounds(newPage)) {
            return false;
        }

        page = newPage;

        reload();

        return true;
    }

    public final boolean isWithinBounds(int page) {
        if(page < 0) return false;
        return page <= getMaxPage();
    }
}
