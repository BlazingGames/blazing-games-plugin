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
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Shulker;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.UUID;

public class CustomSlabPlaceHandler extends BlazingEventHandler<BlockPlaceEvent> {
    @Override
    public boolean fitCriteria(BlockPlaceEvent event) {
        return CustomItem.getCustomItem(event.getItemInHand()) instanceof CustomSlabs.CustomSlab;
    }

    @Override
    public void execute(BlockPlaceEvent event) {
        event.setCancelled(true);
        Bukkit.getScheduler().runTaskLater(BlazingGames.get(), () -> {
            CustomSlabs.CustomSlab item = (CustomSlabs.CustomSlab) CustomItem.getCustomItem(event.getItemInHand());
            if (item == null) return;
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                event.getItemInHand().setAmount(event.getItemInHand().getAmount() - 1);
            RayTraceResult res = event.getBlock().getWorld().rayTraceBlocks(event.getPlayer().getEyeLocation(), event.getPlayer().getEyeLocation().getDirection(), 5);
            if (res == null) return;
            Vector vec = res.getHitPosition();
            Location loc = event.getBlock().getLocation().toCenterLocation();
            if (!isTop(vec.getY())) loc.add(0, -0.5, 0);
            placeCustomSlab(item, loc, event.getPlayer().getEyeLocation(), res.getHitBlockFace());
        }, 1);
    }

    private static void summonArmorstand(Location loc, String slab, UUID uuid) {
        Shulker shulker = (Shulker) loc.getWorld().spawnEntity(loc, EntityType.SHULKER);
        shulker.setAI(false);
        shulker.setInvisible(true);
        shulker.setSilent(true);
        shulker.getPersistentDataContainer().set(BlazingGames.get().key("slab_type"), PersistentDataType.STRING, slab);
        shulker.getPersistentDataContainer().set(BlazingGames.get().key("slab"), PersistentDataType.STRING, uuid.toString());
        Objects.requireNonNull(shulker.getAttribute(Attribute.SCALE)).setBaseValue(0.5);

        ArmorStand armorStand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        armorStand.setInvisible(true);
        armorStand.setInvulnerable(true);
        armorStand.setCanMove(false);
        armorStand.setMarker(true);
        armorStand.setGravity(false);
        armorStand.setSmall(true);
        Objects.requireNonNull(armorStand.getAttribute(Attribute.SCALE)).setBaseValue(0.00001);
        armorStand.addPassenger(shulker);
    }

    public static void placeCustomSlab(CustomSlabs.CustomSlab item, Location location, Location direction, BlockFace blockFace) {
        Bukkit.getScheduler().runTask(BlazingGames.get(), () -> {
            Location originalLoc = location.clone();
            boolean isTop = isTop(originalLoc.getY());
            Location loc = originalLoc.clone().toCenterLocation();
            loc.getBlock().setType(Material.MOVING_PISTON);
            loc.setY(loc.getY() + (isTop ? 0.5 : 0));

            Sound breakSound = item.material.createBlockData().getSoundGroup().getBreakSound();
            location.getWorld().playSound(loc, breakSound, 1, 1);

//            BlockData blockData = item.material.createBlockData();
            ItemDisplay itemDisplay = (ItemDisplay) loc.getBlock().getWorld().spawnEntity(loc, EntityType.ITEM_DISPLAY);
            ItemStack itemStack = item.create();
            if (isTop) {
                itemStack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addFlag(true).build());
            }
            itemDisplay.setItemStack(itemStack);
            loc.subtract(0.5, 0.5, 0.5);

//            if (blockData instanceof Directional || blockData instanceof Orientable) {
//                double yaw = direction.getYaw();
//                BlockFace face = BlockFace.SOUTH;
//                StructureRotation rotation = StructureRotation.CLOCKWISE_90;
//                if (yaw >= -135 && yaw < -45) {
//                    face = BlockFace.WEST;
//                    rotation = StructureRotation.NONE;
//                }
//                if (yaw >= -45 && yaw < 45) {
//                    face = BlockFace.NORTH;
//                    rotation = StructureRotation.COUNTERCLOCKWISE_90;
//                }
//                if (yaw >= 45 && yaw < 135) {
//                    face = BlockFace.EAST;
//                    rotation = StructureRotation.CLOCKWISE_180;
//                }
//                if (blockData instanceof Directional directional) directional.setFacing(face);
//                if (blockData instanceof Orientable orientable) {
//                    orientable.setAxis((blockFace == BlockFace.UP || blockFace == BlockFace.DOWN) ? Axis.Y : Axis.X);
//                    orientable.rotate(rotation);
//                }
//            }
//            blockDisplay.setBlock(blockData);

            loc = loc.add(0.25, 0, 0.25);
            summonArmorstand(loc, item.name, itemDisplay.getUniqueId());

            loc = loc.add(0.5, 0, 0);
            summonArmorstand(loc, item.name, itemDisplay.getUniqueId());

            loc = loc.add(0, 0, 0.5);
            summonArmorstand(loc, item.name, itemDisplay.getUniqueId());

            loc = loc.add(-0.5, 0, 0);
            summonArmorstand(loc, item.name, itemDisplay.getUniqueId());
        });
    }

    public static boolean isTop(double number) {
        double decimalPart = number - Math.floor(number);
        return decimalPart >= 0.5 && decimalPart <= 0.9;
    }
}
