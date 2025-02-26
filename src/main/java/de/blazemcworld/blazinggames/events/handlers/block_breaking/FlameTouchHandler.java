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

package de.blazemcworld.blazinggames.events.handlers.block_breaking;

import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantments;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentHelper;
import de.blazemcworld.blazinggames.events.BlazingBlockDropEvent;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.items.recipes.RecipeHelper;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FlameTouchHandler extends BlazingEventHandler<BlazingBlockDropEvent> {
    @Override
    public boolean fitCriteria(BlazingBlockDropEvent event) {
        return EnchantmentHelper.hasActiveEnchantmentWrapper(event.getTool(), CustomEnchantments.FLAME_TOUCH);
    }

    @Override
    public void execute(BlazingBlockDropEvent event) {
        Iterator<ItemStack> iter = event.getDrops().iterator();
        List<ItemStack> smeltedDrops = new ArrayList<>();

        while(iter.hasNext()) {
            ItemStack stack = iter.next();

            ItemStack smelted = RecipeHelper.smeltItem(stack);

            if(smelted != null) {
                iter.remove();
                smeltedDrops.add(smelted);
            }
            else if(stack.getType().isFuel()) {
                iter.remove();
            }
        }

        event.getDrops().addAll(smeltedDrops);
    }
}
