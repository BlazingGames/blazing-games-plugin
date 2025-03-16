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
package de.blazemcworld.blazinggames.computing.wss.client;

import java.util.UUID;

import com.google.gson.JsonObject;

import dev.ivycollective.ivyhttp.wss.ClientboundPacket;

public class UserListUpdatePacket extends ClientboundPacket {
    public final UUID uuid;
    public final boolean isJoin;
    public UserListUpdatePacket(UUID uuid, boolean isJoin) {
        super("usrupd");
        this.uuid = uuid;
        this.isJoin = isJoin;
    }

    @Override
    public JsonObject serialize() {
        JsonObject out = new JsonObject();
        out.addProperty("actioner", uuid.toString());
        out.addProperty("type", isJoin);
        return out;
    }
}
