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

import de.blazemcworld.blazinggames.items.ContextlessItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EnchantmentTome extends ContextlessItem {
    private final NamespacedKey tomeKey;
    private final String tomeName;
    private final EnchantmentWrapper wrapper;

    public EnchantmentTome(NamespacedKey tomeKey, String tomeName, EnchantmentWrapper wrapper) {
        this.tomeKey = tomeKey;
        this.tomeName = tomeName;
        this.wrapper = wrapper;
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return tomeKey;
    }

    @Override
    protected @NotNull Component itemName() {
        return Component.text(tomeName).color(NamedTextColor.LIGHT_PURPLE);
    }

    @Override
    public @NotNull List<Component> lore(ItemStack stack) {
        return List.of(getComponent());
    }

    protected Component getComponent() {
        return getWrapper().getLevelessComponent();
    }
    public EnchantmentWrapper getWrapper() {
        return wrapper;
    }
}
