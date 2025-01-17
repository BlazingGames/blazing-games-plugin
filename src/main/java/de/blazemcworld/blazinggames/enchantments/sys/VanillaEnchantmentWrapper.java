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

import de.blazemcworld.blazinggames.enchantments.sys.altar.AltarRecipe;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class VanillaEnchantmentWrapper implements EnchantmentWrapper {
    private final Enchantment enchantment;
    private final Supplier<ItemStack> icon;
    private final List<AltarRecipe> recipes;
    private final List<Integer> altarLevels;

    public VanillaEnchantmentWrapper(Enchantment enchantment, Supplier<ItemStack> icon, int tier1, int tier2, int tier3, int tier4, int tier5, AltarRecipe... recipes) {
        this.enchantment = enchantment;
        this.icon = icon;
        this.altarLevels = List.of(tier1, tier2, tier3, tier4, tier5);
        this.recipes = List.of(recipes);
    }

    public VanillaEnchantmentWrapper(Enchantment enchantment, Supplier<ItemStack> icon, int tier1, int tier2, int tier3, int tier4, AltarRecipe... recipes) {
        this.enchantment = enchantment;
        this.icon = icon;
        this.altarLevels = List.of(tier1, tier2, tier3, tier4);
        this.recipes = List.of(recipes);
    }

    public VanillaEnchantmentWrapper(Enchantment enchantment, Supplier<ItemStack> icon, int tier1, int tier2, int tier3, AltarRecipe... recipes) {
        this.enchantment = enchantment;
        this.icon = icon;
        this.altarLevels = List.of(tier1, tier2, tier3);
        this.recipes = List.of(recipes);
    }

    public VanillaEnchantmentWrapper(Enchantment enchantment, Supplier<ItemStack> icon, int tier1, int tier2, AltarRecipe... recipes) {
        this(enchantment, icon, tier1, tier2, tier2, recipes);
    }

    public VanillaEnchantmentWrapper(Enchantment enchantment, Supplier<ItemStack> icon, int tier, AltarRecipe... recipes) {
        this(enchantment, icon, tier, tier, recipes);
    }

    @Override
    public ItemStack apply(ItemStack tool, int level) {
        ItemStack result = tool.clone();

        result.addUnsafeEnchantment(enchantment, level);

        return result;
    }

    @Override
    public int getLevel(ItemStack tool) {
        return tool.getEnchantmentLevel(enchantment);
    }

    @Override
    public int getMaxLevel() {
        return altarLevels.getLast();
    }

    @Override
    public boolean canEnchantItem(ItemStack tool) {
        Map<CustomEnchantment, Integer> customEnchantmentLevels = EnchantmentHelper.getCustomEnchantments(tool);

        for(Map.Entry<CustomEnchantment, Integer> entry : customEnchantmentLevels.entrySet()) {
            if(entry.getKey().conflictsWith(enchantment)) {
                return false;
            }
        }

        if(tool.getItemMeta() != null && tool.getItemMeta().hasConflictingEnchant(enchantment)) {
            for(Map.Entry<Enchantment, Integer> entry : tool.getItemMeta().getEnchants().entrySet()) {
                if(entry.getKey().equals(enchantment)) {
                    continue;
                }
                if(entry.getKey().conflictsWith(enchantment)) {
                    return false;
                }
            }
        }

        if(tool.getItemMeta() instanceof EnchantmentStorageMeta esm) {
            if(esm.hasConflictingStoredEnchant(enchantment)) {
                return false;
            }
        }

        return canGoOnItem(tool);
    }

    @Override
    public boolean canGoOnItem(ItemStack tool) {
        return enchantment.canEnchantItem(tool) || tool.getType() == Material.BOOK
                || tool.getType() == Material.ENCHANTED_BOOK;
    }

    @Override
    public boolean conflictsWith(Enchantment other) {
        return enchantment.conflictsWith(other) || other.conflictsWith(enchantment);
    }

    @Override
    public NamespacedKey getKey() {
        return enchantment.getKey();
    }

    @Override
    public Component getComponent(int level) {
        return enchantment.displayName(level);
    }

    @Override
    public String getWarning(int level) {
        if (enchantment == Enchantment.SILK_TOUCH && level > 1) {
            return "Might break pickaxe when used on spawners.\nAlways breaks Iron pickaxes.\nUses 2/3rd of durability on diamond pickaxes.\nUses half ofdurability on netherite pickaxes.";
        }
        return null;
    }

    @Override
    public Component getLevelessComponent() {
        TextColor color = NamedTextColor.GRAY;

        if(enchantment.isCursed()) {
            color = NamedTextColor.RED;
        }

        return Component.translatable(enchantment.translationKey()).color(color).decoration(TextDecoration.ITALIC, false);
    }

    @Override
    public int maxLevelAvailableInAltar(int altarTier) {
        if (altarTier < 1) return 0;
        if (altarLevels.size() < altarTier) return getMaxLevel();
        return altarLevels.get(altarTier-1);
    }

    @Override
    public ItemStack getPreIcon() {
        return icon.get();
    }

    @Override
    public boolean isTreasure() {
        return enchantment.isTreasure();
    }

    @Override
    public AltarRecipe getRecipe(int level) {
        if(level <= 0) {
            return recipes.getFirst();
        }

        if(level > recipes.size()) {
            return recipes.getLast();
        }

        return recipes.get(level-1);
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }
}
