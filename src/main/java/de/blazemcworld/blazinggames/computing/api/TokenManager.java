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
package de.blazemcworld.blazinggames.computing.api;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.base.Optional;

import de.blazemcworld.blazinggames.utils.JWTUtils;
import de.blazemcworld.blazinggames.utils.Pair;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TokenManager {
    private TokenManager() {}
    private static final SecureRandom rng = new SecureRandom();

    private static final Cache<String, Pair<TokenManager.AuthState, TokenManager.ApplicationClaim>> preAuthKeys = Caffeine.newBuilder()
        .maximumSize(1000L)
        .expireAfterWrite(Duration.ofMinutes(10L))
        .build();
    private static final Cache<String, TokenManager.Profile> unlinkRequests = Caffeine.newBuilder()
        .maximumSize(1000L)
        .expireAfterWrite(Duration.ofMinutes(10L))
        .build();
    private static final Cache<UUID, Integer> levels = Caffeine.newBuilder().maximumSize(1000L).expireAfterWrite(Duration.ofHours(13L)).build();
    private static final long instant = Instant.now().getEpochSecond();

    public static boolean hasConsentRevoked(LinkedUser linked) {
        return (linked.instant() != getInstant()) || (linked.level() != getLevel(linked.uuid()));
    }

    public static long getInstant() {
        return instant;
    }

    public static int getLevel(UUID uuid) {
        return (Integer)Optional.fromNullable((Integer)levels.getIfPresent(uuid)).or(0);
    }

    public static void increaseLevel(UUID uuid) {
        if (getLevel(uuid) >= 500) {
            levels.invalidate(uuid);
        } else {
            levels.put(uuid, getLevel(uuid) + 1);
        }
    }

    public static void storeUnlinkRequest(String token, TokenManager.Profile profile) {
        unlinkRequests.put(token, profile);
    }

    public static TokenManager.Profile getUnlinkRequest(String token) {
        return (TokenManager.Profile)unlinkRequests.getIfPresent(token);
    }

    public static void removeUnlinkRequest(String token) {
        unlinkRequests.invalidate(token);
    }

    public static String generateRandomString(int len) {
        byte[] bytes = new byte[len];
        rng.nextBytes(bytes);
        StringBuilder out = new StringBuilder();

        for (byte b : bytes) {
            out.append(String.format("%02x", b));
        }

        return out.toString();
    }

    public static String startAuthFlow(TokenManager.ApplicationClaim application) {
        String code = generateRandomString(4).toUpperCase();
        preAuthKeys.put(code, new Pair<>(new TokenManager.NotStarted(), application));
        return code;
    }

    private static TokenManager.AuthState _getAuthState(String code) {
        Pair<TokenManager.AuthState, TokenManager.ApplicationClaim> value = (Pair<TokenManager.AuthState, TokenManager.ApplicationClaim>)preAuthKeys.getIfPresent(
            code
        );
        return value == null ? null : value.left;
    }

    public static TokenManager.ApplicationClaim getApplicationClaimFromCode(String code) {
        Pair<TokenManager.AuthState, TokenManager.ApplicationClaim> value = (Pair<TokenManager.AuthState, TokenManager.ApplicationClaim>)preAuthKeys.getIfPresent(
            code
        );
        return value == null ? null : value.right;
    }

    public static String makeServerTokenJWT(String code) {
        return JWTUtils.sign(code, "AFTM", TimeUnit.MINUTES, 10L);
    }

    public static String getCodeFromJWT(String jwt) {
        return JWTUtils.parseToString(jwt, "AFTM");
    }

    public static TokenManager.Profile getProfileFromCode(String code) {
        TokenManager.AuthState state = _getAuthState(code);
        if (state == null) {
            return null;
        } else if (state instanceof TokenManager.UserRedirectingToDeciding) {
            return ((TokenManager.UserRedirectingToDeciding)state).profile();
        } else {
            return state instanceof TokenManager.UserDeciding ? ((TokenManager.UserDeciding)state).profile() : null;
        }
    }

    public static boolean isCodeNotStarted(String code) {
        TokenManager.AuthState state = _getAuthState(code);
        return state == null ? false : state instanceof TokenManager.NotStarted;
    }

    public static boolean isCodeUserLoggingIn(String code) {
        TokenManager.AuthState state = _getAuthState(code);
        return state == null ? false : state instanceof TokenManager.UserLoggingIn;
    }

    public static boolean isCodeUserRedirectingToDeciding(String code) {
        TokenManager.AuthState state = _getAuthState(code);
        return state == null ? false : state instanceof TokenManager.UserRedirectingToDeciding;
    }

    public static boolean isCodeUserDeciding(String code) {
        TokenManager.AuthState state = _getAuthState(code);
        return state == null ? false : state instanceof TokenManager.UserDeciding;
    }

    public static boolean isCodeUserApproved(String code) {
        TokenManager.AuthState state = _getAuthState(code);
        return state == null ? false : state instanceof TokenManager.UserApproved;
    }

    public static void invalidateCode(String code) {
        preAuthKeys.invalidate(code);
    }

    public static LinkedUser invalidateAndReturnLinkedUser(String code) {
        TokenManager.AuthState state = _getAuthState(code);
        if (state != null && state instanceof TokenManager.UserApproved approved) {
            LinkedUser linked = approved.linked();
            preAuthKeys.invalidate(code);
            return linked;
        } else {
            return null;
        }
    }

    public static void updateCodeAuthState(String code, TokenManager.AuthState state) {
        Pair<TokenManager.AuthState, TokenManager.ApplicationClaim> previousValue = (Pair<TokenManager.AuthState, TokenManager.ApplicationClaim>)preAuthKeys.getIfPresent(
            code
        );
        if (previousValue != null) {
            preAuthKeys.put(code, new Pair<>(state, previousValue.right));
        }
    }

    public static boolean verifyConfirmationToken(String code, String token) {
        String confirmationToken;
        if (isCodeUserDeciding(code)) {
            TokenManager.AuthState state = _getAuthState(code);
            confirmationToken = ((TokenManager.UserDeciding)state).confirmationToken();
        } else {
            if (!isCodeUserRedirectingToDeciding(code)) {
                return false;
            }

            TokenManager.AuthState state = _getAuthState(code);
            confirmationToken = ((TokenManager.UserRedirectingToDeciding)state).confirmationToken();
        }

        return token.equals(confirmationToken);
    }

    public static record ApplicationClaim(String name, String contact, String purpose, List<Permission> permissions) {
    }

    public static interface AuthState {}
    public static record Errored() implements TokenManager.AuthState {}
    public static record NotStarted() implements TokenManager.AuthState {}
    public static record UserLoggingIn() implements TokenManager.AuthState {}
    public static record WaitingForMicrosoft() implements TokenManager.AuthState {}
    public static record UserRedirectingToDeciding(TokenManager.Profile profile, String confirmationToken) implements TokenManager.AuthState {}
    public static record UserDeciding(TokenManager.Profile profile, String confirmationToken) implements TokenManager.AuthState {}
    public static record UserApproved(LinkedUser linked) implements TokenManager.AuthState {}
    public static record UserDeclined() implements TokenManager.AuthState {}

    public static record Profile(String username, UUID uuid) {}
}
