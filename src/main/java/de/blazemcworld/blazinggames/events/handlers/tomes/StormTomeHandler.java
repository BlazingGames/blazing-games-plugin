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

import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.items.CustomItems;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.loot.LootTables;

import java.util.ArrayList;

public class StormTomeHandler extends BlazingEventHandler<LootGenerateEvent> {
    @Override
    public boolean fitCriteria(LootGenerateEvent event) {
        NamespacedKey key = event.getLootTable().getKey();
        return key.equals(LootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_RARE.getKey());
    }

    @Override
    public void execute(LootGenerateEvent event) {
        ArrayList<ItemStack> loot = new ArrayList<>(event.getLoot());
        for (int j = 0; j < loot.size(); j++) {
            if (loot.get(j).getItemMeta() instanceof EnchantmentStorageMeta esm) {
                if (esm.hasStoredEnchant(Enchantment.WIND_BURST)) {
                    loot.set(j, CustomItems.STORM_TOME.create());
                }
            }
        }
        event.setLoot(loot);
    }
}
