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
import de.blazemcworld.blazinggames.items.change.ItemChangeProviders;
import de.blazemcworld.blazinggames.items.contexts.ItemContext;
import de.blazemcworld.blazinggames.items.predicates.ItemPredicate;
import de.blazemcworld.blazinggames.items.recipes.RecipeProvider;
import de.blazemcworld.blazinggames.utils.NamespacedKeyDataType;
import net.kyori.adventure.text.Component;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.UseCooldownComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public abstract class CustomItem<T extends ItemContext> implements RecipeProvider, Keyed, ItemPredicate {
    private static final NamespacedKey key = BlazingGames.get().key("custom_item");

    // returns null if not a custom item
    public static @Nullable CustomItem<?> getCustomItem(ItemStack stack) {
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

    public final @NotNull ItemStack create(T context) {
        ItemStack result = new ItemStack(baseMaterial());

        ItemMeta meta = result.getItemMeta();

        meta.getPersistentDataContainer().set(key, NamespacedKeyDataType.instance, getKey());

        meta.setItemModel(getKey());

        UseCooldownComponent cooldown = meta.getUseCooldown();
        cooldown.setCooldownGroup(getKey());
        meta.setUseCooldown(cooldown);

        meta.itemName(itemName());

        meta.setRarity(ItemRarity.COMMON);

        meta.setMaxStackSize(stackSize());

        result.setItemMeta(meta);

        result = modifyMaterial(result, context);

        return ItemChangeProviders.update(result);
    }

    public ItemStack update(ItemStack stack) {
        return stack.clone();
    }

    @Override
    public final boolean matchItem(ItemStack stack) {
        CustomItem<?> other = getCustomItem(stack);

        if(other == null) return false;

        return other.getKey().equals(getKey());
    }

    @Override
    public final Component getDescription() {
        return itemName();
    }

    // DO NOT CALL THIS METHOD, instead call create() on the item's instance
    // also there's no need to set the "custom_item" item tag because
    // the create() method does it anyway
    protected @NotNull ItemStack modifyMaterial(ItemStack stack, T context) {
        return stack;
    }

    protected @NotNull Material baseMaterial() {
        return Material.STRUCTURE_BLOCK;
    }
    protected abstract @NotNull Component itemName();
    protected int stackSize() {
        return 64;
    }
    public List<Component> lore(ItemStack stack) {
        return List.of();
    }
}
