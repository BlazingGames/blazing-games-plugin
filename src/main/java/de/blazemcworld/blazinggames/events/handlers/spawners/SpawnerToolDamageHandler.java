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

package de.blazemcworld.blazinggames.events.handlers.spawners;

import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentHelper;
import de.blazemcworld.blazinggames.enchantments.sys.VanillaEnchantmentWrappers;
import de.blazemcworld.blazinggames.events.BlazingBlockDisappearEvent;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class SpawnerToolDamageHandler extends BlazingEventHandler<BlazingBlockDisappearEvent> {
    @Override
    public boolean fitCriteria(BlazingBlockDisappearEvent event, boolean cancelled) {
        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();

        return event.getBlock().getType() == Material.SPAWNER && ItemUtils.getUncoloredType(tool) == Material.WOODEN_PICKAXE
                && EnchantmentHelper.getActiveEnchantmentWrapperLevel(tool, VanillaEnchantmentWrappers.SILK_TOUCH) > 1;
    }

    @Override
    public void execute(BlazingBlockDisappearEvent event) {
        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();

        Damageable itemMeta = (Damageable) tool.getItemMeta();

        switch(tool.getType()) {
            case DIAMOND_PICKAXE -> itemMeta.setDamage(itemMeta.getDamage() + Material.DIAMOND_PICKAXE.getMaxDurability() / 3 * 2 - 1);
            case NETHERITE_PICKAXE -> itemMeta.setDamage(itemMeta.getDamage() + Material.NETHERITE_PICKAXE.getMaxDurability() / 2 - 1);
            default -> itemMeta.setDamage(Material.IRON_PICKAXE.getMaxDurability());
        }

        tool.setItemMeta(itemMeta);
    }
}
