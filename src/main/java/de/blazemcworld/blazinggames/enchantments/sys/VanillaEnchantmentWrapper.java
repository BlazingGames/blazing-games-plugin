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
import de.blazemcworld.blazinggames.items.CustomItem;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class VanillaEnchantmentWrapper implements EnchantmentWrapper {
    public record Warning(String message, int enchantmentLevel) {
    }

    private final Enchantment enchantment;
    private final NamespacedKey model;
    private final List<AltarRecipe> recipes;
    private final List<Warning> warnings;

    public VanillaEnchantmentWrapper(Enchantment enchantment, NamespacedKey model, List<Warning> warnings, AltarRecipe... recipes) {
        this.enchantment = enchantment;
        this.model = model;
        this.recipes = List.of(recipes);
        this.warnings = warnings;
    }

    public VanillaEnchantmentWrapper(Enchantment enchantment, NamespacedKey model, AltarRecipe... recipes) {
        this.enchantment = enchantment;
        this.model = model;
        this.recipes = List.of(recipes);
        this.warnings = List.of();
    }

    public VanillaEnchantmentWrapper(Enchantment enchantment, Material model, List<Warning> warnings, AltarRecipe... recipes) {
        this.enchantment = enchantment;
        this.model = model.getKey();
        this.recipes = List.of(recipes);
        this.warnings = warnings;
    }

    public VanillaEnchantmentWrapper(Enchantment enchantment, Material model, AltarRecipe... recipes) {
        this.enchantment = enchantment;
        this.model = model.getKey();
        this.recipes = List.of(recipes);
        this.warnings = List.of();
    }
    @Override
    public ItemStack apply(ItemStack tool, int level) {
        ItemStack result = tool.clone();

        if(!CustomItem.isCustomItem(result) && result.getType() == Material.BOOK) {
            result = result.withType(Material.ENCHANTED_BOOK);
        }

        result.addUnsafeEnchantment(enchantment, level);

        return result;
    }

    @Override
    public int getLevel(ItemStack tool) {
        if(tool.hasData(DataComponentTypes.STORED_ENCHANTMENTS)) {
            return tool.getData(DataComponentTypes.STORED_ENCHANTMENTS).enchantments().getOrDefault(enchantment, 0);
        }

        return tool.getEnchantmentLevel(enchantment);
    }

    @Override
    public boolean canGoOnItem(ItemStack tool) {
        if(CustomItem.isCustomItem(tool)) {
            return false;
        }

        return enchantment.canEnchantItem(tool) || tool.getType() == Material.BOOK
                || tool.getType() == Material.ENCHANTED_BOOK;
    }

    @Override
    public boolean conflictsWith(Enchantment other) {
        if(other == enchantment) return false;

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
        return warnings.stream().filter(warning -> warning.enchantmentLevel == level).findFirst().map(Warning::message).orElse(null);
    }

    @Override
    public NamespacedKey getModel() {
        return model;
    }

    @Override
    public Component getDescription() {
        TextColor color = NamedTextColor.GRAY;

        if(enchantment.isCursed()) {
            color = NamedTextColor.RED;
        }

        return enchantment.description().color(color).decoration(TextDecoration.ITALIC, false);
    }

    @Override
    public boolean isTreasure() {
        return enchantment.isTreasure();
    }

    @Override
    public List<AltarRecipe> getRecipes() {
        return recipes;
    }

    @Override
    public boolean canBeRemoved() {
        return enchantment.isCursed();
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }
}
