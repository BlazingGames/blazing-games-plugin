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

import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantments;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentHelper;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import org.bukkit.NamespacedKey;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTables;

import java.util.ArrayList;
import java.util.Random;

public class UnshinyTomeHandler extends BlazingEventHandler<LootGenerateEvent> {
    @Override
    public boolean fitCriteria(LootGenerateEvent event, boolean cancelled) {
        NamespacedKey key = event.getLootTable().getKey();
        return key.equals(LootTables.BASTION_BRIDGE.getKey())
                || key.equals(LootTables.BASTION_OTHER.getKey())
                || key.equals(LootTables.BASTION_TREASURE.getKey())
                || key.equals(LootTables.BASTION_HOGLIN_STABLE.getKey());
    }

    @Override
    public void execute(LootGenerateEvent event) {
        ArrayList<ItemStack> loot = new ArrayList<>(event.getLoot());
        Random random = new Random();
        for (int j = 0; j < loot.size(); j++) {
            if (EnchantmentHelper.canEnchantItem(loot.get(j)) && random.nextInt(20) == 1) {
                loot.set(j, EnchantmentHelper.enchantTool(loot.get(j), CustomEnchantments.UNSHINY, 1));
            }
        }
        event.setLoot(loot);
    }
}
