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

package de.blazemcworld.blazinggames.events.handlers.vaults;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.items.CustomItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultServerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftVault;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class VaultInteractHandler extends BlazingEventHandler<PlayerInteractEvent> {
    @Override
    public boolean fitCriteria(PlayerInteractEvent event, boolean cancelled) {
        Block block = event.getClickedBlock();
        return block != null && block.getType() == Material.VAULT;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) return;

        Bukkit.getScheduler().runTask(BlazingGames.get(), () -> {
            if (!(block.getState() instanceof CraftVault vault)) return;
            VaultBlockEntity vaultBlockEntity = vault.getTileEntity();
            VaultServerData vaultServerData = vaultBlockEntity.getServerData();
            Method getItemsToEject;
            try {
                getItemsToEject = VaultServerData.class.getDeclaredMethod("getItemsToEject");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            getItemsToEject.setAccessible(true);
            List<ItemStack> items;
            try {
                items = (List<net.minecraft.world.item.ItemStack>) getItemsToEject.invoke(vaultServerData);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            AtomicInteger i = new AtomicInteger();
            List<net.minecraft.world.item.ItemStack> finalItems = new ArrayList<>(items);
            items.forEach(itemStack -> {
                org.bukkit.inventory.ItemStack bukkitItemStack = CraftItemStack.asBukkitCopy(itemStack);
                if (bukkitItemStack.getType() == Material.ENCHANTED_BOOK) {
                    EnchantmentStorageMeta esm = (EnchantmentStorageMeta) bukkitItemStack.getItemMeta();
                    if (esm.hasStoredEnchant(Enchantment.WIND_BURST)) {
                        finalItems.set(i.get(), CraftItemStack.asNMSCopy(CustomItems.STORM_TOME.create()));
                    }
                }
                i.addAndGet(1);
            });
            try {
                Method setItemsToEject = VaultServerData.class.getDeclaredMethod("setItemsToEject", List.class);
                setItemsToEject.setAccessible(true);
                setItemsToEject.invoke(vaultServerData, finalItems);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
