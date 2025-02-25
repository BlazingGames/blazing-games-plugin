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

package de.blazemcworld.blazinggames.events.handlers.blocks;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantments;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentHelper;
import de.blazemcworld.blazinggames.events.BreakBlockEventListener;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class LogBreakHandler extends BlazingEventHandler<BlockBreakEvent> {
    private final Set<Material> logs = Set.of(
            Material.OAK_LOG,
            Material.DARK_OAK_LOG,
            Material.ACACIA_LOG,
            Material.BIRCH_LOG,
            Material.JUNGLE_LOG,
            Material.MANGROVE_LOG,
            Material.SPRUCE_LOG,
            Material.CHERRY_LOG,
            Material.WARPED_STEM,
            Material.CRIMSON_STEM,
            Material.PALE_OAK_LOG
    );

    private final Set<Material> leaves = Set.of(
            Material.OAK_LEAVES,
            Material.DARK_OAK_LEAVES,
            Material.ACACIA_LEAVES,
            Material.BIRCH_LEAVES,
            Material.JUNGLE_LEAVES,
            Material.MANGROVE_LEAVES,
            Material.SPRUCE_LEAVES,
            Material.CHERRY_LEAVES,
            Material.AZALEA_LEAVES,
            Material.FLOWERING_AZALEA_LEAVES,
            Material.WARPED_WART_BLOCK,
            Material.NETHER_WART_BLOCK,
            Material.PALE_OAK_LEAVES
    );

    @Override
    public boolean fitCriteria(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();

        if (EnchantmentHelper.hasActiveEnchantmentWrapper(mainHand, CustomEnchantments.TREE_FELLER)) {
            if (logs.contains(event.getBlock().getType())) {
                if (player.getFoodLevel() <= 6) {
                    return false;
                }

                ItemStack axe = player.getInventory().getItemInMainHand();

                int treeFeller = EnchantmentHelper.getActiveEnchantmentWrapperLevel(axe, CustomEnchantments.TREE_FELLER);

                return treeFeller > 0;
            }
        }
        return false;
    }

    @Override
    public void execute(BlockBreakEvent event) {
        List<Block> blocksToBreak = new ArrayList<>();
        blocksToBreak.add(event.getBlock());

        boolean foundLeaves = false;

        for (int i = 0; i < blocksToBreak.size(); i++) {
            Block block = blocksToBreak.get(i);
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    for (int y = -1; y <= 1; y++) {
                        Block relBlock = block.getRelative(x, 1, z);

                        if (leaves.contains(relBlock.getType())) {
                            if (relBlock.getBlockData() instanceof Leaves leaf) {
                                if (!leaf.isPersistent()) {
                                    foundLeaves = true;
                                }
                            }
                        } else if (logs.contains(relBlock.getType())) {
                            if (!blocksToBreak.contains(relBlock)) {
                                blocksToBreak.add(relBlock);
                            }
                        }
                    }
                }
            }
        }

        if (!foundLeaves) return;
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(BlazingGames.get(), () -> treeFeller(player, blocksToBreak), 1);
    }

    private void treeFeller(Player player, List<Block> blocksToBreak) {
        if (blocksToBreak.isEmpty()) {
            return;
        }

        if (player.getFoodLevel() <= 0) {
            return;
        }

        ItemStack axe = player.getInventory().getItemInMainHand();

        int treeFeller = EnchantmentHelper.getActiveEnchantmentWrapperLevel(axe, CustomEnchantments.TREE_FELLER);

        if (treeFeller <= 0) {
            return;
        }

        Block block = blocksToBreak.getFirst();

        BreakBlockEventListener.fakeBreakBlock(player, block);
        blocksToBreak.removeFirst();

        player.damageItemStack(EquipmentSlot.HAND, 1);

        int chance = 100 - treeFeller * 20;

        if (new Random().nextInt(100) + 1 <= chance) {
            player.setFoodLevel(player.getFoodLevel() - 1);
        }

        Bukkit.getScheduler().runTaskLater(BlazingGames.get(), () -> treeFeller(player, blocksToBreak), 1);
    }
}
