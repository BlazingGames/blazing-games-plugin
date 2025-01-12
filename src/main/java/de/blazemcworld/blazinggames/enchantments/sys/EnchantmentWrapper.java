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
import de.blazemcworld.blazinggames.utils.NamespacedKeyDataType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public interface EnchantmentWrapper {
    ItemStack apply(ItemStack tool, int level);
    int getLevel(ItemStack tool);
    int getMaxLevel();
    boolean canEnchantItem(ItemStack tool);
    boolean canGoOnItem(ItemStack tool);

    default boolean conflictsWith(EnchantmentWrapper wrapper) {
        if(wrapper instanceof VanillaEnchantmentWrapper vanilla) {
            return conflictsWith(vanilla.getEnchantment());
        }
        if(wrapper instanceof CustomEnchantment custom) {
            conflictsWith(custom);
        }
        return false;
    }

    default boolean conflictsWith(CustomEnchantment enchantment) {
        return false;
    }

    default boolean conflictsWith(Enchantment enchantment) {
        return false;
    }

    NamespacedKey getKey();
    Component getComponent(int level);
    Component getLevelessComponent();

    int maxLevelAvailableInAltar(int altarTier);

    ItemStack getPreIcon();

    default ItemStack getIcon(ItemStack tool, int lapisAmount, ItemStack material, int tier) {
        ItemStack result = getPreIcon();

        int level = getLevel(tool);

        List<Component> lore = new ArrayList<>();

        if(level < getMaxLevel()) {
            if(level >= maxLevelAvailableInAltar(tier)) {
                lore.add(Component.text("Can't upgrade any more with this tier of altar!")
                        .color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
            }
            else {
                lore.add(Component.text(level > 1 ? "Cost to upgrade to " : "Cost to acquire ")
                        .append(getComponent(level + 1))
                        .append(Component.text(":"))
                        .color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                );

                AltarRecipe recipe = getRecipe(level + 1);

                Component name = recipe.itemRequirement().getDescription();

                lore.add(Component.text(recipe.expAmount() + " Experience Levels")
                        .color(NamedTextColor.YELLOW)
                        .decoration(TextDecoration.ITALIC, false));
                lore.add(Component.text(recipe.lapisAmount() + "x Lapis Lazuli")
                        .color(lapisAmount >= recipe.lapisAmount() ? NamedTextColor.GREEN : NamedTextColor.RED)
                        .decoration(TextDecoration.ITALIC, false));
                assert name != null;
                lore.add(Component.text(recipe.itemAmount() + "x ")
                        .append(name)
                        .color(recipe.matchMaterial(material) ? NamedTextColor.GREEN : NamedTextColor.RED)
                        .decoration(TextDecoration.ITALIC, false));
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
        meta.itemName(level > 0 ? getComponent(level) : getLevelessComponent());
        meta.lore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        result.setItemMeta(meta);

        if(level > 0) {
            result.setAmount(level);
        }

        return result;
    }

    boolean isTreasure();

    AltarRecipe getRecipe(int level);
}
