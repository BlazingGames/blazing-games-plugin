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

import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantment;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentHelper;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentWrapper;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ExtraDamageHandler extends BlazingEventHandler<EntityDamageByEntityEvent> {
    @Override
    public boolean fitCriteria(EntityDamageByEntityEvent event, boolean cancelled) {
        return true;
    }

    @Override
    public void execute(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        Entity damager = event.getDamager();
        ItemStack weapon = ItemStack.empty();

        if (damager instanceof Player p) {
            weapon = p.getInventory().getItemInMainHand();
        } else if (damager instanceof Mob m) {
            weapon = m.getEquipment().getItemInMainHand();
        }

        double damageAdded = 0;
        for (Map.Entry<EnchantmentWrapper, Integer> enchantment : EnchantmentHelper.getActiveEnchantmentWrappers(weapon).entrySet()) {
            if(enchantment.getKey() instanceof CustomEnchantment ce) {
                damageAdded += ce.getDamageIncrease(victim, enchantment.getValue());
            }
        }

        if (damageAdded != 0) {
            if ((victim instanceof Player p && !(p.isBlocking() && event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) != 0)) || !(victim instanceof Player)) {
                event.setDamage(event.getDamage() + damageAdded);
            }
        }
    }
}
