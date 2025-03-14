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

package de.blazemcworld.blazinggames.events.handlers.villagers;

import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.items.CustomItems;
import org.bukkit.Material;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.List;
import java.util.Random;

public class LibrarianAcquireTradeHandler extends BlazingEventHandler<VillagerAcquireTradeEvent> {
    private final List<Material> bannerPatterns = List.of(
            Material.CREEPER_BANNER_PATTERN,
            Material.FLOWER_BANNER_PATTERN,
            Material.SKULL_BANNER_PATTERN,
            Material.MOJANG_BANNER_PATTERN
    );

    @Override
    public boolean fitCriteria(VillagerAcquireTradeEvent event, boolean cancelled) {
        AbstractVillager av = event.getEntity();
        MerchantRecipe recipe = event.getRecipe();
        if (av instanceof Villager villager) {
            if (recipe.getResult().getType() == Material.ENCHANTED_BOOK) {
                return villager.getProfession() == Villager.Profession.LIBRARIAN;
            }
        }
        return false;
    }

    @Override
    public void execute(VillagerAcquireTradeEvent event) {
        Villager villager = (Villager) event.getEntity();
        Random random = new Random();
        event.setRecipe(switch (villager.getVillagerLevel()) {
            case 2 ->
                    createRecipe(new ItemStack(Material.GLOW_INK_SAC), new ItemStack(Material.EMERALD, 3), 12, 5, 0.05f);
            case 3 -> createRecipe(randomBannerPattern(random), new ItemStack(Material.EMERALD, 5), 12, 10, 0.2f);
            case 4 ->
                    createRecipe(CustomItems.GREED_TOME.create(), new ItemStack(Material.EMERALD, random.nextInt(10, 39)), 12, 15, 0.2f);
            default ->
                    createRecipe(new ItemStack(Material.INK_SAC, 5), new ItemStack(Material.EMERALD, 1), 12, 2, 0.05f);
        });
    }

    private MerchantRecipe createRecipe(ItemStack result, ItemStack ingredient, int maxUses, int villagerExperience, float priceMultiplier) {
        MerchantRecipe recipe = new MerchantRecipe(result, 0, maxUses, true, villagerExperience, priceMultiplier);
        recipe.addIngredient(ingredient);
        return recipe;
    }

    private ItemStack randomBannerPattern(Random random) {
        int index = random.nextInt(bannerPatterns.size());
        return new ItemStack(bannerPatterns.get(index));
    }
}
