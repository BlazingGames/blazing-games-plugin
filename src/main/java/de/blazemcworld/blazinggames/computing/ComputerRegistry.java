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

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.computing.types.ComputerTypes;
import de.blazemcworld.blazinggames.data.DataStorage;
import de.blazemcworld.blazinggames.data.compression.GZipCompressionProvider;
import de.blazemcworld.blazinggames.data.name.ULIDNameProvider;
import de.blazemcworld.blazinggames.data.storage.BinaryStorageProvider;
import de.blazemcworld.blazinggames.data.storage.GsonStorageProvider;
import de.blazemcworld.blazinggames.data.storage.RawTextStorageProvider;
import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantments;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentHelper;
import de.blazemcworld.blazinggames.utils.NameGenerator;
import de.blazemcworld.blazinggames.utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ComputerRegistry {
    private static final ArrayList<BootedComputer> computers = new ArrayList<>();
    public static final String defaultCode = "// welcome to the editor!\n" +
            "// this uses JavaScript along with our custom methods to control computers\n// learn more in the documentation: ______";

    public static final DataStorage<ComputerMetadata, String> metadataStorage = DataStorage.forClass(
        ComputerRegistry.class, "metadata",
        new GsonStorageProvider<ComputerMetadata>(ComputerMetadata.class),
        new ULIDNameProvider(), new GZipCompressionProvider()
    );

    public static final DataStorage<byte[], String> stateStorage = DataStorage.forClass(
        ComputerRegistry.class, "state",
        new BinaryStorageProvider(), new ULIDNameProvider(), new GZipCompressionProvider()
    );

    public static final DataStorage<String, String> codeStorage = DataStorage.forClass(
        ComputerRegistry.class, "code",
        new RawTextStorageProvider("js"),
        new ULIDNameProvider(), new GZipCompressionProvider()
    );


    private ComputerRegistry() {}

    /**
     * Load a computer into the world (on server startup)
     */
    public static synchronized void loadComputerIntoWorld(final String id) {
        final ComputerMetadata metadata = metadataStorage.getData(id);
        final byte[] state = stateStorage.getData(id);
        final String code = codeStorage.getData(id);

        if (metadata.location == null) return;
        if (getComputerById(id) != null) return;
        if (getComputerByLocationRounded(metadata.location) != null) return;

        Bukkit.getScheduler().runTaskLater(BlazingGames.get(), () -> {
            BootedComputer computer = new BootedComputer(metadata, metadata.location, state, code == null ? defaultCode : code);
            computers.add(computer);
        }, 2L);
    }

    /**
     * Place an existing computer into the world (on player placing)
     */
    public static synchronized void placeComputer(final String id, final Location location, final BiConsumer<Boolean, BootedComputer> callback) {
        if (getComputerById(id) != null) {
            callback.accept(false, getComputerById(id));
        } else if (getComputerByLocationRounded(location) != null) {
            callback.accept(false, getComputerByLocationRounded(location));
        } else {
            final ComputerMetadata metadata = metadataStorage.getData(id);
            final byte[] state = stateStorage.getData(id);
            final String code = codeStorage.getData(id);

            if (metadata == null) {
                callback.accept(false, null);
                return;
            }

            Bukkit.getScheduler().runTaskLater(BlazingGames.get(), () -> {
                BootedComputer computer = new BootedComputer(metadata, location, state, code == null ? defaultCode : code);
                computers.add(computer);
                callback.accept(true, computer);
            }, 2L);
        }
    }

    /**
     * Unload a computer
     */
    public static synchronized void unload(final String id) {
        BootedComputer computer = getComputerById(id);
        if (computer != null) {
            computer.hibernateNow();
            saveToDisk(computer);
            computers.remove(computer);
        }
    }

    /**
     * Drop the comptuer item in the world. Doesn't break the block, doesn't unload
     */
    public static void dropComputer(final BootedComputer computer, final Player player) {
        ComputerMetadata metadata = computer.getMetadata();
        ItemStack computerItem = computer.getType().item().create(computer.getMetadata().createContext());
        if (player != null && EnchantmentHelper.hasCustomEnchantment(player.getInventory().getItemInMainHand(), CustomEnchantments.COLLECTABLE)) {
            player.getInventory().addItem(new ItemStack[]{computerItem});
        } else {
            metadata.location.getWorld().dropItemNaturally(metadata.location, computerItem);
        }
    }

    /**
     * Create a new computer and place it into the world
     */
    public static synchronized void placeNewComputer(final Location location, final ComputerTypes type, final UUID ownerUUID, final Consumer<BootedComputer> callback) {
        if (getComputerByLocationRounded(location) != null) {
            throw new IllegalArgumentException("Computer already exists at " + location);
        } else {
            final Pair<ComputerMetadata, String> data = metadataStorage.storeNext((id) -> {
                return new ComputerMetadata(
                    id,
                    NameGenerator.generateName(),
                    UUID.randomUUID(),
                    type,
                    List.of(),
                    location,
                    ownerUUID,
                    new UUID[0],
                    false,
                    0
                );
            });
            final ComputerMetadata metadata = data.left;

            Bukkit.getScheduler()
                .runTaskLater(
                    BlazingGames.get(),
                    () -> {
                        BootedComputer computer = new BootedComputer(metadata, location, null, defaultCode);
                        computers.add(computer);
                        callback.accept(computer);
                    }, 2L
                );
        }
    }

    static synchronized void saveToDisk(BootedComputer computer) {
        ComputerMetadata metadata = computer.getMetadata();

        metadataStorage.storeData(computer.getId(), metadata);
        codeStorage.storeData(computer.getId(), computer.getCode());

        if (computer.getState() != null) {
            stateStorage.storeData(computer.getId(), computer.getState());
        } else {
            stateStorage.deleteData(computer.getId());
        }
    }

    public static BootedComputer getComputerById(String id) {
        return computers.stream().filter(computer -> computer.getId().equals(id)).findFirst().orElse(null);
    }

    public static BootedComputer getComputerByAddress(UUID address) {
        return computers.stream().filter(computer -> computer.getMetadata().address.equals(address)).findFirst().orElse(null);
    }

    public static BootedComputer getComputerByLocationExact(Location location) {
        return computers.stream().filter(computer -> computer.getMetadata().location.equals(location)).findFirst().orElse(null);
    }

    public static BootedComputer getComputerByLocationRounded(Location location) {
        Location loc = _roundLocation(location);
        return computers.stream().filter(computer -> _roundLocation(computer.getMetadata().location).equals(loc)).findFirst().orElse(null);
    }

    private static Location _roundLocation(Location loc) {
        return new Location(loc.getWorld(), (double) loc.getBlockX(), (double) loc.getBlockY(), (double) loc.getBlockZ());
    }

    public static void tick() {
        for (BootedComputer computer : computers) {
            computer.tick();
        }
    }

    public static void shutdownHook() {
        for (BootedComputer computer : computers) {
            computer.hibernateNow();
            unload(computer.getId());
        }
        computers.clear();
    }

    public static record ComputerPrivileges(boolean chunkloading, boolean network) {
        public static ComputerPrivileges minimal() {
            return new ComputerPrivileges(false, false);
        }
    }
}
