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
import de.blazemcworld.blazinggames.userinterfaces.UserInterface;
import de.blazemcworld.blazinggames.utils.persistentDataTypes.NamespacedKeyDataType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface EnchantmentWrapper extends Keyed {
    ItemStack apply(ItemStack tool, int level);
    int getLevel(ItemStack tool);
    boolean canGoOnItem(ItemStack tool);

    default boolean canEnchantItem(ItemStack tool) {
        if(!this.canGoOnItem(tool)) {
            return false;
        }

        for(Map.Entry<EnchantmentWrapper, Integer> entry : EnchantmentHelper.getEnchantmentWrappers(tool).entrySet()) {
            if(this.conflictsWith(entry.getKey()))
            {
                return false;
            }

            if(entry.getKey().conflictsWith(this))
            {
                return false;
            }
        }

        return true;
    }

    default ItemStack remove(ItemStack tool) {
        return apply(tool, 0);
    }

    default boolean conflictsWith(EnchantmentWrapper wrapper) {
        if(wrapper instanceof VanillaEnchantmentWrapper vanilla) {
            return conflictsWith(vanilla.getEnchantment());
        }
        if(wrapper instanceof CustomEnchantment custom) {
            return conflictsWith(custom);
        }
        return false;
    }

    default boolean conflictsWith(CustomEnchantment enchantment) {
        return false;
    }

    default boolean conflictsWith(Enchantment enchantment) {
        return false;
    }

    Component getComponent(int level);
    Component getDescription();

    default String getWarning(int level) {
        return null;
    }

    NamespacedKey getModel();

    default ItemStack getIcon(ItemStack tool, int lapisAmount, ItemStack material, int tier) {
        ItemStack result = new ItemStack(Material.STRUCTURE_BLOCK);

        int level = getLevel(tool);

        List<Component> lore = new ArrayList<>();

        if(level < getMaxLevel()) {
            AltarRecipe recipe = getRecipe(level + 1);

            if(tier < recipe.tier()) {
                lore.add(Component.text("Can't upgrade any more with this tier of altar!")
                        .color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
            }
            else {
                lore.add(Component.text(level > 1 ? "Cost to upgrade to " : "Cost to acquire ")
                        .append(getComponent(level + 1))
                        .append(Component.text(":"))
                        .color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                );

                Component name = recipe.itemRequirement().getDescription();

                lore.add(Component.text(recipe.expAmount() + " Experience Levels")
                        .color(NamedTextColor.YELLOW)
                        .decoration(TextDecoration.ITALIC, false));
                lore.add(Component.text(recipe.lapisAmount() + "x Lapis Lazuli")
                        .color(lapisAmount >= recipe.lapisAmount() ? NamedTextColor.GREEN : NamedTextColor.RED)
                        .decoration(TextDecoration.ITALIC, false));
                assert name != null;
                lore.add(Component.text(recipe.itemAmount() + "x ")
                        .append(name.color(recipe.matchMaterial(material) ? NamedTextColor.GREEN : NamedTextColor.RED))
                        .color(recipe.matchMaterial(material) ? NamedTextColor.GREEN : NamedTextColor.RED)
                        .decoration(TextDecoration.ITALIC, false));

                if (getWarning(level + 1) != null) {
                    lore.add(Component.empty());
                    lore.add(Component.text("[WARNING]").color(NamedTextColor.DARK_RED).decoration(TextDecoration.ITALIC, false));
                    String[] warnings = getWarning(level + 1).split("\n");
                    for (String line : warnings) {
                        lore.add(Component.text(line).color(NamedTextColor.DARK_RED).decoration(TextDecoration.ITALIC, false));
                    }
                    lore.add(Component.text("[WARNING]").color(NamedTextColor.DARK_RED).decoration(TextDecoration.ITALIC, false));
                }
            }
        }
        else {
            lore.add(Component.text(getMaxLevel() > 1 ? "Max Level Achieved!" : "Acquired!")
                    .color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
        }

        ItemMeta meta = result.getItemMeta();
        meta.getPersistentDataContainer().set(UserInterface.guiKey, NamespacedKeyDataType.instance, getKey());
        meta.setMaxStackSize(getMaxLevel());
        meta.setHideTooltip(false);
        meta.setEnchantmentGlintOverride(level > 0);
        meta.itemName(level > 0 ? getComponent(level) : getDescription());
        meta.lore(lore);
        meta.setItemModel(getModel());
        result.setItemMeta(meta);

        if(level > 0) {
            result.setAmount(level);
        }

        return result;
    }

    boolean isTreasure();

    List<AltarRecipe> getRecipes();
    default AltarRecipe getRecipe(int level) {
        List<AltarRecipe> recipes = getRecipes();

        if(level < 1) return recipes.getFirst();

        if(level > recipes.size()) return recipes.getLast();

        return recipes.get(level - 1);
    }
    default int getMaxLevel() {
        return getRecipes().size();
    }

    default boolean has(ItemStack stack) {
        return getLevel(stack) != 0;
    }

    boolean canBeRemoved();

    default boolean equals(EnchantmentWrapper other) {
        return getKey().equals(other.getKey());
    }
}
