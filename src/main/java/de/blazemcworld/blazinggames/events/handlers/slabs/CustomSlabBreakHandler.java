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

package de.blazemcworld.blazinggames.events.handlers.slabs;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.items.ContextlessItem;
import de.blazemcworld.blazinggames.items.CustomItem;
import de.blazemcworld.blazinggames.items.CustomItems;
import de.blazemcworld.blazinggames.utils.InventoryUtils;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.UUID;

public class CustomSlabBreakHandler extends BlazingEventHandler<EntityDamageByEntityEvent> {
    @Override
    public boolean fitCriteria(EntityDamageByEntityEvent event, boolean cancelled) {
        Entity victim = event.getEntity();
        Entity damager = event.getDamager();

        if (!(victim instanceof Player && damager instanceof Damageable)) {
            PersistentDataContainer container = victim.getPersistentDataContainer();
            return container.has(BlazingGames.get().key("slab")) && container.has(BlazingGames.get().key("slab_type"));
        }
        return false;
    }

    @Override
    public void execute(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        Entity damager = event.getDamager();
        PersistentDataContainer container = victim.getPersistentDataContainer();

        event.setCancelled(true);
        if (!(damager instanceof Player p)) return;
        UUID displayBlockUUID = UUID.fromString(Objects.requireNonNull(container.get(BlazingGames.get().key("slab"), PersistentDataType.STRING)));
        String slabType = container.get(BlazingGames.get().key("slab_type"), PersistentDataType.STRING);
        ItemDisplay displayBlock = (ItemDisplay) p.getWorld().getEntity(displayBlockUUID);

        if (displayBlock == null) return;
        Location blockLocation = victim.getLocation().toCenterLocation();
        Sound breakSound = displayBlock.getItemStack().getType().createBlockData().getSoundGroup().getBreakSound();
        displayBlock.remove();

        double y = victim.getY();
        p.getWorld().getNearbyEntitiesByType(Shulker.class, blockLocation, 0.5).forEach(shulker -> {
            PersistentDataContainer c = shulker.getPersistentDataContainer();
            if (c.has(BlazingGames.get().key("slab")) && c.has(BlazingGames.get().key("slab_type")) && y == shulker.getLocation().getY()) {
                Objects.requireNonNull(shulker.getVehicle()).remove();
                shulker.remove();
            }
        });

        CustomItem<?> slab = CustomItems.getByKey(BlazingGames.get().key(slabType + "_slab"));

        if (slab instanceof ContextlessItem contextlessSlab) {
            if (p.getGameMode() != GameMode.CREATIVE) {
                InventoryUtils.collectableDrop(p, blockLocation, contextlessSlab.create());
            }
        } else {
            return;
        }

        p.getWorld().playSound(blockLocation, breakSound, 1, 1);

        if (p.getWorld().getNearbyEntitiesByType(Shulker.class, blockLocation, 0.5).isEmpty())
            Bukkit.getScheduler().runTask(BlazingGames.get(), () -> p.getWorld().getBlockAt(blockLocation).setType(Material.AIR));
    }
}
