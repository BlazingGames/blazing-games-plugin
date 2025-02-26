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

package de.blazemcworld.blazinggames.events.handlers.block_breaking;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantments;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentHelper;
import de.blazemcworld.blazinggames.events.BlockBreakEventListener;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

public class TreeFellerHandler extends BlazingEventHandler<BlockBreakEvent> {
    private static final int treeFellerMax = 128;

    @Override
    public boolean fitCriteria(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();

        return ItemUtils.getUncoloredType(event.getBlock()) == Material.OAK_LOG && player.getFoodLevel() > 6 &&
                EnchantmentHelper.hasActiveEnchantmentWrapper(mainHand, CustomEnchantments.TREE_FELLER);
    }

    @Override
    public void execute(BlockBreakEvent event) {
        Player player = event.getPlayer();

        List<Location> blocksToBreak = new ArrayList<>();
        blocksToBreak.add(event.getBlock().getLocation());

        boolean foundLeaves = false;

        treeLoop:
        for (int i = 0; i < blocksToBreak.size(); i++) {
            Block block = blocksToBreak.get(i).getBlock();
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    for (int y = -1; y <= 1; y++) {
                        Block relBlock = block.getRelative(x, 1, z);

                        if (ItemUtils.getUncoloredType(relBlock) == Material.OAK_LEAVES) {
                            if (relBlock.getBlockData() instanceof Leaves leaf) {
                                if (!leaf.isPersistent()) {
                                    foundLeaves = true;
                                }
                            }
                        } else if (ItemUtils.getUncoloredType(relBlock) == Material.OAK_LOG) {
                            if (!blocksToBreak.contains(relBlock.getLocation())) {
                                blocksToBreak.add(relBlock.getLocation());
                            }
                        }

                        if(blocksToBreak.size() >= treeFellerMax) {
                            break treeLoop;
                        }
                    }
                }
            }
        }

        if (!foundLeaves) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(BlazingGames.get(), () -> treeFeller(player, blocksToBreak), 1);
    }

    private void treeFeller(Player player, List<Location> blocksToBreak) {
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

        Block block = blocksToBreak.getFirst().getBlock();

        BlockBreakEventListener.fakeBreakBlock(player, block);
        blocksToBreak.removeFirst();

        player.damageItemStack(EquipmentSlot.HAND, 1);

        int chance = 100 - treeFeller * 20;

        if (new Random().nextInt(100) + 1 <= chance) {
            player.setFoodLevel(player.getFoodLevel() - 1);
        }

        Bukkit.getScheduler().runTaskLater(BlazingGames.get(), () -> treeFeller(player, blocksToBreak), 1);
    }
}
