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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.blazemcworld.blazinggames.utils.GetGson;

import java.net.URI;
import java.util.List;
import java.util.Map;

public record RequestContext(JsonElement body, Map<String, List<String>> headers, String ipAddress, URI uri, RequestMethod method) {
    public static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789()!@#()-_=+[]{}|;:'\",./? ";

    public String getFirstHeader(String header) {
        if (!this.headers.containsKey(header)) {
            return null;
        } else {
            return this.headers.get(header).isEmpty() ? null : this.headers.get(header).getFirst();
        }
    }

    public boolean hasBody() {
        return this.body != null && !this.body.isJsonNull();
    }

    public JsonElement requireBody() throws EarlyResponse {
        if (!this.hasBody()) {
            throw new EarlyResponse(EndpointResponse.of400("No body"));
        } else {
            return this.body();
        }
    }

    public BodyWrapper<EarlyResponse> useBodyWrapper() throws EarlyResponse {
        JsonObject body = GetGson.getAsObject(this.requireBody(), EarlyResponse.of(EndpointResponse.of400("Request body is not an object")));
        return new BodyWrapper<EarlyResponse>(body, key -> new EarlyResponse(EndpointResponse.of400("Missing " + key)));
    }

    public LinkedUser getAuthentication() {
        String authHeader = this.getFirstHeader("Authorization");
        if (authHeader == null) {
            return null;
        } else {
            String[] parts = authHeader.split(" ");
            if (parts.length != 2) {
                return null;
            } else if (!"Bearer".equals(parts[0])) {
                return null;
            } else {
                String token = parts[1];
                return LinkedUser.getLinkedUserFromJWT(token);
            }
        }
    }

    public boolean isAuthenticated() {
        return this.getAuthentication() != null;
    }

    public LinkedUser requireAuthentication() throws EarlyResponse {
        LinkedUser linked = this.getAuthentication();
        if (linked == null) {
            throw new EarlyResponse(EndpointResponse.of401());
        } else {
            return linked;
        }
    }

    public void requirePermission(Permission permission) throws EarlyResponse {
        LinkedUser linked = this.requireAuthentication();
        if (!linked.permissions().contains(permission)) {
            throw new EarlyResponse(EndpointResponse.of403());
        }
    }

    public String requireClean(String paramName, String in) throws EarlyResponse {
        return this.requireCleanCustom(paramName, in, 3, 80);
    }

    public String requireCleanLong(String paramName, String in) throws EarlyResponse {
        return this.requireCleanCustom(paramName, in, 3, 250);
    }

    public String requireCleanCustom(String paramName, String in, int min, int max) throws EarlyResponse {
        if (in == null) {
            throw new EarlyResponse(EndpointResponse.of400("Missing " + paramName));
        } else if (in.length() < min) {
            throw new EarlyResponse(EndpointResponse.of400("Parameter " + paramName + " is too short (at least 3 chars)"));
        } else if (in.length() > max) {
            throw new EarlyResponse(EndpointResponse.of400("Parameter " + paramName + " is too long (at most 80 chars)"));
        } else {
            for (int i = 0; i < in.length(); i++) {
                char c = in.charAt(i);
                if ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789()!@#()-_=+[]{}|;:'\",./? ".indexOf(c) == -1) {
                    throw new EarlyResponse(EndpointResponse.of400("Parameter " + paramName + " contains invalid chars (" + c + ")"));
                }
            }

            return in;
        }
    }
}
