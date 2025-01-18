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
package de.blazemcworld.blazinggames.computing.wss;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.java_websocket.server.WebSocketServer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.computing.ComputerEditor;
import de.blazemcworld.blazinggames.computing.api.BlazingAPI;
import de.blazemcworld.blazinggames.computing.api.LinkedUser;
import de.blazemcworld.blazinggames.computing.api.Permission;
import de.blazemcworld.blazinggames.utils.GZipToolkit;
import de.blazemcworld.blazinggames.utils.GetGson;
import de.blazemcworld.blazinggames.utils.Pair;

public class BlazingWSS extends WebSocketServer {
    public static final int ROOM_CLOSE_OR_LEAVE = 1000;
    public static final int SHUTDOWN = 1001;
    public static final int TOO_LARGE = 1009;
    public static final int INTERNAL_ERROR = 1011;
    public static final int BAD_GATEWAY = 1014;
    public static final int UNAUTHORIZED = 3000;
    public static final int FORBIDDEN = 3003;
    public static final int KEEPALIVE_TIMEOUT = 3008;
    
    public static final Permission[] REQUIRED_PERMISSIONS = {
        Permission.READ_COMPUTERS,
        Permission.COMPUTER_CODE_READ,
        Permission.COMPUTER_CODE_MODIFY
    };

    private final BlazingAPI.WebsiteConfig wssConfig;
    public BlazingWSS(BlazingAPI.WebsiteConfig wssConfig) {
        super(new InetSocketAddress(wssConfig.bindPort()));
        this.wssConfig = wssConfig;
        if (wssConfig.https()) this.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(wssConfig.makeSSLContext()));
    }

    public static Pair<String, JsonObject> decompress(byte[] message) {
        try {
            String decompressed = GZipToolkit.decompress(message);
            JsonObject object = GetGson.getAsObject(JsonParser.parseString(decompressed), new IllegalArgumentException("Invalid JSON (not an object)"));
            
            String type = GetGson.getString(object, "type", new IllegalArgumentException("Missing type property"));
            JsonObject payload = GetGson.getObject(object, "payload", new IllegalArgumentException("Missing payload property"));
            return new Pair<>(type, payload);
        } catch (IllegalArgumentException e) {
            BlazingGames.get().debugLog(e);
            return null;
        }
    }

    public static byte[] compress(String type, JsonObject object) {
        JsonObject output = new JsonObject();
        output.addProperty("type", type);
        output.add("payload", output);
        return GZipToolkit.compress(output.toString());
    }

    public void sendToConn(WebSocket conn, ClientboundPacket packet) {
        sendToConn(conn, compress(packet.typeId, packet.serialize()));
    }

    public void sendToConn(WebSocket conn, byte[] message) {
        ConnectedUser user = conn.getAttachment();
        if (user.disconnectAt > Instant.now().getEpochSecond()) {
            conn.close(UNAUTHORIZED);
        } else {
            conn.send(message);
        }
    }

    public void sendToRoom(String room, ClientboundPacket packet) {
        sendToRoom(room, compress(packet.typeId, packet.serialize()));
    }

    public void sendToRoom(String room, byte[] message) {
        sendToRoomExcept(room, null, message);
    }

    public void sendToRoomExcept(String room, UUID except, ClientboundPacket packet) {
        sendToRoomExcept(room, except, compress(packet.typeId, packet.serialize()));
    }
    
    public void sendToRoomExcept(String room, UUID except, byte[] message) {
        for (WebSocket conn : getConnections()) {
            if (conn.getAttachment() instanceof ConnectedUser connectedUser && connectedUser.room == room && connectedUser.uuid != except) {
                sendToConn(conn, message);
            }
        }
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        // Get IP
        Object ipObj = conn.getAttachment();
        if (ipObj == null || !(ipObj instanceof String ip)) { conn.close(BAD_GATEWAY); return; }

        // Get authorization
        if (!handshake.hasFieldValue("Authorization")) { conn.close(UNAUTHORIZED); return; }
        String authorization = handshake.getFieldValue("Authorization");
        String[] parts = authorization.split(" ");
        if (parts.length != 2 || !("Bearer".equals(parts[0]))) { conn.close(UNAUTHORIZED); return; }
        String bearerToken = parts[1];
        LinkedUser linked = LinkedUser.getLinkedUserFromJWT(bearerToken);
        if (linked == null) { conn.close(UNAUTHORIZED); return; }

        // Get computer id
        if (!handshake.hasFieldValue("Blazing-Computer-Id")) { conn.close(FORBIDDEN); return; }
        String computerId = handshake.getFieldValue("Blazing-Computer-Id");

        // Verify permissions
        if (!ComputerEditor.hasAccessToComputer(linked.uuid(), computerId)) { conn.close(FORBIDDEN); return; }
        for (Permission p : REQUIRED_PERMISSIONS) {
            if (!linked.permissions().contains(p)) { conn.close(FORBIDDEN); return; }
        }

        // Add attachment
        conn.setAttachment(new ConnectedUser(linked.uuid(), linked.username(), linked.level(), linked.expiresAt(), computerId, ip));

        // Send packets
        sendToRoomExcept(computerId, linked.uuid(), new ClientboundPacket.UserListUpdatePacket(linked.uuid(), true));
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        ConnectedUser user = conn.getAttachment();
        sendToRoom(user.room(), new ClientboundPacket.UserListUpdatePacket(user.uuid(), false));
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Pair<String, JsonObject> data = decompress(message.getBytes(StandardCharsets.UTF_8));
        ConnectedUser user = conn.getAttachment();
        String computerId = user.room;
        
        if (user.disconnectAt > Instant.now().getEpochSecond()) {
            conn.close(UNAUTHORIZED);
        } else if (data != null) {
            String type = data.left;
            JsonObject payload = data.right;
            ServerboundPacket packet;
            try {
                packet = ServerboundPacket.Type.valueOf(type.toUpperCase()).factory.apply(payload);
            } catch (IllegalArgumentException e) {
                BlazingGames.get().debugLog(e);
                return;
            }
            packet.process(this, conn, user, computerId);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        BlazingGames.get().debugLog(ex);
        if (ex instanceof RuntimeException && conn != null && conn.isOpen()) {
            conn.close(INTERNAL_ERROR);
        }
    }

    @Override
    public void onStart() {
        BlazingGames.get().log("Websocket server started on port " + getPort());
    }

    @Override
    public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket conn, Draft draft, ClientHandshake request) throws InvalidDataException {
        ServerHandshakeBuilder builder = super.onWebsocketHandshakeReceivedAsServer(conn, draft, request);
        builder.put("Access-Control-Allow-Origin", "*");
        builder.put("Access-Control-Allow-Headers", "Authorization, Blazing-Computer-Id");

        String realIp = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        String ip;
        if (wssConfig.proxyEnabled()) {
            if (!wssConfig.isAllowed(realIp)) {
                BlazingGames.get().debugLog("IP not allowed: " + realIp);
                throw new InvalidDataException(BAD_GATEWAY);
            }

            ip = request.getFieldValue(wssConfig.proxyIpAddressHeader());
            if (ip == null) {
                BlazingGames.get().debugLog("Proxy header not found: " + wssConfig.proxyIpAddressHeader());
                throw new InvalidDataException(BAD_GATEWAY);
            }
        } else {
            ip = realIp;
        }

        conn.setAttachment(ip);
        return builder;
    }

    public static record ConnectedUser(UUID uuid, String username, int level, long disconnectAt, String room, String ipAddr) {}
}
