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
package de.blazemcworld.blazinggames.warpstones;

import de.blazemcworld.blazinggames.BlazingGames;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

public final class PacketHandler extends ChannelDuplexHandler {


    public static final Map<UUID, Predicate<Packet<?>>> PACKET_HANDLERS = new HashMap<>();

    private final Player p; // Store your target player

    public PacketHandler(Player p) {
        this.p = p;
    }

    private static final String PACKET_INJECTOR_ID = "blazinggames:packet_handler";

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object packetO) throws Exception {
        if (!(packetO instanceof Packet<?> packet)) { // Utilize Java 17 features for pattern matching; Only intercept Packet Data
            super.channelRead(ctx, packetO);
            return;
        }

        Predicate<Packet<?>> handler = PACKET_HANDLERS.get(p.getUniqueId());
        if (handler != null) new BukkitRunnable() {
            public void run() {
                boolean success = handler.test(packet); // Check to make sure that the predicate works
                if (success) PACKET_HANDLERS.remove(p.getUniqueId()); // If successful, remove the packet handler
            }
        }.runTask(BlazingGames.get()); // Execute your Predicate Handler on the Synchronous Thread

        super.channelRead(ctx, packetO); // Perform default actions done by the duplex handler
    }

    public static void addPacketInjector(Player p) {
        ServerPlayer sp = ((CraftPlayer) p).getHandle();

        Channel ch = sp.connection.connection.channel;

        if (ch.pipeline().get(PACKET_INJECTOR_ID) != null) return;
        ch.pipeline().addAfter("decoder", PACKET_INJECTOR_ID, new PacketHandler(p));
    }

}