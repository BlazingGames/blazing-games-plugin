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
package de.blazemcworld.blazinggames.computing.api.impl;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.computing.api.APIDocs;
import de.blazemcworld.blazinggames.computing.api.Endpoint;
import de.blazemcworld.blazinggames.computing.api.EndpointList;
import de.blazemcworld.blazinggames.computing.api.EndpointResponse;
import de.blazemcworld.blazinggames.computing.api.RequestContext;
import de.blazemcworld.blazinggames.computing.api.RequestMethod;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.bukkit.configuration.file.FileConfiguration;

public class RootEndpoint implements Endpoint {
    private static EndpointResponse RESPONSE;

    @Override
    public String path() {
        return "/";
    }

    public static void generateDocs() {
        HashMap<String, Object> root = new HashMap<>();
        FileConfiguration config = BlazingGames.get().getConfig();

        HashMap<String, Object> notice = new HashMap<>();
        notice.put("show", config.getBoolean("docs.notice.show"));
        notice.put("title", config.getString("docs.notice.title"));
        notice.put("description", config.getString("docs.notice.description"));
        notice.put("button", config.getString("docs.notice.button-title"));
        notice.put("url", config.getString("docs.notice.button-url"));
        root.put("notice", notice);
        
        HashMap<String, Object> instance = new HashMap<>();
        instance.put("label", config.getString("docs.official-instance.name"));
        instance.put("url", config.getString("docs.official-instance.url"));
        root.put("instance", instance);
        
        ArrayList<HashMap<String, Object>> playerurls = new ArrayList<>();
        config.getMapList("docs.user-links").forEach(map -> {
            HashMap<String, Object> url = new HashMap<>();
            url.put("label", map.get("name"));
            url.put("url", map.get("url"));
            playerurls.add(url);
        });
        root.put("playerurls", playerurls);
        
        ArrayList<HashMap<String, Object>> devurls = new ArrayList<>();
        config.getMapList("docs.developer-links").forEach(map -> {
            HashMap<String, Object> url = new HashMap<>();
            url.put("label", map.get("name"));
            url.put("url", map.get("url"));
            devurls.add(url);
        });
        root.put("devurls", devurls);
        
        HashMap<String, ArrayList<HashMap<String, Object>>> endpoints = new HashMap<>();
        for (EndpointList endpoint : EndpointList.values()) {
            String category = endpoint.category;
            if (category != null) {
                if (!endpoints.containsKey(category)) {
                    endpoints.put(category, new ArrayList<>());
                }

                HashMap<String, Object> entry = new HashMap<>();
                APIDocs[] docs = endpoint.endpoint.docs();

                for (APIDocs doc : docs) {
                    entry.put("title", doc.title());
                    entry.put("description", doc.description());
                    entry.put("path", endpoint.endpoint.path());
                    entry.put("method", doc.method().name());
                    entry.put("hrefid", "docs-" + doc.title().replace(" ", "-").toLowerCase());
                    entry.put("paramstitle", doc.method().flags.contains(RequestMethod.Flag.USE_QUERY_PARAMETERS) ? "Query parameters" : "Request body");
                    entry.put("incoming", doc.incomingArgs());
                    entry.put("outgoing", doc.outgoingArgs());
                    entry.put(
                        "responsecodes", doc.responseCodes().entrySet().stream().collect(Collectors.toMap(e -> String.valueOf(e.getKey()), Entry::getValue))
                    );
                    entry.put("permissions", doc.permissions().stream().collect(Collectors.toMap(Enum::name, p -> p.description)));
                    endpoints.get(category).add(entry);
                }
            }
        }

        root.put("endpointcategories", endpoints);
        RESPONSE = EndpointResponse.ofHTML("docs.html", root);
    }

    @Override
    public EndpointResponse GET(RequestContext context) {
        if (RESPONSE == null) {
            generateDocs();
        }

        return RESPONSE;
    }

    @Override
    public APIDocs[] docs() {
        return new APIDocs[0];
    }
}
