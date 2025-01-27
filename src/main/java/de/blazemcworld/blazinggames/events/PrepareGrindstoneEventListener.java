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
import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantment;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentHelper;
import de.blazemcworld.blazinggames.items.CustomItem;
import de.blazemcworld.blazinggames.items.change.ItemChangeProviders;
import de.blazemcworld.blazinggames.items.predicates.BreakableItemPredicate;
import de.blazemcworld.blazinggames.items.predicates.ItemPredicates;
import de.blazemcworld.blazinggames.utils.Pair;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class PrepareGrindstoneEventListener implements Listener {
    @EventHandler
    public void onGrindstonePrepare(PrepareGrindstoneEvent event) {
        ItemStack up = event.getInventory().getUpperItem();
        ItemStack down = event.getInventory().getLowerItem();

        ItemStack result = grindstoneItem(up, down);

        event.setResult(result);
    }

    private ItemStack scrub(ItemStack tool, ItemStack sponge) {
        ItemStack result = tool.clone();

        if(!EnchantmentHelper.canEnchantItem(tool)) return null;

        if(!CustomItem.isCustomItem(result) && result.getType() == Material.ENCHANTED_BOOK) {
            int total = EnchantmentHelper.getEnchantmentWrappers(result).size();

            if(total <= 1) {
                return null;
            }
        }

        if(!CustomItem.isCustomItem(sponge) && sponge.getType() == Material.SPONGE) {
            Pair<Enchantment, Integer> entry = EnchantmentHelper.getEnchantmentEntryByIndex(tool, sponge.getAmount());

            if(entry != null) {
                if(result.getItemMeta() instanceof EnchantmentStorageMeta meta) {
                    meta.removeStoredEnchant(entry.left);
                    result.setItemMeta(meta);
                }
                else {
                    result.removeEnchantment(entry.left);
                }
            }
        }
        if(!CustomItem.isCustomItem(sponge) && sponge.getType() == Material.WET_SPONGE) {
            Pair<CustomEnchantment, Integer> entry = EnchantmentHelper.getCustomEnchantmentEntryByIndex(tool, sponge.getAmount());

            if(entry != null) {
                result = EnchantmentHelper.removeCustomEnchantment(result, entry.left);
            }
        }

        BlazingGames.get().log(result);

        if(result.equals(tool)) {
            return null;
        }

        result = ItemChangeProviders.update(result);

        return result;
    }

    public ItemStack grindstoneItem(ItemStack up, ItemStack down) {
        if(up == null || up.isEmpty()) {
            ItemStack swap = down;
            down = up;
            up = swap;
            if(up == null || up.isEmpty()) {
                return null;
            }
        }
        else if(down != null && !down.isEmpty())
        {
            // double
            if(ItemPredicates.grindstoneScrubber.matchItem(down)) {
                if(EnchantmentHelper.canEnchantItem(up)) {
                    return scrub(up, down);
                }
            }
            if(ItemPredicates.grindstoneScrubber.matchItem(up)) {
                if(EnchantmentHelper.canEnchantItem(down)) {
                    return scrub(down, up);
                }
            }

            CustomItem<?> customItem = CustomItem.getCustomItem(up);
            if(customItem != null) {
                if(!customItem.matchItem(down)) {
                    return null;
                }
            }
            else if(up.getType() != down.getType()) {
                return null;
            }
        }

        ItemStack result = up.clone();
        result = EnchantmentHelper.removeEnchantments(result);

        if(down != null && !down.isEmpty()) {
            if(BreakableItemPredicate.instance.matchItem(result)) {
                PrepareAnvilEventListener.repairByCombination(result, down, 5);
            }
            else if(result.isSimilar(EnchantmentHelper.removeEnchantments(down))) {
                if(result.getAmount() + down.getAmount() > result.getMaxStackSize()) {
                    return null;
                }
                result.add(down.getAmount());

                if(result.isSimilar(down) && result.isSimilar(up)) {
                    return null;
                }
            }
            else {
                return null;
            }
        }

        if(result.equals(up)) {
            return null;
        }

        return result;
    }
}


