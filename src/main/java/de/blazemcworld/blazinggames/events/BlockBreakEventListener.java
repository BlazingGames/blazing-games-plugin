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

import de.blazemcworld.blazinggames.events.base.BlazingEventListener;
import de.blazemcworld.blazinggames.events.handlers.block_breaking.BlockBreakDamageToolHandler;
import de.blazemcworld.blazinggames.events.handlers.block_breaking.PatternHandler;
import de.blazemcworld.blazinggames.events.handlers.block_breaking.StandardBlockBreakHandler;
import de.blazemcworld.blazinggames.events.handlers.block_breaking.TreeFellerHandler;
import de.blazemcworld.blazinggames.utils.Drops;
import de.blazemcworld.blazinggames.utils.InventoryUtils;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakEventListener extends BlazingEventListener<BlockBreakEvent> {
    public static Drops getBlockDrops(Player player, Block block) {
        ItemStack mainHand = player.getInventory().getItemInMainHand();

        return getBlockDrops(mainHand, block);
    }

    public static Drops getBlockDrops(ItemStack mainHand, Block block) {
        BlazingBlockDropEvent event = new BlazingBlockDropEvent(mainHand, block, new Drops());

        event.callEvent();

        return event.getDrops();
    }

    public static void fakeBreakBlock(Player player, Block block) {
        fakeBreakBlock(player, block, true);
    }

    public static void fakeBreakBlock(Player player, Block block, boolean playEffects) {
        if (block.isEmpty() || block.getType().getHardness() < 0 || block.isLiquid()) {
            return;
        }

        Drops drops = getBlockDrops(player, block);

        BlazingBlockDisappearEvent event = new BlazingBlockDisappearEvent(player, block);

        event.callEvent();

        if(event.isCancelled()) {
            return;
        }

        if(playEffects) block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getBlockData());

        boolean waterlogged = false;

        if (block.getBlockData() instanceof Waterlogged water) {
            waterlogged = water.isWaterlogged();
        }

        block.setType(waterlogged ? Material.WATER : Material.AIR, true);

        InventoryUtils.collectableDrop(player, block.getLocation(), drops);
    }

    public static void awardBlock(Location location, int amount, Player trigger) {
        if(!(location.getWorld() instanceof CraftWorld world)) { return; }
        if(!(trigger instanceof CraftPlayer player)) { return; }

        net.minecraft.world.entity.ExperienceOrb.award(
                world.getHandle(), new Vec3(location.x(), location.y(), location.z()), amount,
                ExperienceOrb.SpawnReason.BLOCK_BREAK, player.getHandleRaw());
    }

    public BlockBreakEventListener() {
        this.handlers.add(new BlockBreakDamageToolHandler());
        this.handlers.add(new TreeFellerHandler());
        this.handlers.add(new PatternHandler());
        this.handlers.add(new StandardBlockBreakHandler());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void event(BlockBreakEvent event) {
        executeEvent(event);
    }
}
