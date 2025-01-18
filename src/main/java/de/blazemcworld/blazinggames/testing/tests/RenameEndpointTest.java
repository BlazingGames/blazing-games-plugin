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

package de.blazemcworld.blazinggames.testing.tests;

import java.util.UUID;

import com.google.gson.JsonObject;

import de.blazemcworld.blazinggames.computing.ComputerEditor;
import de.blazemcworld.blazinggames.computing.ComputerMetadata;
import de.blazemcworld.blazinggames.computing.ComputerRegistry;
import de.blazemcworld.blazinggames.computing.api.Permission;
import de.blazemcworld.blazinggames.computing.api.TokenManager;
import de.blazemcworld.blazinggames.computing.types.ComputerTypes;
import de.blazemcworld.blazinggames.testing.BlazingTest;
import io.azam.ulidj.ULID;

public class RenameEndpointTest extends BlazingTest {
    private UUID owner;
    private String ulidWorld;

    @Override
    public void preRunSync() throws Exception {
        owner = UUID.randomUUID();
        createComputerInWorld(ComputerTypes.CONSOLE, owner, (c) -> {
            ulidWorld = c;
        });
    }

    @Override
    public boolean runAsync() {
        return true;
    }

    @Override
    public void runTest() throws Exception {
        String newName = TokenManager.generateRandomString(8);
        String jwt = createSignedJWT(false, owner, Permission.READ_COMPUTERS, Permission.WRITE_COMPUTERS);

        // world computer
        assertNotNull(owner, ulidWorld);
        JsonObject renameRequest1 = new JsonObject();
        renameRequest1.addProperty("id", ulidWorld);
        renameRequest1.addProperty("name", newName);
        JsonObject renameResponse1 = sendPatchRequest("/computers/rename", renameRequest1, jwt);
        assertBoolean("renaming world computer", renameResponse1.get("success").getAsBoolean());
        assertEquals(newName, ComputerRegistry.getComputerById(ulidWorld).getMetadata().name);
        assertEquals(newName, ComputerEditor.getMetadata(ulidWorld).name);
        
        // item computer
        String ulidItem = ULID.random();
        createComputerInItem(ulidItem, ComputerTypes.CONSOLE, owner);

        JsonObject renameRequest2 = new JsonObject();
        renameRequest2.addProperty("id", ulidItem);
        renameRequest2.addProperty("name", newName);
        JsonObject renameResponse2 = sendPatchRequest("/computers/rename", renameRequest2, jwt);
        assertBoolean("renaming item computer", renameResponse2.get("success").getAsBoolean());
        assertEquals(newName, ComputerEditor.getMetadata(ulidItem).name);

        // unauthorized renaming
        String ulidNoAuth = ULID.random();
        String jwtNoAuth = createSignedJWT(false);
        ComputerMetadata metadataNoAuth = createComputerInItem(ulidNoAuth, ComputerTypes.CONSOLE, owner);
        String oldNameNoAuth = metadataNoAuth.name;

        JsonObject renameRequest3 = new JsonObject();
        renameRequest3.addProperty("id", ulidNoAuth);
        renameRequest3.addProperty("name", newName);
        JsonObject renameResponse3 = sendPatchRequest("/computers/rename", renameRequest3, jwtNoAuth);
        assertBoolean("renaming with bad jwt", !renameResponse3.get("success").getAsBoolean());
        assertEquals(oldNameNoAuth, ComputerEditor.getMetadata(ulidNoAuth).name);

        // permissionless renaming
        String ulidNoPerms = ULID.random();
        String jwtNoPerms = createSignedJWT(false);
        ComputerMetadata metadataNoPerms = createComputerInItem(ulidNoPerms, ComputerTypes.CONSOLE, owner);
        String oldNameNoPerms = metadataNoPerms.name;

        JsonObject renameRequest4 = new JsonObject();
        renameRequest4.addProperty("id", ulidNoPerms);
        renameRequest4.addProperty("name", newName);
        JsonObject renameResponse4 = sendPatchRequest("/computers/rename", renameRequest4, jwtNoPerms);
        assertBoolean("renaming with missing permission", !renameResponse4.get("success").getAsBoolean());
        assertEquals(oldNameNoPerms, ComputerEditor.getMetadata(ulidNoPerms).name);
    }
}