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

package de.blazemcworld.blazinggames.events.handlers.slabs;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.items.CustomItem;
import de.blazemcworld.blazinggames.items.CustomSlabs;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Objects;

public class CustomSlabPlaceBlockHandler extends BlazingEventHandler<PlayerInteractEvent> {
    @Override
    public boolean fitCriteria(PlayerInteractEvent event, boolean cancelled) {
        Player player = event.getPlayer();
        EquipmentSlot hand = event.getHand();
        Block block = event.getClickedBlock();
        BlockFace face = event.getBlockFace();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block != null && hand != null) {
            if (player.getTargetEntity(5) != null) return false;
            Vector v = face.getDirection();
            Location nextBlock = block.getLocation().add(v).toCenterLocation();
            Collection<Shulker> shulkers = nextBlock.getNearbyEntitiesByType(Shulker.class, 0.5);
            return !shulkers.isEmpty() && shulkers.size() < 8;
        }
        return false;
    }

    @Override
    public void execute(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        EquipmentSlot hand = event.getHand();
        Block block = event.getClickedBlock();
        BlockFace face = event.getBlockFace();

        Vector v = face.getDirection();
        Location nextBlock = block.getLocation().add(v).toCenterLocation();
        Collection<Shulker> shulkers = nextBlock.getNearbyEntitiesByType(Shulker.class, 0.5);
        Shulker shulker = shulkers.iterator().next();
        double y = shulker.getY();
        if (CustomSlabPlaceHandler.isTop(y)) nextBlock.add(0, -0.5, 0);
        PersistentDataContainer container = shulker.getPersistentDataContainer();

        ItemStack item = event.getPlayer().getInventory().getItem(hand);
        boolean isSlab = CustomItem.getCustomItem(item) != null && Objects.requireNonNull(item.getPersistentDataContainer().get(BlazingGames.get().key("custom_item"), PersistentDataType.STRING)).contains("slab");
        if (isSlab && container.has(BlazingGames.get().key("slab")) && container.has(BlazingGames.get().key("slab_type"))) {
            CustomSlabs.CustomSlab slab = (CustomSlabs.CustomSlab) CustomItem.getCustomItem(item);
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                event.getPlayer().getInventory().getItem(hand).setAmount(event.getPlayer().getInventory().getItem(hand).getAmount() - 1);
            CustomSlabPlaceHandler.placeCustomSlab(slab, nextBlock, player.getEyeLocation(), face);
        }
    }
}
