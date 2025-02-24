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
import de.blazemcworld.blazinggames.computing.types.IComputerType;
import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantments;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentHelper;
import de.blazemcworld.blazinggames.utils.NameGenerator;
import dev.ivycollective.datastorage.DataStorage;
import dev.ivycollective.datastorage.name.ULIDNameProvider;
import dev.ivycollective.datastorage.storage.BinaryStorageProvider;
import dev.ivycollective.datastorage.storage.GsonStorageProvider;
import dev.ivycollective.datastorage.storage.RawTextStorageProvider;
import dev.ivycollective.datastorage.utils.BiResult;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ComputerRegistry {
    private static final ArrayList<BootedComputer> computers = new ArrayList<>();
    private static int tick = 0;
    private static final int loopOnTick = 100;
    private static final int hitsThreshold = 5;
    public static final String defaultCode = "// welcome to the editor!\n" +
            "// this uses JavaScript along with our custom methods to control computers\n// learn more in the documentation: ______";
    private static final String NAMESPACE = "blazingcomputing";
    public static final NamespacedKey NAMESPACEDKEY_COMPUTER_TYPE = new NamespacedKey(NAMESPACE, "_computer_type");
    public static final NamespacedKey NAMESPACEDKEY_COMPUTER_ID = new NamespacedKey(NAMESPACE, "_computer_id");


    public static final DataStorage<ComputerMetadata, String> metadataStorage = BlazingGames.dataStorageConfig().makeDataStorage(
        ComputerRegistry.class, "metadata",
        new GsonStorageProvider<ComputerMetadata>(ComputerMetadata.class),
        new ULIDNameProvider()
    );

    public static final DataStorage<byte[], String> stateStorage = BlazingGames.dataStorageConfig().makeDataStorage(
        ComputerRegistry.class, "state",
        new BinaryStorageProvider(), new ULIDNameProvider()
    );

    public static final DataStorage<String, String> codeStorage = BlazingGames.dataStorageConfig().makeDataStorage(
        ComputerRegistry.class, "code",
        new RawTextStorageProvider("js"),
        new ULIDNameProvider()
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

        Bukkit.getScheduler().runTask(BlazingGames.get(), () -> {
            BootedComputer computer = new BootedComputer(metadata, metadata.location, state, code == null ? defaultCode : code);
            computers.add(computer);
        });
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

            Bukkit.getScheduler().runTask(BlazingGames.get(), () -> {
                BootedComputer computer = new BootedComputer(metadata, location, state, code == null ? defaultCode : code);
                computers.add(computer);
                callback.accept(true, computer);
            });
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
        ItemStack computerItem = addAttributes(metadata.type.getType().getDisplayItem(computer), metadata.type, computer.getId());
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
            final BiResult<ComputerMetadata, String> data = metadataStorage.storeNext((id) -> {
                return new ComputerMetadata(
                    id,
                    NameGenerator.generateName(),
                    UUID.randomUUID(),
                    type,
                    new String[0],
                    location,
                    ownerUUID,
                    new UUID[0],
                    false,
                    0
                );
            });
            final ComputerMetadata metadata = data.obj1;

            Bukkit.getScheduler()
                .runTask(
                    BlazingGames.get(),
                    () -> {
                        BootedComputer computer = new BootedComputer(metadata, location, null, defaultCode);
                        computers.add(computer);
                        callback.accept(computer);
                    }
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

    public static BootedComputer getComputerByActorUUID(UUID iAmTiredOfMakingTheseMethods) {
        return computers.stream().filter(computer -> iAmTiredOfMakingTheseMethods.equals(computer.motorRuntimeEntityUUID)).findFirst().orElse(null);
    }

    public static BootedComputer getComputerByLocationRounded(Location location) {
        Location loc = _roundLocation(location);
        return computers.stream().filter(computer -> _roundLocation(computer.getMetadata().location).equals(loc)).findFirst().orElse(null);
    }

    private static Location _roundLocation(Location loc) {
        return new Location(loc.getWorld(), (double) loc.getBlockX(), (double) loc.getBlockY(), (double) loc.getBlockZ());
    }

    public static void tick() {
        tick++;
        if (tick >= loopOnTick) {
            tick = 0;

            for (BootedComputer computer : computers) {
                if (computer.motorRuntimeEntityHits >= hitsThreshold) {
                    Player player = Bukkit.getPlayer(computer.motorRuntimeEntityHitAttacker);
                    dropComputer(computer, player);
                    unload(computer.getId());
                } else {
                    computer.damageHookRemoveHit();
                    computer.tick();
                }
            }
        } else {
            for (BootedComputer computerx : computers) {
                computerx.tick();
            }
        }
    }

    public static void registerAllRecipes() {
        for (ComputerTypes value : ComputerTypes.values()) {
            IComputerType type = value.getType();
            NamespacedKey key = new NamespacedKey(NAMESPACE, value.name().toLowerCase());
            Bukkit.addRecipe(type.getRecipe(key, addAttributes(type.getDisplayItem(null), value.name(), "")));
        }
    }

    public static ItemStack addAttributes(ItemStack item, BootedComputer computer) {
        return addAttributes(item, computer.getType(), computer.getId());
    }

    public static ItemStack addAttributes(ItemStack item, IComputerType type, String id) {
        return addAttributes(item, ComputerTypes.valueOf(type), id);
    }

    public static ItemStack addAttributes(ItemStack item, ComputerTypes computerType, String id) {
        return addAttributes(item, computerType.name(), id);
    }

    public static ItemStack addAttributes(ItemStack item, String computerType, String id) {
        ItemStack itemStack = item.clone();
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (computerType != null && !computerType.isEmpty()) {
            container.set(NAMESPACEDKEY_COMPUTER_TYPE, PersistentDataType.STRING, computerType);
        }

        if (!id.equals("")) {
            container.set(NAMESPACEDKEY_COMPUTER_ID, PersistentDataType.STRING, id);
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static boolean isComputerItem(ItemStack item) {
        if (!item.hasItemMeta()) {
            return false;
        } else {
            ItemMeta itemMeta = item.getItemMeta();
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
            return !((String)container.getOrDefault(NAMESPACEDKEY_COMPUTER_TYPE, PersistentDataType.STRING, "")).isEmpty();
        }
    }

    public static record ComputerPrivileges(boolean chunkloading, boolean network) {
        public static ComputerPrivileges minimal() {
            return new ComputerPrivileges(false, false);
        }
    }
}
