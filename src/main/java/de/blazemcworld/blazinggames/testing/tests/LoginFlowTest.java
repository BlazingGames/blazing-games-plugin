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
package de.blazemcworld.blazinggames.testing.tests;

import java.util.UUID;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.blazemcworld.blazinggames.testing.BlazingTest;
import de.blazemcworld.blazinggames.utils.GetGson;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginFlowTest extends BlazingTest {
    @Override
    public boolean runAsync() {
        return true;
    }

    @Override
    protected void runTest() throws Exception {
        // implementation note:
        // the server really doesn't care too much and allows you to submit
        // requests that aren't the same as in the html, such as json instead of form body or
        // giving the boolean as a literal instead of a string, and this test does use those.
        // 
        // if we really wanted a test to make sure it worked perfectly, it may be better to set
        // up something that emulates a web browser and actually followed the redirects, which is
        // outside the scope of these tests.

        String username = "LoginFlowTest";
        UUID uuid = UUID.randomUUID();

        // server: send prepare code request
        JsonObject prepareBody = new JsonObject();
        prepareBody.addProperty("name", "login flow test");
        prepareBody.addProperty("contact", ".....");
        prepareBody.addProperty("purpose", "testing xd");
        prepareBody.add("permissions", new JsonArray());
        JsonObject prepareResponse = sendPostRequestUnauthenticated("/auth/prepare", prepareBody);
        assertBoolean("/auth/prepare", GetGson.getBoolean(prepareResponse, "success", new IllegalStateException()));
        String code = GetGson.getString(prepareResponse, "code", new IllegalArgumentException("Prepare endpoint missing code"));
        String key = GetGson.getString(prepareResponse, "key", new IllegalStateException("Prepare endpoint missing key"));

        // client: send link request
        StringBuilder linkUrl = new StringBuilder("http://localhost:8080/auth/link");
        linkUrl.append("?code=").append(code);
        linkUrl.append("&mcname=").append(username);
        linkUrl.append("&mcuuid=").append(uuid.toString());
        Request linkRequest = new Request.Builder()
            .url(linkUrl.toString())
            .build();
        Response linkResponse = client.newCall(linkRequest).execute();
        assertEquals(302, linkResponse.code());

        // client: send callback request
        StringBuilder callbackUrl = new StringBuilder("http://localhost:8080/auth/callback");
        callbackUrl.append("?code=").append(username).append(".").append(uuid.toString());
        callbackUrl.append("&state=").append(code);
        Request callbackRequest = new Request.Builder()
            .url(callbackUrl.toString())
            .build();
        Response callbackResponse = client.newCall(callbackRequest).execute();
        assertEquals(302, callbackResponse.code());
        String locationHeader = callbackResponse.header("Location");
        assertNotNull(locationHeader);

        // parse for confirmation token
        String[] parts = locationHeader.split("\\?");
        assertBoolean("parts.length == 2", parts.length == 2);
        String[] params = parts[1].split("&");
        String confirmationToken = null;
        for (String param : params) {
            if (param.startsWith("token=")) {
                confirmationToken = param.split("=")[1];
            }
        }
        assertNotNull(confirmationToken);

        // client: send pre consent request
        StringBuilder preConsentUrl = new StringBuilder("http://localhost:8080/auth/consent");
        preConsentUrl.append("?code=").append(code);
        preConsentUrl.append("&token=").append(confirmationToken);
        Request preConsentRequest = new Request.Builder()
            .url(preConsentUrl.toString())
            .get()
            .build();
        Response preConsentResponse = client.newCall(preConsentRequest).execute();
        assertEquals(200, preConsentResponse.code());
        // note: this is needed due to internal flow state checks

        // client: send consent request
        JsonObject consentBody = new JsonObject();
        consentBody.addProperty("code", code);
        consentBody.addProperty("token", confirmationToken);
        consentBody.addProperty("verdict", true);
        Request consentRequest = new Request.Builder()
            .url("http://localhost:8080/auth/consent")
            .post(RequestBody.create(consentBody.toString(), json))
            .build();
        Response consentResponse = client.newCall(consentRequest).execute();
        assertEquals(200, consentResponse.code());

        // server: send redeem request
        JsonObject redeemBody = new JsonObject();
        redeemBody.addProperty("key", key);
        JsonObject redeemResponse = sendPostRequestUnauthenticated("/auth/redeem", redeemBody);
        assertBoolean("/auth/redeem", GetGson.getBoolean(redeemResponse, "success", new IllegalStateException()));
        String token = GetGson.getString(redeemResponse, "token", new IllegalStateException("Redeem endpoint missing token"));

        // server: send test request
        JsonObject testResponse = sendGetRequest("/auth/test", token);
        assertBoolean("/auth/test", GetGson.getBoolean(testResponse, "success", new IllegalStateException()));
    }
}