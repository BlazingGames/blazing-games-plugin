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
package de.blazemcworld.blazinggames.enchantments.sys;

import de.blazemcworld.blazinggames.items.predicates.ItemPredicate;
import de.blazemcworld.blazinggames.utils.NumberUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public abstract class CustomEnchantment implements EnchantmentWrapper {
    public abstract @NotNull NamespacedKey getKey();

    @Override
    public ItemStack apply(ItemStack tool, int level) {
        return EnchantmentHelper.setCustomEnchantment(tool, this, level);
    }

    @Override
    public int getLevel(ItemStack tool) {
        return EnchantmentHelper.getCustomEnchantmentLevel(tool, this);
    }

    public ItemPredicate getItemTarget() {
        return PaperEnchantmentTarget.BREAKABLE;
    }

    public boolean canEnchantItem(@NotNull ItemStack itemStack) {
        Map<CustomEnchantment, Integer> customEnchantmentLevels = EnchantmentHelper.getCustomEnchantments(itemStack);

        for(Map.Entry<CustomEnchantment, Integer> entry : customEnchantmentLevels.entrySet()) {
            if(conflictsWith(entry.getKey()) || entry.getKey().conflictsWith(this)) {
                return false;
            }
        }

        ItemMeta meta = itemStack.getItemMeta();

        Map<Enchantment, Integer> enchantmentLevels = Map.of();

        if(meta != null) {
            enchantmentLevels = itemStack.getItemMeta().getEnchants();
        }

        for(Map.Entry<Enchantment, Integer> entry : enchantmentLevels.entrySet()) {
            if(conflictsWith(entry.getKey())) {
                return false;
            }
        }

        if(itemStack.getItemMeta() instanceof EnchantmentStorageMeta esm) {
            Map<Enchantment, Integer> storedEnchantmentLevels = esm.getStoredEnchants();

            for(Map.Entry<Enchantment, Integer> entry : storedEnchantmentLevels.entrySet()) {
                if(conflictsWith(entry.getKey())) {
                    return false;
                }
            }
        }

        return canGoOnItem(itemStack);
    }

    @Override
    public boolean canGoOnItem(ItemStack tool) {
        return getItemTarget().matchItem(tool) || tool.getType() == Material.BOOK
                || tool.getType() == Material.ENCHANTED_BOOK;
    }

    public boolean canUpgradeLevel(int currentLevel) {
        return true;
    }

    public @NotNull CustomEnchantmentType getEnchantmentType() {
        return CustomEnchantmentType.NORMAL;
    }

    @Override
    public final @NotNull Component getComponent(int level) {
        String levelText = getDisplayLevel(level);

        if(levelText.isBlank()) levelText = "";
        else levelText = " " + levelText;

        return Component.text(getDisplayName() + levelText).color(getEnchantmentType().getColor()).decoration(TextDecoration.ITALIC, false);
    }

    public @NotNull String getDisplayLevel(int level) {
        return NumberUtils.getRomanNumber(level);
    }

    public abstract @NotNull String getDisplayName();

    public @NotNull Set<EquipmentSlot> getActiveSlots() {
        return Set.of(EquipmentSlot.values());
    }

    @Override
    public String toString() {
        return getKey().toString();
    }

    public double getDamageIncrease(Entity victim, int level) {
        return 0;
    }

    @Override
    public Component getLevelessComponent() {
        return Component.text(getDisplayName()).color(getEnchantmentType().getColor()).decoration(TextDecoration.ITALIC, false);
    }

    @Override
    public boolean isTreasure() {
        return false;
    }
}
