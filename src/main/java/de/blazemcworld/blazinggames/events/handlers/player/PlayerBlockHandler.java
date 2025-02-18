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

package de.blazemcworld.blazinggames.events.handlers.player;

import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantments;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentHelper;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class PlayerBlockHandler extends BlazingEventHandler<EntityDamageByEntityEvent> {
    @Override
    public boolean fitCriteria(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        Entity damager = event.getDamager();

        if (victim instanceof Player p && damager instanceof Damageable) {
            if (p.isBlocking() && event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) != 0) {
                ItemStack shield = p.getActiveItem();
                int reflectiveDefenses = EnchantmentHelper.getActiveEnchantmentWrapperLevel(shield, CustomEnchantments.REFLECTIVE_DEFENSES);
                return reflectiveDefenses != 0 && new Random().nextInt(Math.max(7 - reflectiveDefenses, 2)) == 0;
            }
        }
        return false;
    }

    @Override
    public void execute(EntityDamageByEntityEvent event) {
        Player p = (Player) event.getEntity();
        Damageable damageable = (Damageable) event.getDamager();
        ItemStack shield = p.getActiveItem();

        double dmg = Math.abs(event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING)) * 0.15;

        if (dmg < 5) dmg = 5;

        damageable.damage(dmg, p);
        shield = shield.damage(10, p);
        p.getInventory().setItem(p.getActiveItemHand(), shield);

        p.getWorld().playSound(p, Sound.ENCHANT_THORNS_HIT, 1, 1.5f);
    }
}
