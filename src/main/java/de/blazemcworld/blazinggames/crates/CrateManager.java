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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.data.DataStorage;
import de.blazemcworld.blazinggames.data.compression.GZipCompressionProvider;
import de.blazemcworld.blazinggames.data.providers.ULIDNameProvider;
import de.blazemcworld.blazinggames.data.storage.GsonStorageProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class CrateManager {
    private CrateManager() {}
    private static final NamespacedKey KEY = BlazingGames.get().key("death_crate_key");
    private static final DataStorage<CrateData, String> crateStorage = DataStorage.forClass(
        CrateManager.class, null,
        new GsonStorageProvider<>(CrateData.class), new ULIDNameProvider(), new GZipCompressionProvider()
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
        List<String> ids = crateStorage.query(data -> {
            return !data.opened &&
                data.location.getWorld().getName().equals(loc.getWorld().getName()) &&
                data.location.blockX() == loc.getBlockX() &&
                data.location.blockY() == loc.getBlockY() &&
                data.location.blockZ() == loc.getBlockZ();
        });

        if (ids.isEmpty()) {
            return null;
        }

        if (ids.size() > 1) {
            // sort the ULIDs to find the newest
            ids.sort((a, b) -> {
                return b.compareTo(a);
            });
        }

        return ids.get(0);
    }

    public static String createDeathCrate(UUID owner, PlayerInventory inventory, int exp, Location crateLocation) {
        ArrayList<ItemStack> items = new ArrayList<>();
        for (ItemStack item : inventory.getStorageContents()) {
            items.add(item);
        }

        List<ItemStack> hotbarItems = items.subList(0, 9).stream().filter(i -> i == null ? true : CrateManager.shouldStayOnDeath(i)).toList();
        List<ItemStack> inventoryItems = items.subList(9, 36).stream().filter(i -> i == null ? true : CrateManager.shouldStayOnDeath(i)).toList();

        return crateStorage.storeNext(id -> new CrateData(
            id, owner, false,
            crateLocation, exp,
            shouldStayOnDeath(inventory.getHelmet()) ? inventory.getHelmet() : null,
            shouldStayOnDeath(inventory.getChestplate()) ? inventory.getChestplate() : null,
            shouldStayOnDeath(inventory.getLeggings()) ? inventory.getLeggings() : null,
            shouldStayOnDeath(inventory.getBoots()) ? inventory.getBoots() : null,
            shouldStayOnDeath(inventory.getItemInOffHand()) ? inventory.getItemInOffHand() : null,
            hotbarItems, inventoryItems
        )).right;
    }

    public static CrateData readCrate(String ulid) {
        return crateStorage.getData(ulid);
    }

    public static void deleteCrate(String ulid) {
        crateStorage.deleteData(ulid);
    }
    
    public static ItemStack makeKey(String ulid, Location location) {
        ItemStack item = new ItemStack(Material.TRIPWIRE_HOOK, 1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Death Crate Key").color(NamedTextColor.DARK_RED).decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(
            Component.text("Location: %s, %s, %s in %s".formatted(location.getBlockX(), location.getBlockY(), location.getBlockZ(),
                    location.getWorld().getName())).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true),
            Component.text("ULID: %s".formatted(ulid)).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true),
            Component.empty(),
            Component.text("Unlocks the crate at the location above. Can be used by anyone.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true)
        ));
        meta.getPersistentDataContainer().set(KEY, PersistentDataType.STRING, ulid);
        item.setItemMeta(meta);
        return item;
    }

    public static String getKeyULID(ItemStack item) {
        if (item == null) { return null; }
        if (!item.hasItemMeta()) { return null; }
        return item.getItemMeta().getPersistentDataContainer().get(KEY, PersistentDataType.STRING);
    }
}
