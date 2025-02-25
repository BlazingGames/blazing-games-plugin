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
import de.blazemcworld.blazinggames.enchantments.PatternEnchantment;
import de.blazemcworld.blazinggames.enchantments.eventhandlers.PatternHandler;
import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantments;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentHelper;
import de.blazemcworld.blazinggames.events.base.BlazingEventListener;
import de.blazemcworld.blazinggames.events.handlers.blocks.BlockBreakEventHandler;
import de.blazemcworld.blazinggames.events.handlers.blocks.LogBreakHandler;
import de.blazemcworld.blazinggames.events.handlers.tools.ToolDamageHandler;
import de.blazemcworld.blazinggames.items.recipes.RecipeHelper;
import de.blazemcworld.blazinggames.teleportanchor.LodestoneStorage;
import de.blazemcworld.blazinggames.utils.Drops;
import de.blazemcworld.blazinggames.utils.InventoryUtils;
import de.blazemcworld.blazinggames.utils.ItemUtils;
import de.blazemcworld.blazinggames.utils.Pair;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Tool;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.*;

public class BreakBlockEventListener extends BlazingEventListener<BlockBreakEvent> {
    public BreakBlockEventListener() {
        this.handlers.addAll(List.of(
                new BlockBreakEventHandler(),
                new ToolDamageHandler(),
                new LogBreakHandler(),
                new PatternHandler()
        ));
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void event(BlockBreakEvent event) {
        executeEvent(event);
    }

    public static Drops getBlockDrops(Player player, Block block) {
        ItemStack mainHand = player.getInventory().getItemInMainHand();

        return getBlockDrops(mainHand, block);
    }

    public static Drops getBlockDrops(ItemStack mainHand, Block block) {
        BlockState state = block.getState();
        Drops drops = new Drops(block.getDrops(mainHand));

        drops.addExperience(getDroppedExp(block, mainHand));

        if (block.getType() == Material.CHISELED_BOOKSHELF) {
            if (mainHand.getEnchantmentLevel(Enchantment.SILK_TOUCH) <= 0) {
                drops.add(new ItemStack(Material.CHISELED_BOOKSHELF));
            }
        }

        if (block.getType() == Material.SPAWNER) {
            if (mainHand.getEnchantmentLevel(Enchantment.SILK_TOUCH) > 1 && (
                    mainHand.getType() == Material.WOODEN_PICKAXE ||
                            mainHand.getType() == Material.STONE_PICKAXE ||
                            mainHand.getType() == Material.GOLDEN_PICKAXE ||
                mainHand.getType() == Material.IRON_PICKAXE ||
                mainHand.getType() == Material.DIAMOND_PICKAXE ||
                mainHand.getType() == Material.NETHERITE_PICKAXE
            )) {
                drops.setExperience(0);
                CreatureSpawner spawner = (CreatureSpawner) block.getState();
                ItemStack item = new ItemStack(Material.SPAWNER);
                BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
                meta.setBlockState(block.getState());
                if (spawner.getPersistentDataContainer().getOrDefault(BlazingGames.get().key("redstone_control"), PersistentDataType.BOOLEAN, false))
                    meta.getPersistentDataContainer().set(BlazingGames.get().key("redstone_control"), PersistentDataType.BOOLEAN, true);
                item.setItemMeta(meta);

                drops.add(item);

                Damageable itemMeta = (Damageable) mainHand.getItemMeta();
                if (mainHand.getType() == Material.IRON_PICKAXE || mainHand.getType() == Material.GOLDEN_PICKAXE || mainHand.getType() == Material.STONE_PICKAXE || mainHand.getType() == Material.WOODEN_PICKAXE)
                    itemMeta.setDamage(Material.IRON_PICKAXE.getMaxDurability());
                if (mainHand.getType() == Material.DIAMOND_PICKAXE)
                    itemMeta.setDamage(itemMeta.getDamage() + Material.DIAMOND_PICKAXE.getMaxDurability() / 3 * 2 - 1);
                if (mainHand.getType() == Material.NETHERITE_PICKAXE)
                    itemMeta.setDamage(itemMeta.getDamage() + Material.NETHERITE_PICKAXE.getMaxDurability() / 2 - 1);
                mainHand.setItemMeta(itemMeta);
            }
        }

        if (state instanceof Container && ItemUtils.getUncoloredType(block) != Material.SHULKER_BOX) {
            ItemStack[] contents = ((Container) state).getInventory().getContents();
            for (ItemStack item : contents) {
                if (item != null && !item.isEmpty()) {
                    drops.add(item);
                }
            }
        }

        if (EnchantmentHelper.hasActiveEnchantmentWrapper(mainHand, CustomEnchantments.FLAME_TOUCH)) {
            Iterator<ItemStack> iter = drops.iterator();
            List<ItemStack> smeltedDrops = new ArrayList<>();

            while(iter.hasNext()) {
                ItemStack stack = iter.next();

                ItemStack smelted = RecipeHelper.smeltItem(stack);

                if(smelted != null) {
                    iter.remove();
                    smeltedDrops.add(smelted);
                }
                else if(stack.getType().isFuel()) {
                    iter.remove();
                }
            }

            drops.addAll(smeltedDrops);
        }

        if (ComputerRegistry.getComputerByLocationRounded(block.getLocation()) != null) {
            BootedComputer computer = ComputerRegistry.getComputerByLocationRounded(block.getLocation());
            return new Drops(ComputerRegistry.addAttributes(computer.getType().getType().getDisplayItem(computer), computer));
        } else {
            return drops;
        }
    }

    public static void fakeBreakBlock(Player player, Block block) {
        fakeBreakBlock(player, block, true);
    }

    public static void fakeBreakBlock(Player player, Block block, boolean playEffects) {
        if (block.isEmpty() || block.getType().getHardness() < 0 || block.isLiquid()) {
            return;
        }

        Drops drops = getBlockDrops(player, block);

        onAnyBlockBreak(block);

        if(playEffects) block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getBlockData());

        boolean waterlogged = false;

        if (block.getBlockData() instanceof Waterlogged water) {
            waterlogged = water.isWaterlogged();
        }

        block.setType(waterlogged ? Material.WATER : Material.AIR, true);

        InventoryUtils.collectableDrop(player, block.getLocation(), drops);
    }

