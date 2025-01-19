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

import java.io.File;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.computing.api.APIDocs;
import de.blazemcworld.blazinggames.computing.api.EarlyResponse;
import de.blazemcworld.blazinggames.computing.api.Endpoint;
import de.blazemcworld.blazinggames.computing.api.EndpointResponse;
import de.blazemcworld.blazinggames.computing.api.RequestContext;
import de.blazemcworld.blazinggames.computing.api.body.FileBodyOutput;

public class PackZipEndpoint implements Endpoint {
    @Override
    public String path() {
        return "/pack.zip";
    }

    @Override
    public EndpointResponse GET(RequestContext context) throws EarlyResponse {
        if (BlazingGames.get().getPackConfig() == null) {
            throw new IllegalStateException("Pack is not enabled, but pack.zip endpoint was called");
        }
        File file = BlazingGames.get().getPackFile();
        return EndpointResponse.builder(200).body(new FileBodyOutput(file)).build();
    }

    @Override
    public APIDocs[] docs() {
        return new APIDocs[0];
    }
}