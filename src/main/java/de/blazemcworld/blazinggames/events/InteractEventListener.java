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
package de.blazemcworld.blazinggames.events;

import de.blazemcworld.blazinggames.builderwand.eventhandlers.BuilderWandCycleHandler;
import de.blazemcworld.blazinggames.builderwand.eventhandlers.BuilderWandPlaceHandler;
import de.blazemcworld.blazinggames.crates.eventhandlers.CrateInteractHandler;
import de.blazemcworld.blazinggames.enchantments.eventhandlers.NaturesBlessingHandler;
import de.blazemcworld.blazinggames.events.base.BlazingEventListener;
import de.blazemcworld.blazinggames.events.handlers.blueprint.BlueprintEnchantingTableInteractHandler;
import de.blazemcworld.blazinggames.events.handlers.bottle.BottleInteractHandler;
import de.blazemcworld.blazinggames.events.handlers.enchanting_table.EnchantingTableInteractHandler;
import de.blazemcworld.blazinggames.events.handlers.firework.FireworkInteractHandler;
import de.blazemcworld.blazinggames.events.handlers.portable_crafting_table.PortableCraftingTableInteractHandler;
import de.blazemcworld.blazinggames.events.handlers.slabs.CustomSlabPlaceBlockHandler;
import de.blazemcworld.blazinggames.events.handlers.spawners.SpawnerInteractionHandler;
import de.blazemcworld.blazinggames.events.handlers.tome_altars.TomeAltarBreakHandler;
import de.blazemcworld.blazinggames.events.handlers.tome_altars.TomeAltarInteractHandler;
import de.blazemcworld.blazinggames.events.handlers.vaults.VaultInteractHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class InteractEventListener extends BlazingEventListener<PlayerInteractEvent> {
    public InteractEventListener() {
        this.handlers.addAll(List.of(
                new NaturesBlessingHandler(),
                new BuilderWandCycleHandler(),
                new BuilderWandPlaceHandler(),
                new CrateInteractHandler(),
                new BlueprintEnchantingTableInteractHandler(),
                new EnchantingTableInteractHandler(),
                new BottleInteractHandler(),
                new FireworkInteractHandler(),
                new PortableCraftingTableInteractHandler(),
                new SpawnerInteractionHandler(),
                new TomeAltarBreakHandler(),
                new TomeAltarInteractHandler(),
                new VaultInteractHandler(),
                new CustomSlabPlaceBlockHandler()
        ));
    }

    @EventHandler
    public void event(PlayerInteractEvent event) {
        executeEvent(event);
    }
}
