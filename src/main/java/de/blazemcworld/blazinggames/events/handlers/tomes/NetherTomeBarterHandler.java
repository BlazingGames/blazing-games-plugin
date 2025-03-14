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

package de.blazemcworld.blazinggames.events.handlers.tomes;

import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentHelper;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.items.CustomItems;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class NetherTomeBarterHandler extends BlazingEventHandler<PiglinBarterEvent> {
    @Override
    public boolean fitCriteria(PiglinBarterEvent event, boolean cancelled) {
        return true;
    }

    @Override
    public void execute(PiglinBarterEvent event) {
        List<ItemStack> loot = event.getOutcome();

        for (int j = 0; j < loot.size(); j++) {
            if (EnchantmentHelper.hasStoredEnchantment(loot.get(j), Enchantment.SOUL_SPEED)) {
                loot.set(j, CustomItems.NETHER_TOME.create());
            }
        }
    }
}
