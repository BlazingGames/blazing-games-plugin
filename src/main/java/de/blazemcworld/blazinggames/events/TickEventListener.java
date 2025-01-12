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

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.computing.ComputerRegistry;
import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantments;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentHelper;
import de.blazemcworld.blazinggames.userinterfaces.UserInterface;
import de.blazemcworld.blazinggames.utils.TextLocation;
import de.blazemcworld.blazinggames.utils.TomeAltarStorage;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import java.util.ArrayList;
import java.util.Objects;

public class TickEventListener {

    private static int stupidrotate = 0;

    public static void onTick(BukkitTask object) {
        stupidrotate += 8;

        for(World world : Bukkit.getServer().getWorlds()) {
            ArrayList<Location> altars = new ArrayList<>(TomeAltarStorage.getAll(world));
            for(Entity entity : world.getEntities()) {
                // rotate the altars
                if (entity instanceof ItemDisplay display) {
                    for (Location location : altars) {
                        if (TextLocation.serializeRounded(location).equals(TextLocation.serializeRounded(display.getLocation()))) {
                            display.setRotation(stupidrotate, 0);
                        }
                    }
                }

                if(entity instanceof LivingEntity l) {
                    ItemStack chestplate = null;

                    if(entity instanceof Player p) {
                        chestplate = p.getInventory().getChestplate();
                    }
                    else {
                        EntityEquipment eq = l.getEquipment();
                        if(eq != null) {
                            chestplate = eq.getChestplate();
                        }
                    }

                    int updraft = EnchantmentHelper.getActiveCustomEnchantmentLevel(chestplate,
                            CustomEnchantments.UPDRAFT);
                    if(updraft > 0) {
                        if(l.isGliding()) {
                            if(smokeCovered(l.getLocation())) {
                                Vector velocity = l.getVelocity();
                                velocity.add(new Vector(0, 0.1*updraft*updraft, 0));
                                l.setVelocity(velocity);

                                l.getWorld().playSound(l, Sound.ENTITY_WITCH_THROW, SoundCategory.BLOCKS, 2f, 0.5f);
                            }
                        }
                    }
                }
            }

            for (Player p : world.getPlayers()) {
                if(p.getOpenInventory().getTopInventory().getHolder() instanceof UserInterface ui) {
                    ui.tick(p);
                }

                if (p.getTargetBlockExact(5) != null && Objects.requireNonNull(p.getTargetBlockExact(5)).getType() == Material.SPAWNER) {
                    CreatureSpawner spawner = (CreatureSpawner) Objects.requireNonNull(p.getTargetBlockExact(5)).getState();
                    Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

                    Objective objective = scoreboard.registerNewObjective("Spawner", Criteria.DUMMY, Component.text("Spawner").color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD));
                    objective.setDisplaySlot(DisplaySlot.SIDEBAR);

                    Score minDelay = objective.getScore("§eMin Delay");
                    minDelay.setScore(0);
                    minDelay.numberFormat(NumberFormat.fixed(Component.text(spawner.getMinSpawnDelay()).color(NamedTextColor.GREEN)));

                    Score maxDelay = objective.getScore("§eMax Delay");
                    maxDelay.setScore(-1);
                    maxDelay.numberFormat(NumberFormat.fixed(Component.text(spawner.getMaxSpawnDelay()).color(NamedTextColor.GREEN)));

                    Score spawnCount = objective.getScore("§eSpawn Count");
                    spawnCount.setScore(-2);
                    spawnCount.numberFormat(NumberFormat.fixed(Component.text(spawner.getSpawnCount()).color(NamedTextColor.GREEN)));

                    Score maxNearby = objective.getScore("§eMax Nearby");
                    maxNearby.setScore(-3);
                    maxNearby.numberFormat(NumberFormat.fixed(Component.text(spawner.getMaxNearbyEntities()).color(NamedTextColor.GREEN)));

                    Score playerRange = objective.getScore("§ePlayer Range");
                    playerRange.setScore(-4);
                    playerRange.numberFormat(NumberFormat.fixed(Component.text(spawner.getRequiredPlayerRange()).color(NamedTextColor.GREEN)));

                    Score spawnRange = objective.getScore("§eSpawn Range");
                    spawnRange.setScore(-5);
                    spawnRange.numberFormat(NumberFormat.fixed(Component.text(spawner.getSpawnRange()).color(NamedTextColor.GREEN)));

                    Score redstoneControl = objective.getScore("§eRedstone Control");
                    redstoneControl.setScore(-6);
                    if (spawner.getPersistentDataContainer().getOrDefault(BlazingGames.get().key("redstone_control"), PersistentDataType.BOOLEAN, false)) {
                        redstoneControl.numberFormat(NumberFormat.fixed(Component.text("Enabled").color(NamedTextColor.GREEN)));
                    } else {
                        redstoneControl.numberFormat(NumberFormat.fixed(Component.text("Disabled").color(NamedTextColor.RED)));
                    }

                    p.setScoreboard(scoreboard);
                }
                else {
                    Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                    p.setScoreboard(scoreboard);
                }
            }
        }

        ComputerRegistry.tick();
    }

    public static boolean smokeCovered(Location location) {
        Block campfire = null;

        Location search = location.clone();

        for(int i = 0; i < 25; i++) {
            if(search.getBlock().getType() == Material.CAMPFIRE || search.getBlock().getType() == Material.SOUL_CAMPFIRE) {
                campfire = search.getBlock();
                break;
            }
            search.add(0, -1, 0);
        }

        if(campfire == null) {
            return false;
        }

        if(!(campfire.getBlockData() instanceof Campfire camp)) {
            return false;
        }

        if(!camp.isLit()) {
            return false;
        }

        int height = 10;

        if(camp.isSignalFire()) {
            height = 24;
        }

        RayTraceResult result = campfire.getWorld().rayTrace(
                campfire.getLocation().toCenterLocation(), new Vector(0,1,0), height, FluidCollisionMode.NEVER, true,
                0, (e) -> false
        );

        BlazingGames.get().log(result);

        double actualHeight = result == null ? height : result.getHitPosition().getY();

        double startingHeight = campfire.getLocation().toCenterLocation().getY();

        return location.getY() >= startingHeight && location.getY() <= startingHeight + actualHeight;
    }
}
