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

import de.blazemcworld.blazinggames.data.DataStorage;
import de.blazemcworld.blazinggames.data.compression.GZipCompressionProvider;
import de.blazemcworld.blazinggames.data.name.ArbitraryNameProvider;
import de.blazemcworld.blazinggames.data.storage.GsonStorageProvider;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TomeAltarStorage {
    private static DataStorage<ItemStack, String> dataStorage = DataStorage.forClass(
        TomeAltarStorage.class, null,
        new GsonStorageProvider<>(ItemStack.class), new ArbitraryNameProvider(), new GZipCompressionProvider()
    );

    public static void addTomeAltar(Location location) {
        dataStorage.storeData(TextLocation.serializeRounded(location), null);
    }

    public static boolean isTomeAltar(Location location) {
        return dataStorage.hasData(TextLocation.serializeRounded(location));
    }

    public static void setItem(Location location, ItemStack item) {
        String key = TextLocation.serializeRounded(location);
        if (dataStorage.hasData(key)) {
            dataStorage.storeData(key, item);
        }
    }

    public static ItemStack getItem(Location location) {
        return dataStorage.getData(TextLocation.serializeRounded(location), null);
    }

    public static void removeTomeAltar(Location location) {
        dataStorage.deleteData(TextLocation.serializeRounded(location));
    }

    public static List<Location> getNear(Location loc, int radius) {
        int radiusSquared = radius * radius;
        return dataStorage.queryIdentifiers(i -> {
            Location location = TextLocation.deserialize(i);
            if (!location.getWorld().equals(loc.getWorld())) return false;
            return loc.getWorld().equals(location.getWorld()) && loc.distanceSquared(location) < radiusSquared;
        }).stream().map(TextLocation::deserialize).toList();
    }
}