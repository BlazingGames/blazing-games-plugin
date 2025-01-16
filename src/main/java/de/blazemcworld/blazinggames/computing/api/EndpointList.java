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

import de.blazemcworld.blazinggames.computing.api.impl.RootEndpoint;
import de.blazemcworld.blazinggames.computing.api.impl.auth.*;
import de.blazemcworld.blazinggames.computing.api.impl.computers.*;

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

    COMPUTER_LIST("Computers", new ComputersListEndpoint()),
    COMPUTER_CODE_READ("Computers", new ViewCodeEndpoint()),
    COMPUTER_RENAME("Computers", new RenameEndpoint()),
    ;

    public final String category;
    public final Endpoint endpoint;

    private EndpointList(String category, Endpoint endpoint) {
        this.endpoint = endpoint;
        this.category = category;
    }
}
