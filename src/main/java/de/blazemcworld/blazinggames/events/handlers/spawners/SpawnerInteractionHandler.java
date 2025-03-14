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
import de.blazemcworld.blazinggames.items.CustomItem;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class SpawnerInteractionHandler extends BlazingEventHandler<PlayerInteractEvent> {
    @Override
    public boolean fitCriteria(PlayerInteractEvent event, boolean cancelled) {
        ItemStack eventItem = event.getItem();
        EquipmentSlot hand = event.getHand();
        Block block = event.getClickedBlock();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block != null && hand != null && eventItem != null) {
            return block.getType() == Material.SPAWNER;
        }
        return false;
    }

    @Override
    public void execute(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        EquipmentSlot hand = event.getHand();

        ItemStack mainHand = event.getItem();
        if (mainHand == null) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        CreatureSpawner spawner = (CreatureSpawner) block.getState();


        if (hand != EquipmentSlot.HAND) {
            return;
        }

        if (CustomItem.isCustomItem(mainHand)) {
            return;
        }

        boolean inverted = player.getInventory().getItemInOffHand().getType().equals(Material.QUARTZ);

        boolean successful = false;

        if (mainHand.getType() == Material.SUGAR && (
                (spawner.getMinSpawnDelay() - 5 > 0 && !inverted && spawner.getMinSpawnDelay() - 5 <= spawner.getMaxSpawnDelay()) ||
                        (spawner.getMinSpawnDelay() + 5 <= 1000 && inverted && spawner.getMinSpawnDelay() + 5 <= spawner.getMaxSpawnDelay())
        )) {
            if (!inverted) spawner.setMinSpawnDelay(spawner.getMinSpawnDelay() - 5);
            else spawner.setMinSpawnDelay(spawner.getMinSpawnDelay() + 5);
            successful = true;
        }
        if (mainHand.getType() == Material.CLOCK && (
                (spawner.getMaxSpawnDelay() - 5 > 0 && !inverted && spawner.getMinSpawnDelay() <= spawner.getMaxSpawnDelay() - 5) ||
                        (spawner.getMaxSpawnDelay() + 5 <= 1000 && inverted && spawner.getMinSpawnDelay() <= spawner.getMaxSpawnDelay() + 5)
        )) {
            if (!inverted) spawner.setMaxSpawnDelay(spawner.getMaxSpawnDelay() - 5);
            else spawner.setMaxSpawnDelay(spawner.getMaxSpawnDelay() + 5);
            successful = true;
        }
        if (mainHand.getType() == Material.FERMENTED_SPIDER_EYE && (
                (spawner.getSpawnCount() + 1 <= 20 && !inverted) ||
                        (spawner.getSpawnCount() - 1 > 0 && inverted)
        )) {
            if (!inverted) spawner.setSpawnCount(spawner.getSpawnCount() + 1);
            else spawner.setSpawnCount(spawner.getSpawnCount() - 1);
            successful = true;
        }
        if (mainHand.getType() == Material.GHAST_TEAR && (
                (spawner.getMaxNearbyEntities() + 2 <= 200 && !inverted) ||
                        (spawner.getMaxNearbyEntities() - 2 > 0 && inverted && spawner.getMaxNearbyEntities() != 32767)
        )) {
            if (!inverted) spawner.setMaxNearbyEntities(spawner.getMaxNearbyEntities() + 2);
            else spawner.setMaxNearbyEntities(spawner.getMaxNearbyEntities() - 2);
            successful = true;
        }
        if (mainHand.getType() == Material.PRISMARINE_CRYSTALS && (
                (spawner.getRequiredPlayerRange() + 2 <= 50 && !inverted) ||
                        (spawner.getRequiredPlayerRange() - 2 > 0 && inverted && spawner.getRequiredPlayerRange() != 32767)
        )) {
            if (!inverted) spawner.setRequiredPlayerRange(spawner.getRequiredPlayerRange() + 2);
            else spawner.setRequiredPlayerRange(spawner.getRequiredPlayerRange() - 2);
            successful = true;
        }
        if (mainHand.getType() == Material.BLAZE_ROD && (
                (spawner.getSpawnRange() + 1 <= 20 && !inverted) ||
                        (spawner.getSpawnRange() - 1 > 0 && inverted)
        )) {
            if (!inverted) spawner.setSpawnRange(spawner.getSpawnRange() + 1);
            else spawner.setSpawnRange(spawner.getSpawnRange() - 1);
            successful = true;
        }
        if (mainHand.getType() == Material.NETHER_STAR && (
                (spawner.getRequiredPlayerRange() != 32767 && !inverted) ||
                        (spawner.getRequiredPlayerRange() == 32767 && inverted)
        )) {
            if (!inverted) spawner.setRequiredPlayerRange(32767);
            else spawner.setRequiredPlayerRange(16);
            successful = true;
        }
        if (mainHand.getType() == Material.CHORUS_FRUIT && (
                (spawner.getMaxNearbyEntities() != 32767 && !inverted) ||
                        (spawner.getMaxNearbyEntities() == 32767 && inverted)
        )) {
            if (!inverted) spawner.setMaxNearbyEntities(32767);
            else spawner.setMaxNearbyEntities(6);
            successful = true;
        }
        PersistentDataContainer dataContainer = spawner.getPersistentDataContainer();
        if (mainHand.getType() == Material.COMPARATOR &&
                inverted == dataContainer.getOrDefault(BlazingGames.get().key("redstone_control"), PersistentDataType.BOOLEAN, false)
        ) {
            if (!inverted)
                dataContainer.set(BlazingGames.get().key("redstone_control"), PersistentDataType.BOOLEAN, true);
            else dataContainer.remove(BlazingGames.get().key("redstone_control"));
            successful = true;
        }

        if (successful) {
            if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
                mainHand.subtract();
                player.getInventory().setItem(hand, mainHand);
            }
        }

        spawner.update();
    }
}
