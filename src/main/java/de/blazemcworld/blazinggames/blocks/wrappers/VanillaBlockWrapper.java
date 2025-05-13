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
import de.blazemcworld.blazinggames.BlazingGames;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;

public class VanillaBlockWrapper extends BlockWrapper {
    private final BlockData data;

    public VanillaBlockWrapper(BlockData data) {
        this.data = data.clone();
    }

    @Override
    protected boolean replaceUnsafe(Location location) {
        location.getBlock().setBlockData(this.data);

        return true;
    }

    @Override
    protected boolean remove(Location location, boolean dontRemoveMaterial) {
        if(!dontRemoveMaterial) {
            location.getBlock().setType(Material.AIR);
        }

        return true;
    }

    @Override
    public boolean canBeLocked() {
        return true;
    }

    @Override
    public void createDisplay(Location owningLocation, Location displayLocation) {
        displayLocation.getWorld().spawn(displayLocation.toBlockLocation(), BlockDisplay.class, entity -> {
            entity.setBlock(data);
            this.setupDisplayEntity(entity, owningLocation);
        });
    }

    @Override
    public BlockWrapper clone() {
        return new VanillaBlockWrapper(data.clone());
    }

    @Override
    protected BlockWrapperType wrapperType() {
        return BlockWrapperType.VANILLA;
    }

    @Override
    protected JsonObject serializeData() {
        JsonObject obj = new JsonObject();

        obj.addProperty("blockData", data.getAsString());

        return obj;
    }

    protected static VanillaBlockWrapper deserializeData(JsonObject data) {
        return new VanillaBlockWrapper(BlazingGames.get().getServer().createBlockData(data.get("blockData").getAsString()));
    }
}
