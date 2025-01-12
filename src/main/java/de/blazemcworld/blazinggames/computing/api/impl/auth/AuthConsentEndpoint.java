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

import com.google.gson.JsonObject;
import de.blazemcworld.blazinggames.computing.api.APIDocs;
import de.blazemcworld.blazinggames.computing.api.TokenManager;
import de.blazemcworld.blazinggames.computing.api.EarlyResponse;
import de.blazemcworld.blazinggames.computing.api.Endpoint;
import de.blazemcworld.blazinggames.computing.api.EndpointResponse;
import de.blazemcworld.blazinggames.computing.api.LinkedUser;
import de.blazemcworld.blazinggames.computing.api.Permission;
import de.blazemcworld.blazinggames.computing.api.RequestContext;
import de.blazemcworld.blazinggames.utils.GetGson;

import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class AuthConsentEndpoint implements Endpoint {
    @Override
    public String path() {
        return "/auth/consent";
    }

    @Override
    public APIDocs[] docs() {
        return new APIDocs[0];
    }

    @Override
    public EndpointResponse GET(RequestContext context) throws EarlyResponse {
        JsonObject body = GetGson.getAsObject(context.requireBody(), EarlyResponse.of(EndpointResponse.of400("Missing query parameters")));
        String code = context.requireClean("code", GetGson.getString(body, "code", EarlyResponse.of(EndpointResponse.of400("Missing code argument"))));
        String token = context.requireClean("token", GetGson.getString(body, "token", EarlyResponse.of(EndpointResponse.of400("Missing token argument"))));
        if (code.length() != 8) {
            return EndpointResponse.of400("That's not a code, silly!");
        } else if (!TokenManager.isCodeUserRedirectingToDeciding(code)) {
            return EndpointResponse.of400("Code is invalid");
        } else if (!TokenManager.verifyConfirmationToken(code, token)) {
            return EndpointResponse.of400("Token is invalid");
        } else {
            TokenManager.Profile profile = TokenManager.getProfileFromCode(code);
            TokenManager.updateCodeAuthState(code, new TokenManager.UserDeciding(TokenManager.getProfileFromCode(code), token));
            TokenManager.ApplicationClaim appClaim = TokenManager.getApplicationClaimFromCode(code);
            HashMap<String, String> permissions = new HashMap<>();

            for (Permission p : appClaim.permissions()) {
                permissions.put(p.description, p.level.display);
            }

            HashMap<String, Object> params = new HashMap<>();
            params.put("code", code);
            params.put("token", token);
            params.put("username", profile.username());
            params.put("uuid", profile.uuid().toString());
            params.put("appname", appClaim.name());
            params.put("appcontact", appClaim.contact());
            params.put("apppurpose", appClaim.purpose());
            params.put("permissions", permissions);
            return EndpointResponse.ofHTML("consent.html", params);
        }
    }

    @Override
    public EndpointResponse POST(RequestContext context) throws EarlyResponse {
        JsonObject body = GetGson.getAsObject(context.requireBody(), EarlyResponse.of(EndpointResponse.of400("Missing body")));
        String code = context.requireClean("code", GetGson.getString(body, "code", EarlyResponse.of(EndpointResponse.of400("Missing code argument"))));
        String token = context.requireClean("token", GetGson.getString(body, "token", EarlyResponse.of(EndpointResponse.of400("Missing token argument"))));
        boolean verdict = context.requireClean(
                "verdict", GetGson.getString(body, "verdict", EarlyResponse.of(EndpointResponse.of400("Missing verdict argument")))
            ).equals("true");
        TokenManager.ApplicationClaim appClaim = TokenManager.getApplicationClaimFromCode(code);
        if (!TokenManager.isCodeUserDeciding(code)) {
            return EndpointResponse.authError("Code is invalid or expired", "No more details are available");
        } else if (!TokenManager.verifyConfirmationToken(code, token)) {
            return EndpointResponse.authError("Token is invalid or expired", "No more details are available");
        } else {
            TokenManager.Profile profile = TokenManager.getProfileFromCode(code);
            int level = TokenManager.getLevel(profile.uuid());
            TokenManager.updateCodeAuthState(
                code, verdict ? new TokenManager.UserApproved(new LinkedUser(
                        profile.username(),
                        profile.uuid(),
                        level,
                        TokenManager.getInstant(),
                        appClaim.permissions(),
                        Instant.now().plusSeconds(TimeUnit.HOURS.toSeconds(6L)).getEpochSecond()
                    )) : new TokenManager.UserDeclined()
            );
            String title = verdict ? appClaim.name() + " was granted access to your computers" : appClaim.name() + " was denied access to your computers";
            String desc = verdict ? "This expires in 6 hours." : "Change your mind? You can always grant access later.";
            HashMap<String, Object> out = new HashMap<>();
            out.put("title", title);
            out.put("body", desc);
            out.put("username", profile.username());
            out.put("uuid", profile.uuid());
            return EndpointResponse.ofHTML("verdict.html", out);
        }
    }
}
