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
package de.blazemcworld.blazinggames.items;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import de.blazemcworld.blazinggames.BlazingGames;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class PortableCraftingTable extends CustomItem {
    @Override
    public @NotNull NamespacedKey getKey() {
        return BlazingGames.get().key("portable_crafting_table");
    }

    @Override
    protected @NotNull ItemStack material() {
        int[] array = {-2038345071, 1713324536, -1078486308, 1585568499};
        long mostSignificantBits = ((long) array[0] << 32) | (array[1] & 0xFFFFFFFFL);
        long leastSignificantBits = ((long) array[2] << 32) | (array[3] & 0xFFFFFFFFL);

        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(new UUID(mostSignificantBits, leastSignificantBits), "PortableCrafting");
        profile.setProperty(new ProfileProperty("textures","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmNkYzBmZWI3MDAxZTJjMTBmZDUwNjZlNTAxYjg3ZTNkNjQ3OTMwOTJiODVhNTBjODU2ZDk2MmY4YmU5MmM3OCJ9fX0=", "Lfgwh10EgQOoFIRUYJjsKIDdlcOeXWSDAXi6CESfnb3E4pgr9ddqznPvsuo5d6fDhrpKSE4YblWMe1jv6d2M0vEzu+fkhWmG/NvBjAzOCaQ6j0V1urP3cU41nLbJax7eqdf5NDMh9uoXsmVUjUjwRAk4EotdBae4nTXdDNo47sLh80In6WMa04IA9eXkWC+gpNLfPUFgCf9Gn2RTttDSvyPCB5p7rSSd+03vOswGB4U7F1ttYEm1ih8PFzQQm7BLk3RL+L7chlMQqLyrURsPrH7OSKTrBB+Wxx6Z7pZS0yc8/8oRzEb8I6QdrGi1TANpuC1dorGCK4p6j7Pq6UQmUz0OjawEB0kc30v+CTGRnoDyQhLoKRguomxb4R7pCPHCOptAvNNaoFewOWsWPWlBg2lzRel2icKnjzvYyu6PR3Tj9lhNLUsK7IjV7Jqq6obzHNG/v/8dQL13WQPio8Uctkt/hi6b6QgM++lCdf9DbpgLpYPhID9vmXPuOgpVzBlImfYx67NYFPSb4EykG8sWZ1xoh3+y4/dHz2XMY10Q5IEZrxDuctn0oHlKdxi/R23DhJ9M5FwPYnvJ1Ew17EP03wDs85qcI2m5FQC2RwUvAWqBiQ5Qm+wykuWv7DYaul1Q0rSumuSjEIpCf3RxAozOKQOHploee2ekxPpwpHJltMo="));

        meta.setPlayerProfile(profile);
        meta.itemName(Component.text("Portable Crafting Table").color(NamedTextColor.WHITE));
        item.setItemMeta(meta);

        return item;
    }

    public Map<NamespacedKey, Recipe> getRecipes() {
        ShapelessRecipe portableCraftingTableRecipe = new ShapelessRecipe(getKey(), create());
        portableCraftingTableRecipe.addIngredient(Material.STICK);
        portableCraftingTableRecipe.addIngredient(Material.CRAFTING_TABLE);

        return Map.of(
                getKey(), portableCraftingTableRecipe
        );
    }
}
