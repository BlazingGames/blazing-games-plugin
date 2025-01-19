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
package de.blazemcworld.blazinggames.userinterfaces;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.utils.NamespacedKeyDataType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StaticUserInterfaceSlot implements UserInterfaceSlot {
    public static StaticUserInterfaceSlot blank = new StaticUserInterfaceSlot(BlazingGames.get().key("blank"));

    private final NamespacedKey model;

    public StaticUserInterfaceSlot(NamespacedKey model) {
        this.model = model;
    }

    @Override
    public void onUpdate(UserInterface inventory, int slot) {
        ItemStack stack = new ItemStack(Material.STRUCTURE_BLOCK);
        ItemMeta meta = stack.getItemMeta();
        meta.setItemModel(model);
        meta.getPersistentDataContainer().set(UserInterface.guiKey, NamespacedKeyDataType.instance, model);
        meta.setHideTooltip(true);
        stack.setItemMeta(meta);

        inventory.setItem(slot, stack);
    }
}
