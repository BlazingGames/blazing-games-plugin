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

package de.blazemcworld.blazinggames.events.handlers.tomes;

import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.items.CustomItems;
import org.bukkit.NamespacedKey;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.loot.LootTables;

import java.util.Random;

public class GustTomeHandler extends BlazingEventHandler<LootGenerateEvent> {
    @Override
    public boolean fitCriteria(LootGenerateEvent event) {
        NamespacedKey key = event.getLootTable().getKey();
        return key.equals(LootTables.END_CITY_TREASURE.getKey());
    }

    @Override
    public void execute(LootGenerateEvent event) {
        Random random = new Random();
        int getBook = random.nextInt(100) + 1;
        if (getBook <= 11) {
            event.getLoot().add(CustomItems.GUST_TOME.create());
        }
    }
}
