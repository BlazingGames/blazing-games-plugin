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
package de.blazemcworld.blazinggames.teleportanchor;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.items.CustomItems;
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LodestoneInteractionEventListener implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getItem();
            if (CustomItems.TELEPORT_ANCHOR.matchItem(item)) {
                event.setCancelled(true);
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.LODESTONE) {
                    Location signLocation = new Location(event.getPlayer().getWorld(), event.getClickedBlock().getLocation().getX(), event.getClickedBlock().getLocation().getY(), event.getClickedBlock().getLocation().getZ());
                    List<Component> signLines = new ArrayList<>();
                    signLines.add(Component.text("Lodestone name").color(NamedTextColor.AQUA));
                    signLines.add(Component.text("vvvvvvvvvvvvvvv"));
                    signLines.add(Component.text(""));
                    signLines.add(Component.text("^^^^^^^^^^^^^^^"));
                    sendSignPacket(event.getPlayer(), signLocation, signLines);
                }
                else {
                    event.getPlayer().openInventory(new TeleportAnchorInterface(BlazingGames.get(), event.getPlayer()).getInventory());
                }
            }
        }
    }

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

                ClientboundBlockUpdatePacket sent3 = new ClientboundBlockUpdatePacket(blockPos, Blocks.LODESTONE.defaultBlockState());
                ((CraftPlayer) player).getHandle().connection.send(sent3);

                LodestoneStorage.saveLodestoneToPlayer(player.getUniqueId(), location, packet.getLines()[2]);
                player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
                return true;
            });
        }, 1);
    }
}
