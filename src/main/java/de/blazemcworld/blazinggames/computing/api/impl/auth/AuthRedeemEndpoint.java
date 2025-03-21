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
import dev.ivycollective.ivyhttp.http.APIDocs;
import de.blazemcworld.blazinggames.computing.api.TokenManager;
import de.blazemcworld.blazinggames.testing.CoveredByTests;
import de.blazemcworld.blazinggames.testing.tests.LoginFlowTest;
import dev.ivycollective.ivyhttp.http.EarlyResponse;
import dev.ivycollective.ivyhttp.http.Endpoint;
import dev.ivycollective.ivyhttp.http.EndpointResponse;
import de.blazemcworld.blazinggames.computing.api.LinkedUser;
import dev.ivycollective.ivyhttp.http.RequestContext;
import dev.ivycollective.ivyhttp.http.RequestMethod;

@CoveredByTests(LoginFlowTest.class)
public class AuthRedeemEndpoint implements Endpoint {
    @Override
    public EndpointResponse POST(RequestContext context) throws EarlyResponse {
        String key = context.requireCleanLong("key", context.useBodyWrapper().getString("key"));
        String code = TokenManager.getCodeFromJWT(key);
        if (code == null) {
            return EndpointResponse.of400("Invalid key");
        } else if (!TokenManager.isCodeUserApproved(code)) {
            return EndpointResponse.of400("Key isn't approved");
        } else {
            LinkedUser linked = TokenManager.invalidateAndReturnLinkedUser(code);
            String signed = LinkedUser.signLinkedUser(linked);
            JsonObject output = new JsonObject();
            output.addProperty("token", signed);
            output.add("body", LinkedUser.serialize(linked));
            return EndpointResponse.of200(output).build();
        }
    }

    @Override
    public APIDocs[] docs() {
        return new APIDocs[]{
            new APIDocs.Builder()
                .title("Redeem auth code for token")
                .method(RequestMethod.POST)
                .description("After a successful auth flow, you can redeem the key you were given in <code>/auth/prepare</code> for a token.")
                .addIncomingArgument("key", "The key to redeem")
                .addOutgoingArgument("token", "JWT for usage with the other APIs")
                .addOutgoingArgument("body", "The JSON of the JWT. Useful if you cannot parse the JWT, otherwise can be ignored. ")
                .addGenericsUnauthenthicated()
                .build()
        };
    }

    @Override
    public String path() {
        return "/auth/redeem";
    }
}
