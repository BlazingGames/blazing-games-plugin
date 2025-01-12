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

public enum Permission {
    READ_COMPUTERS("Read a list of your computers, with metadata", DangerLevel.LOW),
    WRITE_COMPUTERS("Unregister and edit metadata of your computers", DangerLevel.HIGH),
    RESTART_COMPUTER("Restart your computers", DangerLevel.LOW),
    START_STOP_COMPUTER("Start or stop your computers", DangerLevel.MEDIUM),
    COMPUTER_CODE_READ("View your computer code", DangerLevel.LOW),
    COMPUTER_CODE_MODIFY("Modify your computer code", DangerLevel.MEDIUM),
    COLLABORATOR_MANAGEMENT("Manage collaborators of your computers (with consent)", DangerLevel.MEDIUM),
    OWNER_MANAGEMENT("Transfer ownership of your computers (with consent)", DangerLevel.MEDIUM),
    COLLABORATOR_OWNER_MANAGEMENT_NO_CONSENT("Do not require consent for collaborator and owner changes", DangerLevel.CRITICAL);

    public final String description;
    public final DangerLevel level;

    private Permission(String description, DangerLevel level) {
        this.description = description;
        this.level = level;
    }

    public boolean isAllowed(LinkedUser user) {
        return user.permissions().contains(this);
    }
}
