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
import org.bukkit.damage.DamageType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataContainer;

public class CustomSlabShulkerDeathHandler extends BlazingEventHandler<EntityDeathEvent> {
    @Override
    public boolean fitCriteria(EntityDeathEvent event) {
        LivingEntity victim = event.getEntity();
        PersistentDataContainer container = victim.getPersistentDataContainer();

        return container.has(BlazingGames.get().key("slab")) && container.has(BlazingGames.get().key("slab_type"));
    }

    @Override
    public void execute(EntityDeathEvent event) {
        event.setCancelled(true);
        if (event.getDamageSource().getDamageType() == DamageType.GENERIC_KILL) {
            event.getEntity().remove();
            if (event.getEntity().getVehicle() != null) event.getEntity().getVehicle().remove();
        }
    }
}
