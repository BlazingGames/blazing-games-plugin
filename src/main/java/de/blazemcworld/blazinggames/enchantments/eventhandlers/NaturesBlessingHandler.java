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

package de.blazemcworld.blazinggames.enchantments.eventhandlers;

import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantments;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentHelper;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class NaturesBlessingHandler extends BlazingEventHandler<PlayerInteractEvent> {
    private final Set<Material> dirt = Set.of(
            Material.DIRT,
            Material.ROOTED_DIRT,
            Material.COARSE_DIRT,
            Material.GRASS_BLOCK,
            Material.DIRT_PATH
    );

    @Override
    public boolean fitCriteria(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack eventItem = event.getItem();
        EquipmentSlot hand = event.getHand();
        Block block = event.getClickedBlock();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block != null && hand != null && eventItem != null) {
            if (EnchantmentHelper.hasActiveEnchantmentWrapper(eventItem, CustomEnchantments.NATURE_BLESSING)) {
                if (!dirt.contains(block.getType()) || player.isSneaking()) {
                    return block.applyBoneMeal(event.getBlockFace());
                }
            }
        }
        return false;
    }

    @Override
    public void execute(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        EquipmentSlot hand = event.getHand();
        if (hand == null) return;

        ItemStack eventItem = event.getItem();
        if (eventItem == null) return;

        eventItem = eventItem.damage(1, player);
        player.getInventory().setItem(hand, eventItem);
    }
}
