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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.utils.GetGson;
import de.blazemcworld.blazinggames.utils.JWTUtils;

public record LinkedUser(String username, UUID uuid, int level, long instant, List<Permission> permissions, long expiresAt) {
    public static final String ISSUER = "LNKD";

    public static LinkedUser deserialize(JsonObject json) {
        try {
            String name = GetGson.getString(json, "username", new IllegalArgumentException("Missing username property while deserializing LinkedUser"));
            String rawUUID = GetGson.getString(json, "uuid", new IllegalArgumentException("Missing uuid property while deserializing LinkedUser"));
            int level = GetGson.getNumber(json, "level", new IllegalArgumentException("Missing level property while deserializing LinkedUser")).intValue();
            long instant = GetGson.getNumber(json, "instant", new IllegalArgumentException("Missing instant property while deserializing LinkedUser")).longValue();
            long expiresAt = GetGson.getNumber(json, "expiresAt", new IllegalArgumentException("Missing expiresAt property while deserializing LinkedUser")).longValue();

            JsonArray rawPermissions = GetGson.getArray(
                json, "permissions", new IllegalArgumentException("Missing permissions property while deserializing LinkedUser")
            );
            ArrayList<Permission> permissions = new ArrayList<>();
            for (JsonElement elem : rawPermissions) {
                String permission = GetGson.getAsString(
                    elem, new IllegalArgumentException("Invalid permission while deserializing LinkedUser (not string)")
                );

                permissions.add(Permission.valueOf(permission));
            }

            return new LinkedUser(name, UUID.fromString(rawUUID), level, instant, List.copyOf(permissions), expiresAt);
        } catch (IllegalArgumentException e) {
            BlazingGames.get().debugLog(e);
            return null;
        }
    }

    public static JsonObject serialize(LinkedUser linked) {
        JsonArray permissions = new JsonArray();
        for (Permission p : linked.permissions) {
            permissions.add(p.name());
        }

        JsonObject out = new JsonObject();
        out.addProperty("username", linked.username);
        out.addProperty("uuid", linked.uuid.toString());
        out.addProperty("level", linked.level);
        out.addProperty("instant", linked.instant);
        out.addProperty("expiresAt", linked.expiresAt);
        out.add("permissions", permissions);
        return out;
    }

    public static String signLinkedUser(LinkedUser linked) {
        long timeUntilExp = TimeUnit.SECONDS.toMillis(linked.expiresAt()) - System.currentTimeMillis();
        if (timeUntilExp < 0) {
            timeUntilExp = 0;
        }
        return JWTUtils.sign(serialize(linked), ISSUER, timeUntilExp);
    }

    public static LinkedUser getLinkedUserFromJWT(String jwt) {
        JsonObject obj = JWTUtils.parseToJsonObject(jwt, ISSUER);
        LinkedUser linked = obj == null ? null : deserialize(obj);
        if (linked == null) { return null; }
        if (TokenManager.hasConsentRevoked(linked)) { return null; }
        return linked;
    }
}
