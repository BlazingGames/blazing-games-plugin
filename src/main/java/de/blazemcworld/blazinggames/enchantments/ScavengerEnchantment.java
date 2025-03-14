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
import de.blazemcworld.blazinggames.enchantments.sys.CustomTreasureEnchantment;
import de.blazemcworld.blazinggames.enchantments.sys.PaperEnchantmentTarget;
import de.blazemcworld.blazinggames.enchantments.sys.altar.AltarRecipe;
import de.blazemcworld.blazinggames.items.predicates.ItemPredicate;
import de.blazemcworld.blazinggames.items.predicates.MaterialItemPredicate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ScavengerEnchantment extends CustomTreasureEnchantment {
    @Override
    public @NotNull NamespacedKey getKey() {
        return BlazingGames.get().key("scavenger");
    }

    @Override
    public NamespacedKey getModel() {
        return Material.GOLD_INGOT.getKey();
    }

    @Override
    public List<AltarRecipe> getRecipes() {
        return List.of(
                new AltarRecipe(3, 1, 8, 16, new MaterialItemPredicate(Material.GOLD_INGOT)),
                new AltarRecipe(3, 2, 16, 16, new MaterialItemPredicate(Material.DIAMOND)),
                new AltarRecipe(4, 3, 24, 1, new MaterialItemPredicate(Material.NETHERITE_SCRAP))
        );
    }

    @Override
    public @NotNull String getDisplayName() {
        return "Scavenger";
    }

    public ItemPredicate getItemTarget() {
        return PaperEnchantmentTarget.WEAPON;
    }
}
