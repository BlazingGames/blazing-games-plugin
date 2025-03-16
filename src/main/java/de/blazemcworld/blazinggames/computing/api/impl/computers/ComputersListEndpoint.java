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
package de.blazemcworld.blazinggames.computing.api.impl.computers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.blazemcworld.blazinggames.computing.ComputerEditor;
import de.blazemcworld.blazinggames.computing.ComputerMetadata;
import dev.ivycollective.ivyhttp.http.APIDocs;
import dev.ivycollective.ivyhttp.http.EarlyResponse;
import dev.ivycollective.ivyhttp.http.Endpoint;
import dev.ivycollective.ivyhttp.http.EndpointResponse;
import de.blazemcworld.blazinggames.computing.api.APIUtils;
import de.blazemcworld.blazinggames.computing.api.LinkedUser;
import de.blazemcworld.blazinggames.computing.api.Permission;
import dev.ivycollective.ivyhttp.http.RequestContext;
import dev.ivycollective.ivyhttp.http.RequestMethod;

public class ComputersListEndpoint implements Endpoint {
    @Override
    public String path() {
        return "/computers/list";
    }

    @Override
    public EndpointResponse GET(RequestContext context) throws EarlyResponse {
        APIUtils utils = APIUtils.of(context);
        LinkedUser linked = utils.requireAuthentication();
        utils.requirePermission(Permission.READ_COMPUTERS);
        JsonObject object = new JsonObject();
        JsonArray computers = new JsonArray();

        for (ComputerMetadata computer : ComputerEditor.getAccessibleComputers(linked.uuid())) {
            computers.add(computer.serialize());
        }

        object.add("computers", computers);
        return EndpointResponse.of200(object).build();
    }

    @Override
    public APIDocs[] docs() {
        return new APIDocs[]{
            APIDocs.builder()
                .title("List computers")
                .description("View a list of all computers that the player owns or has permissions on")
                .method(RequestMethod.GET)
                .addGenerics()
                .removeBodyFromGenerics()
                .addOutgoingArgument(
                    "computers",
                    "List of computers as an array containing objects with properties: id, name, address, type, upgrades, location, running, owner"
                )
                .build()
        };
    }
}
