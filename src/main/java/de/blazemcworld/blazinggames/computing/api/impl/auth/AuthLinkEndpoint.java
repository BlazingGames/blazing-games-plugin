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

import dev.ivycollective.ivyhttp.http.APIDocs;
import de.blazemcworld.blazinggames.computing.api.TokenManager;
import de.blazemcworld.blazinggames.testing.CoveredByTests;
import de.blazemcworld.blazinggames.testing.tests.LoginFlowTest;
import de.blazemcworld.blazinggames.BlazingAPI;
import de.blazemcworld.blazinggames.computing.api.APIUtils;
import dev.ivycollective.ivyhttp.http.EarlyResponse;
import dev.ivycollective.ivyhttp.http.Endpoint;
import dev.ivycollective.ivyhttp.http.EndpointResponse;
import dev.ivycollective.ivyhttp.http.RequestContext;
import dev.ivycollective.ivyhttp.http.RequestMethod;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;

@CoveredByTests(LoginFlowTest.class)
public class AuthLinkEndpoint implements Endpoint {
    private static final String BASE_URL = "https://login.live.com/oauth20_authorize.srf";
    private static final String SCOPES = "Xboxlive.signin";

    @Override
    public String path() {
        return "/auth/link";
    }

    public static String generateMicrosoftLoginURL(String code) {
        BlazingAPI.Config config = BlazingAPI.getConfig();
        return BASE_URL + "?response_type=code&approval_prompt=auto&scope=" + SCOPES
            + "&client_id="
            + BlazingAPI.getConfig().microsoftClientID()
            + "&redirect_uri="
            + URLEncoder.encode(config.apiConfig().findAt() + AuthCallbackEndpoint.PATH, Charset.defaultCharset())
            + "&state="
            + code;
    }

    @Override
    public EndpointResponse GET(RequestContext context) throws EarlyResponse {
        boolean invalidCode = false;

        if (context.hasBody()) {
            var body = context.useBodyWrapper();
            if (body.hasValue("code")) {
                String code = body.getString("code");
                if (code.length() != 8) {
                    invalidCode = true;
                } else {
                    if (TokenManager.isCodeNotStarted(code.toUpperCase())) {
                        TokenManager.updateCodeAuthState(code.toUpperCase(), new TokenManager.UserLoggingIn());

                        var config = BlazingAPI.getConfig();
                        if (config.spoofMicrosoftServer()) {
                            String username = context.requireCleanCustom("mcname", body.getString("mcname"), 2, 16);
                            String uuid = context.requireCleanCustom("mcuuid", body.getString("mcuuid"), 36, 36);

                            return EndpointResponse.redirect(config.apiConfig().findAt() + AuthCallbackEndpoint.PATH + "?code=" + username + "." + uuid + "&state=" + code);
                        }

                        return EndpointResponse.redirect(generateMicrosoftLoginURL(code.toUpperCase()));
                    }

                    invalidCode = true;
                }
            }
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put("error", invalidCode);
        data.put("offline", BlazingAPI.getConfig().spoofMicrosoftServer());
        return APIUtils.ofHTML("codeinput.html", data);
    }

    @Override
    public APIDocs[] docs() {
        return new APIDocs[]{
            APIDocs.builder()
                .title("Start authentication flow (client-side)")
                .method(RequestMethod.GET)
                .description("Redirect the user to this page, or ask them to open it. This request should NOT be done by the server.")
                .addIncomingArgument("code", "(optional) The code if redirected to make the flow faster.")
                .addGenericsUnauthenthicated()
                .removeBodyFromGenerics()
                .addResponseCode(200, "Success (no code param)")
                .addResponseCode(302, "Success (with code param)")
                .build()
        };
    }
}
