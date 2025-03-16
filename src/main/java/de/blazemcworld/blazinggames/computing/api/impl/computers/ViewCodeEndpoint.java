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

import java.util.Base64;

import com.google.gson.JsonObject;

import de.blazemcworld.blazinggames.computing.ComputerEditor;
import dev.ivycollective.ivyhttp.http.APIDocs;
import dev.ivycollective.ivyhttp.http.EarlyResponse;
import dev.ivycollective.ivyhttp.http.Endpoint;
import dev.ivycollective.ivyhttp.http.EndpointResponse;
import de.blazemcworld.blazinggames.computing.api.APIUtils;
import de.blazemcworld.blazinggames.computing.api.LinkedUser;
import de.blazemcworld.blazinggames.computing.api.Permission;
import dev.ivycollective.ivyhttp.http.RequestContext;
import dev.ivycollective.ivyhttp.http.RequestMethod;
import de.blazemcworld.blazinggames.utils.GZipToolkit;

public class ViewCodeEndpoint implements Endpoint {
    @Override
    public String path() {
        return "/computers/code";
    }

    @Override
    public EndpointResponse GET(RequestContext context) throws EarlyResponse {
        APIUtils utils = APIUtils.of(context);
        LinkedUser linked = utils.requireAuthentication();
        utils.requirePermission(Permission.COMPUTER_CODE_READ);
        
        var body = context.useBodyWrapper();
        String id = context.requireClean("id", body.getString("id"));

        if (!ComputerEditor.hasAccessToComputer(linked.uuid(), id)) {
            return EndpointResponse.of403();
        }

        JsonObject out = new JsonObject();
        out.addProperty("data", Base64.getEncoder().encodeToString(GZipToolkit.compress(ComputerEditor.getCode(id))));

        return EndpointResponse.of200(out).build();
    }

    @Override
    public APIDocs[] docs() {
        return new APIDocs[]{
            APIDocs.builder()
                .title("Read computer code")
                .description("View the code of a computer. Use the WSS to edit it.")
                .method(RequestMethod.GET)
                .addGenerics()
                .removeBodyFromGenerics()
                .addIncomingArgument(
                    "id",
                    "The ID of the computer"
                )
                .addOutgoingArgument(
                    "data",
                    "Base64-encoded, gzip-compressed computer code"
                )
                .build()
        };
    }
}
