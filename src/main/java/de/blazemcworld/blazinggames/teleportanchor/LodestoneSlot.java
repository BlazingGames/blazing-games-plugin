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

package de.blazemcworld.blazinggames.teleportanchor;

import de.blazemcworld.blazinggames.userinterfaces.UserInterface;
import de.blazemcworld.blazinggames.userinterfaces.UserInterfaceSlot;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LodestoneSlot implements UserInterfaceSlot {
    private final int index;

    public LodestoneSlot(int index) {
        this.index = index;
    }

    @Override
    public void onUpdate(UserInterface inventory, int slot) {
        if(!(inventory instanceof TeleportAnchorInterface tpi)) {
            return;
        }

        Map.Entry<Location, String> lodestone = tpi.getLodestone(index);

        if(lodestone == null) {
            inventory.setItem(slot, ItemStack.empty());
            return;
        }

        ItemStack lodestoneItem = new ItemStack(Material.LODESTONE);

        Location loc = lodestone.getKey();

        lodestoneItem.setData(DataComponentTypes.ITEM_NAME, Component.text(lodestone.getValue()).color(NamedTextColor.AQUA));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("World: " + loc.getWorld().getName()).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ()).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
        lodestoneItem.setData(DataComponentTypes.LORE, ItemLore.lore(lore));

        inventory.setItem(slot, lodestoneItem);
    }

    @Override
    public boolean onClick(UserInterface inventory, ItemStack current, ItemStack cursor, int slot, InventoryAction action, boolean isShiftClick, InventoryClickEvent event) {
        if(!(inventory instanceof TeleportAnchorInterface tpi) || isShiftClick) {
            return false;
        }

        Map.Entry<Location, String> lodestone = tpi.getLodestone(index);

        if(lodestone == null) {
            return false;
        }

        if(lodestone.getKey().getBlock().getType() != Material.LODESTONE || event.isShiftClick()) {
            LodestoneStorage.removeSavedLodestoneForPlayer(tpi.getPlayer().getUniqueId(), lodestone.getKey());
            tpi.getPlayer().playSound(tpi.getPlayer().getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 1, 1);
            tpi.reload();
            return false;
        }

        Location tpLoc = lodestone.getKey().toCenterLocation();
        tpLoc.setY(tpLoc.getY() + 1);
        tpLoc.setPitch(tpi.getPlayer().getLocation().getPitch());
        tpLoc.setYaw(tpi.getPlayer().getLocation().getYaw());
        tpi.getPlayer().teleport(tpLoc);
        tpi.getPlayer().playSound(tpLoc, Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1, 1);
        event.getView().close();

        return false;
    }
}
