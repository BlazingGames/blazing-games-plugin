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

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;

import org.bukkit.inventory.ItemStack;

public class CrateData {
    public final String id;
    public final UUID owner;
    public final boolean opened;
    public final Location location;
    public final int exp;

    public final ItemStack helmet;
    public final ItemStack chestplate;
    public final ItemStack leggings;
    public final ItemStack boots;
    public final ItemStack offhand;

    public final List<ItemStack> hotbarItems;
    public final List<ItemStack> inventoryItems;

    public CrateData(String id, UUID owner, boolean opened, Location location, int exp,
            ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack offhand,
            List<ItemStack> hotbarItems, List<ItemStack> inventoryItems) {
        this.id = id;
        this.owner = owner;
        this.opened = opened;
        this.location = location;
        this.exp = exp;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.offhand = offhand;
        this.hotbarItems = hotbarItems;
        this.inventoryItems = inventoryItems;
    }
}

