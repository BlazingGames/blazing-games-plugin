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

import de.blazemcworld.blazinggames.computing.api.APIDocs;
import de.blazemcworld.blazinggames.computing.api.TokenManager;
import de.blazemcworld.blazinggames.computing.api.EarlyResponse;
import de.blazemcworld.blazinggames.computing.api.Endpoint;
import de.blazemcworld.blazinggames.computing.api.EndpointResponse;
import de.blazemcworld.blazinggames.computing.api.RequestContext;
import java.util.HashMap;

public class AuthUnlinkConfirmEndpoint implements Endpoint {
    @Override
    public APIDocs[] docs() {
        return new APIDocs[0];
    }

    @Override
    public String path() {
        return "/auth/unlink-confirm";
    }

    @Override
    public EndpointResponse GET(RequestContext context) throws EarlyResponse {
        String token = context.requireClean("token", context.useBodyWrapper().getString("token"));
        TokenManager.Profile profile = TokenManager.getUnlinkRequest(token);
        if (profile == null) {
            return EndpointResponse.authError("Token is invalid or expired", "Tokens expire after 10 minutes. If you want to start over, visit /auth/link.");
        } else {
            HashMap<String, Object> map = new HashMap<>();
            map.put("token", token);
            map.put("username", profile.username());
            map.put("uuid", profile.uuid().toString());
            return EndpointResponse.ofHTML("unlink.html", map);
        }
    }

    @Override
    public EndpointResponse POST(RequestContext context) throws EarlyResponse {
        var body = context.useBodyWrapper();
        String token = context.requireClean("token", body.getString("token"));
        boolean verdict = body.getBoolean("verdict");
        TokenManager.Profile profile = TokenManager.getUnlinkRequest(token);
        if (profile == null) {
            return EndpointResponse.authError("Token is invalid or expired", "Tokens expire after 10 minutes.");
        } else {
            TokenManager.removeUnlinkRequest(token);
            if (verdict) {
                TokenManager.increaseLevel(profile.uuid());
            }

            String title = verdict ? "Applications unlinked" : "Canceled unlink request";
            String desc = verdict ? "All currently linked applications are now no longer linked." : "You can always unlink if you want.";
            HashMap<String, Object> out = new HashMap<>();
            out.put("title", title);
            out.put("body", desc);
            out.put("username", profile.username());
            out.put("uuid", profile.uuid());
            return EndpointResponse.ofHTML("verdict.html", out);
        }
    }
}
