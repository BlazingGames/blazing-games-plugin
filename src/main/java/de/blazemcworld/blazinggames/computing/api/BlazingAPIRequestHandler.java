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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import de.blazemcworld.blazinggames.BlazingGames;

public class BlazingAPIRequestHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ((exchange.getRequestURI().getPath().equals("/favicon.ico") || exchange.getRequestURI().getPath().equals("/favicon.ico/"))
            && exchange.getRequestMethod().equals("GET")) {
        }

        RequestContext context;
        try {
            context = this.makeContext(exchange);
        } catch (Exception e) {
            BlazingGames.get().debugLog("Failed to make context (error below)");
            BlazingGames.get().debugLog(e);
            throw e;
        }

        EndpointResponse response;
        try {
            if (!ComputingAPI.config.apiConfig().findAt().replace("https://", "").replace("http://", "").equals(context.getFirstHeader("Host"))) {
                BlazingGames.get().debugLog("Refused to respond: Host header is " + context.getFirstHeader("Host"));
                response = EndpointResponse.builder(421).build();
            } else if (context.ipAddress() == null) {
                BlazingGames.get().debugLog("Refused to respond: IP address is null");
                response = EndpointResponse.builder(421).build();
            } else {
                response = this.getResponse(context);
            }
        } catch (Exception e) {
            BlazingGames.get().debugLog("Failed to get response");
            BlazingGames.get().debugLog(e);
            throw e;
        }

        boolean emptyBody;
        try {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders()
                .add("Access-Control-Allow-Methods", Arrays.stream(RequestMethod.values()).map(m -> m.name()).collect(Collectors.joining(", ")));
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization");
            emptyBody = context.method().flags.contains(RequestMethod.Flag.RESPOND_EMPTY_BODY);
            response.headers.forEach((s, s2) -> exchange.getResponseHeaders().add(s, s2));
            exchange.sendResponseHeaders(this.emptyBodyVersionIfNeeded(response.status, emptyBody), (long)response.body.getBytes().length);
        } catch (Exception e) {
            BlazingGames.get().debugLog("Failed to send response headers");
            BlazingGames.get().debugLog(e);
            throw e;
        }

        try {
            if (!response.body.isEmpty() && !emptyBody) {
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.body.getBytes());
                }
            } else {
                exchange.getResponseBody().close();
            }
        } catch (Exception e) {
            BlazingGames.get().debugLog("Failed to send response body (error below)");
            BlazingGames.get().debugLog(e);
            throw e;
        }
    }

    private int emptyBodyVersionIfNeeded(int status, boolean needed) {
        if (!needed) {
            return status;
        } else {
            return status == 200 ? 204 : status;
        }
    }

    private RequestContext makeContext(HttpExchange exchange) {
        Map<String, List<String>> headers = exchange.getRequestHeaders();

        RequestMethod method;
        try {
            method = RequestMethod.valueOf(exchange.getRequestMethod());
        } catch (IllegalArgumentException e) {
            BlazingGames.get().debugLog(e);
            method = RequestMethod.NULL;
        }

        String body;
        if (method.flags.contains(RequestMethod.Flag.USE_QUERY_PARAMETERS)) {
            body = exchange.getRequestURI().getQuery();
        } else {
            try (
                InputStream stream = exchange.getRequestBody();
                InputStreamReader streamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(streamReader);
            ) {
                StringBuilder builder = new StringBuilder(512);

                int buffer;
                while ((buffer = reader.read()) != -1) {
                    builder.append((char)buffer);
                }

                body = builder.toString();
            } catch (IOException e) {
                BlazingGames.get().debugLog(e);
                body = null;
            }
        }

        String contentType = headers.getOrDefault("Content-Type", Collections.emptyList()).stream().findFirst().orElse(null);
        if (method.flags.contains(RequestMethod.Flag.USE_QUERY_PARAMETERS)) {
            contentType = "application/x-www-form-urlencoded";
        }

        if (contentType == null) {
            contentType = "application/json";
        }

        contentType = contentType.split(" ")[0].split(";")[0];
        JsonElement jsonBody;
        if (body == null) {
            jsonBody = null;
        } else {
            switch (contentType) {
                case "application/x-www-form-urlencoded":
                    JsonObject out = new JsonObject();
                    String[] pairs = body.split("&");

                    for (String pair : pairs) {
                        int idx = pair.indexOf("=");
                        if (idx != -1) {
                            out.addProperty(
                                URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8),
                                URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8)
                            );
                        }
                    }

                    jsonBody = out;
                    break;
                case "application/json":
                    try {
                        jsonBody = JsonParser.parseString(body);
                    } catch (JsonParseException e) {
                        jsonBody = null;
                        BlazingGames.get().debugLog(e);
                    }
                    break;
                default:
                    jsonBody = null;
                    BlazingGames.get().debugLog("No ContentType parser is available for body");
            }
        }

        String realIp = exchange.getRemoteAddress().getAddress().getHostAddress();
        String ip;
        if (ComputingAPI.config.apiConfig().proxyEnabled()) {
            boolean isAllowed = ComputingAPI.config.apiConfig().isAllowed(realIp);
            if (headers.containsKey(ComputingAPI.config.apiConfig().proxyIpAddressHeader()) && !headers.get(ComputingAPI.config.apiConfig().proxyIpAddressHeader()).isEmpty()) {
                ip = headers.get(ComputingAPI.config.apiConfig().proxyIpAddressHeader()).getFirst();
            } else {
                BlazingGames.get().debugLog("Missing IP address header on proxy " + realIp);
                ip = null;
            }

            if (!isAllowed) {
                BlazingGames.get().debugLog("Request from ip " + ip + " via proxy " + realIp + " was denied (not allowed)");
                ip = null;
            }
        } else {
            ip = realIp;
        }

        return new RequestContext(jsonBody, headers, ip, exchange.getRequestURI(), method);
    }

    private EndpointResponse getResponse(RequestContext context) {
        String path = context.uri().getPath();
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        if (path.equals("")) {
            path = "/";
        }

        String finalPath = path;
        Endpoint endpoint = Arrays.stream(EndpointList.values())
            .filter(ex -> ex.endpoint.path().equals(finalPath))
            .map(ex -> ex.endpoint)
            .findFirst()
            .orElse(null);
        if (endpoint == null) {
            return EndpointResponse.of404();
        } else {
            try {
                String method = context.method().methodCall;

                EndpointResponse response = switch (method) {
                    case "GET" -> endpoint.GET(context);
                    case "POST" -> endpoint.POST(context);
                    case "PUT" -> endpoint.PUT(context);
                    case "DELETE" -> endpoint.DELETE(context);
                    case "PATCH" -> endpoint.PATCH(context);
                    case "OPTIONS" -> EndpointResponse.of204().build();
                    default -> EndpointResponse.of405();
                };
                BlazingGames.get().debugLog("Got response " + response.status);
                return response;
            } catch (EarlyResponse e) {
                BlazingGames.get().debugLog("Got early response " + e.getResponse().status);
                return e.getResponse();
            } catch (Exception e) {
                BlazingGames.get().debugLog("Got exception");
                BlazingGames.get().log(e);
                return EndpointResponse.of500();
            }
        }
    }
}