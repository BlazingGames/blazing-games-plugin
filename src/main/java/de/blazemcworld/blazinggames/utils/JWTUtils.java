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
package de.blazemcworld.blazinggames.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.computing.api.ComputingAPI;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;

public class JWTUtils {
    private JWTUtils() {
    }

    private static SecretKey key() {
        return ComputingAPI.getConfig().jwtSecretKey();
    }

    public static String sign(JsonElement body, String issuer, TimeUnit unit, long duration) {
        return sign(body.toString(), issuer, unit.toMillis(duration));
    }

    public static String sign(JsonElement body, String issuer, long millis) {
        return sign(body.toString(), issuer, millis);
    }

    public static String sign(String body, String issuer, TimeUnit unit, long duration) {
        return sign(body, issuer, unit.toMillis(duration));
    }

    public static String sign(String body, String issuer, long millis) {
        return Jwts.builder()
            .notBefore(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + millis))
            .subject(body)
            .issuer(issuer)
            .signWith(key())
            .compact();
    }

    public static String parseToString(String jwt, String issuer) {
        try {
            Claims claims = (Claims)Jwts.parser().verifyWith(key()).requireIssuer(issuer).build().parseSignedClaims(jwt).getPayload();
            Long exp = (Long)claims.get("exp", Long.class);
            Long nbf = (Long)claims.get("nbf", Long.class);
            long realTime = System.currentTimeMillis() / 1000L;
            if (exp == null || nbf == null) {
                return null;
            } else {
                return realTime >= nbf && realTime <= exp ? claims.getSubject() : null;
            }
        } catch (IllegalArgumentException | JwtException e) {
            BlazingGames.get().debugLog(e);
            return null;
        }
    }

    public static JsonElement parseToJson(String jwt, String issuer) {
        String string = parseToString(jwt, issuer);
        if (string == null) {
            return null;
        } else {
            try {
                return JsonParser.parseString(string);
            } catch (JsonParseException e) {
                BlazingGames.get().debugLog(e);
                return null;
            }
        }
    }

    public static JsonObject parseToJsonObject(String jwt, String issuer) {
        try {
            return GetGson.getAsObject(parseToJson(jwt, issuer), new IllegalArgumentException("Invalid JWT"));
        } catch (IllegalArgumentException e) {
            BlazingGames.get().debugLog(e);
            return null;
        }
    }

    public static JsonArray parseToJsonArray(String jwt, String issuer) {
        try {
            return GetGson.getAsArray(parseToJson(jwt, issuer), new IllegalArgumentException("Invalid JWT"));
        } catch (IllegalArgumentException e) {
            BlazingGames.get().debugLog(e);
            return null;
        }
    }
}
