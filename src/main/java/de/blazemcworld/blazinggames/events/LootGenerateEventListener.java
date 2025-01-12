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

import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantment;
import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantments;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentHelper;
import de.blazemcworld.blazinggames.items.CustomItems;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.loot.LootTables;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LootGenerateEventListener implements Listener {

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {
        List<ItemStack> loot = event.getLoot();
        ArrayList<ItemStack> newLoot = new ArrayList<>(loot);

        NamespacedKey key = event.getLootTable().getKey();

        Random random = new Random();

        if (key.equals(LootTables.BASTION_BRIDGE.getKey())
                || key.equals(LootTables.BASTION_OTHER.getKey())
                || key.equals(LootTables.BASTION_TREASURE.getKey())) {
            int getBook = random.nextInt(100) + 1;
            if (getBook <= 25) {
                newLoot.add(CustomItems.DIM_TOME.create());
            }
        }
        if (key.equals(LootTables.PILLAGER_OUTPOST.getKey())) {
            int getBook = random.nextInt(100) + 1;
            if (getBook <= 50) {
                newLoot.add(CustomItems.BLACK_TOME.create());
            }
        }
        if(key.equals(LootTables.PILLAGER_OUTPOST.getKey())
                || key.equals(LootTables.WOODLAND_MANSION.getKey())) {
            int getBook = random.nextInt(100) + 1;
            if (getBook <= 25) {
                newLoot.add(CustomItems.BIND_TOME.create());
            }
            getBook = random.nextInt(100) + 1;
            if (getBook <= 25) {
                newLoot.add(CustomItems.VANISH_TOME.create());
            }
        }
        if (key.equals(LootTables.END_CITY_TREASURE.getKey())) {
            int getBook = random.nextInt(100) + 1;
            if (getBook <= 11) {
                newLoot.add(CustomItems.GUST_TOME.create());
            }
        }
        if(key.equals(LootTables.STRONGHOLD_CORRIDOR.getKey())
                || key.equals(LootTables.STRONGHOLD_CROSSING.getKey())
                || key.equals(LootTables.STRONGHOLD_LIBRARY.getKey())
                || key.equals(LootTables.END_CITY_TREASURE.getKey())) {
            int getBook = random.nextInt(100) + 1;
            if (getBook <= 8) {
                newLoot.add(CustomItems.FUSE_TOME.create());
            }
        }
        if (key.equals(LootTables.IGLOO_CHEST.getKey())) {
            int getBook = random.nextInt(100) + 1;
            if (getBook <= 40) {
                newLoot.add(CustomItems.CHILL_TOME.create());
            }
        }

        if(key.equals(LootTables.ANCIENT_CITY.getKey())) {
            for (int j = 0; j < newLoot.size(); j++) {
                if (hasStoredEnchantment(newLoot.get(j), Enchantment.SWIFT_SNEAK)) {
                    newLoot.set(j, CustomItems.ECHO_TOME.create());
                }
            }
        }

        if(key.equals(LootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_RARE.getKey())) {
            for (int j = 0; j < newLoot.size(); j++) {
                if (hasStoredEnchantment(newLoot.get(j), Enchantment.WIND_BURST)) {
                    newLoot.set(j, CustomItems.STORM_TOME.create());
                }
            }
        }

        if (key.equals(LootTables.BASTION_OTHER.getKey())) {
            for (int j = 0; j < newLoot.size(); j++) {
                if (hasStoredEnchantment(newLoot.get(j), Enchantment.SOUL_SPEED)) {
                    newLoot.set(j, CustomItems.NETHER_TOME.create());
                }
            }
        }

        if (key.equals(LootTables.BASTION_BRIDGE.getKey())
                || key.equals(LootTables.BASTION_OTHER.getKey())
                || key.equals(LootTables.BASTION_TREASURE.getKey())
                || key.equals(LootTables.BASTION_HOGLIN_STABLE.getKey())) {
            for (int j = 0; j < newLoot.size(); j++) {
                if (EnchantmentHelper.canEnchantItem(newLoot.get(j)) && random.nextInt(20) == 1) {
                    newLoot.set(j, EnchantmentHelper.enchantTool(newLoot.get(j), CustomEnchantments.UNSHINY, 1));
                }
            }
        }

        while(newLoot.size() > 27) {
            newLoot.removeLast();
        }

        event.setLoot(newLoot);
    }

    private boolean hasStoredEnchantment(ItemStack book, Enchantment enchantment) {
        if(book.getItemMeta() instanceof EnchantmentStorageMeta esm) {
            return esm.hasStoredEnchant(enchantment);
        }
        return false;
    }

    private ItemStack createRandomBook(CustomEnchantment enchantment, Random random) {
        return enchantRandomTool(new ItemStack(Material.BOOK), enchantment, random);
    }

    private ItemStack createBook(CustomEnchantment enchantment, int level) {
        return EnchantmentHelper.enchantTool(new ItemStack(Material.BOOK), enchantment, level);
    }

    private ItemStack enchantRandomTool(ItemStack stack, CustomEnchantment enchantment, Random random) {
        return EnchantmentHelper.enchantTool(stack, enchantment, random.nextInt(enchantment.getMaxLevel()) + 1);
    }
}
