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

import com.google.gson.JsonObject;

import de.blazemcworld.blazinggames.computing.api.LinkedUser;
import de.blazemcworld.blazinggames.computing.api.impl.auth.AuthUnlinkEndpoint;
import de.blazemcworld.blazinggames.testing.BlazingTest;
import de.blazemcworld.blazinggames.utils.GetGson;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UnlinkFlowTest extends BlazingTest {

    @Override
    public boolean runAsync() {
        return true;
    }

    @Override
    protected void runTest() throws Exception {
        LinkedUser linkedUser = createLinkedUser(false);
        String signed = LinkedUser.signLinkedUser(linkedUser);

        // send test request 1
        JsonObject testResponse1 = sendGetRequest("/auth/test", signed);
        assertBoolean("/auth/test linked", GetGson.getBoolean(testResponse1, "success", new IllegalStateException()));

        // send callback request
        StringBuilder callbackUrl = new StringBuilder("http://localhost:8080/auth/callback");
        callbackUrl.append("?code=").append(linkedUser.username()).append(".").append(linkedUser.uuid().toString());
        callbackUrl.append("&state=").append(AuthUnlinkEndpoint.MAGIC_UNLINK_STATE);
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

        // send unlink confirm request
        JsonObject unlinkConfirmBody = new JsonObject();
        unlinkConfirmBody.addProperty("token", confirmationToken);
        unlinkConfirmBody.addProperty("verdict", true);
        Request unlinkConfirmRequest = new Request.Builder()
            .url("http://localhost:8080/auth/unlink-confirm")
            .post(RequestBody.create(unlinkConfirmBody.toString(), json))
            .build();
        Response unlinkConfirmResponse = client.newCall(unlinkConfirmRequest).execute();
        assertEquals(200, unlinkConfirmResponse.code());

        // send test request 2
        JsonObject testResponse2 = sendGetRequest("/auth/test", signed);
        assertBoolean("/auth/test unlinked", !GetGson.getBoolean(testResponse2, "success", new IllegalStateException()));
    }
}