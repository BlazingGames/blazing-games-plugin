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

package de.blazemcworld.blazinggames.enchantments.eventhandlers;

import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantments;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentHelper;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ScavagerHandler extends BlazingEventHandler<EntityDeathEvent> {
    @Override
    public boolean fitCriteria(EntityDeathEvent event) {
        LivingEntity victim = event.getEntity();

        Player killer = victim.getKiller();
        if (killer == null) return false;

        ItemStack mainHand = killer.getInventory().getItemInMainHand();

        if (!event.isCancelled()) {
            int scavenger = EnchantmentHelper.getActiveEnchantmentWrapperLevel(mainHand, CustomEnchantments.SCAVENGER);
            return Math.random() < scavenger * 0.01;
        }
        return false;
    }

    @Override
    public void execute(EntityDeathEvent event) {
        LivingEntity victim = event.getEntity();

        victim.getWorld().playSound(victim, Sound.BLOCK_CHISELED_BOOKSHELF_PICKUP_ENCHANTED, 1, 0.5f);
        List<ItemStack> extraDrops = new ArrayList<>();
        for (ItemStack stack : event.getDrops()) {
            extraDrops.add(stack.clone());
        }
        event.getDrops().addAll(extraDrops);
    }
}
