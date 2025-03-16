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

import java.util.List;

import de.blazemcworld.blazinggames.RequiredFeature;
import de.blazemcworld.blazinggames.computing.api.impl.PackZipEndpoint;
import de.blazemcworld.blazinggames.computing.api.impl.RootEndpoint;
import de.blazemcworld.blazinggames.computing.api.impl.auth.*;
import de.blazemcworld.blazinggames.computing.api.impl.computers.*;
import dev.ivycollective.ivyhttp.http.Endpoint;

public enum EndpointList {
    ROOT(null, new RootEndpoint()),
    AUTH_PREPARE("Authenthication", new AuthPrepareEndpoint()),
    AUTH_LINK("Authenthication", new AuthLinkEndpoint()),
    AUTH_TEST("Authenthication", new AuthTestEndpoint()),
    AUTH_REDEEM("Authenthication", new AuthRedeemEndpoint()),
    AUTH_CALLBACK(null, new AuthCallbackEndpoint()),
    AUTH_CONSENT(null, new AuthConsentEndpoint()),
    AUTH_ERROR(null, new AuthErrorEndpoint()),
    AUTH_UNLINK(null, new AuthUnlinkEndpoint()),
    AUTH_UNLINK_CONFIRM(null, new AuthUnlinkConfirmEndpoint()),

    COMPUTER_LIST("Computers", new ComputersListEndpoint(), List.of(RequiredFeature.COMPUTERS), Permission.READ_COMPUTERS),
    COMPUTER_CODE_READ("Computers", new ViewCodeEndpoint(), List.of(RequiredFeature.COMPUTERS), Permission.COMPUTER_CODE_READ),
    COMPUTER_RENAME("Computers", new RenameEndpoint(), List.of(RequiredFeature.COMPUTERS), Permission.READ_COMPUTERS, Permission.WRITE_COMPUTERS),

    PACK_ZIP(null, new PackZipEndpoint(), List.of(RequiredFeature.RESOURCE_PACK)),
    ;

    public final String category;
    public final Endpoint endpoint;
    public final List<Permission> permissions;
    public final List<RequiredFeature> requiredFeatures;

    private EndpointList(String category, Endpoint endpoint, Permission... permissions) {
        this.endpoint = endpoint;
        this.category = category;
        this.permissions = List.of(permissions);
        this.requiredFeatures = List.of();
    }

    private EndpointList(String category, Endpoint endpoint, List<RequiredFeature> requiredFeatures, Permission... permissions) {
        this.endpoint = endpoint;
        this.category = category;
        this.permissions = List.of(permissions);
        this.requiredFeatures = List.copyOf(requiredFeatures);
    }
}
