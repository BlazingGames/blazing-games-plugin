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

package de.blazemcworld.blazinggames.builderwand.eventhandlers;

import de.blazemcworld.blazinggames.builderwand.BuilderWand;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;
import de.blazemcworld.blazinggames.items.CustomItem;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class BuilderWandCyclehandler extends BlazingEventHandler<PlayerInteractEvent> {
    @Override
    public boolean fitCriteria(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack eventItem = event.getItem();
        EquipmentSlot hand = event.getHand();

        if (event.getAction().isLeftClick() && hand != null) {
            return player.isSneaking() && CustomItem.getCustomItem(eventItem) instanceof BuilderWand;
        }
        return false;
    }

    @Override
    public void execute(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack eventItem = event.getItem();

        EquipmentSlot hand = event.getHand();
        if (hand == null) return;

        BuilderWand wand = (BuilderWand) CustomItem.getCustomItem(eventItem);
        if (wand == null) return;

        event.setCancelled(true);
        eventItem = wand.cycleMode(eventItem);
        player.getInventory().setItem(hand, eventItem);
        player.sendActionBar(Component.text(wand.getModeText(eventItem)));
    }
}
