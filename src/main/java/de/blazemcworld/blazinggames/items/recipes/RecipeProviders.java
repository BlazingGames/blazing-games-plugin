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

package de.blazemcworld.blazinggames.items.recipes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.blazemcworld.blazinggames.items.ItemProviders;
import de.blazemcworld.blazinggames.utils.providers.ValueProviders;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;

import java.util.List;
import java.util.Map;

public class RecipeProviders extends ValueProviders<RecipeWrapper, RecipeProvider> implements RecipeProvider {
    public static RecipeProviders instance = new RecipeProviders();

    private RecipeProviders() {}

    @Override
    public List<RecipeProvider> getProviders() {
        ImmutableList.Builder<RecipeProvider> providers = new ImmutableList.Builder<>();
        providers.addAll(ItemProviders.instance.list());

        providers.add(new SlabsToBlockRecipes());
        providers.add(new NametagRecipe());
        providers.add(new SaddleRecipe());
        providers.add(new HorseArmorRecipes());
        providers.add(new CustomRecipes());

        return providers.build();
    }

    @Override
    public Map<NamespacedKey, Recipe> getRecipes() {
        ImmutableMap.Builder<NamespacedKey, Recipe> map = new ImmutableMap.Builder<>();

        for(RecipeWrapper wrapper : list()) {
            map.put(wrapper.getKey(), wrapper.getRecipe());
        }

        return map.build();
    }
}
