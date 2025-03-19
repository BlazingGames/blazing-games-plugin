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
package de.blazemcworld.blazinggames.computing.api.impl.skins;

import java.util.UUID;

import de.blazemcworld.blazinggames.utils.SkinRenderer;
import dev.ivycollective.ivyhttp.http.APIDocs;
import dev.ivycollective.ivyhttp.http.EarlyResponse;
import dev.ivycollective.ivyhttp.http.Endpoint;
import dev.ivycollective.ivyhttp.http.EndpointResponse;
import dev.ivycollective.ivyhttp.http.RequestContext;
import dev.ivycollective.ivyhttp.http.body.BasicBodyOutput;

public class SkinsEndpoint implements Endpoint {
    @Override
    public String path() {
        return "/skins";
    }

    @Override
    public EndpointResponse GET(RequestContext context) throws EarlyResponse {
        var body = context.useBodyWrapper();
        String uuidRaw = body.getString("uuid");
        UUID uuid;
        try {
            uuid = UUID.fromString(uuidRaw);
        } catch (IllegalArgumentException e) {
            return EndpointResponse.of400("Invalid UUID");
        }
        String modeRaw = body.getString("mode");
        boolean isMineskin;
        if ("vanilla".equals(modeRaw)) {
            isMineskin = false;
        } else if ("mineskin".equals(modeRaw)) {
            isMineskin = true;
        } else {
            return EndpointResponse.of400("Invalid mode");
        }
        
        if (SkinRenderer.hasSkin(uuid, isMineskin)) {
            return EndpointResponse
                .builder(200)
                .body(new BasicBodyOutput(SkinRenderer.getSkin(uuid, isMineskin)))
                .build();
        } else {
            return EndpointResponse.of404();
        }
    }

    @Override
    public APIDocs[] docs() {
        return new APIDocs[0];
    }
}
