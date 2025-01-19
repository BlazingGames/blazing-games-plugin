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
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.computing.api.APIDocs;
import de.blazemcworld.blazinggames.computing.api.TokenManager;
import de.blazemcworld.blazinggames.computing.api.BlazingAPI;
import de.blazemcworld.blazinggames.computing.api.EarlyResponse;
import de.blazemcworld.blazinggames.computing.api.Endpoint;
import de.blazemcworld.blazinggames.computing.api.EndpointResponse;
import de.blazemcworld.blazinggames.computing.api.RequestContext;
import de.blazemcworld.blazinggames.utils.GetGson;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.FormBody.Builder;

public class AuthCallbackEndpoint implements Endpoint {
    public static final String PATH = "/auth/callback";
    private final OkHttpClient client = new OkHttpClient();

    @Override
    public String path() {
        return PATH;
    }

    @Override
    public EndpointResponse GET(RequestContext context) throws EarlyResponse {
        var body = context.useBodyWrapper();
        if (body.hasValue("error") && body.hasValue("error_description")) {
            return EndpointResponse.authError(body.getString("error"), body.getString("error_description"));
        } else {
            String msCode = context.requireClean("code", body.getString("code"));
            String state = context.requireCleanCustom("state", body.getString("state"), 8, 8);

            boolean isUnlinkRequest = (state.equals(AuthUnlinkEndpoint.MAGIC_UNLINK_STATE));
            if (!isUnlinkRequest) {
                if (!TokenManager.isCodeUserLoggingIn(state)) {
                    return EndpointResponse.authError("Token (state) is invalid or expired", "The token might've expired");
                }
                TokenManager.updateCodeAuthState(state, new TokenManager.WaitingForMicrosoft());
            }

            TokenManager.Profile profile = this.microsoftAuthenticationDance(msCode);
            if (profile == null) {
                if (!isUnlinkRequest) {
                    TokenManager.updateCodeAuthState(state, new TokenManager.Errored());
                }

                if (BlazingAPI.getConfig().spoofMicrosoftServer()) {
                    return EndpointResponse.authError("Bad username/UUID", "Or you didn't provide any.");
                }

                return EndpointResponse.authError(
                    "An error with Microsoft authenthication occurred", "Make sure that you own Minecraft and have picked a username."
                );
            } else {
                String confirmationToken = TokenManager.generateRandomString(32);
                if (isUnlinkRequest) {
                    TokenManager.invalidateCode(state);
                    TokenManager.storeUnlinkRequest(confirmationToken, profile);
                    return EndpointResponse.redirect("/auth/unlink-confirm?token=" + confirmationToken);
                } else {
                    TokenManager.updateCodeAuthState(state, new TokenManager.UserRedirectingToDeciding(profile, confirmationToken));
                    return EndpointResponse.redirect("/auth/consent?code=" + state + "&token=" + confirmationToken);
                }
            }
        }
    }

    private RequestBody _makeMultipartBodyFromJson(JsonElement json) {
        Builder builder = new Builder();

        for (String key : json.getAsJsonObject().keySet()) {
            builder.add(key, json.getAsJsonObject().get(key).getAsString());
        }

        return builder.build();
    }

    private JsonElement _post(String url, JsonElement body, boolean formUrlEncoded) {
        Request request = new okhttp3.Request.Builder()
            .url(url)
            .header("Accept", "application/json")
            .post(formUrlEncoded ? this._makeMultipartBodyFromJson(body) : RequestBody.create(BlazingGames.gson.toJson(body), MediaType.parse("application/json")))
            .header("Content-Type", formUrlEncoded ? "application/x-www-form-urlencoded" : "application/json")
            .build();

        try {
            Response response = this.client.newCall(request).execute();
            if (!response.isSuccessful()) {
                BlazingGames.get().debugLog("-------- Failed request log start");
                BlazingGames.get().debugLog("Unexpected reply " + response);
                BlazingGames.get().debugLog(response.body().string());
                BlazingGames.get().debugLog("-------- Failed request log end");
                return null;
            }

            JsonElement responseJson;
            if (response.body() == null) {
                throw new IOException("Body is empty");
            }

            String rawBody = response.body().string();
            responseJson = JsonParser.parseString(rawBody);

            if (response != null) {
                response.close();
            }

            return responseJson;
        } catch (JsonParseException | IOException e) {
            BlazingGames.get().debugLog(e);
            return null;
        }
    }

