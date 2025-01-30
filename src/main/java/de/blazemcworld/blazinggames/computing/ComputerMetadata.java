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
package de.blazemcworld.blazinggames.computing;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.Location;

import com.google.gson.JsonObject;

import de.blazemcworld.blazinggames.computing.types.ComputerItemContext;
import de.blazemcworld.blazinggames.computing.types.ComputerTypes;
import de.blazemcworld.blazinggames.computing.upgrades.UpgradeType;
import de.blazemcworld.blazinggames.utils.GetGson;
import de.blazemcworld.blazinggames.utils.TextLocation;

public class ComputerMetadata {
    public final String id;
    public final String name;
    public final UUID address;
    public final ComputerTypes type;
    public final List<UpgradeType> upgrades;
    public final Location location;
    public final UUID owner;
    public final UUID[] collaborators;
    public final boolean shouldRun;
    public final int frozenTicks;

    public ComputerMetadata(String id, String name, UUID address, ComputerTypes type, List<UpgradeType> upgrades,
            Location location, UUID owner, UUID[] collaborators, boolean shouldRun, int frozenTicks) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.type = type;
        this.upgrades = List.copyOf(upgrades);
        this.location = location;
        this.owner = owner;
        this.collaborators = collaborators;
        this.shouldRun = shouldRun;
        this.frozenTicks = frozenTicks;
    }

    public ComputerMetadata(JsonObject json) {
        Function<String, IllegalArgumentException> e = (msg) -> new IllegalArgumentException("Invalid computer metadata: missing property " + msg);
        this.id = GetGson.getString(json, "id", e.apply("id"));
        this.name = GetGson.getString(json, "name", e.apply("name"));
        this.address = UUID.fromString(GetGson.getString(json, "address", e.apply("address")));
        this.type = ComputerTypes.valueOf(GetGson.getString(json, "type", e.apply("type")));
        this.upgrades = List.copyOf(Arrays.stream(GetGson.getString(json, "upgrades", e.apply("upgrades")).split(",")).map(UpgradeType::valueOf).collect(Collectors.toList()));
        this.location = TextLocation.deserialize(json.get("location").isJsonNull() ? null : json.get("location").getAsString());
        this.owner = UUID.fromString(GetGson.getString(json, "owner", e.apply("owner")));
        this.collaborators = Arrays.stream(GetGson.getString(json, "collaborators", e.apply("collaborators")).split(",")).filter(s -> !s.isEmpty()).map(UUID::fromString).toArray(UUID[]::new);
        this.shouldRun = GetGson.getBoolean(json, "shouldRun", e.apply("shouldRun"));
        this.frozenTicks = GetGson.getNumber(json, "frozenTicks", e.apply("frozenTicks")).intValue();
    }

    public JsonObject serialize() {
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        object.addProperty("name", name);
        object.addProperty("address", address.toString());
        object.addProperty("type", type.name());
        object.addProperty("upgrades", String.join(",", upgrades.stream().map(UpgradeType::name).toList()));
        object.addProperty("location", TextLocation.serialize(location));
        object.addProperty("owner", owner.toString());
        object.addProperty("collaborators", String.join(",", Arrays.stream(collaborators).map(UUID::toString).toArray(String[]::new)));
        object.addProperty("shouldRun", shouldRun);
        object.addProperty("frozenTicks", frozenTicks);
        return object;
    }

    @Override
    public String toString() {
        return serialize().toString();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof ComputerMetadata other) {
            return toString().equals(other.toString());
        }
        return false;
    }

    public int getUpgradeCount(UpgradeType upgrade) {
        for (UpgradeType type : upgrades) {
            if (upgrade.incompatibilities.contains(type)) return 0;
        }

        if (upgrade.unique) return upgrades.stream().filter(type -> type == upgrade).findFirst().isPresent() ? 1 : 0;

        return (int) upgrades.stream().filter(type -> type == upgrade).count();
    }

    /**
     * Checks if the type of computer being used already has the upgrade being added by default.
     */
    public boolean isUpgradePresentForComputerType(UpgradeType upgrade) {
        return List.of(type.getType().getDefaultUpgrades()).stream().anyMatch(type -> type == upgrade);
    }

    public ComputerItemContext createContext() {
        return new ComputerItemContext(id);
    }
}
