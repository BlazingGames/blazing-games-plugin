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
package de.blazemcworld.blazinggames.computing.types;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import de.blazemcworld.blazinggames.computing.BootedComputer;
import de.blazemcworld.blazinggames.computing.functions.JSFunctionalClass;
import de.blazemcworld.blazinggames.computing.motor.HeadComputerMotor;
import de.blazemcworld.blazinggames.computing.motor.IComputerMotor;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.SkullMeta;

public class ConsoleCT implements IComputerType {
    public static final String MINESKIN_USERNAME = "Computer";
    public static final UUID MINESKIN_UUID = UUID.fromString("ea238963-0dbe-45dd-b5a3-6c44da1e57c4");
    public static final String MINESKIN_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBmMTBlODU0MThlMzM0ZjgyNjczZWI0OTQwYjIwOGVjYWVlMGM5NWMyODc2ODVlOWVhZjI0NzUxYTMxNWJmYSJ9fX0=";

    public static PlayerProfile makeProfile() {
        PlayerProfile profile = Bukkit.createProfile(MINESKIN_UUID, MINESKIN_USERNAME);
        profile.setProperty(new ProfileProperty("textures", MINESKIN_TEXTURE));
        return profile;
    }

    @Override
    public ItemStack getDisplayItem(BootedComputer computer) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta)item.getItemMeta();
        meta.setPlayerProfile(makeProfile());
        meta.displayName(((TextComponent)Component.text("Console").color(NamedTextColor.WHITE)).decoration(TextDecoration.ITALIC, false));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public CraftingRecipe getRecipe(NamespacedKey key, ItemStack result) {
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape(new String[]{"III", "IRI", "III"});
        recipe.setIngredient('I', Material.IRON_INGOT);
        recipe.setIngredient('R', Material.REDSTONE_BLOCK);
        return recipe;
    }

    @Override
    public IComputerMotor getMotor() {
        return new HeadComputerMotor(makeProfile());
    }

    @Override
    public JSFunctionalClass[] getFunctions(BootedComputer computer) {
        return new JSFunctionalClass[0];
    }

    @Override
    public String[] getDefaultUpgrades() {
        return new String[0];
    }
}
