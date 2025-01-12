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
package de.blazemcworld.blazinggames.userinterfaces;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.utils.NamespacedKeyDataType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class UserInterface implements InventoryHolder {
    public final static NamespacedKey guiKey = BlazingGames.get().key("gui");
    private final Inventory inventory;
    protected final HashMap<Integer, UserInterfaceSlot> slots = new HashMap<>();
    private final int rows;

    public UserInterface(BlazingGames plugin, Component title, int rows) {
        this.inventory = plugin.getServer().createInventory(this, rows*9, title);
        this.rows = rows;

        preload();
        reload();
    }

    protected abstract void preload();

    public UserInterface(BlazingGames plugin, String title, int rows) {
        this(plugin, Component.text(title), rows);
    }

    @Override
    public final @NotNull Inventory getInventory() {
        return this.inventory;
    }

    protected void reload() {
        for(Map.Entry<Integer, UserInterfaceSlot> slot : slots.entrySet()) {
            slot.getValue().onUpdate(this, slot.getKey());
        }
    }

    // returning false means that the event is cancelled, while true does the opposite
    public boolean onClick(ItemStack clicked, ItemStack cursor, int slot, InventoryAction action, InventoryClickEvent event) {
        boolean result = false;

        if(clicked == null) {
            clicked = ItemStack.empty();
        }

        if(slots.containsKey(slot)) {
            result = slots.get(slot).onClick(this, clicked, cursor, slot, action, false, event);
        }

        Bukkit.getScheduler().runTask(BlazingGames.get(), this::reload);

        return result;
    }

    public boolean onShiftClick(ItemStack clicked, InventoryAction action, InventoryClickEvent event) {
        return switch(action) {
            case MOVE_TO_OTHER_INVENTORY -> {
                for(Map.Entry<Integer, UserInterfaceSlot> entry : slots.entrySet()) {
                    if(entry.getValue() instanceof UsableInterfaceSlot usable) {
                        if(usable.onClick(this, getItem(entry.getKey()), clicked, entry.getKey(), action, true, event)) {
                            break;
                        }
                    }
                }

                Bukkit.getScheduler().runTask(BlazingGames.get(), this::reload);

                yield false;
            }
            case COLLECT_TO_CURSOR -> {
                for(Map.Entry<Integer, UserInterfaceSlot> entry : slots.entrySet()) {
                    if(entry.getValue() instanceof UsableInterfaceSlot usable) {
                        if(usable.onClick(this, getItem(entry.getKey()), clicked, entry.getKey(), action, true, event)) {
                            break;
                        }
                    }
                }

                Inventory playerInventory = event.getWhoClicked().getInventory();
                for (ItemStack stack : playerInventory) {
                    if (clicked.isSimilar(stack)) {
                        int amount = stack.getAmount();
                        if (amount > clicked.getMaxStackSize() - clicked.getAmount()) {
                            amount = clicked.getMaxStackSize() - clicked.getAmount();
                        }
                        clicked.add(amount);
                        stack.subtract(amount);
                    }
                }

                Bukkit.getScheduler().runTask(BlazingGames.get(), this::reload);

                yield false;
            }
            default -> true;
        };
    }

    public final void setItem(int slot, ItemStack stack) {
        inventory.setItem(slot, stack);
    }

    public final void setItem(int x, int y, ItemStack stack) {
        setItem(x+y*9, stack);
    }

    public final void setArea(int x1, int y1, int x2, int y2, ItemStack stack) {
        if (x2 > x1) {
            int temp = x2;
            x2 = x1;
            x1 = temp;
        }
        if (y2 > y1) {
            int temp = y2;
            y2 = y1;
            y1 = temp;
        }

        for (int j = y1; j <= y2; j++) {
            for(int i = x1; i <= x2; i++) {
                setItem(i, j, stack.clone());
            }
        }
    }

    public static ItemStack element(Material material, NamespacedKey elementKey) {
        ItemStack element = new ItemStack(material);

        ItemMeta meta = element.getItemMeta();
        meta.getPersistentDataContainer().set(guiKey, NamespacedKeyDataType.instance, elementKey);
        meta.setHideTooltip(true);
        element.setItemMeta(meta);

        return element;
    }

    public final ItemStack getItem(int slot) {
        ItemStack result = inventory.getItem(slot);
        return result == null ? ItemStack.empty() : result;
    }

    public final ItemStack getItem(int x, int y) {
        return getItem(x+y*9);
    }

    protected final void addSlot(int x, int y, UserInterfaceSlot slot) {
        if(x < 0 || x >= 9) {
            throw new IllegalStateException("Can't have a slot outside bounds!");
        }
        if(y < 0 || y >= rows) {
            throw new IllegalStateException("Can't have a slot outside bounds!");
        }
        slots.put(x+y*9, slot);
    }

    public void onDrag(InventoryDragEvent event) {
        event.setCancelled(true);

        int refund = 0;

        if(event.getCursor() != null) {
            refund = event.getCursor().getAmount();
        }

        for(Map.Entry<Integer, ItemStack> entry : event.getNewItems().entrySet()) {
            Inventory dragInv = event.getWhoClicked().getOpenInventory().getInventory(entry.getKey());

            ItemStack stack = entry.getValue();

            if(dragInv != null) {
                if(dragInv.getHolder() == this)
                {
                    int max = 0;

                    if(slots.containsKey(entry.getKey())) {
                        max = slots.get(entry.getKey()).getDragMax(entry.getValue());

                        if(slots.get(entry.getKey()) instanceof InputSlot i) {
                            if(!i.filterItem(entry.getValue())) {
                                max = 0;
                            }
                        }
                    }

                    if(stack.getAmount() > max) {
                        int overfill = entry.getValue().getAmount() - max;
                        stack.subtract(overfill);
                        refund += overfill;
                    }
                }
            }

            event.getWhoClicked().getOpenInventory().setItem(entry.getKey(), stack);
        }

        int finalRefund = refund;
        Bukkit.getScheduler().runTask(BlazingGames.get(), () -> {
            if(finalRefund > 0) {
                event.getWhoClicked().setItemOnCursor(event.getOldCursor().asQuantity(finalRefund));
            }
            else {
                event.getWhoClicked().setItemOnCursor(null);
            }
            reload();
        });
    }

    public void tick(Player p) {

    }

    public void onClose(Player p) {

    }
}
