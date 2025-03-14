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

package de.blazemcworld.blazinggames.crates.eventhandlers;

import de.blazemcworld.blazinggames.crates.CrateManager;
import de.blazemcworld.blazinggames.crates.DeathCrateKey;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.items.CustomItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;

public class MakeCrateHandler extends BlazingEventHandler<PlayerDeathEvent> {
    public static final List<Material> LIQUIDS = List.of(
            Material.WATER,
            Material.LAVA
    );

    public static final List<Material> ILLEGALS = List.of(
            Material.BEDROCK,
            Material.END_PORTAL_FRAME,
            Material.NETHER_PORTAL,
            Material.MOVING_PISTON, // custom slab compatibility
            Material.END_PORTAL
    );

    @Override
    public boolean fitCriteria(PlayerDeathEvent event, boolean cancelled) {
        return true;
    }

    @Override
    public void execute(PlayerDeathEvent event) {
        event.setKeepLevel(false);
        event.setKeepInventory(false);

        Player player = event.getEntity();
        Location deathLocation = event.getEntity().getLocation();
        player.sendMessage(Component.text("You've died at: %s, %s, %s. A crate with your items and exp will spawn nearby."
                .formatted(deathLocation.blockX(), deathLocation.blockY(), deathLocation.blockZ())).color(NamedTextColor.RED));
        Location crateLocation = deathLocation.clone();
        World world = crateLocation.getWorld();

        if (crateLocation.getBlockY() < world.getMinHeight()) {
            crateLocation.setY(world.getMinHeight());
        }

        int illegalsTriesRemaining = 20;
        while (illegalsTriesRemaining > 0 && ILLEGALS.contains(crateLocation.getBlock().getType())) {
            crateLocation = crateLocation.add(0, 1, 0);
            illegalsTriesRemaining--;
        }

        int liquidsTriesRemaining = 20;
        while (liquidsTriesRemaining > 0 && LIQUIDS.contains(crateLocation.getBlock().getType())) {
            crateLocation = crateLocation.add(0, 1, 0);
            liquidsTriesRemaining--;
        }

        if (crateLocation.getBlockY() >= world.getMaxHeight()) {
            crateLocation.setY(world.getMaxHeight() - 1);
        }

        if (crateLocation.getBlock().getType() != Material.AIR) {
            crateLocation.getBlock().breakNaturally(true);
        }

        crateLocation.getBlock().setType(Material.END_PORTAL_FRAME);

        event.setDroppedExp(0);
        event.getDrops().clear();
        String ulid = CrateManager.createDeathCrate(player.getUniqueId(), event.getPlayer().getInventory(), event.getPlayer().calculateTotalExperiencePoints(), crateLocation);
        event.getItemsToKeep().add(CustomItems.DEATH_CRATE_KEY.create(new DeathCrateKey.DeathCrateKeyContext(ulid)));
    }
}
