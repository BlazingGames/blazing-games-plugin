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

package de.blazemcworld.blazinggames.events.handlers.block_breaking;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Tool;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class BlockBreakDamageToolHandler extends BlazingEventHandler<BlockBreakEvent> {
    @Override
    public boolean fitCriteria(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();

        return mainHand.hasData(DataComponentTypes.TOOL);
    }

    @Override
    public void execute(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();

        Tool toolComponent = mainHand.getData(DataComponentTypes.TOOL);
        if(toolComponent != null) {
            int damagePerBlock = toolComponent.damagePerBlock();
            if(mainHand.getType() == Material.SHEARS) {
                if(event.getBlock().getType() != Material.FIRE && event.getBlock().getType() != Material.SOUL_FIRE) {
                    Bukkit.getScheduler().runTask(BlazingGames.get(), () -> player.damageItemStack(EquipmentSlot.HAND, 1));
                }
            }
            else if(event.getBlock().getType().getHardness() > 0) {
                Bukkit.getScheduler().runTask(BlazingGames.get(), () -> player.damageItemStack(EquipmentSlot.HAND, damagePerBlock));
            }
        }
    }
}
