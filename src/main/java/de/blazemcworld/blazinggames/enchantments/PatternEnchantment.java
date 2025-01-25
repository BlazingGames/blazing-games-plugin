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
package de.blazemcworld.blazinggames.enchantments;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantment;
import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantments;
import de.blazemcworld.blazinggames.enchantments.sys.PaperEnchantmentTarget;
import de.blazemcworld.blazinggames.enchantments.sys.altar.AltarRecipe;
import de.blazemcworld.blazinggames.items.predicates.ItemPredicate;
import de.blazemcworld.blazinggames.items.predicates.MaterialItemPredicate;
import de.blazemcworld.blazinggames.utils.Pair;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PatternEnchantment extends CustomEnchantment {
    public static List<Pair<Integer, Integer>> dimensions = List.of(
            new Pair<>(1,2),
            new Pair<>(2,2),
            new Pair<>(3,3)
    );

    @Override
    public @NotNull NamespacedKey getKey() {
        return BlazingGames.get().key("pattern");
    }

    @Override
    public ItemStack getPreIcon() {
        return new ItemStack(Material.IRON_PICKAXE);
    }

    @Override
    public List<AltarRecipe> getRecipes() {
        return List.of(
                new AltarRecipe(1, 1, 4, new MaterialItemPredicate(Material.WOODEN_PICKAXE)),
                new AltarRecipe(1, 2, 8, new MaterialItemPredicate(Material.STONE_PICKAXE)),
                new AltarRecipe(1, 3, 12, new MaterialItemPredicate(Material.IRON_PICKAXE))
        );
    }

    @Override
    public @NotNull String getDisplayName() {
        return "Pattern";
    }

    public @NotNull String getDisplayLevel(int level) {
        if(level <= 0 || level > dimensions.size()) {
            return super.getDisplayLevel(level);
        }

        return dimensions.get(level-1).left + "x" + dimensions.get(level-1).right;
    }

    public ItemPredicate getItemTarget() {
        return PaperEnchantmentTarget.TOOL;
    }

    public boolean conflictsWith(@NotNull CustomEnchantment enchantment) {
        return enchantment == CustomEnchantments.TREE_FELLER;
    }
}
