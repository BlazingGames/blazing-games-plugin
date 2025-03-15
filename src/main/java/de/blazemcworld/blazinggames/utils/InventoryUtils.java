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
package de.blazemcworld.blazinggames.utils;

import de.blazemcworld.blazinggames.computing.ComputerRegistry;
import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantments;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentHelper;
import de.blazemcworld.blazinggames.events.BlockBreakEventListener;
import de.blazemcworld.blazinggames.items.CustomItem;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class InventoryUtils {
    public static boolean canFitItem(Inventory inventory, ItemStack stack) {
        stack = stack.clone();
        for (ItemStack curr : inventory) {
            if(curr == null || curr.isEmpty()) {
                return true;
            }
            if(curr.isSimilar(stack)) {
                int total = curr.getAmount() + stack.getAmount();
                if(total <= curr.getMaxStackSize()) {
                    return true;
                }
                stack.setAmount(total - curr.getMaxStackSize());
            }
        }
        return false;
    }

    public static void giveItem(Inventory inventory, ItemStack stack) {
        for (ItemStack curr : inventory) {
            if(curr.isSimilar(stack)) {
                int total = curr.getAmount() + stack.getAmount();
                if(total > curr.getMaxStackSize()) {
                    curr.setAmount(curr.getMaxStackSize());
                    stack.setAmount(total - curr.getMaxStackSize());
                }
                else {
                    curr.setAmount(total);
                    return;
                }
            }
        }
        int slot = inventory.firstEmpty();
        if(slot >= 0) {
            inventory.setItem(slot, stack);
        }
    }

    public static void collectableDrop(Player player, Location location, ItemStack... drops) {
        collectableDrop(player, location, new Drops(drops));
    }

    public static void collectableDrop(Player player, Location location, Drops drops) {
        if (EnchantmentHelper.hasActiveEnchantmentWrapper(player.getInventory().getItemInMainHand(), CustomEnchantments.COLLECTABLE)) {
            player.giveExp(drops.getExperienceDropped(), true);

            for (ItemStack drop : drops) {
                for (Map.Entry<Integer, ItemStack> overflow : player.getInventory().addItem(drop).entrySet()) {
                    drop(player, location, overflow.getValue());
                }
            }
        } else {
            BlockBreakEventListener.awardBlock(location, drops.getExperienceDropped(), player);

            for (ItemStack drop : drops) {
                drop(player, location, drop);
            }
        }
    }

    private static void drop(Player player, Location location, ItemStack drop) {
        if(player.getGameMode() != GameMode.SURVIVAL && player.getGameMode() != GameMode.ADVENTURE) {
            // always drop computers (pwease uwu)
            if (ComputerRegistry.getComputerByLocationRounded(location) == null) {
                if(CustomItem.isCustomItem(drop)) {
                    return;
                }
                if(ItemUtils.getUncoloredType(drop) != Material.SHULKER_BOX) {
                    return;
                }
            }
        }
        location.getWorld().dropItemNaturally(location, drop);
    }
}
