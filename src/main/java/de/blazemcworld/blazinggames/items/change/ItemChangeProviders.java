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

package de.blazemcworld.blazinggames.items.change;

import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentHelper;
import de.blazemcworld.blazinggames.items.CustomItem;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemChangeProviders {
    public static List<ItemChangeProvider> getChangeProviders() {
        return List.of(
            new EnchantmentHelper()
        );
    }

    public static List<Component> getLore(ItemStack stack) {
        ArrayList<Component> lore = new ArrayList<>();

        CustomItem<?> customItem = CustomItem.getCustomItem(stack);
        if(customItem != null) {
            lore.addAll(customItem.lore(stack));
        }

        for(ItemChangeProvider provider : getChangeProviders()) {
            lore.addAll(provider.getLore(stack));
        }

        return lore;
    }

    // returns an updated clone of the provided item stack
    public static ItemStack update(ItemStack stack) {
        ItemStack result = stack.clone();

        CustomItem<?> customItem = CustomItem.getCustomItem(result);
        if(customItem != null) {
            result = customItem.update(result);
        }

        for(ItemChangeProvider provider : getChangeProviders()) {
            result = provider.update(stack);
        }

        List<Component> lore = getLore(stack);

        ItemMeta meta = result.getItemMeta();

        if(!lore.isEmpty()) {
            meta.lore(lore);
        }
        else
        {
            meta.lore(null);
        }

        result.setItemMeta(meta);

        return result;
    }
}
