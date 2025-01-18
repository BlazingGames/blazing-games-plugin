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

import com.google.gson.JsonObject;

import de.blazemcworld.blazinggames.computing.ComputerEditor;
import de.blazemcworld.blazinggames.computing.api.APIDocs;
import de.blazemcworld.blazinggames.computing.api.EarlyResponse;
import de.blazemcworld.blazinggames.computing.api.Endpoint;
import de.blazemcworld.blazinggames.computing.api.EndpointResponse;
import de.blazemcworld.blazinggames.computing.api.LinkedUser;
import de.blazemcworld.blazinggames.computing.api.Permission;
import de.blazemcworld.blazinggames.computing.api.RequestContext;
import de.blazemcworld.blazinggames.computing.api.RequestMethod;
import de.blazemcworld.blazinggames.testing.CoveredByTests;
import de.blazemcworld.blazinggames.testing.tests.RenameEndpointTest;

@CoveredByTests(RenameEndpointTest.class)
public class RenameEndpoint implements Endpoint {
    @Override
    public String path() {
        return "/computers/rename";
    }

    @Override
    public EndpointResponse PATCH(RequestContext context) throws EarlyResponse {
        var body = context.useBodyWrapper();
        LinkedUser user = context.requireAuthentication();
        context.requirePermission(Permission.READ_COMPUTERS);
        context.requirePermission(Permission.WRITE_COMPUTERS);

        String id = context.requireClean("id", body.getString("id"));
        String name = context.requireClean("name", body.getString("name"));

        if (!ComputerEditor.hasAccessToComputer(user.uuid(), id)) {
            return EndpointResponse.of403();
        }

        ComputerEditor.rename(id, name);
        return EndpointResponse.of200(new JsonObject()).build();
    }

    @Override
    public APIDocs[] docs() {
        return new APIDocs[]{
            APIDocs.builder()
                .title("Rename computer")
                .description("Set a computer's name.")
                .method(RequestMethod.POST)
                .addGenerics()
                .removeBodyFromGenerics()
                .addPermission(Permission.READ_COMPUTERS)
                .addPermission(Permission.WRITE_COMPUTERS)
                .addIncomingArgument("id", "The ID of the computer")
                .addIncomingArgument("name", "The name of the computer")
                .build()
        };
    }
}