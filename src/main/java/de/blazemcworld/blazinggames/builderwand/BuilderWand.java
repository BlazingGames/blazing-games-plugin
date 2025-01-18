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
package de.blazemcworld.blazinggames.builderwand;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.items.ContextlessItem;
import de.blazemcworld.blazinggames.items.CustomItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BuilderWand extends ContextlessItem {
    private static final NamespacedKey modeKey = BlazingGames.get().key("builder_mode");

    @Override
    public @NotNull NamespacedKey getKey() {
        return BlazingGames.get().key("builder_wand");
    }

    @Override
    protected @NotNull ItemStack modifyMaterial(ItemStack wand) {
        ItemMeta meta = wand.getItemMeta();

        meta.getPersistentDataContainer().set(modeKey, BuilderWandMode.persistentType, BuilderWandMode.NO_LOCK);

        wand.setItemMeta(meta);

        return updateWand(wand);
    }

    @Override
    protected @NotNull Component itemName() {
        return Component.text("Builder's Wand").color(NamedTextColor.GOLD);
    }

    private ItemStack updateWand(ItemStack wand) {
        if(!matchItem(wand)) {
            return wand;
        }

        ItemStack result = wand.clone();

        ItemMeta meta = result.getItemMeta();

        meta.lore(List.of(Component.text(getModeText(wand)).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));

        result.setItemMeta(meta);

        return result;
    }

    public ItemStack cycleMode(ItemStack wand) {
        if(!matchItem(wand)) {
            return wand;
        }

        ItemStack result = wand.clone();

        ItemMeta meta = wand.getItemMeta();

        BuilderWandMode mode = meta.getPersistentDataContainer().getOrDefault(modeKey, BuilderWandMode.persistentType, BuilderWandMode.NO_LOCK);
        mode = mode.getNextMode();

        meta.getPersistentDataContainer().set(modeKey, BuilderWandMode.persistentType, mode);

        result.setItemMeta(meta);

        return updateWand(result);
    }

    public String getModeText(ItemStack wand) {
        if(!matchItem(wand)) {
            return "";
        }

        ItemMeta meta = wand.getItemMeta();

        BuilderWandMode mode = meta.getPersistentDataContainer().getOrDefault(modeKey, BuilderWandMode.persistentType, BuilderWandMode.NO_LOCK);

        return "Mode: " + mode.getModeText();
    }

    public int build(Player player, ItemStack eventItem, Block block, BlockFace face, Vector interactionPoint) {
        if(!matchItem(eventItem)) {
            return 0;
        }

        BuilderWandMode mode = eventItem.getItemMeta().getPersistentDataContainer().getOrDefault(modeKey, BuilderWandMode.persistentType, BuilderWandMode.NO_LOCK);

        if(!mode.canBuildOnFace(face)) {
            return 0;
        }

        Material placementMaterial = block.getBlockData().getPlacementMaterial();
        Material itemMaterial = block.getBlockData().getMaterial();

        int maxBlocks = 0;

        Inventory inv = player.getInventory();

        for (ItemStack itemStack : inv.getStorageContents()) {
            if(itemStack == null) continue;
            if(itemStack.getType() != itemMaterial) continue;
            if(CustomItem.isCustomItem(itemStack)) continue;

            maxBlocks += itemStack.getAmount();
        }

        if(maxBlocks > 128 || player.getGameMode() == GameMode.CREATIVE) {
            maxBlocks = 128;
        }

        BuilderLocation location = getBuilderLocationFromInteractionPoint(block, interactionPoint);

        List<BuilderLocation> locations = getBuilderWandLocations(player, face, location, mode, placementMaterial, maxBlocks);

        int blocksUsed = 0;

        for(BuilderLocation currentLocation : locations) {
            if(currentLocation.add(face).placeBlock(player, placementMaterial)) {
                blocksUsed++;
            }
        }

        if(blocksUsed <= 0) {
            return 0;
        }

        int copy = blocksUsed;

        if(player.getGameMode() != GameMode.CREATIVE) {
            for (ItemStack itemStack : inv.getStorageContents()) {
                if(blocksUsed <= 0) break;
                if(itemStack == null) continue;
                if(itemStack.getType() != itemMaterial) continue;
                if(CustomItem.isCustomItem(itemStack)) continue;

                if(itemStack.getAmount() <= blocksUsed) {
                    blocksUsed -= itemStack.getAmount();
                    itemStack.subtract(itemStack.getAmount());
                }
                else {
                    int a = blocksUsed;
                    blocksUsed -= a;
                    itemStack.subtract(a);
                }
            }
        }

        return copy;
    }

    private static List<BuilderLocation> getBuilderWandLocations(Player player, BlockFace face, BuilderLocation location, BuilderWandMode mode, Material placementMaterial, int maxBlocks) {
        List<BuilderLocation> locations = new ArrayList<>();
        locations.add(location);

        List<BlockFace> validFaces = new ArrayList<>(mode.getBuildDirections());
        validFaces.remove(face);
        validFaces.remove(face.getOppositeFace());

        for(int i = 0; i < locations.size(); i++) {
            BuilderLocation currentLocation = locations.get(i);

            for(BlockFace spreadFace : validFaces) {
                BuilderLocation spreadLocation = currentLocation.add(spreadFace);
                BuilderLocation buildLocation = spreadLocation.add(face);

                if(!locations.contains(spreadLocation)) {
                    if(spreadLocation.canPlaceOnLocation(player, placementMaterial)) {
                        if(buildLocation.canPlaceAtLocation(player, placementMaterial)) {
                            locations.add(spreadLocation);
                        }
                    }
                }

                if(locations.size() >= maxBlocks) {
                    break;
                }
            }

            if(locations.size() >= maxBlocks) {
                break;
            }
        }

        return locations;
    }

    private static @NotNull BuilderLocation getBuilderLocationFromInteractionPoint(Block block, Vector interactionPoint) {
        BuilderLocation location = new BuilderLocation(block.getLocation());

        if(block.getBlockData() instanceof Slab slab) {
            BuilderSlabLocation.BuilderSlabHalf half = switch(slab.getType()) {
                case TOP -> BuilderSlabLocation.BuilderSlabHalf.TOP;
                case BOTTOM -> BuilderSlabLocation.BuilderSlabHalf.BOTTOM;
                case DOUBLE -> interactionPoint.getY() > 0.5 ? BuilderSlabLocation.BuilderSlabHalf.TOP
                                                             : BuilderSlabLocation.BuilderSlabHalf.BOTTOM;
            };

            location = new BuilderSlabLocation(block.getLocation(), half);
        }
        return location;
    }

    public Map<NamespacedKey, Recipe> getRecipes() {
        ShapedRecipe wandRecipe = new ShapedRecipe(getKey(), create());
        wandRecipe.shape(
                "  S",
                " R ",
                "R  "
        );
        wandRecipe.setIngredient('R', Material.BLAZE_ROD);
        wandRecipe.setIngredient('S', Material.NETHER_STAR);

        return Map.of(
            getKey(), wandRecipe
        );
    }
}

