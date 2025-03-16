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

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import com.google.gson.JsonObject;

import de.blazemcworld.blazinggames.computing.ComputerEditor;
import de.blazemcworld.blazinggames.computing.api.LinkedUser;
import de.blazemcworld.blazinggames.computing.api.Permission;
import de.blazemcworld.blazinggames.computing.wss.client.UserListUpdatePacket;
import dev.ivycollective.ivyhttp.wss.ClientboundPacket;
import dev.ivycollective.ivyhttp.wss.MessagePayload;
import dev.ivycollective.ivyhttp.wss.ServerboundPacket;
import dev.ivycollective.ivyhttp.wss.SocketRejection;
import dev.ivycollective.ivyhttp.wss.SocketServer;
import dev.ivycollective.ivyhttp.wss.WebSocketEventHandler;

public class BlazingWSS implements WebSocketEventHandler {
    public void sendToConn(WebSocket conn, ClientboundPacket packet) {
        sendToConn(conn, new MessagePayload(packet.typeId, packet.serialize()).compress());
    }

    public void sendToConn(WebSocket conn, byte[] message) {
        ConnectedUser user = ConnectedUser.decode(conn.getAttachment());
        if (user.disconnectAt() > Instant.now().getEpochSecond()) {
            conn.close(SocketServer.UNAUTHORIZED);
        } else {
            conn.send(message);
        }
    }

    public void sendToRoom(SocketServer server, String room, ClientboundPacket packet) {
        sendToRoom(server, room, new MessagePayload(packet.typeId, packet.serialize()).compress());
    }

    public void sendToRoom(SocketServer server, String room, byte[] message) {
        sendToRoomExcept(server, room, null, message);
    }

    public void sendToRoomExcept(SocketServer server, String room, UUID except, ClientboundPacket packet) {
        sendToRoomExcept(server, room, except, new MessagePayload(packet.typeId, packet.serialize()).compress());
    }
    
    public void sendToRoomExcept(SocketServer server, String room, UUID except, byte[] message) {
        for (WebSocket conn : server.getConnections()) {
            ConnectedUser connectedUser = ConnectedUser.decode(conn.getAttachment());
            if (room.equals(connectedUser.room()) && !connectedUser.uuid().equals(except)) {
                sendToConn(conn, message);
            }
        }
    }

    public static final Permission[] REQUIRED_PERMISSIONS = {
        Permission.READ_COMPUTERS,
        Permission.COMPUTER_CODE_READ,
        Permission.COMPUTER_CODE_MODIFY
    };

    @Override
    public Map<String, ServerboundPacket> getPacketHandlers() {
        return Map.of(); // TBD
    }

    private final Pattern urlPattern = Pattern.compile("^\\/computer\\/([A-Z0-9]{26})\\/?$");
    @Override
    public JsonObject identify(String ip, String authorizationHeader, String path) throws SocketRejection, IOException {
        // Get computer id
        Matcher matcher = urlPattern.matcher(path);
        if (!matcher.matches()) throw new SocketRejection("bad path", SocketServer.FORBIDDEN);
        String computerId = matcher.group(1);
        
        // Get authorization
        String[] parts = authorizationHeader.split(" ");
        if (parts.length != 2 || !("Bearer".equals(parts[0]))) throw new SocketRejection("bad auth header", SocketServer.UNAUTHORIZED);
        String bearerToken = parts[1];
        LinkedUser linked = LinkedUser.getLinkedUserFromJWT(bearerToken);
        if (linked == null) throw new SocketRejection("unknown user", SocketServer.UNAUTHORIZED);

        // Verify permissions
        if (!ComputerEditor.hasAccessToComputer(linked.uuid(), computerId)) throw new SocketRejection("missing permissions", SocketServer.UNAUTHORIZED);
        for (Permission p : REQUIRED_PERMISSIONS) {
            if (!linked.permissions().contains(p)) throw new SocketRejection("missing permissions", SocketServer.UNAUTHORIZED);
        }

        // Create attachment
        return new ConnectedUser(linked.uuid(), linked.username(), linked.level(), linked.expiresAt(), computerId, ip).encode();
    }
    @Override
    public void connectHook(SocketServer server, WebSocket conn, ClientHandshake handshake) {
        ConnectedUser user = ConnectedUser.decode(conn.getAttachment());
        sendToRoomExcept(server, user.room(), user.uuid(), new UserListUpdatePacket(user.uuid(), true));
    }

    @Override
    public void disconnectHook(SocketServer server, WebSocket conn, int code, String reason, boolean remote) {
        ConnectedUser user = ConnectedUser.decode(conn.getAttachment());
        sendToRoomExcept(server, user.room(), user.uuid(), new UserListUpdatePacket(user.uuid(), false));
    }
    
    @Override
    public boolean messageHook(SocketServer server, WebSocket conn, String message) {
        ConnectedUser user = ConnectedUser.decode(conn.getAttachment());
        if (user.disconnectAt() > Instant.now().getEpochSecond()) {
            conn.close(SocketServer.UNAUTHORIZED);
            return false;
        }

        return true;
    }
}