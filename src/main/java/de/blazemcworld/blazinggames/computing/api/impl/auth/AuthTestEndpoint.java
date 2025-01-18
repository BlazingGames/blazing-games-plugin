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
import de.blazemcworld.blazinggames.computing.api.EarlyResponse;
import de.blazemcworld.blazinggames.computing.api.Endpoint;
import de.blazemcworld.blazinggames.computing.api.EndpointResponse;
import de.blazemcworld.blazinggames.computing.api.RequestContext;
import de.blazemcworld.blazinggames.computing.api.RequestMethod;
import de.blazemcworld.blazinggames.testing.CoveredByTests;
import de.blazemcworld.blazinggames.testing.tests.LoginFlowTest;
import de.blazemcworld.blazinggames.testing.tests.UnlinkFlowTest;

@CoveredByTests({LoginFlowTest.class, UnlinkFlowTest.class})
public class AuthTestEndpoint implements Endpoint {
    @Override
    public String path() {
        return "/auth/test";
    }

    @Override
    public EndpointResponse GET(RequestContext context) throws EarlyResponse {
        context.requireAuthentication();
        return EndpointResponse.of200(new JsonObject()).build();
    }

    @Override
    public APIDocs[] docs() {
        return new APIDocs[]{
            APIDocs.builder()
                .title("Test Authenthication")
                .description("Verify that your JWT is still valid and can be used to authenthicate")
                .method(RequestMethod.GET)
                .addGenericsUnauthenthicated()
                .removeBodyFromGenerics()
                .build()
        };
    }
}
