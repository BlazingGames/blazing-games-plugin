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

package de.blazemcworld.blazinggames.events.handlers.spawners;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.persistence.PersistentDataType;

public class SpawnerPlaceHandler extends BlazingEventHandler<BlockPlaceEvent> {
    @Override
    public boolean fitCriteria(BlockPlaceEvent event) {
        return event.getItemInHand().getType() == Material.SPAWNER;
    }

    @Override
    public void execute(BlockPlaceEvent event) {
        CreatureSpawner spawner = (CreatureSpawner) event.getBlock().getState();
        CreatureSpawner item = (CreatureSpawner) ((BlockStateMeta) event.getItemInHand().getItemMeta()).getBlockState();

        spawner.setSpawnedType(item.getSpawnedType());
        spawner.setSpawnCount(item.getSpawnCount());
        spawner.setDelay(item.getDelay());
        spawner.setMaxNearbyEntities(item.getMaxNearbyEntities());
        spawner.setMinSpawnDelay(item.getMinSpawnDelay());
        spawner.setMaxSpawnDelay(item.getMaxSpawnDelay());
        spawner.setRequiredPlayerRange(item.getRequiredPlayerRange());
        spawner.setSpawnRange(item.getSpawnRange());
        if (item.getPersistentDataContainer().getOrDefault(BlazingGames.get().key("redstone_control"), PersistentDataType.BOOLEAN, false))
            spawner.getPersistentDataContainer().set(BlazingGames.get().key("redstone_control"), PersistentDataType.BOOLEAN, true);

        spawner.update();
    }
}
