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
import de.blazemcworld.blazinggames.crates.CrateData;
import de.blazemcworld.blazinggames.crates.CrateManager;
import de.blazemcworld.blazinggames.crates.DeathCrateKey;
import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantments;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentHelper;
import de.blazemcworld.blazinggames.enchantments.sys.altar.AltarInterface;
import de.blazemcworld.blazinggames.items.CustomItem;
import de.blazemcworld.blazinggames.items.CustomItems;
import de.blazemcworld.blazinggames.items.CustomSlabs;
import de.blazemcworld.blazinggames.utils.InventoryUtils;
import de.blazemcworld.blazinggames.utils.TomeAltarStorage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultServerData;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.block.CraftVault;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class InteractEventListener implements Listener {
    private final Set<Material> dirt = Set.of(
            Material.DIRT,
            Material.ROOTED_DIRT,
            Material.COARSE_DIRT,
            Material.GRASS_BLOCK,
            Material.DIRT_PATH
    );

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack eventItem = event.getItem();
        EquipmentSlot hand = event.getHand();
        Block block = event.getClickedBlock();
        BlockFace face = event.getBlockFace();
        Location interactionPoint = event.getInteractionPoint();
        Vector clampedInteractionPoint = null;

        if(interactionPoint != null && block != null) {
            clampedInteractionPoint = interactionPoint.clone().subtract(block.getLocation()).toVector();
        }

        if (block != null && block.getType() == Material.VAULT) vaultShit(block);

        if (block != null && block.getType() == Material.END_PORTAL_FRAME && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            String crateId = CrateManager.getKeyULID(block.getLocation());

            boolean allowOpening = false;

            if(crateId != null) {
                if(CustomItems.DEATH_CRATE_KEY.matchItem(eventItem)) {
                    String keyId = DeathCrateKey.getKeyULID(eventItem);

                    if(Objects.equals(crateId, keyId)) {
                        allowOpening = true;
                    }
                }

                if(CustomItems.SKELETON_KEY.matchItem(eventItem) || CustomItems.TO_GO_BOX.matchItem(eventItem)) {
                    assert eventItem != null;

                    if(!player.hasCooldown(eventItem)) {
                        allowOpening = true;
                        player.setCooldown(eventItem, 200);
                    }
                }
            }

            if (allowOpening) {
                CrateData data = CrateManager.readCrate(crateId);
                Location crateLocation = data.location;

                if (CustomItems.TO_GO_BOX.matchItem(eventItem)) {
                    crateLocation.getBlock().breakNaturally();
                    ItemStack filledToGoBox = new ItemStack(Material.BUNDLE);
                    BundleMeta bundleMeta = (BundleMeta) filledToGoBox.getItemMeta();

                    if (data.helmet != null) bundleMeta.addItem(data.helmet);
                    if (data.chestplate != null) bundleMeta.addItem(data.chestplate);
                    if (data.leggings != null) bundleMeta.addItem(data.leggings);
                    if (data.boots != null) bundleMeta.addItem(data.boots);
                    if (data.offhand != null) bundleMeta.addItem(data.offhand);
                    for (ItemStack itemStack : data.hotbarItems) {
                        if (itemStack == null) continue; bundleMeta.addItem(itemStack);
                    }
                    for (ItemStack itemStack : data.inventoryItems) {
                        if (itemStack == null) continue; bundleMeta.addItem(itemStack);
                    }

                    filledToGoBox.setItemMeta(bundleMeta);
                    player.getInventory().getItem(hand).subtract(1);
                    if (player.getInventory().firstEmpty() != -1) {
                        player.getInventory().addItem(filledToGoBox);
                    } else {
                        player.getWorld().dropItemNaturally(player.getLocation(), filledToGoBox);
                    }
                    player.giveExp(data.exp);
                    CrateManager.deleteCrate(crateId);
                    return;
                }
                else {
                    PlayerInventory inventory = player.getInventory();

                    if (
                            (data.offhand != null && inventory.getItemInOffHand() != null && !inventory.getItemInOffHand().isEmpty()) ||
                                    (data.helmet != null && inventory.getHelmet() != null && !inventory.getHelmet().isEmpty()) ||
                                    (data.chestplate != null && inventory.getChestplate() != null && !inventory.getChestplate().isEmpty()) ||
                                    (data.leggings != null && inventory.getLeggings() != null && !inventory.getLeggings().isEmpty()) ||
                                    (data.boots != null && inventory.getBoots() != null && !inventory.getBoots().isEmpty())
                    ) {
                        player.sendActionBar(Component.text("Move your offhand/armor into your inventory to open").color(NamedTextColor.RED));
                        return;
                    }

                    player.getInventory().getItem(hand).subtract(1);
                    crateLocation.getBlock().breakNaturally(true);

                    if (data.offhand != null) inventory.setItemInOffHand(data.offhand);
                    if (data.helmet != null) inventory.setHelmet(data.helmet);
                    if (data.chestplate != null) inventory.setChestplate(data.chestplate);
                    if (data.leggings != null) inventory.setLeggings(data.leggings);
                    if (data.boots != null) inventory.setBoots(data.boots);

                    int hotbarIndex = -1;
                    for (ItemStack hotbarItem : data.hotbarItems) {
                        hotbarIndex++;
                        if (hotbarItem == null) continue;
                        if (inventory.getItem(hotbarIndex) == null) {
                            inventory.setItem(hotbarIndex, hotbarItem);
                        } else {
                            if (inventory.firstEmpty() == -1) {
                                crateLocation.getWorld().dropItemNaturally(crateLocation, hotbarItem);
                            }
                            inventory.addItem(hotbarItem);
                        }
                    }

                    int inventoryIndex = 8;
                    for (ItemStack inventoryItem : data.inventoryItems) {
                        inventoryIndex++;
                        if (inventoryItem == null) continue;
                        if (inventory.getItem(inventoryIndex) == null) {
                            inventory.setItem(inventoryIndex, inventoryItem);
                        } else {
                            if (inventory.firstEmpty() == -1) {
                                crateLocation.getWorld().dropItemNaturally(crateLocation, inventoryItem);
                            }
                            else {
                                inventory.addItem(inventoryItem);
                            }
                        }
                    }

                    player.giveExp(data.exp);

                    CrateManager.deleteCrate(crateId);
                }
            }
            return;
        }

        if(block != null && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.useInteractedBlock() != Event.Result.DENY) {
            if (block.getType() == Material.ENCHANTING_TABLE) {
                player.openInventory(new AltarInterface(BlazingGames.get(), block.getState()).getInventory());
                event.setCancelled(true);
            }
        }

        if (CustomItems.PORTABLE_CRAFTING_TABLE.matchItem(eventItem)) {
            // TODO: use a non-deprecated method
            player.openWorkbench(null, true);
            return;
        }

        if (player.isSneaking() && eventItem != null && hand != null && eventItem.getType() == Material.GLASS_BOTTLE) {
            Block target = event.getPlayer().getTargetBlockExact(5, FluidCollisionMode.ALWAYS);
            if (target != null && target.getType() == Material.WATER) return;
            if (player.getLevel() > 0) {
                event.setCancelled(true);
                player.setLevel(player.getLevel() - 1);
                if (player.getInventory().getItem(hand).getAmount() == 1) {
                    player.getInventory().setItem(hand, new ItemStack(Material.EXPERIENCE_BOTTLE, 1));
                } else {
                    player.getInventory().getItem(hand).subtract(1);
                    HashMap<Integer, ItemStack> remaining = event.getPlayer().getInventory().addItem(new ItemStack(Material.EXPERIENCE_BOTTLE));
                    for (ItemStack item : remaining.values()) {
                        event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), item);
                    }
                }
                event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1);
            }
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block != null && hand != null && eventItem != null) {
            if(EnchantmentHelper.hasActiveCustomEnchantment(eventItem, CustomEnchantments.NATURE_BLESSING)) {
                if(!dirt.contains(block.getType()) || player.isSneaking()) {
                    if(block.applyBoneMeal(event.getBlockFace())) {
                        eventItem = eventItem.damage(1, player);

                        player.getInventory().setItem(hand, eventItem);
                    }
                }
            }
            if(CustomItems.BUILDER_WAND.matchItem(eventItem)) {
                if(!player.hasCooldown(eventItem)) {
                    int blocksUsed = CustomItems.BUILDER_WAND.build(player, eventItem, block, face, clampedInteractionPoint);
                    if(blocksUsed > 0) {
                        player.setCooldown(eventItem, 5);
                        player.getWorld().playSound(player, Sound.ENTITY_CHICKEN_STEP, 1, 1.25f);
                    }
                }
            }
            if(CustomItems.BLUEPRINT.matchItem(eventItem)) {
                if(!player.hasCooldown(eventItem)) {
                    CustomItems.BLUEPRINT.outputMultiBlockProgress(player, block.getLocation());
                    player.setCooldown(eventItem, 40);
                }
            }
            if (block.getType() == Material.SPAWNER)
                spawnerInteractions(player, hand, eventItem, (CreatureSpawner) block.getState());
        }

        if(event.getAction().isLeftClick() && hand != null) {
            if(player.isSneaking() && CustomItems.BUILDER_WAND.matchItem(eventItem)) {
                event.setCancelled(true);

                eventItem = CustomItems.BUILDER_WAND.cycleMode(eventItem);

                player.getInventory().setItem(hand, eventItem);

                player.sendActionBar(Component.text(CustomItems.BUILDER_WAND.getModeText(eventItem)));
            }
        }

        if(event.getAction().isRightClick() && eventItem != null) {
            if(eventItem.getType() == Material.FIREWORK_ROCKET) {
                if(eventItem.getEnchantmentLevel(Enchantment.INFINITY) > 0) {
                    event.setUseInteractedBlock(Event.Result.ALLOW);
                    event.setUseItemInHand(Event.Result.DENY);

                    if(player.isGliding()) {
                        player.fireworkBoost(eventItem);
                    }
                    else if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        if(interactionPoint != null && eventItem.getItemMeta() instanceof FireworkMeta fire) {
                            Firework work = interactionPoint.getWorld().spawn(interactionPoint, Firework.class);
                            work.setFireworkMeta(fire);
                            work.setShooter(player);
                        }
                    }
                }
            }
        }

        if (event.getAction() == Action.LEFT_CLICK_BLOCK && block != null && block.getType() == Material.BARRIER) {
            if (TomeAltarStorage.isTomeAltar(block.getLocation())) {
                event.setCancelled(true);
                ItemStack tomeItem = TomeAltarStorage.getItem(block.getLocation());
                BlazingGames.get().log(tomeItem);
                if (tomeItem == null) tomeItem = new ItemStack(Material.AIR);
                ItemStack finalTomeItem = tomeItem;
                TomeAltarStorage.removeTomeAltar(block.getLocation());
                Location bLoc = block.getLocation().toCenterLocation();
                List<Entity> entities = new ArrayList<>();
                for (Entity e : player.getWorld().getEntities()) {
                    Location eLoc = e.getLocation().toCenterLocation();
                    if (eLoc.getX() == bLoc.getX() && eLoc.getY() == bLoc.getY() && eLoc.getZ() == bLoc.getZ()) {
                        entities.add(e);
                    }
                }
                for (Entity e : entities) {
                    e.remove();
                }
                player.getWorld().setBlockData(bLoc, Material.AIR.createBlockData());
                Bukkit.getScheduler().runTaskLater(BlazingGames.get(), () -> InventoryUtils.collectableDrop(player, bLoc, CustomItems.TOME_ALTAR.create(), finalTomeItem), 1);
            }
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block != null && block.getType() == Material.BARRIER) {
            if(!BlazingGames.get().interactCooldown.onCooldown(player)) {
                if (TomeAltarStorage.isTomeAltar(block.getLocation())) {
                    BlazingGames.get().interactCooldown.setCooldown(player, 4);

                    event.setCancelled(true);

                    ItemStack giveItemNullable = TomeAltarStorage.getItem(block.getLocation());
                    final ItemStack giveItem = giveItemNullable == null ? new ItemStack(Material.AIR) : giveItemNullable;

                    ItemStack item = event.getItem();
                    if (item == null) item = new ItemStack(Material.AIR);

                    if(!giveItem.isSimilar(item)) {
                        ItemStack setItem = item.clone();
                        item.setAmount(item.getAmount() - 1);

                        setItem.setAmount(1);
                        TomeAltarStorage.setItem(block.getLocation(), setItem);

                        Location bLoc = block.getLocation().toCenterLocation();
                        ItemDisplay display = null;
                        for (Entity e : player.getWorld().getEntities()) {
                            Location eLoc = e.getLocation().toCenterLocation();
                            if (eLoc.getX() == bLoc.getX() && eLoc.getY() == bLoc.getY() && eLoc.getZ() == bLoc.getZ() && e.getType() == EntityType.ITEM_DISPLAY) {
                                display = (ItemDisplay) e;
                                break;
                            }
                        }
                        if (display == null) {
                            Location loc = block.getLocation().toCenterLocation();
                            loc.setY(loc.getY() + 0.375);
                            loc.setX(loc.getX());
                            loc.setZ(loc.getZ());

                            display = (ItemDisplay) block.getWorld().spawnEntity(loc, EntityType.ITEM_DISPLAY);
                            display.setItemStack(setItem);
                            Transformation transformation = new Transformation(new Vector3f(), new Quaternionf(),new Vector3f(0.25f, 0.25f, 0.25f),new Quaternionf());
                            display.setTransformation(transformation);
                        } else {
                            display.setItemStack(setItem);
                        }

                        Bukkit.getScheduler().runTaskLater(BlazingGames.get(), () -> {
                            if (!player.getInventory().addItem(giveItem).values().isEmpty()) {
                                player.getWorld().dropItem(player.getLocation(), giveItem);
                            }
                        }, 1);
                    }
                }
            }
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block != null && hand != null) {
            Vector v = face.getDirection();
            Location nextBlock = block.getLocation().add(v).toCenterLocation();
            Collection<Shulker> shulkers = nextBlock.getNearbyEntitiesByType(Shulker.class, 0.5);
            if (!shulkers.isEmpty() && shulkers.size() < 8) {
                Shulker shulker = shulkers.iterator().next();
                double y = shulker.getY();
                if (BlockPlaceEventListener.isTop(y)) nextBlock.add(0, -0.5, 0);
                PersistentDataContainer container = shulker.getPersistentDataContainer();

                ItemStack item = event.getPlayer().getInventory().getItem(hand);
                boolean isSlab = CustomItem.getCustomItem(item) != null && Objects.requireNonNull(item.getPersistentDataContainer().get(BlazingGames.get().key("custom_item"), PersistentDataType.STRING)).contains("slab");
                if (isSlab && container.has(BlazingGames.get().key("slab")) && container.has(BlazingGames.get().key("slab_type"))) {
                    CustomSlabs.CustomSlab slab = (CustomSlabs.CustomSlab) CustomItem.getCustomItem(item);
                    if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                        event.getPlayer().getInventory().getItem(hand).setAmount(event.getPlayer().getInventory().getItem(hand).getAmount() - 1);
                    BlockPlaceEventListener.placeCustomSlab(slab, nextBlock, player.getEyeLocation(), face);
                }
            }
        }
    }

    private void spawnerInteractions(Player player, EquipmentSlot hand, ItemStack mainHand, CreatureSpawner spawner) {
        if(hand != EquipmentSlot.HAND) {
            return;
        }

        if(CustomItem.isCustomItem(mainHand)) {
            return;
        }

        boolean inverted = player.getInventory().getItemInOffHand().getType().equals(Material.QUARTZ);

        boolean successful = false;

        if (mainHand.getType() == Material.SUGAR && (
            (spawner.getMinSpawnDelay() - 5 > 0 && !inverted && spawner.getMinSpawnDelay()-5 <= spawner.getMaxSpawnDelay()) ||
            (spawner.getMinSpawnDelay() + 5 <= 1000 && inverted && spawner.getMinSpawnDelay()+5 <= spawner.getMaxSpawnDelay())
        )) {
            if (!inverted) spawner.setMinSpawnDelay(spawner.getMinSpawnDelay() - 5);
            else spawner.setMinSpawnDelay(spawner.getMinSpawnDelay() + 5);
            successful = true;
        }
        if (mainHand.getType() == Material.CLOCK && (
            (spawner.getMaxSpawnDelay() - 5 > 0 && !inverted && spawner.getMinSpawnDelay() <= spawner.getMaxSpawnDelay()-5) ||
            (spawner.getMaxSpawnDelay() + 5 <= 1000 && inverted && spawner.getMinSpawnDelay() <= spawner.getMaxSpawnDelay()+5)
        )) {
            if (!inverted) spawner.setMaxSpawnDelay(spawner.getMaxSpawnDelay() - 5);
            else spawner.setMaxSpawnDelay(spawner.getMaxSpawnDelay() + 5);
            successful = true;
        }
        if (mainHand.getType() == Material.FERMENTED_SPIDER_EYE && (
            (spawner.getSpawnCount() + 1 <= 20 && !inverted) ||
            (spawner.getSpawnCount() - 1 > 0 && inverted)
        )) {
            if (!inverted) spawner.setSpawnCount(spawner.getSpawnCount() + 1);
            else spawner.setSpawnCount(spawner.getSpawnCount() - 1);
            successful = true;
        }
        if (mainHand.getType() == Material.GHAST_TEAR && (
            (spawner.getMaxNearbyEntities() + 2 <= 200 && !inverted) ||
            (spawner.getMaxNearbyEntities() - 2 > 0 && inverted && spawner.getMaxNearbyEntities() != 32767)
        )) {
            if (!inverted) spawner.setMaxNearbyEntities(spawner.getMaxNearbyEntities() + 2);
            else spawner.setMaxNearbyEntities(spawner.getMaxNearbyEntities() - 2);
            successful = true;
        }
        if (mainHand.getType() == Material.PRISMARINE_CRYSTALS && (
            (spawner.getRequiredPlayerRange() + 2 <= 50 && !inverted) ||
            (spawner.getRequiredPlayerRange() - 2 > 0 && inverted && spawner.getRequiredPlayerRange() != 32767)
        )) {
            if (!inverted) spawner.setRequiredPlayerRange(spawner.getRequiredPlayerRange() + 2);
            else spawner.setRequiredPlayerRange(spawner.getRequiredPlayerRange() - 2);
            successful = true;
        }
        if (mainHand.getType() == Material.BLAZE_ROD && (
            (spawner.getSpawnRange() + 1 <= 20 && !inverted) ||
            (spawner.getSpawnRange() - 1 > 0 && inverted)
        )) {
            if (!inverted) spawner.setSpawnRange(spawner.getSpawnRange() + 1);
            else spawner.setSpawnRange(spawner.getSpawnRange() - 1);
            successful = true;
        }
        if (mainHand.getType() == Material.NETHER_STAR && (
            (spawner.getRequiredPlayerRange() != 32767 && !inverted) ||
            (spawner.getRequiredPlayerRange() == 32767 && inverted)
        )) {
            if (!inverted) spawner.setRequiredPlayerRange(32767);
            else spawner.setRequiredPlayerRange(16);
            successful = true;
        }
        if (mainHand.getType() == Material.CHORUS_FRUIT && (
            (spawner.getMaxNearbyEntities() != 32767 && !inverted) ||
            (spawner.getMaxNearbyEntities() == 32767 && inverted)
        )) {
            if (!inverted) spawner.setMaxNearbyEntities(32767);
            else spawner.setMaxNearbyEntities(6);
            successful = true;
        }
        PersistentDataContainer dataContainer = spawner.getPersistentDataContainer();
        if (mainHand.getType() == Material.COMPARATOR &&
            inverted == dataContainer.getOrDefault(BlazingGames.get().key("redstone_control"), PersistentDataType.BOOLEAN, false)
        ) {
            if (!inverted) dataContainer.set(BlazingGames.get().key("redstone_control"), PersistentDataType.BOOLEAN, true);
            else dataContainer.remove(BlazingGames.get().key("redstone_control"));
            successful = true;
        }

        if(successful) {
            if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
                mainHand.subtract();
                player.getInventory().setItem(hand, mainHand);
            }
        }

        spawner.update();
    }

    @SuppressWarnings("unchecked")
    private void vaultShit(Block block) {
        Bukkit.getScheduler().runTask(BlazingGames.get(), () -> {
            if (!(block.getState() instanceof CraftVault vault)) return;
            VaultBlockEntity vaultBlockEntity = vault.getTileEntity();
            VaultServerData vaultServerData = vaultBlockEntity.getServerData();
            Method getItemsToEject;
            try {
                getItemsToEject = VaultServerData.class.getDeclaredMethod("getItemsToEject");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            getItemsToEject.setAccessible(true);
            List<net.minecraft.world.item.ItemStack> items;
            try {
                items = (List<net.minecraft.world.item.ItemStack>) getItemsToEject.invoke(vaultServerData);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            AtomicInteger i = new AtomicInteger();
            List<net.minecraft.world.item.ItemStack> finalItems = new ArrayList<>(items);
            items.forEach(itemStack -> {
                ItemStack bukkitItemStack = CraftItemStack.asBukkitCopy(itemStack);
                if (bukkitItemStack.getType() == Material.ENCHANTED_BOOK) {
                    EnchantmentStorageMeta esm = (EnchantmentStorageMeta) bukkitItemStack.getItemMeta();
                    if (esm.hasStoredEnchant(Enchantment.WIND_BURST)) {
                        finalItems.set(i.get(), CraftItemStack.asNMSCopy(CustomItems.STORM_TOME.create()));
                    }
                }
                i.addAndGet(1);
            });
            try {
                Method setItemsToEject = VaultServerData.class.getDeclaredMethod("setItemsToEject", List.class);
                setItemsToEject.setAccessible(true);
                setItemsToEject.invoke(vaultServerData, finalItems);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
