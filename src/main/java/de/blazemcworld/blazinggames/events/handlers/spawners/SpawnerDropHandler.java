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
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentHelper;
import de.blazemcworld.blazinggames.enchantments.sys.VanillaEnchantmentWrappers;
import de.blazemcworld.blazinggames.events.BlazingBlockDropEvent;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.utils.Drops;
import de.blazemcworld.blazinggames.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.persistence.PersistentDataType;

public class SpawnerDropHandler extends BlazingEventHandler<BlazingBlockDropEvent> {
    @Override
    public boolean fitCriteria(BlazingBlockDropEvent event, boolean cancelled) {
        return event.getBlock().getType() == Material.SPAWNER && ItemUtils.getUncoloredType(event.getTool()) == Material.WOODEN_PICKAXE
                && EnchantmentHelper.getActiveEnchantmentWrapperLevel(event.getTool(), VanillaEnchantmentWrappers.SILK_TOUCH) > 1;
    }

    @Override
    public void execute(BlazingBlockDropEvent event) {
        Drops drops = event.getDrops();
        Block block = event.getBlock();

        drops.setExperience(0);
        CreatureSpawner spawner = (CreatureSpawner) block.getState();
        ItemStack item = new ItemStack(Material.SPAWNER);
        BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
        meta.setBlockState(block.getState());
        if (spawner.getPersistentDataContainer().getOrDefault(BlazingGames.get().key("redstone_control"), PersistentDataType.BOOLEAN, false))
            meta.getPersistentDataContainer().set(BlazingGames.get().key("redstone_control"), PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);

        drops.add(item);
    }
}
