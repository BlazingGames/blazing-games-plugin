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

package de.blazemcworld.blazinggames.events.handlers.chiseled_bookshelves;

import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentHelper;
import de.blazemcworld.blazinggames.enchantments.sys.VanillaEnchantmentWrappers;
import de.blazemcworld.blazinggames.events.BlazingBlockDropEvent;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ChiseledBookshelfDropHandler extends BlazingEventHandler<BlazingBlockDropEvent> {
    @Override
    public boolean fitCriteria(BlazingBlockDropEvent event, boolean cancelled) {
        return event.getBlock().getType() == Material.CHISELED_BOOKSHELF &&
                !EnchantmentHelper.hasActiveEnchantmentWrapper(event.getTool(), VanillaEnchantmentWrappers.SILK_TOUCH);
    }

    @Override
    public void execute(BlazingBlockDropEvent event) {
        event.getDrops().add(new ItemStack(Material.CHISELED_BOOKSHELF));
    }
}
