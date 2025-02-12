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
package de.blazemcworld.blazinggames.computing.types.impl;

import de.blazemcworld.blazinggames.computing.BootedComputer;
import de.blazemcworld.blazinggames.computing.functions.JSFunctionalClass;
import de.blazemcworld.blazinggames.computing.types.IComputerType;
import de.blazemcworld.blazinggames.computing.upgrades.UpgradeType;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class ConsoleCT implements IComputerType {
    @Override
    public Component getName() {
        return Component.text("Console").color(TextColor.color(0x1BF97F));
    }

    @Override
    public String getDescription() {
        return "A computer allowing for basic operations without interactions.";
    }

    @Override
    public CraftingRecipe getRecipe(NamespacedKey key, ItemStack result) {
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape(new String[]{"III", "IRI", "III"});
        recipe.setIngredient('I', Material.IRON_INGOT);
        recipe.setIngredient('R', Material.REDSTONE_BLOCK);
        return recipe;
    }

    @Override
    public JSFunctionalClass[] getFunctions(BootedComputer computer) {
        return new JSFunctionalClass[0];
    }

    @Override
    public UpgradeType[] getDefaultUpgrades() {
        return new UpgradeType[0];
    }

    @Override
    public int getUpgradeSlots() {
        return 5;
    }
}
