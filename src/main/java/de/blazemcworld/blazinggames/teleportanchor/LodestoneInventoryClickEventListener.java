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
package de.blazemcworld.blazinggames.teleportanchor;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import de.blazemcworld.blazinggames.utils.TextLocation;

public class LodestoneInventoryClickEventListener implements Listener {

    @EventHandler
    public void onLodestoneClick(InventoryClickEvent event) {
        if (event.getView().title().equals(Component.text("Teleportation Menu").color(NamedTextColor.AQUA)) && event.getCurrentItem() != null) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemMeta meta = event.getCurrentItem().getItemMeta();

            if (!meta.getPersistentDataContainer().has(new NamespacedKey("blazinggames", "loc"))) return;
            Location location = TextLocation.deserialize(meta.getPersistentDataContainer().get(new NamespacedKey("blazinggames", "loc"), PersistentDataType.STRING));
            if (location == null) return;

            if (location.getBlock().getType() != Material.LODESTONE || event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                LodestoneStorage.removeSavedLodestoneForPlayer(player.getUniqueId(), location);
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 1, 1);
                LodestoneInteractionEventListener.openTeleportAnchor(player);
            } else {
                location = location.toCenterLocation();
                location.setY(location.getY() + 1);
                location.setPitch(player.getLocation().getPitch());
                location.setYaw(player.getLocation().getYaw());
                player.teleport(location);
                player.playSound(location, Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1, 1);
                event.getView().close();
            }
        }
    }
}
