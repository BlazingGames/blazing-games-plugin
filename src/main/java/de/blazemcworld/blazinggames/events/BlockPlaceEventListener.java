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
package de.blazemcworld.blazinggames.events;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.computing.BootedComputer;
import de.blazemcworld.blazinggames.computing.ComputerRegistry;
import de.blazemcworld.blazinggames.computing.types.ComputerTypes;
import de.blazemcworld.blazinggames.items.CustomItem;
import de.blazemcworld.blazinggames.items.CustomItems;
import de.blazemcworld.blazinggames.items.CustomSlabs;
import de.blazemcworld.blazinggames.utils.TextLocation;
import de.blazemcworld.blazinggames.utils.TomeAltarStorage;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Orientable;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Objects;
import java.util.UUID;

public class BlockPlaceEventListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(CustomItem.isCustomItem(event.getItemInHand())) {
            event.setCancelled(true);
        }

        if (event.getItemInHand().getType() == Material.SPAWNER) {
            CreatureSpawner spawner = (CreatureSpawner) event.getBlock().getState();
            CreatureSpawner item = (CreatureSpawner) ((BlockStateMeta) event.getItemInHand().getItemMeta()).getBlockState();

            spawner.setSpawnedType(item.getSpawnedType());
            spawner.setSpawnCount(item.getSpawnCount());
            spawner.setDelay(item.getDelay());
            spawner.setMaxNearbyEntities(item.getMaxNearbyEntities());
            spawner.setMinSpawnDelay(item.getMinSpawnDelay());
            spawner.setMaxSpawnDelay(item.getMaxSpawnDelay());
            spawner.setRequiredPlayerRange(item.getRequiredPlayerRange());
            spawner.setSpawnRange(item.getSpawnRange());
            if (item.getPersistentDataContainer().getOrDefault(BlazingGames.get().key("redstone_control"), PersistentDataType.BOOLEAN, false))
                spawner.getPersistentDataContainer().set(BlazingGames.get().key("redstone_control"), PersistentDataType.BOOLEAN, true);

            spawner.update();
        } else if (CustomItems.TOME_ALTAR.matchItem(event.getItemInHand())) {
            event.setCancelled(true);
            event.getItemInHand().setAmount(event.getItemInHand().getAmount() - 1);
            TomeAltarStorage.addTomeAltar(event.getBlock().getLocation());

            Bukkit.getScheduler().runTask(BlazingGames.get(), () -> {
                event.getBlock().getLocation().getBlock().setType(Material.BARRIER);

                Location loc = event.getBlock().getLocation().toCenterLocation();

                ItemDisplay display = (ItemDisplay) event.getBlock().getWorld().spawnEntity(loc, EntityType.ITEM_DISPLAY);
                ItemStack altarItem = CustomItems.TOME_ALTAR.create();
                display.setItemStack(altarItem);
            });
        } else {
            boolean isSlab = CustomItem.getCustomItem(event.getItemInHand()) instanceof CustomSlabs.CustomSlab;

            if (isSlab) {
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
        }

        ItemStack handItem = event.getItemInHand();
        if (handItem.hasItemMeta()) {
            PersistentDataContainer container = handItem.getItemMeta().getPersistentDataContainer();
            String computerTypeString = container.getOrDefault(ComputerRegistry.NAMESPACEDKEY_COMPUTER_TYPE, PersistentDataType.STRING, "");
            if (!computerTypeString.isEmpty()) {
                event.setCancelled(true);

                ComputerTypes computerTypes;
                try {
                    computerTypes = ComputerTypes.valueOf(computerTypeString);
                } catch (IllegalArgumentException ignored) {
                    event.getPlayer().sendMessage("This computer type doesn't exist?");
                    return;
                }

                String computerId = container.getOrDefault(ComputerRegistry.NAMESPACEDKEY_COMPUTER_ID, PersistentDataType.STRING, "");
                Location placeLocation = event.getBlockPlaced().getLocation();
                UUID uuid = event.getPlayer().getUniqueId();
                if (event.getItemInHand().getAmount() > 1) {
                    ItemStack newStack = event.getItemInHand();
                    newStack.setAmount(event.getItemInHand().getAmount() - 1);
                    event.getPlayer().getInventory().setItem(event.getHand(), newStack);
                } else {
                    event.getPlayer().getInventory().setItem(event.getHand(), new ItemStack(Material.AIR));
                }
                if (computerId.isEmpty()) {
                    ComputerRegistry.placeNewComputer(
                        placeLocation,
                        computerTypes,
                        uuid,
                        computer -> {
                            Player player = Bukkit.getPlayer(uuid);
                            if (player != null) {
                                if (computer == null) {
                                    player.sendActionBar(Component.text("Failed to create a computer, please tell a developer to check the console."));
                                } else {
                                    player.sendActionBar(
                                        Component.text("Your new computer (%s) has been created with id %s!".formatted(computer.getMetadata().name, computer.getId()))
                                    );
                                }
                            }
                        }
                    );
                } else {
                    ComputerRegistry.placeComputer(
                        computerId,
                        placeLocation,
                        (result, computer) -> {
                            Player player = Bukkit.getPlayer(uuid);
                            if (player != null) {
                                if (result && computer != null) {
                                    player.sendActionBar(
                                        Component.text("The computer (%s) has been placed (id: %s)!".formatted(computer.getMetadata().name, computer.getId()))
                                    );
                                }

                                if (result && computer == null) {
                                    player.sendActionBar(Component.text("fear me."));
                                }

                                if (!result && computer != null) {
                                    if (ComputerRegistry.getComputerById(computerId) != null) {
                                        BootedComputer dupe = ComputerRegistry.getComputerById(computerId);
                                        player.sendMessage(
                                            "Duplicate computer has an ID of %s and is at [ %s ]."
                                                .formatted(dupe.getId(), TextLocation.serialize(dupe.getMetadata().location))
                                        );
                                        player.sendActionBar(Component.text("A computer with this ID is already present in the world. See chat for details."));
                                    } else {
                                        player.sendActionBar(Component.text("A computer with this location is already present here."));
                                    }
                                }

                                if (!result && computer == null) {
                                    player.sendActionBar(Component.text("Failed to place that computer, please tell a developer to check the console."));
                                }
                            }
                        }
                    );
                }
            }
        }
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
            loc.setX(loc.getX() - 0.5);
            loc.setZ(loc.getZ() - 0.5);
            loc.setY(loc.getY() - (isTop ? 0 : 0.5));

            BlockData blockData = item.material.createBlockData();
            BlockDisplay blockDisplay = (BlockDisplay) loc.getBlock().getWorld().spawnEntity(loc, EntityType.BLOCK_DISPLAY);
            if (blockData instanceof Directional || blockData instanceof Orientable) {
                double yaw = direction.getYaw();
                BlockFace face = BlockFace.SOUTH;
                StructureRotation rotation = StructureRotation.CLOCKWISE_90;
                if (yaw >= -135 && yaw < -45) {
                    face = BlockFace.WEST;
                    rotation = StructureRotation.NONE;
                }
                if (yaw >= -45 && yaw < 45) {
                    face = BlockFace.NORTH;
                    rotation = StructureRotation.COUNTERCLOCKWISE_90;
                }
                if (yaw >= 45 && yaw < 135) {
                    face = BlockFace.EAST;
                    rotation = StructureRotation.CLOCKWISE_180;
                }
                if (blockData instanceof Directional directional) directional.setFacing(face);
                if (blockData instanceof Orientable orientable) {
                    orientable.setAxis((blockFace == BlockFace.UP || blockFace == BlockFace.DOWN) ? Axis.Y : Axis.X);
                    orientable.rotate(rotation);
                }
            }
            blockDisplay.setBlock(blockData);
            Transformation transformation = new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1f, 0.5f, 1f), new Quaternionf());
            blockDisplay.setTransformation(transformation);

            loc = loc.add(0.25, 0, 0.25);
            summonArmorstand(loc, item.name, blockDisplay.getUniqueId());

            loc = loc.add(0.5, 0, 0);
            summonArmorstand(loc, item.name, blockDisplay.getUniqueId());

            loc = loc.add(0, 0, 0.5);
            summonArmorstand(loc, item.name, blockDisplay.getUniqueId());

            loc = loc.add(-0.5, 0, 0);
            summonArmorstand(loc, item.name, blockDisplay.getUniqueId());
        });
    }

    public static boolean isTop(double number) {
        double decimalPart = number - Math.floor(number);
        return decimalPart >= 0.5 && decimalPart <= 0.9;
    }
}
