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

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.items.CustomItem;
import de.blazemcworld.blazinggames.items.change.ItemChangeProvider;
import de.blazemcworld.blazinggames.items.change.ItemChangeProviders;
import de.blazemcworld.blazinggames.items.predicates.ItemPredicates;
import de.blazemcworld.blazinggames.utils.Pair;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import java.util.*;

public class EnchantmentHelper implements ItemChangeProvider {
    private static final NamespacedKey key = BlazingGames.get().key("custom_enchantments");

    public static Map<EnchantmentWrapper, Integer> getEnchantmentWrappers(ItemStack stack) {
        if(stack == null || !stack.hasItemMeta()) {
            return new HashMap<>();
        }

        HashMap<EnchantmentWrapper, Integer> enchantments = new HashMap<>();

        for(EnchantmentWrapper wrapper : EnchantmentWrappers.list(false)) {
            if(wrapper.has(stack)) {
                enchantments.put(wrapper, wrapper.getLevel(stack));
            }
        }

        return enchantments;
    }

    public static Map<CustomEnchantment, Integer> getCustomEnchantments(ItemStack stack) {
        if(stack == null || !stack.hasItemMeta()) {
            return new HashMap<>();
        }

        PersistentDataContainer enchantments = stack.getItemMeta().getPersistentDataContainer()
                .get(key, PersistentDataType.TAG_CONTAINER);

        Map<CustomEnchantment, Integer> enchantmentLevels = new HashMap<>();

        if(enchantments != null) {
            CustomEnchantments.list().forEach((customEnchantment) -> {
                if(enchantments.has(customEnchantment.getKey(), PersistentDataType.INTEGER)) {
                    enchantmentLevels.put(customEnchantment, enchantments.get(customEnchantment.getKey(), PersistentDataType.INTEGER));
                }
            });
        }

        return enchantmentLevels;
    }

    public static ItemStack setCustomEnchantment(ItemStack stack, CustomEnchantment enchantment, int level) {
        if(level == 0)
        {
            return removeCustomEnchantment(stack, enchantment);
        }

        ItemStack result = stack.clone();

        if(!canEnchantItem(result)) {
            return result;
        }

        ItemMeta meta = result.getItemMeta();

        PersistentDataContainer container = meta.getPersistentDataContainer();

        PersistentDataContainer enchantments;

        if(!container.has(key, PersistentDataType.TAG_CONTAINER)) {
            enchantments = container.getAdapterContext().newPersistentDataContainer();
        }
        else {
            enchantments = stack.getItemMeta().getPersistentDataContainer()
                    .get(key, PersistentDataType.TAG_CONTAINER);
        }

        assert enchantments != null;
        enchantments.set(enchantment.getKey(), PersistentDataType.INTEGER, level);
        container.set(key, PersistentDataType.TAG_CONTAINER, enchantments);

        result.setItemMeta(meta);

        return ItemChangeProviders.update(result);
    }

    public static ItemStack removeCustomEnchantment(ItemStack stack, CustomEnchantment enchantment) {
        ItemStack result = stack.clone();

        if(!canEnchantItem(result)) {
            return result;
        }

        ItemMeta meta = result.getItemMeta();

        PersistentDataContainer container = meta.getPersistentDataContainer();

        PersistentDataContainer enchantments;

        if(!container.has(key, PersistentDataType.TAG_CONTAINER)) {
            return result;
        }
        else {
            enchantments = stack.getItemMeta().getPersistentDataContainer()
                    .get(key, PersistentDataType.TAG_CONTAINER);
        }

        assert enchantments != null;
        enchantments.remove(enchantment.getKey());

        if(enchantments.isEmpty())
        {
            container.remove(key);
        }
        else
        {
            container.set(key, PersistentDataType.TAG_CONTAINER, enchantments);
        }

        result.setItemMeta(meta);

        return ItemChangeProviders.update(result);
    }

    public static int getCustomEnchantmentLevel(ItemStack stack, CustomEnchantment enchantment) {
        return getCustomEnchantments(stack).getOrDefault(enchantment, 0);
    }

    public static boolean hasCustomEnchantment(ItemStack stack, CustomEnchantment enchantment) {
        return getCustomEnchantmentLevel(stack, enchantment) != 0;
    }

    public static boolean canEnchantItem(ItemStack stack) {
        if(!CustomItem.isCustomItem(stack)) {
            if(stack.getType() == Material.ENCHANTED_BOOK
                    || stack.getType() == Material.BOOK) {
                return true;
            }
        }

        return ItemPredicates.enchantability.matchItem(stack);
    }

    public static ItemStack enchantTool(ItemStack stack, EnchantmentWrapper enchantment, int level) {
        ItemStack result = stack.clone();

        if(!canEnchantItem(result)) {
            return result;
        }

        if(enchantment.canEnchantItem(result)) {
            int current = enchantment.getLevel(result);

            if(current > level) {
                level = current;
            }
            if(level > enchantment.getMaxLevel()) {
                level = enchantment.getMaxLevel();
            }
            if(level < 0) {
                level = 0;
            }

            return enchantment.apply(result, level);
        }

        return result;
    }

