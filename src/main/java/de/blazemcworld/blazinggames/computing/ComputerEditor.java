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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;

import de.blazemcworld.blazinggames.computing.upgrades.UpgradeType;

/**
 * Utility class for directly modifiying computers.
 */
public class ComputerEditor {
    private static void _setMetadata(final ComputerMetadata metadata) {
        if (ComputerRegistry.getComputerById(metadata.id) != null) {
            ComputerRegistry.getComputerById(metadata.id).updateMetadata(metadata);
        }
        ComputerRegistry.metadataStorage.storeData(metadata.id, metadata);
    }
    private ComputerEditor() {}

    /**
     * Get a list of computers that a user can access.
     */
    public static List<ComputerMetadata> getAccessibleComputers(final UUID uuid) {
        return ComputerRegistry.metadataStorage.query(metadata -> {
            if (List.of(metadata.collaborators).contains(uuid)) return true;
            return metadata.owner.equals(uuid);
        }).stream().map(id -> ComputerRegistry.metadataStorage.getData(id)).toList();
    }

    /**
     * Returns true if the user has permissions to access the computer
     */
    public static boolean hasAccessToComputer(final UUID user, final String computer) {
        ComputerMetadata metadata = ComputerRegistry.metadataStorage.getData(computer);

        if (metadata == null) {
            return false;
        }

        return metadata.owner.equals(user) || Arrays.asList(metadata.collaborators).contains(user);
    }

    public static ComputerMetadata getMetadata(final String computer) {
        if (ComputerRegistry.getComputerById(computer) != null) {
            return ComputerRegistry.getComputerById(computer).getMetadata();
        }
        return ComputerRegistry.metadataStorage.getData(computer);
    }

    /**
     * Get the stored code for a computer
     */
    public static String getCode(final String computer) {
        if (ComputerRegistry.getComputerById(computer) != null) {
            return ComputerRegistry.getComputerById(computer).getCode();
        }

        if (!ComputerRegistry.codeStorage.hasData(computer)) {
            return ComputerRegistry.defaultCode;
        }
        return ComputerRegistry.codeStorage.getData(computer);
    }

    /**
     * Set (override) all ugprades of a computer
     */
    public static void setUpgrades(final String computer, UpgradeType[] upgrades) {
        ComputerMetadata metadataObj = getMetadata(computer);
        if (metadataObj == null) return;
        JsonObject metadata = metadataObj.serialize();
        metadata.addProperty("upgrades", Arrays.stream(upgrades).map(UpgradeType::toString).collect(Collectors.joining(",")));
        _setMetadata(new ComputerMetadata(metadata));
    }

    /**
     * Add one upgrade to a computer. Does not check if there is free space.
     */
    public static void addUpgrade(final String computer, UpgradeType type) {
        ComputerMetadata metadataObj = getMetadata(computer);
        if (metadataObj == null) return;
        ArrayList<UpgradeType> existing = new ArrayList<>(metadataObj.upgrades);
        existing.add(type);
        setUpgrades(computer, existing.toArray(UpgradeType[]::new));
    }

    /**
     * Remove exactly one of an upgrade type from a computer.
     */
    public static void removeUpgrade(final String computer, UpgradeType type) {
        ComputerMetadata metadataObj = getMetadata(computer);
        if (metadataObj == null) return;
        ArrayList<UpgradeType> existing = new ArrayList<>(metadataObj.upgrades);
        existing.remove(type);
        setUpgrades(computer, existing.toArray(UpgradeType[]::new));
    }

    public static void rename(final String computerId, final String name) {
        JsonObject metadata = getMetadata(computerId).serialize();
        metadata.addProperty("name", name);
        _setMetadata(new ComputerMetadata(metadata));
    }
}
