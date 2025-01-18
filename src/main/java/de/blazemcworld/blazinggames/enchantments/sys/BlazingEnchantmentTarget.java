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

import de.blazemcworld.blazinggames.items.CustomItem;
import de.blazemcworld.blazinggames.items.ItemPredicate;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public enum BlazingEnchantmentTarget implements ItemPredicate {
    AXE("Axe", Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE),
    HOE("Hoe", Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE, Material.GOLDEN_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE),
    SHIELD("Shield", Material.SHIELD), ELYTRA("Elytra", Material.ELYTRA),
    WEAPON_TRIDENT("Weapons OR Trident", Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD,
            Material.DIAMOND_SWORD, Material.NETHERITE_SWORD, Material.TRIDENT);

    final Set<Material> allowed;
    final String description;

    BlazingEnchantmentTarget(String description, Material... allowed) {
        this.allowed = Set.of(allowed);
        this.description = description;
    }

    public boolean matchItem(@NotNull Material item) {
        return allowed.contains(item);
    }

    @Override
    public boolean matchItem(ItemStack stack) {
        if(CustomItem.isCustomItem(stack)) return false;

        return matchItem(stack.getType());
    }

    @Override
    public Component getDescription() {
        return Component.text(description);
    }
}
