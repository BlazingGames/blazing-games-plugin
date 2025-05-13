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
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LockedBlock extends CustomBlock<LockedBlockData> {
    @Override
    public void createDisplay(Location owningLocation, Location displayLocation, LockedBlockData data) {
        data.getWrapper().createDisplay(owningLocation, displayLocation);
    }

    @Override
    protected LockedBlockData deserializeData(JsonObject data) {
        return LockedBlockData.deserialize(data);
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return BlazingGames.get().key("locked_block");
    }

    public static boolean lock(Location location, CustomItem<?> key) {
        BlockWrapper wrapper = BlockWrapper.fromLocation(location);
        if(wrapper == null || !wrapper.canBeLocked()) return false;

        BlockWrapper newWrapper = CustomBlocks.LOCKED_BLOCK.createWrapper(new LockedBlockData(wrapper, key));

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
