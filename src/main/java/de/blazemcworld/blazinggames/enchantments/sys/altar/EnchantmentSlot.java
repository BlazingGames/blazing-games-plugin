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
package de.blazemcworld.blazinggames.enchantments.sys.altar;

import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentWrapper;
import de.blazemcworld.blazinggames.items.change.ItemChangeProviders;
import de.blazemcworld.blazinggames.userinterfaces.IndexedUserInterfaceSlot;
import de.blazemcworld.blazinggames.userinterfaces.UserInterface;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EnchantmentSlot extends IndexedUserInterfaceSlot {
    public EnchantmentSlot(int index) {
        super(index);
    }

    @Override
    public void onUpdate(UserInterface inventory, int slot) {
        if(!(inventory instanceof AltarInterface altarInterface)) {
            return;
        }

        List<EnchantmentWrapper> wrappers = altarInterface.getAvailable();
        ItemStack tool = altarInterface.getTool();
        int lapis = altarInterface.getLapis().getAmount();
        ItemStack material = altarInterface.getMaterial();

        int index = getIndex(inventory);

        if(index >= 0 && index < wrappers.size()) {
            inventory.setItem(slot, wrappers.get(index).getIcon(tool, lapis, material, altarInterface.getTier()));
            return;
        }

        inventory.setItem(slot, ItemStack.empty());
    }

    @Override
    public boolean onClick(UserInterface inventory, ItemStack current, ItemStack cursor, int slot, InventoryAction action, boolean isShiftClick, InventoryClickEvent event) {
        if(!(inventory instanceof AltarInterface altarInterface)) {
            return false;
        }

        List<EnchantmentWrapper> wrappers = altarInterface.getAvailable();
        ItemStack tool = altarInterface.getTool();
        ItemStack lapis = altarInterface.getLapis();
        ItemStack material = altarInterface.getMaterial();

        int index = getIndex(inventory);

        if(index >= 0 && index < wrappers.size()) {
            EnchantmentWrapper wrapper = wrappers.get(index);

            int level = wrapper.getLevel(tool);

            if(level >= wrapper.getMaxLevel()) {
                return false;
            }

            if(altarInterface.getTier() < wrapper.getRecipe(level).tier()) {
                return false;
            }

            if(!(event.getWhoClicked() instanceof Player player)) {
                return false;
            }

            AltarRecipe recipe = wrapper.getRecipe(level+1);

            if(lapis.getAmount() >= recipe.lapisAmount()) {
                if(recipe.matchMaterial(material)) {
                    if(player.getLevel() >= recipe.expAmount()) {
                        ItemStack result = wrapper.apply(tool, level+1);
                        result = ItemChangeProviders.update(result);

                        altarInterface.setItem(1, 1, result);
                        lapis.subtract(recipe.lapisAmount());
                        material.subtract(recipe.itemAmount());
                        player.setLevel(player.getLevel() - recipe.expAmount());

                        player.getWorld().playSound(player, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);

                        Advancement advancement = Bukkit.getAdvancement(NamespacedKey.minecraft("story/enchant_item"));
                        AdvancementProgress progress = player.getAdvancementProgress(advancement);
                        if (!progress.isDone()) {
                            progress.getRemainingCriteria().forEach(progress::awardCriteria);
                        }

                        return false;
                    }
                }
            }

            player.playSound(player, Sound.ENTITY_SHULKER_HURT_CLOSED, 1, 1);

            return false;
        }

        return false;
    }
}
