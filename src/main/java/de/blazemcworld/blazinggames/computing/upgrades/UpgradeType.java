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
package de.blazemcworld.blazinggames.computing.upgrades;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import de.blazemcworld.blazinggames.computing.BootedComputer;
import de.blazemcworld.blazinggames.computing.functions.JSFunctionalClass;
import de.blazemcworld.blazinggames.items.CustomItem;
import de.blazemcworld.blazinggames.utils.AutomaticItemProvider;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public enum UpgradeType {
    ACTION_SPEED("Speed Upgrade", TextColor.color(0xCCDDE2), "Decreases action wait time by 10% per item", false,
        (item, key) -> {
            ShapedRecipe recipe = new ShapedRecipe(key, item);
            recipe.shape(
                    "DRD",
                    "RSR",
                    "DRD"
            );
            recipe.setIngredient('D', Material.DIAMOND);
            recipe.setIngredient('R', Material.REDSTONE);
            recipe.setIngredient('S', Material.SUGAR);
            return recipe;
        }, null),
    
    STORAGE_1("Storage Upgrade Tier I", NamedTextColor.AQUA, "Add 1 row of storage space (when applicable)", true,
        (item, key) -> {
            ShapedRecipe recipe = new ShapedRecipe(key, item);
            recipe.shape(
                    "DID",
                    "IHI",
                    "DID"
            );
            recipe.setIngredient('I', Material.IRON_INGOT);
            recipe.setIngredient('D', Material.DIAMOND);
            recipe.setIngredient('H', Material.HOPPER);
            return recipe;
        }, null),
    
    STORAGE_2("Storage Upgrade Tier II", NamedTextColor.AQUA, "Add 2 rows of storage space (when applicable)", true,
        (item, key) -> {
            ShapedRecipe recipe = new ShapedRecipe(key, item);
            recipe.shape(
                    "DID",
                    "IHI",
                    "DID"
            );
            recipe.setIngredient('I', Material.GOLD_INGOT);
            recipe.setIngredient('D', Material.DIAMOND);
            recipe.setIngredient('H', new UpgradeItem(STORAGE_1).create());
            return recipe;
        }, null),
    
    STORAGE_3("Storage Upgrade Tier III", NamedTextColor.AQUA, "Add 3 rows of storage space (when applicable)", true,
        (item, key) -> {
            ShapedRecipe recipe = new ShapedRecipe(key, item);
            recipe.shape(
                    "DID",
                    "IHI",
                    "DID"
            );
            recipe.setIngredient('I', Material.EMERALD);
            recipe.setIngredient('D', Material.DIAMOND);
            recipe.setIngredient('H', new UpgradeItem(STORAGE_2).create());
            return recipe;
        }, null),

    STORAGE_4("Storage Upgrade Tier IV", NamedTextColor.AQUA, "Add 4 rows of storage space (when applicable)", true,
        (item, key) -> {
            ShapedRecipe recipe = new ShapedRecipe(key, item);
            recipe.shape(
                    "DID",
                    "IHI",
                    "DID"
            );
            recipe.setIngredient('I', Material.AMETHYST_SHARD);
            recipe.setIngredient('D', Material.DIAMOND);
            recipe.setIngredient('H', new UpgradeItem(STORAGE_3).create());
            return recipe;
        }, null),

    STORAGE_5("Storage Upgrade Tier V", NamedTextColor.AQUA, "Add 5 rows of storage space (when applicable)", true,
        (item, key) -> {
            ShapedRecipe recipe = new ShapedRecipe(key, item);
            recipe.shape(
                    "DID",
                    "IHI",
                    "DID"
            );
            recipe.setIngredient('I', Material.NETHERITE_SCRAP);
            recipe.setIngredient('D', Material.DIAMOND);
            recipe.setIngredient('H', new UpgradeItem(STORAGE_4).create());
            return recipe;
        }, null),

    CHUNKLOADER("Chunkloader Upgrade", NamedTextColor.AQUA, "Always load the chunk that the turtle is in.", true,
        (item, key) -> {
            ShapedRecipe recipe = new ShapedRecipe(key, item);
            recipe.shape(
                    "DND",
                    "NWN",
                    "DND"
            );
            recipe.setIngredient('N', Material.NETHERITE_INGOT);
            recipe.setIngredient('D', Material.DIAMOND);
            recipe.setIngredient('W', Material.NETHER_STAR);
            return recipe;
        }, null),

    NETWORKING_BASIC("Networking Upgrade", NamedTextColor.AQUA, "Send network requests across computers.", true,
        (item, key) -> {
            ShapedRecipe recipe = new ShapedRecipe(key, item);
            recipe.shape(
                    "III",
                    "IPI",
                    "III"
            );
            recipe.setIngredient('I', Material.IRON_INGOT);
            recipe.setIngredient('P', Material.ENDER_PEARL);
            return recipe;
        }, null),

    NETWORKING_ADVANCED("Dial-Up Internet Upgrade", NamedTextColor.AQUA, "We're in the future now! (send real HTTP requests at dial-up speeds)", true,
        (item, key) -> {
            ShapedRecipe recipe = new ShapedRecipe(key, item);
            recipe.shape(
                    "DDD",
                    "DID",
                    "DDD"
            );
            recipe.setIngredient('D', Material.DIAMOND);
            recipe.setIngredient('I', Material.IRON_BLOCK);
            return recipe;
        }, null),
    ;

    public final String displayName;
    public final TextColor color;
    public final String description;
    public final boolean unique;
    public final BiFunction<ItemStack, NamespacedKey, Recipe> recipe;
    public final Function<BootedComputer, JSFunctionalClass> functions;
    public final List<UpgradeType> incompatibilities;
    UpgradeType(String displayName, TextColor color, String description, boolean unique, BiFunction<ItemStack, NamespacedKey, Recipe> recipe, Function<BootedComputer, JSFunctionalClass> functions, UpgradeType... incompatibilities) {
        this.displayName = displayName;
        this.color = color;
        this.description = description;
        this.unique = unique;
        this.recipe = recipe;
        this.functions = functions;
        this.incompatibilities = List.of(incompatibilities);
    }

    public static class UpgradeTypeProvider implements AutomaticItemProvider.ItemBuilder<UpgradeType> {
        @Override
        public CustomItem<?> item(UpgradeType enumValue) {
            return new UpgradeItem(enumValue);
        }

        @Override
        public String directoryName() {
            return "upgradeitems";
        }
    }
}