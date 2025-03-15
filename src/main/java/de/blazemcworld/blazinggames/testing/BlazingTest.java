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
package de.blazemcworld.blazinggames.testing;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.computing.ComputerMetadata;
import de.blazemcworld.blazinggames.computing.ComputerRegistry;
import de.blazemcworld.blazinggames.computing.api.LinkedUser;
import de.blazemcworld.blazinggames.computing.api.Permission;
import de.blazemcworld.blazinggames.computing.api.TokenManager;
import de.blazemcworld.blazinggames.computing.types.ComputerTypes;
import de.blazemcworld.blazinggames.utils.NameGenerator;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class BlazingTest {
    public abstract boolean runAsync();
    public boolean run() {
        try {
            runTest();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    protected abstract void runTest() throws Exception;
    public void preRunSync() throws Exception {}

    // methods for test runner
    protected void assertBoolean(String condition, boolean assertion) throws TestFailedException {
        if (!assertion) {
            throw new TestFailedException(getClass(), "assertBoolean failed: \"" + condition + "\"");
        }
    }

    protected void assertEquals(Object expected, Object actual) throws TestFailedException {
        if (expected == null && actual == null) {
            return;
        }
        
        if (expected == null || actual == null || !expected.equals(actual)) {
            throw new TestFailedException(getClass(), "assertEquals expected \"" + expected + "\" but got \"" + actual + "\"");
        }
    }

    protected void assertNotNull(Object... objects) throws TestFailedException {
        for (Object object : objects) {
            if (object == null) {
                throw new TestFailedException(getClass(), "assertNotNull failed: object is null");
            }
        }
    }

    protected void assertNotEmpty(Object[] array) throws TestFailedException {
        if (array.length == 0) {
            throw new TestFailedException(getClass(), "assertNotEmpty failed: array is empty");
        }
    }

    protected void assertNotEmpty(List<Object> list) throws TestFailedException {
        if (list.size() == 0) {
            throw new TestFailedException(getClass(), "assertNotEmpty failed: list is empty");
        }
    }

    protected void debugLog(String... contents) {
        String message = String.join(" ", contents);
        BlazingGames.get().debugLog(getClass().getSimpleName() + ": " + message);
    }

    private static int id = 0;
    protected synchronized LinkedUser createLinkedUser(boolean expired, UUID uuid, Permission... permissions) {
        id++;
        List<Permission> perms = List.of(permissions);
        long expAt = expired ? 0L : Instant.now().plusSeconds(TimeUnit.HOURS.toSeconds(6L)).getEpochSecond();
        return new LinkedUser("UnitTest" + id, uuid, 0, TokenManager.getInstant(), perms, expAt);
    }

    protected LinkedUser createLinkedUser(boolean expired, Permission... permissions) {
        return createLinkedUser(expired, UUID.randomUUID(), permissions);
    }

    protected String createSignedJWT(boolean expired, UUID uuid, Permission... permissions) {
        LinkedUser user = createLinkedUser(expired, uuid, permissions);
        return LinkedUser.signLinkedUser(user);
    }

    protected String createSignedJWT(boolean expired, Permission... permissions) {
        return createSignedJWT(expired, UUID.randomUUID(), permissions);
    }

    public static final OkHttpClient client = new OkHttpClient.Builder()
        .followRedirects(false)
        .followSslRedirects(false)
        .build();
    public static final MediaType json = MediaType.parse("application/json");
    protected JsonObject sendRequest(Request request) throws IOException {
        Response response = client.newCall(request).execute();
        JsonElement json = BlazingGames.gson.fromJson(response.body().charStream(), JsonElement.class);
        debugLog(request.url() + ": " + json.toString());
        return json.getAsJsonObject();
    }

    protected JsonObject sendGetRequestUnauthenticated(String url) throws IOException {
        return sendRequest(new Request.Builder()
            .url("http://localhost:8080" + url)
            .build());
    }

    protected JsonObject sendGetRequest(String url, String authorization) throws IOException {
        return sendRequest(new Request.Builder()
            .url("http://localhost:8080" + url)
            .header("Authorization", "Bearer " + authorization)
            .build());
    }

    protected JsonObject sendPostRequestUnauthenticated(String url, JsonObject body) throws IOException {
        return sendRequest(new Request.Builder()
            .url("http://localhost:8080" + url)
            .post(RequestBody.create(BlazingGames.gson.toJson(body), json))
            .build());
    }

    protected JsonObject sendPostRequest(String url, JsonObject body, String authorization) throws IOException {
        return sendRequest(new Request.Builder()
            .url("http://localhost:8080" + url)
            .post(RequestBody.create(BlazingGames.gson.toJson(body), json))
            .header("Authorization", "Bearer " + authorization)
            .build());
    }

    protected JsonObject sendPutRequest(String url, JsonObject body, String authorization) throws IOException {
        return sendRequest(new Request.Builder()
            .url("http://localhost:8080" + url)
            .put(RequestBody.create(BlazingGames.gson.toJson(body), json))
            .header("Authorization", "Bearer " + authorization)
            .build());
    }

    protected JsonObject sendPatchRequest(String url, JsonObject body, String authorization) throws IOException {
        return sendRequest(new Request.Builder()
            .url("http://localhost:8080" + url)
            .patch(RequestBody.create(BlazingGames.gson.toJson(body), json))
            .header("Authorization", "Bearer " + authorization)
            .build());
    }

    protected JsonObject sendDeleteRequest(String url, String authorization) throws IOException {
        return sendRequest(new Request.Builder()
            .url("http://localhost:8080" + url)
            .delete()
            .header("Authorization", "Bearer " + authorization)
            .build());
    }

    private static int xSafeValue = 0;
    protected synchronized void createComputerInWorld(final ComputerTypes type, final UUID owner, final Consumer<String> ulidCallback) {
        final World world = Bukkit.getWorlds().get(0);
        final Location location = new Location(world, xSafeValue, 10, 0);
        xSafeValue += 10;
        location.getBlock().setType(Material.AIR);

        ComputerRegistry.placeNewComputer(
            location, type, owner, (c) -> {
                ulidCallback.accept(c.getId());
            }
        );
    }

    protected synchronized ComputerMetadata createComputerInItem(final String ulid, final ComputerTypes type, final UUID owner) {
        var metadata = new ComputerMetadata(
            ulid,
            NameGenerator.generateName(),
            UUID.randomUUID(),
            type,
            List.of(),
            null,
            owner,
            new UUID[0],
            false,
            0
        );
        ComputerRegistry.metadataStorage.storeData(ulid, metadata);
        return metadata;
    }
}