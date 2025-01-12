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

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import de.blazemcworld.blazinggames.BlazingGames;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.regex.Pattern;

import javax.crypto.SecretKey;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;

import org.bukkit.configuration.file.FileConfiguration;

public class ComputingAPI {
    static ComputingAPI.Config config = null;
    private static HttpServer apiServer;

    private ComputingAPI() {
    }

    public static void setConfig(ComputingAPI.Config config) {
        ComputingAPI.config = config;
    }

    public static ComputingAPI.Config getConfig() {
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
            if (apiServer != null) {
                stopAll();
            }

            // API
            apiServer = getConfig().apiConfig.makeServer();
            if (apiServer == null) { return false; }
            apiServer.createContext("/", new BlazingAPIRequestHandler());
            apiServer.setExecutor(null);
            apiServer.start();


            return true;
        }
    }

    public static void stopAll() {
        if (apiServer != null) {
            apiServer.stop(15);
        }
    }

    public static record Config(
        boolean spoofMicrosoftServer,
        String microsoftClientID,
        String microsoftClientSecret,
        SecretKey jwtSecretKey,
        WebsiteConfig apiConfig,
        WebsiteConfig wssConfig
    ) {}

    public static record WebsiteConfig(
        boolean enabled,
        String findAt,
        int bindPort,
        boolean https,
        String httpsPassword,
        File httpsFile,
        boolean proxyEnabled,
        String proxyIpAddressHeader,
        boolean proxyAllowAll,
        List<String> proxyAllowedIPV4,
        List<String> proxyAllowedIPV6
    ) {
        public SSLContext makeSSLContext() {
            try (FileInputStream fileInputStream = new FileInputStream(this.httpsFile)) {
                KeyStore keystore = KeyStore.getInstance("PKCS12");
                keystore.load(fileInputStream, this.httpsPassword.toCharArray());
                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyManagerFactory.init(keystore, this.httpsPassword.toCharArray());
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
                return sslContext;
            } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | UnrecoverableKeyException | KeyManagementException | IOException e) {
                BlazingGames.get().log(e);
                return null;
            }
        }

        public HttpServer makeServer() {
            HttpServer server;

            if (this.https) {
                try {
                    HttpsServer https = HttpsServer.create(new InetSocketAddress(this.bindPort), 0);
                    https.setHttpsConfigurator(new HttpsConfigurator(this.makeSSLContext()) {
                        @Override
                        public void configure(HttpsParameters params) {
                            SSLContext context = this.getSSLContext();
                            SSLEngine engine = context.createSSLEngine();
                            params.setNeedClientAuth(false);
                            params.setCipherSuites(engine.getEnabledCipherSuites());
                            params.setProtocols(engine.getEnabledProtocols());
                            SSLParameters sslParameters = context.getDefaultSSLParameters();
                            params.setSSLParameters(sslParameters);
                        }
                    });
                    server = https;
                } catch (IOException e) {
                    server = null;
                    BlazingGames.get().log(e);
                }
            } else {
                try {
                    server = HttpServer.create(new InetSocketAddress(this.bindPort), 0);
                } catch (IOException e) {
                    server = null;
                    BlazingGames.get().log(e);
                }
            }

            return server;
        }

        public static WebsiteConfig auto(FileConfiguration config, String rootPath) {
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
                config.getStringList(rootPath + ".proxy.allowed-ipv6")
            );
        }

        public boolean isAllowed(String ip) {
            if (this.proxyAllowAll) {
                return true;
            }
            if (Pattern.matches("^(?:(?:25[0-5]|2[0-4]\\d|1?\\d{1,2})(?:\\.(?!$)|$)){4}$", ip)) {
                return this.proxyAllowedIPV4.contains(ip);
            } else {
                return Pattern.matches("^((([0-9A-Fa-f]{1,4}:){1,6}:)|(([0-9A-Fa-f]{1,4}:){7}))([0-9A-Fa-f]{1,4})$", ip)
                    ? this.proxyAllowedIPV6.contains(ip)
                    : false;
            }
        }
    }
}