    private static void onAnyBlockBreak(Block block) {
        if (block.getType() == Material.LODESTONE) {
            LodestoneStorage.destroyLodestone(block.getLocation());
            LodestoneStorage.refreshAllInventories();
        }

        if (ComputerRegistry.getComputerByLocationRounded(block.getLocation()) != null) {
            BootedComputer computer = ComputerRegistry.getComputerByLocationRounded(block.getLocation());
            ComputerRegistry.unload(computer.getId());
        }
    }

    private static int getDroppedExp(Block block, ItemStack tool) {
        if (!(block instanceof CraftBlock craftBlock)) return 0;

        net.minecraft.world.level.block.state.BlockState nmsBlockState = craftBlock.getNMS();
        BlockPos pos = craftBlock.getPosition();
        ServerLevel level = craftBlock.getCraftWorld().getHandle();

        return nmsBlockState.getBlock().getExpDrop(nmsBlockState, level, pos, CraftItemStack.asNMSCopy(tool), true);
    }

    public static void awardBlock(Location location, int amount, Player trigger) {
        if(!(location.getWorld() instanceof CraftWorld world)) { return; }
        if(!(trigger instanceof CraftPlayer player)) { return; }

        net.minecraft.world.entity.ExperienceOrb.award(
                world.getHandle(), new Vec3(location.x(), location.y(), location.z()), amount,
                ExperienceOrb.SpawnReason.BLOCK_BREAK, player.getHandleRaw());
    }
}
