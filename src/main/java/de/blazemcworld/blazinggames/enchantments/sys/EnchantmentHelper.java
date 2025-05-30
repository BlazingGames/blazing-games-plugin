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

import de.blazemcworld.blazinggames.items.CustomItem;
import de.blazemcworld.blazinggames.items.change.ItemChangeProvider;
import de.blazemcworld.blazinggames.items.change.ItemChangeProviders;
import de.blazemcworld.blazinggames.items.predicates.ItemPredicates;
import de.blazemcworld.blazinggames.utils.Pair;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.Predicate;

public class EnchantmentHelper implements ItemChangeProvider {
    public static Map<EnchantmentWrapper, Integer> getEnchantmentWrappers(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) {
            return new HashMap<>();
        }

        HashMap<EnchantmentWrapper, Integer> enchantments = new HashMap<>();

        for(EnchantmentWrapper wrapper : EnchantmentWrappers.instance.list()) {
            if(wrapper.has(stack)) {
                enchantments.put(wrapper, wrapper.getLevel(stack));
            }
        }

        return enchantments;
    }

    public static Map<CustomEnchantment, Integer> getCustomEnchantments(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) {
            return new HashMap<>();
        }

        Map<CustomEnchantment, Integer> enchantmentLevels = new HashMap<>();

        CustomEnchantments.instance.list().forEach((customEnchantment) -> {
            if(customEnchantment.has(stack)) {
                enchantmentLevels.put(customEnchantment, customEnchantment.getLevel(stack));
            }
        });

        return enchantmentLevels;
    }

    public static boolean canEnchantItem(ItemStack stack) {
        if (!CustomItem.isCustomItem(stack)) {
            if (stack.getType() == Material.ENCHANTED_BOOK
                    || stack.getType() == Material.BOOK) {
                return true;
            }
        }

        return ItemPredicates.enchantability.matchItem(stack);
    }

    public static ItemStack enchantTool(ItemStack stack, EnchantmentWrapper enchantment, int level) {
        ItemStack result = stack.clone();

        if (!canEnchantItem(result)) {
            return result;
        }

        if (enchantment.canEnchantItem(result)) {
            int current = enchantment.getLevel(result);

            if (current > level) {
                level = current;
            }
            if (level > enchantment.getMaxLevel()) {
                level = enchantment.getMaxLevel();
            }
            if (level < 0) {
                level = 0;
            }

            return enchantment.apply(result, level);
        }

        return result;
    }

    public static Pair<EnchantmentWrapper, Integer> getEnchantmentWrapperEntryByIndex(ItemStack stack, int index, Predicate<EnchantmentWrapper> filter) {
        index--;

        ItemStack result = stack.clone();

        if (!canEnchantItem(result)) {
            return null;
        }

        if(index < 0) {
            return null;
        }

        ArrayList<EnchantmentWrapper> enchantments = new ArrayList<>(EnchantmentWrappers.instance.list());

        enchantments.removeIf(filter.negate());
        enchantments.removeIf(wrapper -> !wrapper.has(stack));

        if(index < enchantments.size()) {
            EnchantmentWrapper wrapper = enchantments.get(index);
            return new Pair<>(wrapper, wrapper.getLevel(stack));
        }

        return null;
    }

    public static Pair<ItemStack, Integer> enchantFromItem(ItemStack in, ItemStack enchantingItem) {
        ItemStack result = in.clone();

        if (!canEnchantItem(result)) {
            return new Pair<>(result, 0);
        }

        int cost = 0;

        Map<EnchantmentWrapper, Integer> enchantmentLevels = getEnchantmentWrappers(enchantingItem);

        for (Map.Entry<EnchantmentWrapper, Integer> enchantment : enchantmentLevels.entrySet()) {
            result = enchantTool(result, enchantment.getKey(), enchantment.getValue());
            cost++;
        }

        return new Pair<>(ItemChangeProviders.update(result), cost);
    }

    public static ItemStack removeEnchantments(ItemStack stack) {
        ItemStack result = stack.clone();

        if (!canEnchantItem(result)) {
            return result;
        }

        Set<EnchantmentWrapper> enchantments = getEnchantmentWrappers(stack).keySet();

        for (EnchantmentWrapper enchantment : enchantments) {
            if (enchantment.canBeRemoved()) {
                result = enchantment.remove(result);
            }
        }

        return ItemChangeProviders.update(result);
    }

    // This version of the getCustomEnchantmentLevel function ignores enchanted books
    public static int getActiveEnchantmentWrapperLevel(ItemStack stack, EnchantmentWrapper enchantment) {
        if (stack != null && stack.getType() == Material.ENCHANTED_BOOK) {
            return 0;
        }
        return enchantment.getLevel(stack);
    }

    // This version of the hasCustomEnchantment function ignores enchanted books
    public static boolean hasActiveEnchantmentWrapper(ItemStack stack, EnchantmentWrapper enchantment) {
        return getActiveEnchantmentWrapperLevel(stack, enchantment) > 0;
    }

    public static Map<EnchantmentWrapper, Integer> getActiveEnchantmentWrappers(ItemStack stack) {
        if(stack != null && stack.getType() == Material.ENCHANTED_BOOK) {
            return new HashMap<>();
        }
        return getEnchantmentWrappers(stack);
    }

    @Override
    public List<Component> getLore(ItemStack stack) {
        if (!canEnchantItem(stack)) {
            return List.of();
        }

        List<Component> lore = new ArrayList<>();

        for(CustomEnchantment enchantment : CustomEnchantments.instance.list()) {
            if(enchantment.has(stack)) {
                lore.add(enchantment.getComponent(enchantment.getLevel(stack)));
            }
        }

        return lore;
    }

    @Override
    public ItemStack update(ItemStack stack) {
        ItemStack result = stack.clone();

        if (!canEnchantItem(result)) {
            return result;
        }

        ItemMeta meta = result.getItemMeta();

        meta.setEnchantmentGlintOverride(null);

        if (getCustomEnchantments(stack).isEmpty()) {
            if (stack.getType() == Material.ENCHANTED_BOOK && meta instanceof EnchantmentStorageMeta esm) {
                if (!esm.hasStoredEnchants()) {
                    result = result.withType(Material.BOOK);
                    meta = result.getItemMeta();
                }
            }
        } else {
            if (result.getType() != Material.BOOK && result.getType() != Material.ENCHANTED_BOOK) {
                if (!meta.hasEnchants()) {
                    meta.setEnchantmentGlintOverride(true);
                }
            } else if (result.getType() == Material.BOOK) {
                result = result.withType(Material.ENCHANTED_BOOK);
                meta = result.getItemMeta();
            }

            if (CustomEnchantments.UNSHINY.has(stack)) {
                meta.setEnchantmentGlintOverride(false);
            }
        }

        result.setItemMeta(meta);

        return result;
    }

    public static boolean hasStoredEnchantment(ItemStack book, Enchantment enchantment) {
        if (book.getItemMeta() instanceof EnchantmentStorageMeta esm) {
            return esm.hasStoredEnchant(enchantment);
        }
        return false;
    }
}
