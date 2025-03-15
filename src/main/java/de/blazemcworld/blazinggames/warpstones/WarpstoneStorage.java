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
package de.blazemcworld.blazinggames.warpstones;

import com.google.common.reflect.TypeToken;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.data.DataStorage;
import de.blazemcworld.blazinggames.data.compression.GZipCompressionProvider;
import de.blazemcworld.blazinggames.data.name.ArbitraryNameProvider;
import de.blazemcworld.blazinggames.data.name.UUIDNameProvider;
import de.blazemcworld.blazinggames.data.storage.GsonStorageProvider;
import de.blazemcworld.blazinggames.items.CustomItems;
import de.blazemcworld.blazinggames.utils.TextLocation;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WarpstoneStorage {
    private static final NamespacedKey metadataKey = BlazingGames.get().key("tagged_as_warpstone");
    private WarpstoneStorage() {}
    
    private static final DataStorage<WarpstoneDetails, String> warpstoneStorage = DataStorage.forClass(
        WarpstoneStorage.class, "warpstones",
        new GsonStorageProvider<>(WarpstoneDetails.class),
        new ArbitraryNameProvider(), new GZipCompressionProvider()
    );

    private static final DataStorage<HashMap<String, WarpstoneOverrideDetails>, UUID> playerStorage = DataStorage.forClass(
        WarpstoneStorage.class, "players",
        new GsonStorageProvider<>(new TypeToken<HashMap<String, WarpstoneOverrideDetails>>() {}.getType()),
        new UUIDNameProvider(), new GZipCompressionProvider()
    );

    public static WarpstoneDetails getDetails(Location location) {
        return warpstoneStorage.getData(TextLocation.serializeRounded(location));
    }
    
    public static boolean isWarpstone(Location location) {
        return warpstoneStorage.hasData(TextLocation.serializeRounded(location));
    }

    public static void placeWarpstone(Location location, Player player, String name) {
        String key = TextLocation.serializeRounded(location);
        if (warpstoneStorage.hasData(key)) {
            warpstoneStorage.deleteData(key);
        }

        WarpstoneDetails details = new WarpstoneDetails();
        details.owner = player.getUniqueId();
        if (name != null) details.defaultName = name;
        warpstoneStorage.storeData(key, details);

        location.getWorld().spawn(location.toCenterLocation(), ItemDisplay.class, entity -> {
            entity.setItemStack(CustomItems.WARPSTONE.create());
            entity.getPersistentDataContainer().set(metadataKey, PersistentDataType.BOOLEAN, true);
        });
    }

    public static boolean permissionCheck(Location warpstone, Player player) {
        WarpstoneDetails details = getDetails(warpstone);
        if (details == null) return false;

        if (details.locked) return player.getUniqueId().equals(details.owner);
        else return true;
    }

    public static void breakWarpstone(Location location) {
        String key = TextLocation.serializeRounded(location);
        if (warpstoneStorage.hasData(key)) {
            warpstoneStorage.deleteData(key);
        }
        
        location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, null);

        Location checkLocation = location.toCenterLocation();
        for (Entity e : location.getWorld().getEntities()) {
            Location entityLocation = e.getLocation().toCenterLocation();
            if (
                entityLocation.getBlockX() == checkLocation.getBlockX() &&
                entityLocation.getBlockY() == checkLocation.getBlockY() &&
                entityLocation.getBlockZ() == checkLocation.getBlockZ()
            ) {
                if (e.getPersistentDataContainer().has(metadataKey, PersistentDataType.BOOLEAN)) {
                    e.remove();
                }
            }
        }
    }

    public static void updateWarpstoneLock(Location location, boolean locked) {
        String key = TextLocation.serializeRounded(location);
        if (warpstoneStorage.hasData(key)) {
            WarpstoneDetails details = warpstoneStorage.getData(key);
            details.locked = locked;
            warpstoneStorage.storeData(key, details);
        }
    }

    private static void modifyPlayerMap(Player player, String key, WarpstoneOverrideDetails value) {
        HashMap<String, WarpstoneOverrideDetails> map = playerStorage.getData(player.getUniqueId());
        if (map == null) map = new HashMap<>();

        if (value == null) map.remove(key);
        else map.put(key, value);

        playerStorage.storeData(player.getUniqueId(), map);
    }

    public static void saveWarpstone(Player player, Location location, String name) {
        WarpstoneDetails details = getDetails(location);
        if (details == null) return;

        WarpstoneOverrideDetails overrides = new WarpstoneOverrideDetails();
        if (name != null && !name.isBlank()) overrides.name = name;

        modifyPlayerMap(player, TextLocation.serializeRounded(location), overrides);
    }

    public static Map<Location, WarpstoneOverrideDetails> getSavedWarpstones(Player player) {
        HashMap<String, WarpstoneOverrideDetails> map = playerStorage.getData(player.getUniqueId());
        if (map == null) return new HashMap<>();
        
        HashMap<Location, WarpstoneOverrideDetails> warpstones = new HashMap<>();
        for(Map.Entry<String, WarpstoneOverrideDetails> entry : map.entrySet()) {
            Location location = TextLocation.deserialize(entry.getKey());
            warpstones.put(location, entry.getValue());
        }
        return warpstones;
    }

    public static boolean isObstructed(Location warpstone) {
        WarpstoneDetails details = getDetails(warpstone);
        if (details == null) return true;

        if (!Material.BARRIER.equals(warpstone.getBlock().getType())) return true;
        if (!Material.AIR.equals(warpstone.getBlock().getRelative(0, 1, 0).getType())) return true;
        if (!Material.AIR.equals(warpstone.getBlock().getRelative(0, 2, 0).getType())) return true;

        return false;
    }

    public static void forgetWarpstone(Player player, Location location) {
        modifyPlayerMap(player, TextLocation.serializeRounded(location), null);
    }

    public static WarpstoneOverrideDetails getOverrideDetails(Player player, Location location) {
        HashMap<String, WarpstoneOverrideDetails> map = playerStorage.getData(player.getUniqueId());
        if (map == null) return null;
        
        return map.get(TextLocation.serializeRounded(location));
    }

    public static void updateOverrideDetails(Player player, Location location, WarpstoneOverrideDetails details) {
        HashMap<String, WarpstoneOverrideDetails> map = playerStorage.getData(player.getUniqueId());
        if (map == null) map = new HashMap<>();
        
        map.put(TextLocation.serializeRounded(location), details);
        playerStorage.storeData(player.getUniqueId(), map);
    }
}
