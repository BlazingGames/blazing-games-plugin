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

package de.blazemcworld.blazinggames.blocks.wrappers;

import com.google.gson.*;
import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.computing.ComputerRegistry;
import de.blazemcworld.blazinggames.crates.CrateManager;
import de.blazemcworld.blazinggames.utils.TomeAltarStorage;
import de.blazemcworld.blazinggames.utils.persistentDataTypes.BlockLocationDataType;
import de.blazemcworld.blazinggames.warpstones.WarpstoneStorage;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.function.Function;

public abstract class BlockWrapper implements Cloneable {
    public static final NamespacedKey owningLocation = BlazingGames.get().key("owning_location");

    // this method is unsafe as it doesn't bother to clear away
    protected abstract boolean replaceUnsafe(Location location);

    public boolean replace(Location location) {
        BlockWrapper old = BlockWrapper.fromLocation(location);

        if(old == null) {
            return false;
        }

        if(!old.remove(location, true)) {
            return false;
        }

        return replaceUnsafe(location);
    }

    protected abstract boolean remove(Location location, boolean dontRemoveMaterial);

    public boolean remove(Location location) {
        if(location == null) {
            return false;
        }

        return remove(location, false);
    }

    public boolean canBeLocked() {
        return false;
    }

    public static @Nullable BlockWrapper fromLocation(Location location) {
        return fromBlock(location.getBlock());
    }

    public static @Nullable BlockWrapper fromBlock(Block block) {
        Location location = block.getLocation();

        if(CustomBlockWrapper.isCustomBlock(location)) {
            return CustomBlockWrapper.fromStorage(location);
        }

        if(block.getState() instanceof TileState) {
            // no clue what to do with those for now
            return null;
        }

        if(ComputerRegistry.getComputerByLocationRounded(location) != null)
        {
            // can't lock running computers now can we
            return null;
        }

        if(CrateManager.getKeyULID(location) != null)
        {
            // why would you want to prevent someone from getting their items back
            return null;
        }

        if(TomeAltarStorage.isTomeAltar(location))
        {
            // don't feel like implementing this atm lol
            return null;
        }

        if(WarpstoneStorage.isWarpstone(location))
        {
            // ditto
            return null;
        }

        return new VanillaBlockWrapper(block.getBlockData());
    }

    public abstract void createDisplay(Location owningLocation, Location displayLocation);

    public static void setupDisplayEntity(Entity entity, Location owningLocation) {
        PersistentDataContainer container = entity.getPersistentDataContainer();
        container.set(BlockWrapper.owningLocation, BlockLocationDataType.instance, owningLocation);
    }

    protected void removeDisplay(Location owningLocation) {
        for (Entity e : owningLocation.getWorld().getEntities()) {
            PersistentDataContainer container = e.getPersistentDataContainer();

            Location displayLoc = container.get(BlockWrapper.owningLocation, BlockLocationDataType.instance);

            if(displayLoc == null) {
                continue;
            }

            if(displayLoc.getBlockX() != owningLocation.getBlockX()) {
                continue;
            }

            if(displayLoc.getBlockY() != owningLocation.getBlockY()) {
                continue;
            }

            if(displayLoc.getBlockZ() != owningLocation.getBlockZ()) {
                continue;
            }

            e.remove();
        }
    }

    @Override
    public abstract BlockWrapper clone();

    protected abstract BlockWrapperType wrapperType();
    protected abstract JsonObject serializeData();

    public enum BlockWrapperType {
        VANILLA(BlazingGames.get().key("vanilla"), VanillaBlockWrapper::deserializeData),
        CUSTOM(BlazingGames.get().key("custom"), CustomBlockWrapper::deserializeData);

        final NamespacedKey key;
        final Function<JsonObject, BlockWrapper> dataDeserializationFunction;

        BlockWrapperType(NamespacedKey key, Function<JsonObject, BlockWrapper> dataDeserializationFunction) {
            this.key = key;
            this.dataDeserializationFunction = dataDeserializationFunction;
        }

        private BlockWrapper deserializeData(JsonObject data) {
            return this.dataDeserializationFunction.apply(data);
        }

        private static BlockWrapperType getByKey(NamespacedKey key) {
            for(BlockWrapperType type : values()) {
                if(type.key.equals(key)) {
                    return type;
                }
            }

            return null;
        }
    }

    public static class Serializer implements JsonSerializer<BlockWrapper>, JsonDeserializer<BlockWrapper> {

        @Override
        public BlockWrapper deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            JsonObject obj = jsonElement.getAsJsonObject();

            NamespacedKey key = ctx.deserialize(obj.get("type"), NamespacedKey.class);
            BlockWrapperType wrapperType = BlockWrapperType.getByKey(key);

            if(wrapperType == null) {
                throw new JsonParseException("Unable to find Block Wrapper type with id " + key.toString());
            }

            return wrapperType.deserializeData(obj.getAsJsonObject("data"));
        }

        @Override
        public JsonElement serialize(BlockWrapper blockWrapper, Type type, JsonSerializationContext ctx) {
            JsonObject object = new JsonObject();

            BlockWrapperType wrapperType = blockWrapper.wrapperType();

            object.add("type", ctx.serialize(wrapperType.key));
            object.add("data", blockWrapper.serializeData());

            return object;
        }
    }
}
