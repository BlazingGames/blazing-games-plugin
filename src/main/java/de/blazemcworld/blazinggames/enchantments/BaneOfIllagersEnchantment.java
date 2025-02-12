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
import de.blazemcworld.blazinggames.enchantments.sys.PaperEnchantmentTarget;
import de.blazemcworld.blazinggames.enchantments.sys.altar.AltarRecipe;
import de.blazemcworld.blazinggames.items.predicates.ItemPredicate;
import de.blazemcworld.blazinggames.items.predicates.MaterialItemPredicate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Illager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BaneOfIllagersEnchantment extends CustomEnchantment {
    @Override
    public @NotNull NamespacedKey getKey() {
        return BlazingGames.get().key("bane_of_illagers");
    }

    @Override
    public @NotNull String getDisplayName() {
        return "Bane of Illagers";
    }

    public ItemPredicate getItemTarget() {
        return PaperEnchantmentTarget.WEAPON;
    }

    public double getDamageIncrease(Entity victim, int level) {
        if(victim instanceof Illager) {
            return level*2.5;
        }
        return 0;
    }

    @Override
    public NamespacedKey getModel() {
        return Material.IRON_AXE.getKey();
    }

    @Override
    public List<AltarRecipe> getRecipes() {
        return List.of(
                new AltarRecipe(1, 1, 1, 1, new MaterialItemPredicate(Material.SADDLE)),
                new AltarRecipe(2, 2, 2, 1, new MaterialItemPredicate(Material.IRON_AXE)),
                new AltarRecipe(3, 3, 3, 16, new MaterialItemPredicate(Material.EMERALD)),
                new AltarRecipe(4, 4, 4, 16, new MaterialItemPredicate(Material.EMERALD_BLOCK)),
                new AltarRecipe(4, 5, 5, 1, new MaterialItemPredicate(Material.TOTEM_OF_UNDYING))
        );
    }
}
