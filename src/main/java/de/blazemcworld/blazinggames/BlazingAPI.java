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
package de.blazemcworld.blazinggames;

import dev.ivycollective.ivyhttp.IvyHttp;
import dev.ivycollective.ivyhttp.RateLimiter;
import dev.ivycollective.ivyhttp.WebsiteConfig;
import dev.ivycollective.ivyhttp.http.RestAPIWebsite;
import dev.ivycollective.ivyhttp.http.RestEndpoints;
import dev.ivycollective.ivyhttp.wss.SocketServerWebsite;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

import org.bukkit.configuration.file.FileConfiguration;

import de.blazemcworld.blazinggames.computing.api.EndpointList;
import de.blazemcworld.blazinggames.computing.wss.BlazingWSS;

public class BlazingAPI {
    static BlazingAPI.Config config = null;
    private static IvyHttp ivyHttp = null;
    private BlazingAPI() {}

    public static void setConfig(BlazingAPI.Config config) {
        BlazingAPI.config = config;
    }

    public static BlazingAPI.Config getConfig() {
        if (config == null) {
            throw new IllegalStateException();
        } else {
            return config;
        }
    }

    public static boolean startAll() {
        if (config == null) {
            throw new IllegalArgumentException("Config is not defined");
        } else {
            if (ivyHttp != null) {
                ivyHttp.stopAll();
            }

            RestEndpoints endpoints = new RestEndpoints();
            for (EndpointList endpoint : EndpointList.values()) {
                if (config.hasAllRequiredFeatures(endpoint.requiredFeatures)) {
                    endpoints.addEndpoint(endpoint.endpoint);
                }
            }

            ivyHttp = new IvyHttp();
            ivyHttp.addWebsite(new RestAPIWebsite(endpoints), config.apiConfig());
            ivyHttp.addWebsite(new SocketServerWebsite(new BlazingWSS()), config.wssConfig());

            ivyHttp.start();

            return true;
        }
    }

    public static void stopAll() {
        if (ivyHttp != null) {
            ivyHttp.stopAll();
            ivyHttp = null;
        }
    }

    public static record Config(
        boolean spoofMicrosoftServer,
        String microsoftClientID,
        String microsoftClientSecret,
        SecretKey jwtSecretKey,
        WebsiteConfig apiConfig,
        WebsiteConfig wssConfig,
        List<RequiredFeature> availableFeatures
    ) {
        public boolean hasAllRequiredFeatures(List<RequiredFeature> features) {
            ArrayList<RequiredFeature> missingFeatures = new ArrayList<>(features);
            for (RequiredFeature feature : this.availableFeatures) {
                missingFeatures.remove(feature);
            }
            return missingFeatures.isEmpty();
        }
    }

    public static WebsiteConfig createWebsiteConfig(FileConfiguration config, String rootPath) {
        return new WebsiteConfig(
            config.getBoolean(rootPath + ".enabled"),
            config.getString(rootPath + ".find-at"),
            config.getInt(rootPath + ".bind.port"),
            config.getBoolean(rootPath + ".bind.https.enabled"),
            config.getString(rootPath + ".bind.https.password"),
            new File(config.getString(rootPath + ".bind.https.file")),
            config.getBoolean(rootPath + ".proxy.in-use"),
            config.getString(rootPath + ".proxy.ip-address-header"),
            config.getBoolean(rootPath + ".proxy.allow-all"),
            config.getStringList(rootPath + ".proxy.allowed-ipv4"),
            config.getStringList(rootPath + ".proxy.allowed-ipv6"),
            new RateLimiter()
        );
    }
}
