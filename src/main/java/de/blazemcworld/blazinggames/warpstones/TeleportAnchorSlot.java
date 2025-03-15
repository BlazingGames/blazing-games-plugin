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

package de.blazemcworld.blazinggames.warpstones;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.userinterfaces.IndexedUserInterfaceSlot;
import de.blazemcworld.blazinggames.userinterfaces.UserInterface;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TeleportAnchorSlot extends IndexedUserInterfaceSlot {
    private static final TextColor titleColor = TextColor.color(0x70E5BC);
    private static final TextColor infoColor = TextColor.color(0xC9BEE0);
    private static final TextColor errorColor = TextColor.color(0xEF4747);
    private static final TextColor lockedColor = TextColor.color(0xEDEA3B);
    private static final TextColor hintColor = TextColor.color(0xA1A3A1);

    private static final TextColor xColor = TextColor.color(0xFC2216);
    private static final TextColor yColor = TextColor.color(0x1EFC16);
    private static final TextColor zColor = TextColor.color(0x16FCED);

    public TeleportAnchorSlot(int index) {
        super(index);
    }

    @Override
    public void onUpdate(UserInterface inventory, int slot) {
        if (!(inventory instanceof TeleportAnchorInterface tpi)) {
            return;
        }

        Map.Entry<Location, WarpstoneOverrideDetails> warpstone = tpi.getWarpstone(getIndex(inventory));
        if (warpstone == null) {
            inventory.setItem(slot, ItemStack.empty());
            return;
        }
        Location location = warpstone.getKey();
        WarpstoneDetails details = WarpstoneStorage.getDetails(warpstone.getKey());
        WarpstoneOverrideDetails overrides = warpstone.getValue();
        if (details == null) details = new WarpstoneDetails();
        
        String name = overrides.name != null ? overrides.name : details.defaultName;
        Key icon = overrides.icon != null ? overrides.icon : details.icon;
        AnchorSlotStatus status = getStatus(location, tpi.getPlayer());
        boolean isOwner = tpi.getPlayer().getUniqueId().equals(details.owner);

        ItemStack item = new ItemStack(Material.STONE);
        item.setData(DataComponentTypes.ITEM_NAME, Component.text(name).color(titleColor).decoration(TextDecoration.STRIKETHROUGH, !status.isOk()).decoration(TextDecoration.ITALIC, false));
        item.setData(DataComponentTypes.ITEM_MODEL, icon);

        List<Component> lore = new ArrayList<>();
        if (!status.isOk()) {
            lore.add(Component.text(status.message, errorColor).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.empty());
        }
        lore.add(Component.text("Position: ", infoColor).append(getFancyLocation(location)).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("Dimension: " + getDimensionName(location), infoColor).decoration(TextDecoration.ITALIC, false));
        if (isOwner) {
            lore.add(Component.text("Placed by you", infoColor).decoration(TextDecoration.ITALIC, false));
            if (details.locked) {
                lore.add(Component.text("Locked (only accessible by you)", lockedColor).decoration(TextDecoration.ITALIC, false));
            }
        }
        lore.add(Component.empty());
        lore.add(Component.text("Hint: Shift+Click to delete", hintColor).decoration(TextDecoration.ITALIC, false));
        if (isOwner) {
            lore.add(Component.text("Hint: Drop item to toggle lock", hintColor).decoration(TextDecoration.ITALIC, false));
        }
        lore.add(Component.text("Hint: Click with an item to change icon", hintColor).decoration(TextDecoration.ITALIC, false));
        item.setData(DataComponentTypes.LORE, ItemLore.lore(lore));

        inventory.setItem(slot, item);
    }

    @Override
    public boolean onClick(UserInterface inventory, ItemStack current, ItemStack cursor, int slot, InventoryAction action, boolean isShiftClick, InventoryClickEvent event) {
        if (!(inventory instanceof TeleportAnchorInterface tpi) || isShiftClick) {
            return false;
        }

        Map.Entry<Location, WarpstoneOverrideDetails> warpstone = tpi.getWarpstone(getIndex(inventory));
        if (warpstone == null) {
            return false;
        }
        Location location = warpstone.getKey();

        if (cursor != null && !cursor.isEmpty()) {
            WarpstoneOverrideDetails overrides = warpstone.getValue();
            if (cursor.hasData(DataComponentTypes.ITEM_MODEL)) {
                overrides.icon = cursor.getData(DataComponentTypes.ITEM_MODEL);
            } else {
                overrides.icon = cursor.getType().getKey();
            }
            WarpstoneStorage.updateOverrideDetails(tpi.getPlayer(), location, overrides);
            tpi.reload();
            return false;
        }

        if (event.isShiftClick()) {
            WarpstoneStorage.forgetWarpstone(tpi.getPlayer(), location);
            tpi.getPlayer().playSound(tpi.getPlayer().getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 1, 1);
            tpi.reload();
            return false;
        }

        if (!getStatus(location, tpi.getPlayer()).isOk()) {
            event.getView().close();
            tpi.getPlayer().sendActionBar(Component.text(getStatus(location, tpi.getPlayer()).message, errorColor));
            return false;
        }

        WarpstoneDetails details = WarpstoneStorage.getDetails(location);

        if (event.getClick() == ClickType.DROP) {
            if (!tpi.getPlayer().getUniqueId().equals(details.owner)) {
                event.getView().close();
                tpi.getPlayer().sendActionBar(Component.text("You are not the owner of this warpstone", errorColor));
                return false;
            }

            WarpstoneStorage.updateWarpstoneLock(location, !details.locked);
            tpi.reload();
            return false;
        }

        Location teleportTarget = location.toCenterLocation();
        teleportTarget.setY(teleportTarget.getY() + 1);
        teleportTarget.setPitch(tpi.getPlayer().getLocation().getPitch());
        teleportTarget.setYaw(tpi.getPlayer().getLocation().getYaw());
        tpi.getPlayer().teleport(teleportTarget);
        tpi.getPlayer().playSound(teleportTarget, Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1, 1);
        event.getView().close();

        return false;
    }

    public static enum AnchorSlotStatus {
        OK(null),
        ERROR_MISSING("The warpstone was broken"),
        ERROR_LOCKED("The warpstone was locked by the owner"),
        ERROR_OBSTRUCTED("The warpstone is obstructed and cannot be used"),
        ERROR_INTERNAL("An internal error occured with this warpstone")
        ;
        public final String message;
        AnchorSlotStatus(String msg) { this.message = msg; }

        public boolean isOk() { return this.message == null; }
    }

    private AnchorSlotStatus getStatus(Location location, Player player) {
        try {
            if (!WarpstoneStorage.isWarpstone(location)) {
                return AnchorSlotStatus.ERROR_MISSING;
            }

            if (!WarpstoneStorage.permissionCheck(location, player)) {
                return AnchorSlotStatus.ERROR_LOCKED;
            }

            if (WarpstoneStorage.isObstructed(location)) {
                return AnchorSlotStatus.ERROR_OBSTRUCTED;
            }
        } catch (Exception e) {
            BlazingGames.get().log(e);
            return AnchorSlotStatus.ERROR_INTERNAL;
        }

        return AnchorSlotStatus.OK;
    }

    private String getDimensionName(Location location) {
        if (location.getWorld().getEnvironment().equals(Environment.NORMAL)) {
            return "Overworld";
        }

        if (location.getWorld().getEnvironment().equals(Environment.NETHER)) {
            return "The Nether";
        }

        if (location.getWorld().getEnvironment().equals(Environment.THE_END)) {
            return "The End";
        }

        return "Unknown";
    }

    private Component getFancyLocation(Location location) {
        return Component.text()
            .append(Component.text(location.getBlockX(), xColor))
            .appendSpace()
            .append(Component.text(location.getBlockY(), yColor))
            .appendSpace()
            .append(Component.text(location.getBlockZ(), zColor))
        .build();
    }
}
