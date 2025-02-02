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
package de.blazemcworld.blazinggames.computing;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interception.logging.JavetStandardConsoleInterceptor;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.options.V8RuntimeOptions;
import com.caoccao.javet.values.reference.IV8ValueObject;
import com.caoccao.javet.values.reference.V8ValueObject;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.computing.functions.GlobalFunctions;
import de.blazemcworld.blazinggames.computing.functions.JSFunctionalClass;
import de.blazemcworld.blazinggames.computing.types.ComputerItemContext;
import de.blazemcworld.blazinggames.computing.types.ComputerTypes;
import de.blazemcworld.blazinggames.computing.upgrades.UpgradeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class BootedComputer {
    private String code;

    // Static metadata
    private final String id;
    private final ComputerTypes type;
    
    // Dynamic metadata
    private UUID address;
    private String name;
    private Location location;
    private ArrayList<UpgradeType> upgrades;
    private UUID owner;
    private ArrayList<UUID> collaborators;
    private boolean shouldRun;
    private int frozenTicks;

    // Runtime data
    private DesiredState desiredState = DesiredState.NO_CHANGE;
    private boolean wantsStateReset = false;
    UUID runtimeEntityUUID;

    // State
    private V8RuntimeOptions state;

    BootedComputer(
        final ComputerMetadata metadata,
        final Location location,
        final byte[] state,
        final String code
    ) {
        this.id = metadata.id;
        this.type = metadata.type;
        this.code = code;
        this.location = location;
        this.address = metadata.address;
        this.name = metadata.name;
        this.upgrades = new ArrayList<>(metadata.upgrades);
        upgrades.addAll(List.of(type.getType().getDefaultUpgrades())); // add defaults
        upgrades = new ArrayList<>(upgrades.stream().distinct().collect(Collectors.toList())); // remove duplicates
        this.owner = metadata.owner;
        this.collaborators = new ArrayList<>(List.of(metadata.collaborators));
        this.shouldRun = metadata.shouldRun;
        this.frozenTicks = metadata.frozenTicks;

        this.state = new V8RuntimeOptions();
        this.state.setCreateSnapshotEnabled(true);
        if (state != null) {
            this.state.setSnapshotBlob(state);
        }

        location.getBlock().setType(Material.BARRIER);

        ItemDisplay display = location.getWorld().spawn(location.toCenterLocation(), ItemDisplay.class, SpawnReason.CUSTOM, (entity) -> {
            entity.setItemStack(type.item().create(ComputerItemContext.defaultContext()));
        });
        this.runtimeEntityUUID = display.getUniqueId();
    }

    void hibernateNow() {
        this.stopCodeExecution();
        this.location.getBlock().setType(Material.AIR);
        this.location.getWorld().getEntity(this.runtimeEntityUUID).remove();
        this.runtimeEntityUUID = null;
        this.location = null;
    }

    void saveNow() {
        ComputerRegistry.saveToDisk(this);
    }

    void tick() {
        if (this.location == null) throw new IllegalStateException("tick() called after hibernateNow()");

        // switch state if needed
        if (this.desiredState == DesiredState.STOPPED) {
            this.shouldRun = false;
            this.desiredState = DesiredState.NO_CHANGE;
        } else if (this.desiredState == DesiredState.RUNNING) {
            this.shouldRun = true;
            this.desiredState = DesiredState.NO_CHANGE;
        } else if (this.desiredState == DesiredState.RESTART) {
            this.shouldRun = false;
            this.wantsStateReset = true;
            this.desiredState = DesiredState.RUNNING;
        }

        // reset state if needed
        if (this.wantsStateReset) {
            this.state.setSnapshotBlob(null);
            this.frozenTicks = 0;
            this.wantsStateReset = false;
        }

        if (this.shouldRun && this.frozenTicks > 0) {
            // unfreeze if frozen
            this.frozenTicks--;
        } else if (this.shouldRun) {
            try (
                V8Runtime runtime = V8Host.getV8Instance().createV8Runtime(this.state)
            ) {
                JavetStandardConsoleInterceptor console = new JavetStandardConsoleInterceptor(runtime);
                console.register(new IV8ValueObject[]{runtime.getGlobalObject()});

                Set<JSFunctionalClass> functionList = Set.of();
                functionList.add(new GlobalFunctions(this, runtime));
                Set<UpgradeType> upgradeList = Set.of(type.getType().getDefaultUpgrades());
                upgradeList.addAll(upgrades);
                for (UpgradeType type : upgradeList) {
                    if (type.functions != null) {
                        functionList.add(type.functions.apply(this, runtime));
                    }
                }


                for (JSFunctionalClass functionalClass : functionList) {
                    V8ValueObject obj = runtime.createV8ValueObject();
                    runtime.getGlobalObject().set(functionalClass.getNamespace(), obj);
                    obj.bind(functionalClass);
                    obj.close();
                }

                runtime.getExecutor(this.code).executeVoid();
            } catch (JavetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void startCodeExecution() {
        this.desiredState = DesiredState.RUNNING;
    }

    public void stopCodeExecution() {
        this.desiredState = DesiredState.STOPPED;
    }

    public void resetCodeExecutionState() {
        this.wantsStateReset = true;
    }

    public void restartCodeExecution() {
        this.desiredState = DesiredState.RESTART;
    }

    public void freeze(int ticks) {
        if (this.frozenTicks < 0) {
            this.frozenTicks = 0;
        }

        this.frozenTicks += ticks;
    }

    public ComputerMetadata getMetadata() {
        List<UpgradeType> defaultUpgrades = List.of(type.getType().getDefaultUpgrades());
        return new ComputerMetadata(
            this.id,
            this.name,
            this.address,
            this.type,
            this.upgrades.stream().filter(upgrade -> !defaultUpgrades.contains(upgrade)).toList(),
            this.location,
            this.owner,
            this.collaborators.toArray(UUID[]::new),
            this.shouldRun,
            this.frozenTicks
        );
    }

    void updateLocation(final Location newLocation) {
        if (newLocation == null) throw new IllegalArgumentException("cannot move to a null location");

        if (!newLocation.toCenterLocation().equals(this.location.toCenterLocation())) {
            Bukkit.getScheduler().runTask(BlazingGames.get(), () -> {
                this.location.getBlock().setType(Material.AIR);
                newLocation.getBlock().setType(Material.BARRIER);
                this.location.getWorld().getEntity(this.runtimeEntityUUID).teleport(newLocation.toCenterLocation());
            });
        }
        this.location = newLocation;
    }

    void updateMetadata(final ComputerMetadata metadata) {
        updateLocation(metadata.location);
        this.address = metadata.address;
        this.name = metadata.name;
        this.upgrades = new ArrayList<>(metadata.upgrades);
        upgrades.addAll(List.of(type.getType().getDefaultUpgrades())); // add defaults
        upgrades = new ArrayList<>(upgrades.stream().distinct().collect(Collectors.toList())); // remove duplicates
        this.owner = metadata.owner;
    }

    void updateCode(String newCode) {
        this.code = newCode;
        if (this.shouldRun) {
            this.resetCodeExecutionState();
            this.restartCodeExecution();
        }
    }

    private static enum DesiredState {
        NO_CHANGE,
        RUNNING,
        STOPPED,
        RESTART;
    }

    public String getId() {
        return this.id;
    }

    public ComputerTypes getType() {
        return this.type;
    }

    public byte[] getState() {
        return this.state.getSnapshotBlob();
    }

    public String getCode() {
        return this.code;
    }
}
