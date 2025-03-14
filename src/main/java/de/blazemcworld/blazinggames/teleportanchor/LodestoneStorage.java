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
package de.blazemcworld.blazinggames.teleportanchor;

import com.google.common.reflect.TypeToken;
import de.blazemcworld.blazinggames.data.DataStorage;
import de.blazemcworld.blazinggames.data.compression.GZipCompressionProvider;
import de.blazemcworld.blazinggames.data.name.ULIDNameProvider;
import de.blazemcworld.blazinggames.data.storage.GsonStorageProvider;
import de.blazemcworld.blazinggames.utils.TextLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class LodestoneStorage {
    private LodestoneStorage() {}
    private static final DataStorage<HashMap<UUID, String>, String> dataStorage = DataStorage.forClass(
        LodestoneStorage.class, null,
        new GsonStorageProvider<>(new TypeToken<HashMap<UUID, String>>() {}.getType()),
        new ULIDNameProvider(), new GZipCompressionProvider()
    );

    public static void saveLodestoneToPlayer(UUID player, Location location, String customName) {
        var storage = dataStorage.getData(TextLocation.serializeRounded(location));
        if (storage == null) storage = new HashMap<>();
        storage.put(player, customName);
        dataStorage.storeData(TextLocation.serializeRounded(location), storage);
    }

    public static void removeSavedLodestoneForPlayer(UUID player, Location location) {
        var storage = dataStorage.getData(TextLocation.serializeRounded(location));
        if (storage == null) return;
        storage.remove(player);
        dataStorage.storeData(TextLocation.serializeRounded(location), storage);
    }

    public static Map<Location, String> getSavedLodestones(UUID player) {
        return dataStorage.query(storage -> storage.getOrDefault(player, null) != null)
            .stream().collect(Collectors.toMap(TextLocation::deserialize, i -> dataStorage.getData(i).getOrDefault(player, "error")));
    }

    public static void destroyLodestone(Location location) {
        dataStorage.deleteData(TextLocation.serializeRounded(location));
    }

    public static void refreshAllInventories() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getOpenInventory().getTopInventory().getHolder() instanceof TeleportAnchorInterface tpi) {
                tpi.reload();
            }
        }
    }
}
