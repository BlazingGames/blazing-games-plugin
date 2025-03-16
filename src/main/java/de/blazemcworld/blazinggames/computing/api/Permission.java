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
package de.blazemcworld.blazinggames.computing.api;

import java.util.List;

import de.blazemcworld.blazinggames.RequiredFeature;

public enum Permission {
    READ_COMPUTERS("Read a list of your computers, with metadata", DangerLevel.LOW, RequiredFeature.COMPUTERS),
    WRITE_COMPUTERS("Unregister and edit metadata of your computers", DangerLevel.HIGH, RequiredFeature.COMPUTERS),
    RESTART_COMPUTER("Restart your computers", DangerLevel.LOW, RequiredFeature.COMPUTERS),
    START_STOP_COMPUTER("Start or stop your computers", DangerLevel.MEDIUM, RequiredFeature.COMPUTERS),
    COMPUTER_CODE_READ("View your computer code", DangerLevel.LOW, RequiredFeature.COMPUTERS),
    COMPUTER_CODE_MODIFY("Modify your computer code", DangerLevel.MEDIUM, RequiredFeature.COMPUTERS),
    COLLABORATOR_MANAGEMENT("Manage collaborators of your computers (with consent)", DangerLevel.MEDIUM, RequiredFeature.COMPUTERS),
    OWNER_MANAGEMENT("Transfer ownership of your computers (with consent)", DangerLevel.MEDIUM, RequiredFeature.COMPUTERS),
    COLLABORATOR_OWNER_MANAGEMENT_NO_CONSENT("Do not require consent for collaborator and owner changes", DangerLevel.CRITICAL, RequiredFeature.COMPUTERS);

    public final String description;
    public final DangerLevel level;
    public final List<RequiredFeature> requiredFeatures;

    private Permission(String description, DangerLevel level, RequiredFeature... requiredFeatures) {
        this.description = description;
        this.level = level;
        this.requiredFeatures = List.of(requiredFeatures);
    }

    public boolean isAllowed(LinkedUser user) {
        return user.permissions().contains(this);
    }
}
