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

package de.blazemcworld.blazinggames.events.handlers.villagers;

import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class VillagerRClickHandler extends BlazingEventHandler<PlayerInteractEntityEvent> {
    @Override
    public boolean fitCriteria(PlayerInteractEntityEvent event, boolean cancelled) {
        if (event.getRightClicked() instanceof Villager) {
            PlayerInventory inventory = event.getPlayer().getInventory();
            return inventory.getItemInMainHand().getType() == Material.LEAD;
        }
        return false;
    }

    @Override
    public void execute(PlayerInteractEntityEvent event) {
        Villager villager = (Villager) event.getRightClicked();
        PlayerInventory inventory = event.getPlayer().getInventory();
        event.setCancelled(true);

        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            ItemStack itemStack = inventory.getItemInMainHand();
            itemStack.setAmount(itemStack.getAmount() - 1);
            if (itemStack.getAmount() == 0) itemStack = new ItemStack(Material.AIR);
            inventory.setItemInMainHand(itemStack);
        }

        villager.setLeashHolder(event.getPlayer());
    }
}
