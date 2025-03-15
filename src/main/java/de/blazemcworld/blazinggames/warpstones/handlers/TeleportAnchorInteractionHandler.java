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
package de.blazemcworld.blazinggames.warpstones.handlers;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.items.CustomItems;
import de.blazemcworld.blazinggames.warpstones.PacketHandler;
import de.blazemcworld.blazinggames.warpstones.TeleportAnchorInterface;
import de.blazemcworld.blazinggames.warpstones.WarpstoneStorage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class TeleportAnchorInteractionHandler extends BlazingEventHandler<PlayerInteractEvent> {
    @Override
    public boolean fitCriteria(PlayerInteractEvent event, boolean cancelled) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (CustomItems.TELEPORT_ANCHOR.matchItem(event.getItem())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void execute(PlayerInteractEvent event) {
        event.setCancelled(true);
        if (
            event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null &&
            WarpstoneStorage.isWarpstone(event.getClickedBlock().getLocation())
        ) {
            if (!WarpstoneStorage.permissionCheck(event.getClickedBlock().getLocation(), event.getPlayer())) {
                event.getPlayer().sendActionBar(Component.text("This warpstone is locked by its owner!", NamedTextColor.RED));
                return;
            }

            if (WarpstoneStorage.getSavedWarpstones(event.getPlayer()).size() >= 45) {
                event.getPlayer().sendActionBar(Component.text("Warpstone limit reached!", NamedTextColor.RED));
                return;
            }

            String defaultName = WarpstoneStorage.getDetails(event.getClickedBlock().getLocation()).defaultName;
            Location signLocation = new Location(event.getPlayer().getWorld(), event.getClickedBlock().getLocation().getX(), event.getClickedBlock().getLocation().getY(), event.getClickedBlock().getLocation().getZ());
            List<Component> signLines = new ArrayList<>();
            signLines.add(Component.text("Warpstone name"));
            signLines.add(Component.text("vvvvvvvvvvvvvvv"));
            signLines.add(Component.text(defaultName));
            signLines.add(Component.text("^^^^^^^^^^^^^^^"));
            sendSignPacket(event.getPlayer(), signLocation, signLines);
        } else {
            event.getPlayer().openInventory(new TeleportAnchorInterface(BlazingGames.get(), event.getPlayer()).getInventory());
        }
    }

    @SuppressWarnings("deprecation")
    public void sendSignPacket(Player player, Location location, List<Component> lines) {
        Bukkit.getScheduler().runTaskLater(BlazingGames.get(), () -> {
            PacketHandler.addPacketInjector(player);

            BlockState blockState = Material.OAK_SIGN.createBlockData().createBlockState();
            Sign sign = (Sign) blockState;
            for (int i = 0; i < 4; i++) sign.getSide(Side.FRONT).line(i, lines.get(i));
            player.sendBlockChange(location, sign.getBlockData());
            player.sendSignChange(location, lines);

            ClientboundOpenSignEditorPacket openSignEditorPacket = new ClientboundOpenSignEditorPacket(
                    new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ()), true
            );

            ((CraftPlayer) player).getHandle().connection.send(openSignEditorPacket);

            PacketHandler.PACKET_HANDLERS.put(player.getUniqueId(), packetO -> {
                if (!(packetO instanceof ServerboundSignUpdatePacket packet)) return false;

                BlockPos blockPos = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());

                ClientboundBlockUpdatePacket sent3 = new ClientboundBlockUpdatePacket(blockPos, Blocks.BARRIER.defaultBlockState());
                ((CraftPlayer) player).getHandle().connection.send(sent3);

                WarpstoneStorage.saveWarpstone(player, location, packet.getLines()[2]);
                player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
                return true;
            });
        }, 1);
    }
}
