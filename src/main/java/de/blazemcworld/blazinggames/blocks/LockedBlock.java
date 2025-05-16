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

package de.blazemcworld.blazinggames.blocks;

import com.google.gson.JsonObject;
import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.blocks.data.LockedBlockData;
import de.blazemcworld.blazinggames.blocks.wrappers.BlockWrapper;
import de.blazemcworld.blazinggames.items.CustomItem;
import de.blazemcworld.blazinggames.items.predicates.ItemPredicate;
import de.blazemcworld.blazinggames.packs.hooks.LockedBlockStylesHook;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

public class LockedBlock extends CustomBlock<LockedBlockData> {
    @Override
    public void createDisplay(Location owningLocation, Location displayLocation, LockedBlockData data) {
        data.getWrapper().createDisplay(owningLocation, displayLocation);

        displayLocation.getWorld().spawn(displayLocation.toCenterLocation(), ItemDisplay.class, entity -> {
            ItemStack lockedBlockModel = new ItemStack(Material.ICE);
            lockedBlockModel.setData(DataComponentTypes.ITEM_MODEL, data.getStyle().getKey());

            CustomModelData customModelData = CustomModelData.customModelData().addColor(data.getColor()).build();

            lockedBlockModel.setData(DataComponentTypes.CUSTOM_MODEL_DATA, customModelData);

            entity.setItemStack(lockedBlockModel);

            entity.setTransformation(new Transformation(
                    new Vector3f(),
                    new AxisAngle4f(),
                    new Vector3f(1.001f, 1.001f, 1.001f),
                    new AxisAngle4f()
            ));

            BlockWrapper.setupDisplayEntity(entity, owningLocation);
        });
    }

    @Override
    protected LockedBlockData deserializeData(JsonObject data) {
        return LockedBlockData.deserialize(data);
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return BlazingGames.get().key("locked_block");
    }

    public static boolean lock(Location location, CustomItem<?> key, LockedBlockStylesHook.LockedBlockStyle style, Color color) {
        BlockWrapper wrapper = BlockWrapper.fromLocation(location);
        if(wrapper == null || !wrapper.canBeLocked()) return false;

        BlockWrapper newWrapper = CustomBlocks.LOCKED_BLOCK.createWrapper(new LockedBlockData(wrapper, key, style, color));

        return newWrapper.replace(location);
    }

    public void onRightClick(PlayerInteractEvent event, @NotNull Location location, LockedBlockData blockData) {
        ItemPredicate keyPredicate = blockData.getKeyPredicate();

        ItemStack mainHand = event.getItem();

        if(mainHand != null && keyPredicate.matchItem(mainHand)) {
            if(blockData.getWrapper().replace(location)) {
                event.setCancelled(true);
                mainHand.subtract();
            }
        }
    }
}