    public static Pair<CustomEnchantment, Integer> getCustomEnchantmentEntryByIndex(ItemStack stack, int index) {
        index--;

        ItemStack result = stack.clone();

        if(!canEnchantItem(result)) {
            return null;
        }

        Map<CustomEnchantment, Integer> enchantmentLevels = getCustomEnchantments(result);

        for(Map.Entry<CustomEnchantment, Integer> enchantment : enchantmentLevels.entrySet()) {
            if(index == 0) {
                return new Pair<>(enchantment.getKey(), enchantment.getValue());
            }
            if(!enchantment.getKey().getEnchantmentType().canBeRemoved()) {
                continue;
            }
            index--;
        }

        return null;
    }

    public static Pair<Enchantment, Integer> getEnchantmentEntryByIndex(ItemStack stack, int index) {
        index--;

        ItemStack result = stack.clone();

        if(!canEnchantItem(result)) {
            return null;
        }

        Map<Enchantment, Integer> enchantmentLevels;

        if(stack.getItemMeta() instanceof EnchantmentStorageMeta meta) {
            enchantmentLevels = meta.getStoredEnchants();
        }
        else {
            enchantmentLevels = result.getEnchantments();
        }

        for(Enchantment enchantment : EnchantmentOrder.order()) {
            if(enchantment.isCursed()) {
                continue;
            }
            if(!enchantmentLevels.containsKey(enchantment)) {
                continue;
            }
            if(index == 0) {
                return new Pair<>(enchantment, enchantmentLevels.get(enchantment));
            }
            index--;
        }

        return null;
    }

    public static ItemStack enchantFromItem(ItemStack in, ItemStack enchantingItem) {
        ItemStack result = in.clone();

        if(!canEnchantItem(result)) {
            return result;
        }

        Map<EnchantmentWrapper, Integer> enchantmentLevels = getEnchantmentWrappers(enchantingItem);

        for(Map.Entry<EnchantmentWrapper, Integer> enchantment : enchantmentLevels.entrySet()) {
            result = enchantTool(result, enchantment.getKey(), enchantment.getValue());
        }

        return ItemChangeProviders.update(result);
    }

    public static boolean hasCustomEnchantments(ItemStack stack) {
        return !getCustomEnchantments(stack).isEmpty();
    }

    public static ItemStack removeEnchantments(ItemStack stack) {
        ItemStack result = stack.clone();

        if(!canEnchantItem(result)) {
            return result;
        }

        Set<EnchantmentWrapper> enchantments = getEnchantmentWrappers(stack).keySet();

        for(EnchantmentWrapper enchantment : enchantments) {
            if(enchantment.canBeRemoved()) {
                result = enchantment.remove(result);
            }
        }

        return ItemChangeProviders.update(result);
    }

    // This version of the getCustomEnchantmentLevel function ignores enchanted books
    public static int getActiveEnchantmentWrapperLevel(ItemStack stack, EnchantmentWrapper enchantment) {
        if(stack != null && stack.getType() == Material.ENCHANTED_BOOK) {
            return 0;
        }
        return enchantment.getLevel(stack);
    }

    // This version of the hasCustomEnchantment function ignores enchanted books
    public static boolean hasActiveEnchantmentWrapper(ItemStack stack, EnchantmentWrapper enchantment) {
        return getActiveEnchantmentWrapperLevel(stack, enchantment) > 0;
    }

    public static Map<CustomEnchantment, Integer> getActiveEnchantmentWrappers(ItemStack stack) {
        if(stack != null && stack.getType() == Material.ENCHANTED_BOOK) {
            return new HashMap<>();
        }
        return getCustomEnchantments(stack);
    }

    @Override
    public List<Component> getLore(ItemStack stack) {
        if(!canEnchantItem(stack)) {
            return List.of();
        }

        List<Component> lore = new ArrayList<>();

        getCustomEnchantments(stack).forEach((enchantment, level) -> lore.add(enchantment.getComponent(level)));

        return lore;
    }

    @Override
    public ItemStack update(ItemStack stack) {
        ItemStack result = stack.clone();

        if(!canEnchantItem(result)) {
            return result;
        }

        ItemMeta meta = result.getItemMeta();

        meta.setEnchantmentGlintOverride(null);

        if(getCustomEnchantments(stack).isEmpty()) {
            if(stack.getType() == Material.ENCHANTED_BOOK && meta instanceof EnchantmentStorageMeta esm)
            {
                if(!esm.hasStoredEnchants()) {
                    result = result.withType(Material.BOOK);
                    meta = result.getItemMeta();
                }
            }
        }
        else {
            if(result.getType() != Material.BOOK && result.getType() != Material.ENCHANTED_BOOK)
            {
                if(!meta.hasEnchants()) {
                    meta.setEnchantmentGlintOverride(true);
                }
            }
            else if(result.getType() == Material.BOOK)
            {
                result = result.withType(Material.ENCHANTED_BOOK);
                meta = result.getItemMeta();
            }

            if(CustomEnchantments.UNSHINY.has(stack)) {
                meta.setEnchantmentGlintOverride(false);
            }
        }

        result.setItemMeta(meta);

        return result;
    }
}
