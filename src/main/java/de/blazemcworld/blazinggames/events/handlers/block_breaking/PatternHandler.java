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

import de.blazemcworld.blazinggames.enchantments.PatternEnchantment;
import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantments;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentHelper;
import de.blazemcworld.blazinggames.events.BlockBreakEventListener;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.utils.Pair;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PatternHandler extends BlazingEventHandler<BlockBreakEvent> {
    @Override
    public boolean fitCriteria(BlockBreakEvent event, boolean cancelled) {
        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        BlockFace face = event.getPlayer().getTargetBlockFace(6);

        return EnchantmentHelper.hasActiveEnchantmentWrapper(mainHand, CustomEnchantments.PATTERN) && face != null;
    }

    @Override
    public void execute(BlockBreakEvent event) {
        Player player = event.getPlayer();
        BlockFace playerDir = player.getFacing();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        BlockFace face = event.getPlayer().getTargetBlockFace(6);

        int pattern = EnchantmentHelper.getActiveEnchantmentWrapperLevel(mainHand, CustomEnchantments.PATTERN);

        if (face != null) {
            Pair<Integer, Integer> dimensions = PatternEnchantment.getDimensions(pattern);

            for (int i = 0; i < dimensions.left; i++) {
                int x = -dimensions.left / 2 + i;
                for (int j = 0; j < dimensions.right; j++) {
                    Vector vec = new Vector(0, 0, 0);
                    if (face.getModY() != 0) {
                        int y = -dimensions.right / 2 + j;
                        switch (playerDir) {
                            case EAST -> vec = new Vector(y, 0, x);
                            case WEST -> vec = new Vector(-y, 0, -x);
                            case NORTH -> vec = new Vector(x, 0, -y);
                            case SOUTH -> vec = new Vector(-x, 0, y);
                        }
                    } else {
                        switch (face) {
                            case EAST -> vec = new Vector(0, j, -x);
                            case WEST -> vec = new Vector(0, j, x);
                            case NORTH -> vec = new Vector(-x, j, 0);
                            case SOUTH -> vec = new Vector(x, j, 0);
                        }
                    }
                    if (!vec.isZero()) {
                        BlockBreakEventListener.fakeBreakBlock(player,
                                event.getBlock().getLocation().clone().add(vec).getBlock());
                    }
                }
            }
        }
    }
}