    private TokenManager.Profile microsoftAuthenticationDance(String code) {
        BlazingAPI.Config config = BlazingAPI.getConfig();
        if (config.spoofMicrosoftServer()) {
            String[] parts = code.split("\\.");
            if (parts.length != 2) {
                BlazingGames.get().debugLog("Bad test username/UUID: bad part count");
                return null;
            }
            
            String username = parts[0];
            if (!Pattern.matches("^[a-zA-Z0-9_]{2,16}$", username)) {
                BlazingGames.get().debugLog("Bad test username/UUID: name regex didn't match");
                return null;
            }

            UUID uuid;
            try {
                uuid = UUID.fromString(parts[1]);
            } catch (IllegalArgumentException e) {
                BlazingGames.get().debugLog("Bad test username/UUID: illegal uuid");
                return null;
            }

            return new TokenManager.Profile(username, uuid);
        }

        try {
            JsonObject token = new JsonObject();
            token.addProperty("client_id", config.microsoftClientID());
            token.addProperty("client_secret", config.microsoftClientSecret());
            token.addProperty("code", code);
            token.addProperty("grant_type", "authorization_code");
            token.addProperty("redirect_uri", config.apiConfig().findAt() + PATH);
            JsonElement tokenResponseRaw = this._post("https://login.live.com/oauth20_token.srf", token, true);
            if (tokenResponseRaw == null) {
                return null;
            } else {
                JsonObject tokenResponse = GetGson.getAsObject(tokenResponseRaw, new IOException("Token response is not an object"));
                String tokenToken = GetGson.getString(tokenResponse, "access_token", new IOException("Token response does not contain access_token"));
                JsonObject xboxLive = new JsonObject();
                JsonObject xboxLiveProperties = new JsonObject();
                xboxLiveProperties.addProperty("AuthMethod", "RPS");
                xboxLiveProperties.addProperty("SiteName", "user.auth.xboxlive.com");
                xboxLiveProperties.addProperty("RpsTicket", "d=" + tokenToken);
                xboxLive.add("Properties", xboxLiveProperties);
                xboxLive.addProperty("RelyingParty", "http://auth.xboxlive.com");
                xboxLive.addProperty("TokenType", "JWT");
                JsonElement xboxLiveResponseRaw = this._post("https://user.auth.xboxlive.com/user/authenticate", xboxLive, false);
                if (xboxLiveResponseRaw == null) {
                    return null;
                } else {
                    JsonObject xboxLiveResponse = GetGson.getAsObject(xboxLiveResponseRaw, new IOException("Xbox Live response is not an object"));
                    String xboxLiveToken = GetGson.getString(xboxLiveResponse, "Token", new IOException("Xbox Live response does not contain Token"));
                    JsonObject xboxLiveDisplayClaims = GetGson.getObject(
                        xboxLiveResponse, "DisplayClaims", new IOException("Xbox Live response does not contain DisplayClaims")
                    );
                    JsonArray xboxLiveXUI = GetGson.getArray(
                        xboxLiveDisplayClaims, "xui", new IOException("Xbox Live response does not contain DisplayClaims.xui")
                    );
                    if (xboxLiveXUI.isEmpty()) {
                        throw new IOException("DisplayClaims.xui is empty in Xbox Live response");
                    } else {
                        JsonObject xboxLiveXUIElem = GetGson.getAsObject(
                            xboxLiveXUI.get(0), new IOException("DisplayClaims.xui[0] is not an object in Xbox Live response")
                        );
                        String userHash = GetGson.getString(
                            xboxLiveXUIElem, "uhs", new IOException("Xbox Live response does not contain DisplayClaims.xui[0].uhs")
                        );
                        JsonObject xsts = new JsonObject();
                        JsonObject xstsProperties = new JsonObject();
                        JsonArray xstsUserTokens = new JsonArray();
                        xstsUserTokens.add(xboxLiveToken);
                        xstsProperties.add("UserTokens", xstsUserTokens);
                        xstsProperties.addProperty("SandboxId", "RETAIL");
                        xsts.add("Properties", xstsProperties);
                        xsts.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
                        xsts.addProperty("TokenType", "JWT");
                        JsonElement xstsResponseRaw = this._post("https://xsts.auth.xboxlive.com/xsts/authorize", xsts, false);
                        if (xstsResponseRaw == null) {
                            return null;
                        } else {
                            JsonObject xstsResponse = GetGson.getAsObject(xstsResponseRaw, new IOException("XSTS response is not an object"));
                            String xstsToken = GetGson.getString(xstsResponse, "Token", new IOException("XSTS response does not contain Token"));
                            JsonObject minecraft = new JsonObject();
                            minecraft.addProperty("identityToken", "XBL3.0 x=%s;%s".formatted(userHash, xstsToken));
                            JsonElement minecraftResponseRaw = this._post("https://api.minecraftservices.com/authentication/login_with_xbox", minecraft, false);
                            if (minecraftResponseRaw == null) {
                                return null;
                            } else {
                                JsonObject minecraftResponse = GetGson.getAsObject(minecraftResponseRaw, new IOException("Minecraft response is not an object"));
                                String minecraftToken = GetGson.getString(
                                    minecraftResponse, "access_token", new IOException("Minecraft response does not contain access_token")
                                );
                                return this.getMinecraftProfileFromMicrosoftAuthenticationDance(minecraftToken);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            BlazingGames.get().debugLog(e);
            return null;
        }
    }

    private TokenManager.Profile getMinecraftProfileFromMicrosoftAuthenticationDance(String minecraftToken) {
        Request request = new okhttp3.Request.Builder()
            .url("https://api.minecraftservices.com/minecraft/profile")
            .header("Accept", "application/json")
            .header("Authorization", "Bearer " + minecraftToken)
            .build();

        try {
            Response response = this.client.newCall(request).execute();

            TokenManager.Profile profile;
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            if (response.body() == null) {
                throw new IOException("Body is empty");
            }

            String rawRawBody = response.body().string();
            JsonElement rawBody = JsonParser.parseString(rawRawBody);
            JsonObject body = GetGson.getAsObject(rawBody, new IOException("Body isn't an object"));
            String rawUUID = GetGson.getString(body, "id", new IOException("UUID isn't included in body"));
            String username = GetGson.getString(body, "name", new IOException("Username isn't included in body"));
            StringBuilder uuidBuffer = new StringBuilder(rawUUID);
            uuidBuffer.insert(20, '-');
            uuidBuffer.insert(16, '-');
            uuidBuffer.insert(12, '-');
            uuidBuffer.insert(8, '-');
            UUID uuid = UUID.fromString(uuidBuffer.toString());
            profile = new TokenManager.Profile(username, uuid);

            if (response != null) {
                response.close();
            }

            return profile;
        } catch (JsonParseException | IOException e) {
            BlazingGames.get().debugLog(e);
            return null;
        }
    }

    @Override
    public APIDocs[] docs() {
        return new APIDocs[0];
    }
}
