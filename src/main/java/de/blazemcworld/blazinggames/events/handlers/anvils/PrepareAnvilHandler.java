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

package de.blazemcworld.blazinggames.events.handlers.anvils;

import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentHelper;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.items.CustomItem;
import de.blazemcworld.blazinggames.utils.ItemUtils;
import de.blazemcworld.blazinggames.utils.Pair;
import de.blazemcworld.blazinggames.utils.TextUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Material;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

public class PrepareAnvilHandler extends BlazingEventHandler<PrepareAnvilEvent> {
    @Override
    public boolean fitCriteria(PrepareAnvilEvent event, boolean cancelled) {
        return true;
    }

    @Override
    public void execute(PrepareAnvilEvent event) {
        event.getView().setMaximumRepairCost(Integer.MAX_VALUE);
        if (event.getView().getRepairCost() > 10) {
            event.getView().setRepairCost(10);
        }

        ItemStack in = event.getInventory().getFirstItem();
        ItemStack enchantingItem = event.getInventory().getSecondItem();

        if (in == null || in.isEmpty()) {
            event.setResult(null);
            return;
        }

        ItemStack result = in.clone();

        event.getView().setRepairItemCountCost(0);

        int repairCost = result.getDataOrDefault(DataComponentTypes.REPAIR_COST, 0);
        int minRepairCost = repairCost;

        boolean increaseRepairCost = false;

        if (enchantingItem != null && !enchantingItem.isEmpty()) {
            repairCost += enchantingItem.getDataOrDefault(DataComponentTypes.REPAIR_COST, 0);
            if (ItemUtils.canRepairTool(result, enchantingItem)) {
                if (result.hasData(DataComponentTypes.MAX_DAMAGE) &&
                        result.hasData(DataComponentTypes.DAMAGE) &&
                        !result.hasData(DataComponentTypes.UNBREAKABLE)) {
                    int damage = result.getData(DataComponentTypes.DAMAGE);
                    int maxDamage = result.getData(DataComponentTypes.MAX_DAMAGE);

                    int damageReduced = maxDamage / 4;

                    int count = 0;
                    while (count * damageReduced < damage) {
                        if (count >= enchantingItem.getAmount()) {
                            break;
                        }
                        count++;
                    }

                    damage -= count * damageReduced;
                    if (damage < 0) damage = 0;

                    result.setData(DataComponentTypes.DAMAGE, damage);

                    event.getView().setRepairItemCountCost(count);
                    repairCost += count;
                    increaseRepairCost = true;
                }
            } else if (CustomItem.isCustomItem(enchantingItem)) {
                if (EnchantmentHelper.canEnchantItem(result) && result.getAmount() == 1) {
                    CustomItem<?> item = CustomItem.getCustomItem(enchantingItem);

                    if (item != null && item.matchItem(result)) {
                        Pair<ItemStack, Integer> enchantResult = EnchantmentHelper.enchantFromItem(result, enchantingItem);
                        result = enchantResult.left;
                        repairCost += repairByCombination(result, enchantingItem, 12) + enchantResult.right;
                        increaseRepairCost = true;

                        minRepairCost = Math.min(minRepairCost, enchantingItem.getDataOrDefault(DataComponentTypes.REPAIR_COST, 0));
                    }
                }
            } else if (EnchantmentHelper.canEnchantItem(result) && result.getAmount() == 1) {
                if (enchantingItem.getType() == Material.ENCHANTED_BOOK) {
                    Pair<ItemStack, Integer> enchantResult = EnchantmentHelper.enchantFromItem(result, enchantingItem);
                    result = enchantResult.left;
                    repairCost += enchantResult.right;
                    increaseRepairCost = true;

                    minRepairCost = Math.min(minRepairCost, enchantingItem.getDataOrDefault(DataComponentTypes.REPAIR_COST, 0));
                } else if (!CustomItem.isCustomItem(result) && enchantingItem.getType() == result.getType()) {
                    Pair<ItemStack, Integer> enchantResult = EnchantmentHelper.enchantFromItem(result, enchantingItem);
                    result = enchantResult.left;
                    repairCost += repairByCombination(result, enchantingItem, 12) + enchantResult.right;
                    increaseRepairCost = true;

                    minRepairCost = Math.min(minRepairCost, enchantingItem.getDataOrDefault(DataComponentTypes.REPAIR_COST, 0));
                }
            }
        }

        String renameText = event.getView().getRenameText();

        if (renameText == null || renameText.isBlank()) {
            if (result.hasData(DataComponentTypes.CUSTOM_NAME)) {
                repairCost++;
                result.resetData(DataComponentTypes.CUSTOM_NAME);
            }
        } else {
            if (!TextUtils.componentToAmpersandString(result.displayName()).equals(renameText)) {
                repairCost++;
                result.setData(DataComponentTypes.CUSTOM_NAME, TextUtils.ampersandStringToComponent(renameText));
            }
        }

        if (result.equals(in)) {
            event.setResult(null);
            return;
        }

        if (increaseRepairCost) {
            minRepairCost *= 2;
            minRepairCost++;
            result.setData(DataComponentTypes.REPAIR_COST, minRepairCost);
        }

        if (repairCost > 10) repairCost = 10;

        event.setResult(result);
        event.getView().setRepairCost(repairCost);
    }

    public static int repairByCombination(ItemStack in, ItemStack repairer, int extraPercentage) {
        if (!in.hasData(DataComponentTypes.MAX_DAMAGE) ||
                !in.hasData(DataComponentTypes.DAMAGE) ||
                in.hasData(DataComponentTypes.UNBREAKABLE)) {
            return 0;
        }

        int inDamage = in.getDataOrDefault(DataComponentTypes.DAMAGE, 0);
        int repairerDamage = repairer.getDataOrDefault(DataComponentTypes.DAMAGE, 0);

        int inMaxDamage = in.getDataOrDefault(DataComponentTypes.MAX_DAMAGE, 0);
        int repairerMaxDamage = repairer.getDataOrDefault(DataComponentTypes.MAX_DAMAGE, 0);

        int inDurability = inMaxDamage - inDamage;
        int repairerDurability = repairerMaxDamage - repairerDamage;

        int extraDurability = inMaxDamage * extraPercentage / 100;

        int totalDurability = inDurability + repairerDurability + extraDurability;
        int newDamage = inMaxDamage - totalDurability;

        if (newDamage < 0) newDamage = 0;
        if (newDamage > inMaxDamage) newDamage = inMaxDamage;

        in.setData(DataComponentTypes.DAMAGE, newDamage);

        return 2;
    }
}
