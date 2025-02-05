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

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.computing.ComputerEditor;
import de.blazemcworld.blazinggames.computing.ComputerMetadata;
import de.blazemcworld.blazinggames.items.CustomItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class ComputerItemWrapper extends CustomItem<ComputerItemContext> {
    private static final NamespacedKey computerContext = BlazingGames.get().key("computer_context");

    public final ComputerTypes type;
    public ComputerItemWrapper(ComputerTypes type) {
        this.type = type;
    }

    public ComputerItemContext useContext(ItemStack stack) {
        if (stack.getItemMeta().getPersistentDataContainer().has(computerContext, PersistentDataType.STRING)) {
            return BlazingGames.gson.fromJson(
                stack.getItemMeta().getPersistentDataContainer().get(computerContext, PersistentDataType.STRING),
                ComputerItemContext.class
            );
        }

        return null;
    }

    @Override
    public NamespacedKey getKey() {
        return BlazingGames.get().key(type.name().toLowerCase() + "_computer");
    }

    @Override
    protected Component itemName() {
        return type.getType().getName();
    }

    @Override
    protected Material baseMaterial() {
        return Material.REINFORCED_DEEPSLATE;
    }

    @Override
    public List<Component> lore(ItemStack stack) {
        ComputerItemContext context = useContext(stack);

        if (context == null) {
            return List.of(
                Component.text("buggy computer; please report this issue", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)
            );
        }

        if (context.ulid == null) {
            return List.of(
                Component.text(type.getType().getDescription(), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true),
                Component.text("Place to create a new computer!", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true)
            );
        } else {
            ComputerMetadata metadata = ComputerEditor.getMetadata(context.ulid);
            if (metadata == null) {
                return List.of(
                    Component.text("refrenced computer no longer exists", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)
                );
            }

            OfflinePlayer owner = Bukkit.getOfflinePlayer(metadata.owner);
            String username = owner.getName() == null ? metadata.owner.toString() : owner.getName();

            return List.of(
                Component.text(metadata.name, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false),
                Component.text(type.getType().getDescription(), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true),
                Component.empty(),
                Component.text("Unique ID: %s".formatted(metadata.id), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true),
                Component.text("Owner: %s".formatted(username), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true),
                Component.text(type.name(), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true)
            );
        }
    }

    @Override
    protected ItemStack modifyMaterial(ItemStack stack, ComputerItemContext context) {
        ItemMeta meta = stack.getItemMeta();
        meta.getPersistentDataContainer().set(computerContext, PersistentDataType.STRING, BlazingGames.gson.toJson(context));
        stack.setItemMeta(meta);
        return stack;
    }

    @Override
    public Map<NamespacedKey, Recipe> getRecipes() {
        return Map.of(getKey(), type.getType().getRecipe(
            getKey(), create(ComputerItemContext.defaultContext())
        ));
    }

    @Override
    protected ComputerItemContext parseRawContext(Player player, String raw) throws ParseException {
        if (raw.isBlank()) {
            return ComputerItemContext.defaultContext();
        }
        return BlazingGames.gson.fromJson(raw, ComputerItemContext.class);
    }
}
