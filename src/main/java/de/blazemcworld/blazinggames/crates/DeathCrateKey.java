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

package de.blazemcworld.blazinggames.crates;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.items.CustomItem;
import de.blazemcworld.blazinggames.items.CustomItems;
import de.blazemcworld.blazinggames.items.contexts.ItemContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DeathCrateKey extends CustomItem<DeathCrateKey.DeathCrateKeyContext> {
    private static final NamespacedKey crateKey = BlazingGames.get().key("death_crate_key");

    @Override
    public @NotNull NamespacedKey getKey() {
        return crateKey;
    }

    @Override
    protected @NotNull Component itemName() {
        return Component.text("Death Crate Key").color(NamedTextColor.DARK_RED);
    }

    @Override
    protected int stackSize() {
        return 1;
    }

    @Override
    protected @NotNull ItemStack modifyMaterial(ItemStack stack, DeathCrateKeyContext context) {
        String crateId = context.crateId();
        Location location = CrateManager.readCrate(crateId).location;

        ItemMeta meta = stack.getItemMeta();

        meta.getPersistentDataContainer().set(crateKey, PersistentDataType.STRING, crateId);
        meta.lore(List.of(
                Component.text("Location: %s, %s, %s in %s".formatted(location.getBlockX(), location.getBlockY(), location.getBlockZ(),
                        location.getWorld().getName())).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true),
                Component.text("ULID: %s".formatted(crateId)).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true),
                Component.empty(),
                Component.text("Unlocks the crate at the location above. Can be used by anyone.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true)
        ));

        stack.setItemMeta(meta);

        return stack;
    }

    public static String getKeyULID(ItemStack item) {
        if(CustomItems.DEATH_CRATE_KEY.matchItem(item))
        {
            return item.getItemMeta().getPersistentDataContainer().get(crateKey, PersistentDataType.STRING);
        }

        return null;
    }

    public record DeathCrateKeyContext(String crateId) implements ItemContext {
    }
}
