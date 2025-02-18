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
import de.blazemcworld.blazinggames.utils.Box;
import de.blazemcworld.blazinggames.utils.Face;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Shulker;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Objects;

public class CustomSlabInteractHandler extends BlazingEventHandler<PlayerInteractEntityEvent> {

    @Override
    public boolean fitCriteria(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Shulker shulker) {
            PersistentDataContainer container = shulker.getPersistentDataContainer();

            ItemStack item = event.getPlayer().getInventory().getItem(event.getHand());
            boolean isSlab = CustomItem.getCustomItem(item) != null && Objects.requireNonNull(item.getPersistentDataContainer().get(BlazingGames.get().key("custom_item"), PersistentDataType.STRING)).contains("slab");
            return isSlab && container.has(BlazingGames.get().key("slab")) && container.has(BlazingGames.get().key("slab_type"));
        }
        return false;
    }

    @Override
    public void execute(PlayerInteractEntityEvent event) {
        Shulker shulker = (Shulker) event.getRightClicked();
        ItemStack item = event.getPlayer().getInventory().getItem(event.getHand());
        Location loc = shulker.getLocation();
        Vector vec = new Vector(loc.getX(), loc.getY(), loc.getZ());
        Box box = new Box(
                new Face( // up
                        vec.clone().add(new Vector(-0.25, 0.5, 0.25)),
                        vec.clone().add(new Vector(0.25, 0.5, -0.25))
                ),
                new Face( // down
                        vec.clone().add(new Vector(-0.25, 0, 0.25)),
                        vec.clone().add(new Vector(0.25, 0, -0.25))
                ),
                new Face( // west
                        vec.clone().add(new Vector(-0.25, 0, 0.25)),
                        vec.clone().add(new Vector(-0.25, 0.5, -0.25))
                ),
                new Face( // east
                        vec.clone().add(new Vector(0.25, 0, 0.25)),
                        vec.clone().add(new Vector(0.25, 0.5, -0.25))
                ),
                new Face( // south
                        vec.clone().add(new Vector(-0.25, 0, 0.25)),
                        vec.clone().add(new Vector(0.25, 0.5, 0.25))
                ),
                new Face( // north
                        vec.clone().add(new Vector(-0.25, 0, -0.25)),
                        vec.clone().add(new Vector(0.25, 0.5, -0.25))
                )
        );
        RayTraceResult res = event.getPlayer().rayTraceEntities(5);
        if (res == null) return;
        Vector dir = box.getDirection(res.getHitPosition());
        BlockFace face = box.getFace(res.getHitPosition());
        if (dir == null || face == null) return;
        dir = dir.multiply(0.5);
        CustomSlabs.CustomSlab slab = (CustomSlabs.CustomSlab) CustomItem.getCustomItem(item);
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
            event.getPlayer().getInventory().getItem(event.getHand()).setAmount(event.getPlayer().getInventory().getItem(event.getHand()).getAmount() - 1);
        CustomSlabPlaceHandler.placeCustomSlab(slab, loc.add(dir), event.getPlayer().getEyeLocation(), face);
    }
}
