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
package de.blazemcworld.blazinggames.crates;

import java.util.*;

import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import de.blazemcworld.blazinggames.BlazingGames;
import dev.ivycollective.datastorage.DataStorage;
import dev.ivycollective.datastorage.name.ULIDNameProvider;
import dev.ivycollective.datastorage.storage.GsonStorageProvider;

public class CrateManager {
    private CrateManager() {}
    private static final DataStorage<CrateData, String> crateStorage = BlazingGames.dataStorageConfig().makeDataStorage(
        CrateManager.class, null,
        new GsonStorageProvider<>(CrateData.class), new ULIDNameProvider()
    );

    private static boolean shouldStayOnDeath(ItemStack item) {
        if (item == null || item.isEmpty()) {
            return false;
        }

        if (!item.hasItemMeta()) {
            return true;
        }

        ItemMeta meta = item.getItemMeta();
        return !meta.getEnchants().containsKey(Enchantment.VANISHING_CURSE);
    }

    public static String getKeyULID(Location loc) {
        List<String> ids = crateStorage.query(data -> !data.opened &&
            data.location.getWorld().getName().equals(loc.getWorld().getName()) &&
            data.location.blockX() == loc.getBlockX() &&
            data.location.blockY() == loc.getBlockY() &&
            data.location.blockZ() == loc.getBlockZ());

        if (ids.isEmpty()) {
            return null;
        }

        if (ids.size() > 1) {
            // sort the ULIDs to find the newest
            ids.sort(Comparator.reverseOrder());
        }

        return ids.getFirst();
    }

    public static String createDeathCrate(UUID owner, PlayerInventory inventory, int exp, Location crateLocation) {
        ArrayList<ItemStack> items = new ArrayList<>(Arrays.asList(inventory.getStorageContents()));

        List<ItemStack> hotbarItems = items.subList(0, 9).stream().filter(i -> i == null || CrateManager.shouldStayOnDeath(i)).toList();
        List<ItemStack> inventoryItems = items.subList(9, 36).stream().filter(i -> i == null || CrateManager.shouldStayOnDeath(i)).toList();

        return crateStorage.storeNext(id -> new CrateData(
            id, owner, false,
            crateLocation, exp,
            shouldStayOnDeath(inventory.getHelmet()) ? inventory.getHelmet() : null,
            shouldStayOnDeath(inventory.getChestplate()) ? inventory.getChestplate() : null,
            shouldStayOnDeath(inventory.getLeggings()) ? inventory.getLeggings() : null,
            shouldStayOnDeath(inventory.getBoots()) ? inventory.getBoots() : null,
            shouldStayOnDeath(inventory.getItemInOffHand()) ? inventory.getItemInOffHand() : null,
            hotbarItems, inventoryItems
        )).obj2;
    }

    public static CrateData readCrate(String ulid) {
        return crateStorage.getData(ulid);
    }

    public static void deleteCrate(String ulid) {
        crateStorage.deleteData(ulid);
    }
}
