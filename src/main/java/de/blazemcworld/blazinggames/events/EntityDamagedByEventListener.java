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
package de.blazemcworld.blazinggames.events;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.computing.ComputerRegistry;
import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantment;
import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantments;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentHelper;
import de.blazemcworld.blazinggames.items.ContextlessItem;
import de.blazemcworld.blazinggames.items.CustomItem;
import de.blazemcworld.blazinggames.items.CustomItems;
import de.blazemcworld.blazinggames.utils.InventoryUtils;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class EntityDamagedByEventListener implements Listener {
    @SuppressWarnings("deprecation")
    @EventHandler
    public void onEntityDeath(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        Entity damager = event.getDamager();

        ItemStack weapon = ItemStack.empty();

        if (damager instanceof Player attacker && ComputerRegistry.getComputerByActorUUID(victim.getUniqueId()) != null) {
            event.setCancelled(true);
            ComputerRegistry.getComputerByActorUUID(victim.getUniqueId()).damageHookAddHit(attacker);
        }

        if(damager instanceof Player p) {
            weapon = p.getInventory().getItemInMainHand();
        }
        else if(damager instanceof Mob m) {
            weapon = m.getEquipment().getItemInMainHand();
        }

        double damageAdded = 0;
        for(Map.Entry<CustomEnchantment, Integer> enchantment : EnchantmentHelper.getActiveCustomEnchantments(weapon).entrySet()) {
            damageAdded += enchantment.getKey().getDamageIncrease(victim, enchantment.getValue());
        }

        if (damageAdded != 0) {
            if ((victim instanceof Player p && !(p.isBlocking() && event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) != 0)) || !(victim instanceof Player)) {
                event.setDamage(event.getDamage() + damageAdded);
            }
        }

        if(victim instanceof Player p && damager instanceof Damageable damageable) {
            if(p.isBlocking() && event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) != 0) {
                ItemStack shield = p.getActiveItem();
                int reflectiveDefenses = EnchantmentHelper.getActiveCustomEnchantmentLevel(shield, CustomEnchantments.REFLECTIVE_DEFENSES);

                if(reflectiveDefenses != 0 && new Random().nextInt(Math.max(7 - reflectiveDefenses, 2)) == 0) {
                    double dmg = Math.abs(event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING)) * 0.15;

                    if(dmg < 5) dmg = 5;

                    damageable.damage(dmg, p);
                    shield = shield.damage(10, p);
                    p.getInventory().setItem(p.getActiveItemHand(), shield);

                    p.getWorld().playSound(p, Sound.ENCHANT_THORNS_HIT, 1, 1.5f);
                }
            }
        } else if (damager instanceof Player p) {
            PersistentDataContainer container = victim.getPersistentDataContainer();

            if (container.has(BlazingGames.get().key("slab")) && container.has(BlazingGames.get().key("slab_type"))) {
                event.setCancelled(true);
                UUID displayBlockUUID = UUID.fromString(Objects.requireNonNull(container.get(BlazingGames.get().key("slab"), PersistentDataType.STRING)));
                String slabType = container.get(BlazingGames.get().key("slab_type"), PersistentDataType.STRING);
                BlockDisplay displayBlock = (BlockDisplay) p.getWorld().getEntity(displayBlockUUID);

                if (displayBlock == null) return;
                Location blockLocation = displayBlock.getLocation().toCenterLocation();
                Sound breakSound = displayBlock.getBlock().getSoundGroup().getBreakSound();
                displayBlock.remove();

                double y = victim.getY();
                p.getWorld().getNearbyEntitiesByType(Shulker.class, blockLocation, 0.5).forEach(shulker -> {
                    PersistentDataContainer c = shulker.getPersistentDataContainer();
                    if (c.has(BlazingGames.get().key("slab")) && c.has(BlazingGames.get().key("slab_type")) && y == shulker.getLocation().getY()) {
                        Objects.requireNonNull(shulker.getVehicle()).remove();
                        shulker.remove();
                    }
                });

                CustomItem<?> slab = CustomItems.getByKey(BlazingGames.get().key(slabType + "_slab"));

                if(slab instanceof ContextlessItem contextlessSlab)
                {
                    if (p.getGameMode() != GameMode.CREATIVE) {
                        InventoryUtils.collectableDrop(p, blockLocation, contextlessSlab.create());
                    }
                }
                else
                {
                    return;
                }

                p.getWorld().playSound(blockLocation, breakSound, 1, 1);

                if (p.getWorld().getNearbyEntitiesByType(Shulker.class, blockLocation, 0.5).isEmpty())
                    Bukkit.getScheduler().runTask(BlazingGames.get(), () -> p.getWorld().getBlockAt(blockLocation).setType(Material.AIR));
            }
        }
    }
}
