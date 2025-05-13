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

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.blocks.CustomBlock;
import de.blazemcworld.blazinggames.blocks.CustomBlockProviders;
import de.blazemcworld.blazinggames.blocks.data.CustomBlockData;
import de.blazemcworld.blazinggames.utils.TextLocation;
import dev.ivycollective.datastorage.DataStorage;
import dev.ivycollective.datastorage.name.ArbitraryNameProvider;
import dev.ivycollective.datastorage.storage.GsonStorageProvider;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class CustomBlockWrapper<T extends CustomBlockData> extends BlockWrapper {
    private static final DataStorage<BlockWrapper, String> dataStorage = BlazingGames.dataStorageConfig().makeDataStorage(
            CustomBlockWrapper.class, null,
            new GsonStorageProvider<>(BlockWrapper.class), new ArbitraryNameProvider()
    );

    CustomBlock<T> block;
    T blockData;

    public CustomBlockWrapper(CustomBlock<T> block, T blockData) {
        this.block = block;
        this.blockData = blockData;
    }

    @Override
    protected boolean replaceUnsafe(Location location) {
        dataStorage.storeData(TextLocation.serializeRounded(location), this);

        location.getBlock().setType(block.getBaseMaterial());

        createDisplay(location, location);

        return true;
    }

    @Override
    protected boolean remove(Location location, boolean dontRemoveMaterial) {
        if(!dataStorage.hasData(TextLocation.serializeRounded(location))) {
            return false;
        }

        if(!dontRemoveMaterial) {
            location.getBlock().setType(Material.AIR);
        }

        removeDisplay(location);

        dataStorage.deleteData(TextLocation.serializeRounded(location));

        return true;
    }

    public void createDisplay(Location owningLocation, Location displayLocation) {
        block.createDisplay(owningLocation, displayLocation, blockData);
    }

    @Override
    public BlockWrapper clone() {
        return block.createWrapper((T) blockData.clone());
    }

    @Override
    protected BlockWrapperType wrapperType() {
        return BlockWrapperType.CUSTOM;
    }

    @Override
    protected JsonObject serializeData() {
        JsonObject object = blockData.serialize();

        object.add("type", BlazingGames.gson.toJsonTree(block.getKey(), NamespacedKey.class));

        return object;
    }

    protected static CustomBlockWrapper<?> deserializeData(JsonObject data) {
        NamespacedKey typeKey = BlazingGames.gson.fromJson(data.get("type"), NamespacedKey.class);
        CustomBlock<?> blockType = CustomBlockProviders.instance.getByKey(typeKey);

        if(blockType == null) {
            throw new JsonParseException("Could not find a custom block type with id " + typeKey.toString());
        }

        return blockType.deserializeAndCreateWrapper(data);
    }

    public static boolean isCustomBlock(Location location) {
        if(location == null) return false;

        return dataStorage.hasData(TextLocation.serializeRounded(location));
    }

    protected static BlockWrapper fromStorage(Location location) {
        if(!isCustomBlock(location)) {
            return null;
        }

        return dataStorage.getData(TextLocation.serializeRounded(location));
    }

    public static boolean isSpecificCustomBlock(Location location, CustomBlock<?> block) {
        BlockWrapper wrapper = BlockWrapper.fromLocation(location);

        if(wrapper instanceof CustomBlockWrapper<?> cbw) {
            return cbw.block == block;
        }

        return false;
    }

    public void onRightClick(PlayerInteractEvent event, @NotNull Location location) {
        block.onRightClick(event, location, blockData);
    }
}
