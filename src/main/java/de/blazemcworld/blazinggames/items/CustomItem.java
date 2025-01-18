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
package de.blazemcworld.blazinggames.items;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.utils.NamespacedKeyDataType;
import net.kyori.adventure.text.Component;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.UseCooldownComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class CustomItem implements RecipeProvider, Keyed, ItemPredicate {
    private static final NamespacedKey key = BlazingGames.get().key("custom_item");

    // returns null if not a custom item
    public static @Nullable CustomItem getCustomItem(ItemStack stack) {
        if(stack == null || !stack.hasItemMeta()) {
            return null;
        }

        if(!stack.getItemMeta().getPersistentDataContainer().has(key, NamespacedKeyDataType.instance)) {
            return null;
        }

        try {
            return CustomItems.getByKey(stack.getItemMeta().getPersistentDataContainer().get(key, NamespacedKeyDataType.instance));
        }
        catch(Exception err) {
            BlazingGames.get().log(err);
            return null;
        }
    }

    public static boolean isCustomItem(ItemStack stack) {
        if(stack == null) return false;

        if(!stack.hasItemMeta()) {
            return false;
        }

        return stack.getItemMeta().getPersistentDataContainer().has(key, NamespacedKeyDataType.instance);
    }

    public abstract @NotNull NamespacedKey getKey();

    public final @NotNull ItemStack create() {
        ItemStack result = material();

        ItemMeta meta = result.getItemMeta();
        meta.getPersistentDataContainer().set(key, NamespacedKeyDataType.instance, getKey());

        UseCooldownComponent cooldown = meta.getUseCooldown();
        cooldown.setCooldownGroup(getKey());
        meta.setUseCooldown(cooldown);

        result.setItemMeta(meta);

        return modifyMaterial(result);
    }

    @Override
    public final boolean matchItem(ItemStack stack) {
        CustomItem other = getCustomItem(stack);

        if(other == null) return false;

        return other.getKey().equals(getKey());
    }

    @Override
    public final Component getDescription() {
        ItemStack item = create();

        Component name = Component.translatable(item.translationKey());
        ItemMeta reqMeta = item.getItemMeta();

        if(reqMeta != null) {
            if(reqMeta.hasItemName()) {
                name = reqMeta.itemName();
            }

            if(reqMeta.hasDisplayName()) {
                name = reqMeta.displayName();
            }
        }

        return name;
    }

    // DO NOT CALL THIS METHOD, instead call create() on the item's instance
    // also there's no need to set the "custom_item" item tag because
    // the create() method does it anyway
    // if you want to call a function that requires a custom item stack,
    // do it in modifyMaterial(ItemStack stack)
    protected abstract @NotNull ItemStack material();
    protected @NotNull ItemStack modifyMaterial(ItemStack stack) {
        return stack;
    }
}
