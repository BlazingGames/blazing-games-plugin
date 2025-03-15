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
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CapturingHandler extends BlazingEventHandler<EntityDeathEvent> {
    private static final List<Material> EGG_BLACKLIST = List.of(
            Material.ENDER_DRAGON_SPAWN_EGG,
            Material.WITHER_SPAWN_EGG,
            Material.ELDER_GUARDIAN_SPAWN_EGG,
            Material.WARDEN_SPAWN_EGG
    );

    @Override
    public boolean fitCriteria(EntityDeathEvent event, boolean cancelled) {
        LivingEntity victim = event.getEntity();

        Player killer = victim.getKiller();
        if (killer == null) return false;

        ItemStack mainHand = killer.getInventory().getItemInMainHand();

        if (!event.isCancelled()) {
            int capturing = EnchantmentHelper.getActiveEnchantmentWrapperLevel(mainHand, CustomEnchantments.CAPTURING);
            if (Math.random() < capturing * 0.03 && !victim.getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER)) {
                Material spawnEgg = Material.getMaterial(victim.getType().getKey().getKey().toUpperCase() + "_SPAWN_EGG");
                return spawnEgg != null && !EGG_BLACKLIST.contains(spawnEgg);
            }
        }
        return false;
    }

    @Override
    public void execute(EntityDeathEvent event) {
        LivingEntity victim = event.getEntity();
        Material spawnEgg = Material.getMaterial(victim.getType().getKey().getKey().toUpperCase() + "_SPAWN_EGG");
        if (spawnEgg == null) return;

        victim.getWorld().playSound(victim, Sound.ENTITY_ITEM_PICKUP, 1, 0.75f);
        event.getDrops().add(new ItemStack(spawnEgg));
    }
}
