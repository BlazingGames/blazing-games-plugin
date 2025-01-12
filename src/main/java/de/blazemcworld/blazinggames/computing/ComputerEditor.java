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

/**
 * Utility class for directly modifiying computers.
 */
public class ComputerEditor {
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

    public static boolean hasAccessToComputer(final UUID user, final String computer) {
        ComputerMetadata metadata = ComputerRegistry.metadataStorage.getData(computer);

        if (metadata == null) {
            return false;
        }

        return metadata.owner == user || Arrays.asList(metadata.collaborators).contains(user);
    }
}
