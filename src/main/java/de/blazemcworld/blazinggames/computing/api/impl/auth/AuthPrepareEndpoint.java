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
package de.blazemcworld.blazinggames.computing.api.impl.auth;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.computing.api.APIDocs;
import de.blazemcworld.blazinggames.computing.api.TokenManager;
import de.blazemcworld.blazinggames.computing.api.EarlyResponse;
import de.blazemcworld.blazinggames.computing.api.Endpoint;
import de.blazemcworld.blazinggames.computing.api.EndpointResponse;
import de.blazemcworld.blazinggames.computing.api.Permission;
import de.blazemcworld.blazinggames.computing.api.RequestContext;
import de.blazemcworld.blazinggames.computing.api.RequestMethod;
import de.blazemcworld.blazinggames.utils.GetGson;
import java.util.ArrayList;
import java.util.List;

public class AuthPrepareEndpoint implements Endpoint {
    @Override
    public String path() {
        return "/auth/prepare";
    }

    @Override
    public EndpointResponse POST(RequestContext context) throws EarlyResponse {
        var body = context.useBodyWrapper();
        String name = context.requireClean("name", body.getString("name"));
        String contact = context.requireClean("contact", body.getString("contact"));
        String purpose = context.requireClean("purpose", body.getString("purpose"));
        JsonArray rawPermissions = GetGson.getArray(body.body, "permissions", EarlyResponse.of(EndpointResponse.of400("Missing permissions array")));
        ArrayList<Permission> permissions = new ArrayList<>();

        for (JsonElement elem : rawPermissions) {
            try {
                permissions.add(Permission.valueOf(
                    context.requireClean("permission", GetGson.getAsString(elem, EarlyResponse.of(EndpointResponse.of400("Permission is not a string"))))
                ));
            } catch (IllegalArgumentException e) {
                BlazingGames.get().debugLog(e);
                return EndpointResponse.of400("Unrecognised permission (is is null? check for trailing commas)");
            }
        }

        TokenManager.ApplicationClaim application = new TokenManager.ApplicationClaim(name, contact, purpose, List.copyOf(permissions));
        String code = TokenManager.startAuthFlow(application);
        String key = TokenManager.makeServerTokenJWT(code);
        JsonObject output = new JsonObject();
        output.addProperty("code", code);
        output.addProperty("key", key);
        return EndpointResponse.of200(output).build();
    }

    @Override
    public APIDocs[] docs() {
        return new APIDocs[]{
            APIDocs.builder()
                .title("Prepare authentication flow")
                .method(RequestMethod.POST)
                .description("Prepares a new authenthication flow.")
                .addIncomingArgument("name", "The application name, which will be shown on the consent screen.")
                .addIncomingArgument("contact", "How you want to get contacted as the owner of the application.")
                .addIncomingArgument("purpose", "What the app does. Should be short and clear.")
                .addIncomingArgument("permissions", "A list of valid permissions of what the app needs to do. An empty array is accepted, for identification.")
                .addOutgoingArgument("code", "8-char code consisting of uppercase letters and numbers for the user to use. See <code>/auth/link</code>.")
                .addOutgoingArgument("key", "Key which will allow you to retrieve the real token after the flow is finished. See <code>/auth/redeem</code>.")
                .addGenericsUnauthenthicated()
                .build()
        };
    }
}
