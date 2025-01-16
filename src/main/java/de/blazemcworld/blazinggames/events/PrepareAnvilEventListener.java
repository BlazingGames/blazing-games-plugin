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

import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentHelper;
import de.blazemcworld.blazinggames.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class PrepareAnvilEventListener implements Listener {
    @EventHandler
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        event.getView().setMaximumRepairCost(Integer.MAX_VALUE);
        if (event.getView().getRepairCost() > 10) {
            event.getView().setRepairCost(10);
        }
        
        ItemStack in = event.getInventory().getFirstItem();
        ItemStack enchantingItem = event.getInventory().getSecondItem();
        ItemStack result = event.getInventory().getResult();

        if(in == null || in.isEmpty() || enchantingItem == null || enchantingItem.isEmpty()) {
            return;
        }

        if ((in.getType() == Material.BOOK || in.getType() == Material.ENCHANTED_BOOK) &&
            (enchantingItem.getType() == Material.BOOK || enchantingItem.getType() == Material.ENCHANTED_BOOK)) {
            event.setResult(null);
            return;
        }

        if(in.getType() == Material.FIREWORK_ROCKET) {
            if(enchantingItem.getType() == Material.ENCHANTED_BOOK) {
                if(enchantingItem.hasItemMeta() && enchantingItem.getItemMeta() instanceof EnchantmentStorageMeta esm) {
                    if(esm.hasStoredEnchant(Enchantment.INFINITY)) {
                        result = in.clone();
                        result.addUnsafeEnchantment(Enchantment.INFINITY, 1);
                        event.setResult(result);
                        return;
                    }
                }
            }
        }

        if(in.getAmount() != 1 || enchantingItem.getAmount() != 1) {
            return;
        }

        if(result == null || result.isEmpty()) {
            result = in.clone();
        }

        if(!EnchantmentHelper.canEnchantItem(result)) {
            return;
        }

        if(CustomItem.isCustomItem(enchantingItem) || enchantingItem.getType() != Material.ENCHANTED_BOOK
                && enchantingItem.getType() != result.getType()) {
            return;
        }

        result = EnchantmentHelper.enchantFromItem(result, enchantingItem);

        if(result.equals(in)) {
            return;
        }

        event.setResult(result);
    }
}
