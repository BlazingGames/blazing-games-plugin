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
package de.blazemcworld.blazinggames.utils;

import de.blazemcworld.blazinggames.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class ItemUtils {
    public static Material getUncoloredType(ItemStack stack) {
        return getUncoloredType(stack.getType());
    }

    public static Material getUncoloredType(Block block) {
        return getUncoloredType(block.getType());
    }

    public static Material getUncoloredType(Material mat) {
        return switch(mat) {
            case SHULKER_BOX, WHITE_SHULKER_BOX, LIGHT_GRAY_SHULKER_BOX, GRAY_SHULKER_BOX, BLACK_SHULKER_BOX,
                 BROWN_SHULKER_BOX, RED_SHULKER_BOX, ORANGE_SHULKER_BOX, YELLOW_SHULKER_BOX, LIME_SHULKER_BOX,
                 GREEN_SHULKER_BOX, CYAN_SHULKER_BOX, LIGHT_BLUE_SHULKER_BOX, BLUE_SHULKER_BOX, PURPLE_SHULKER_BOX,
                 MAGENTA_SHULKER_BOX, PINK_SHULKER_BOX 
                    -> Material.SHULKER_BOX;
            case WHITE_CARPET, LIGHT_GRAY_CARPET, GRAY_CARPET, BLACK_CARPET, BROWN_CARPET, RED_CARPET, ORANGE_CARPET, YELLOW_CARPET,
                 LIME_CARPET, GREEN_CARPET, CYAN_CARPET, LIGHT_BLUE_CARPET, BLUE_CARPET, PURPLE_CARPET, MAGENTA_CARPET, PINK_CARPET 
                    -> Material.WHITE_CARPET;
            case WHITE_BED, LIGHT_GRAY_BED, GRAY_BED, BLACK_BED, BROWN_BED, RED_BED, ORANGE_BED, YELLOW_BED,
                 LIME_BED, GREEN_BED, CYAN_BED, LIGHT_BLUE_BED, BLUE_BED, PURPLE_BED, MAGENTA_BED, PINK_BED 
                    -> Material.WHITE_BED;
            case WHITE_BANNER, LIGHT_GRAY_BANNER, GRAY_BANNER, BLACK_BANNER, BROWN_BANNER, RED_BANNER, ORANGE_BANNER, YELLOW_BANNER,
                 LIME_BANNER, GREEN_BANNER, CYAN_BANNER, LIGHT_BLUE_BANNER, BLUE_BANNER, PURPLE_BANNER, MAGENTA_BANNER, PINK_BANNER 
                    -> Material.WHITE_BANNER;
            case TERRACOTTA, WHITE_TERRACOTTA, LIGHT_GRAY_TERRACOTTA, GRAY_TERRACOTTA, BLACK_TERRACOTTA, BROWN_TERRACOTTA, RED_TERRACOTTA, ORANGE_TERRACOTTA, YELLOW_TERRACOTTA,
                 LIME_TERRACOTTA, GREEN_TERRACOTTA, CYAN_TERRACOTTA, LIGHT_BLUE_TERRACOTTA, BLUE_TERRACOTTA, PURPLE_TERRACOTTA, MAGENTA_TERRACOTTA, PINK_TERRACOTTA 
                    -> Material.TERRACOTTA;
            case WHITE_CONCRETE, LIGHT_GRAY_CONCRETE, GRAY_CONCRETE, BLACK_CONCRETE, BROWN_CONCRETE, RED_CONCRETE, ORANGE_CONCRETE, YELLOW_CONCRETE,
                 LIME_CONCRETE, GREEN_CONCRETE, CYAN_CONCRETE, LIGHT_BLUE_CONCRETE, BLUE_CONCRETE, PURPLE_CONCRETE, MAGENTA_CONCRETE, PINK_CONCRETE 
                    -> Material.WHITE_CONCRETE;
            case WHITE_CONCRETE_POWDER, LIGHT_GRAY_CONCRETE_POWDER, GRAY_CONCRETE_POWDER, BLACK_CONCRETE_POWDER, BROWN_CONCRETE_POWDER, RED_CONCRETE_POWDER, ORANGE_CONCRETE_POWDER, YELLOW_CONCRETE_POWDER,
                 LIME_CONCRETE_POWDER, GREEN_CONCRETE_POWDER, CYAN_CONCRETE_POWDER, LIGHT_BLUE_CONCRETE_POWDER, BLUE_CONCRETE_POWDER, PURPLE_CONCRETE_POWDER, MAGENTA_CONCRETE_POWDER, PINK_CONCRETE_POWDER 
                    -> Material.WHITE_CONCRETE_POWDER;
            case WHITE_GLAZED_TERRACOTTA, LIGHT_GRAY_GLAZED_TERRACOTTA, GRAY_GLAZED_TERRACOTTA, BLACK_GLAZED_TERRACOTTA, BROWN_GLAZED_TERRACOTTA, RED_GLAZED_TERRACOTTA, ORANGE_GLAZED_TERRACOTTA, YELLOW_GLAZED_TERRACOTTA,
                 LIME_GLAZED_TERRACOTTA, GREEN_GLAZED_TERRACOTTA, CYAN_GLAZED_TERRACOTTA, LIGHT_BLUE_GLAZED_TERRACOTTA, BLUE_GLAZED_TERRACOTTA, PURPLE_GLAZED_TERRACOTTA, MAGENTA_GLAZED_TERRACOTTA, PINK_GLAZED_TERRACOTTA 
                    -> Material.WHITE_GLAZED_TERRACOTTA;
            case GLASS, TINTED_GLASS, WHITE_STAINED_GLASS, LIGHT_GRAY_STAINED_GLASS, GRAY_STAINED_GLASS, BLACK_STAINED_GLASS, BROWN_STAINED_GLASS, RED_STAINED_GLASS, ORANGE_STAINED_GLASS, YELLOW_STAINED_GLASS,
                 LIME_STAINED_GLASS, GREEN_STAINED_GLASS, CYAN_STAINED_GLASS, LIGHT_BLUE_STAINED_GLASS, BLUE_STAINED_GLASS, PURPLE_STAINED_GLASS, MAGENTA_STAINED_GLASS, PINK_STAINED_GLASS 
                    -> Material.GLASS;
            case GLASS_PANE, WHITE_STAINED_GLASS_PANE, LIGHT_GRAY_STAINED_GLASS_PANE, GRAY_STAINED_GLASS_PANE, BLACK_STAINED_GLASS_PANE, BROWN_STAINED_GLASS_PANE, RED_STAINED_GLASS_PANE, ORANGE_STAINED_GLASS_PANE, YELLOW_STAINED_GLASS_PANE,
                 LIME_STAINED_GLASS_PANE, GREEN_STAINED_GLASS_PANE, CYAN_STAINED_GLASS_PANE, LIGHT_BLUE_STAINED_GLASS_PANE, BLUE_STAINED_GLASS_PANE, PURPLE_STAINED_GLASS_PANE, MAGENTA_STAINED_GLASS_PANE, PINK_STAINED_GLASS_PANE 
                    -> Material.GLASS_PANE;
            case CANDLE, WHITE_CANDLE, LIGHT_GRAY_CANDLE, GRAY_CANDLE, BLACK_CANDLE, BROWN_CANDLE, RED_CANDLE, ORANGE_CANDLE, YELLOW_CANDLE,
                 LIME_CANDLE, GREEN_CANDLE, CYAN_CANDLE, LIGHT_BLUE_CANDLE, BLUE_CANDLE, PURPLE_CANDLE, MAGENTA_CANDLE, PINK_CANDLE 
                    -> Material.CANDLE;
            case WHITE_WOOL, LIGHT_GRAY_WOOL, GRAY_WOOL, BLACK_WOOL, BROWN_WOOL, RED_WOOL, ORANGE_WOOL, YELLOW_WOOL,
                 LIME_WOOL, GREEN_WOOL, CYAN_WOOL, LIGHT_BLUE_WOOL, BLUE_WOOL, PURPLE_WOOL, MAGENTA_WOOL, PINK_WOOL 
                    -> Material.WHITE_WOOL;
            case OAK_CHEST_BOAT, SPRUCE_CHEST_BOAT, BIRCH_CHEST_BOAT, JUNGLE_CHEST_BOAT, ACACIA_CHEST_BOAT, DARK_OAK_CHEST_BOAT, MANGROVE_CHEST_BOAT, CHERRY_CHEST_BOAT, BAMBOO_CHEST_RAFT, PALE_OAK_CHEST_BOAT
                    -> Material.OAK_CHEST_BOAT;
            case OAK_BOAT, SPRUCE_BOAT, BIRCH_BOAT, JUNGLE_BOAT, ACACIA_BOAT, DARK_OAK_BOAT, MANGROVE_BOAT, CHERRY_BOAT, BAMBOO_RAFT, PALE_OAK_BOAT
                    -> Material.OAK_BOAT;
            case OAK_WOOD, SPRUCE_WOOD, BIRCH_WOOD, JUNGLE_WOOD, ACACIA_WOOD, DARK_OAK_WOOD, MANGROVE_WOOD, CHERRY_WOOD, PALE_OAK_WOOD,
                 WARPED_HYPHAE, CRIMSON_HYPHAE
                    -> Material.OAK_WOOD;
            case STRIPPED_OAK_WOOD, STRIPPED_SPRUCE_WOOD, STRIPPED_BIRCH_WOOD, STRIPPED_JUNGLE_WOOD, STRIPPED_ACACIA_WOOD, STRIPPED_DARK_OAK_WOOD, STRIPPED_MANGROVE_WOOD, STRIPPED_CHERRY_WOOD, STRIPPED_PALE_OAK_WOOD,
                 STRIPPED_WARPED_HYPHAE, STRIPPED_CRIMSON_HYPHAE
                    -> Material.STRIPPED_OAK_WOOD;
            case OAK_LOG, SPRUCE_LOG, BIRCH_LOG, JUNGLE_LOG, ACACIA_LOG, DARK_OAK_LOG, MANGROVE_LOG, CHERRY_LOG, PALE_OAK_LOG,
                 WARPED_STEM, CRIMSON_STEM, BAMBOO_BLOCK
                    -> Material.OAK_LOG;
            case STRIPPED_OAK_LOG, STRIPPED_SPRUCE_LOG, STRIPPED_BIRCH_LOG, STRIPPED_JUNGLE_LOG, STRIPPED_ACACIA_LOG, STRIPPED_DARK_OAK_LOG, STRIPPED_MANGROVE_LOG, STRIPPED_CHERRY_LOG, STRIPPED_PALE_OAK_LOG,
                 STRIPPED_WARPED_STEM, STRIPPED_CRIMSON_STEM, STRIPPED_BAMBOO_BLOCK
                    -> Material.STRIPPED_OAK_LOG;
            case OAK_FENCE_GATE, SPRUCE_FENCE_GATE, BIRCH_FENCE_GATE, JUNGLE_FENCE_GATE, ACACIA_FENCE_GATE, DARK_OAK_FENCE_GATE, MANGROVE_FENCE_GATE, CHERRY_FENCE_GATE,
                 WARPED_FENCE_GATE, CRIMSON_FENCE_GATE, BAMBOO_FENCE_GATE, PALE_OAK_FENCE_GATE
                    -> Material.OAK_FENCE_GATE;
            case OAK_FENCE, SPRUCE_FENCE, BIRCH_FENCE, JUNGLE_FENCE, ACACIA_FENCE, DARK_OAK_FENCE, MANGROVE_FENCE, CHERRY_FENCE,
                 WARPED_FENCE, CRIMSON_FENCE, BAMBOO_FENCE, PALE_OAK_FENCE
                    -> Material.OAK_FENCE;
            case OAK_SIGN, SPRUCE_SIGN, BIRCH_SIGN, JUNGLE_SIGN, ACACIA_SIGN, DARK_OAK_SIGN, MANGROVE_SIGN, CHERRY_SIGN,
                 WARPED_SIGN, CRIMSON_SIGN, BAMBOO_SIGN, PALE_OAK_SIGN
                    -> Material.OAK_SIGN;
            case OAK_HANGING_SIGN, SPRUCE_HANGING_SIGN, BIRCH_HANGING_SIGN, JUNGLE_HANGING_SIGN, ACACIA_HANGING_SIGN, DARK_OAK_HANGING_SIGN, MANGROVE_HANGING_SIGN, CHERRY_HANGING_SIGN,
                 WARPED_HANGING_SIGN, CRIMSON_HANGING_SIGN, BAMBOO_HANGING_SIGN, PALE_OAK_HANGING_SIGN
                    -> Material.OAK_HANGING_SIGN;
            case OAK_BUTTON, SPRUCE_BUTTON, BIRCH_BUTTON, JUNGLE_BUTTON, ACACIA_BUTTON, DARK_OAK_BUTTON, MANGROVE_BUTTON, CHERRY_BUTTON,
                 WARPED_BUTTON, CRIMSON_BUTTON, BAMBOO_BUTTON, PALE_OAK_BUTTON
                    -> Material.OAK_BUTTON;
            case OAK_DOOR, SPRUCE_DOOR, BIRCH_DOOR, JUNGLE_DOOR, ACACIA_DOOR, DARK_OAK_DOOR, MANGROVE_DOOR, CHERRY_DOOR,
                 WARPED_DOOR, CRIMSON_DOOR, BAMBOO_DOOR, PALE_OAK_DOOR
                    -> Material.OAK_DOOR;
            case OAK_PRESSURE_PLATE, SPRUCE_PRESSURE_PLATE, BIRCH_PRESSURE_PLATE, JUNGLE_PRESSURE_PLATE, ACACIA_PRESSURE_PLATE, DARK_OAK_PRESSURE_PLATE, MANGROVE_PRESSURE_PLATE, CHERRY_PRESSURE_PLATE,
                 WARPED_PRESSURE_PLATE, CRIMSON_PRESSURE_PLATE, BAMBOO_PRESSURE_PLATE, PALE_OAK_PRESSURE_PLATE
                    -> Material.OAK_PRESSURE_PLATE;
            case OAK_SLAB, SPRUCE_SLAB, BIRCH_SLAB, JUNGLE_SLAB, ACACIA_SLAB, DARK_OAK_SLAB, MANGROVE_SLAB, CHERRY_SLAB,
                 WARPED_SLAB, CRIMSON_SLAB, BAMBOO_SLAB, PETRIFIED_OAK_SLAB, BAMBOO_MOSAIC_SLAB, PALE_OAK_SLAB
                    -> Material.OAK_SLAB;
            case OAK_STAIRS, SPRUCE_STAIRS, BIRCH_STAIRS, JUNGLE_STAIRS, ACACIA_STAIRS, DARK_OAK_STAIRS, MANGROVE_STAIRS, CHERRY_STAIRS,
                 WARPED_STAIRS, CRIMSON_STAIRS, BAMBOO_STAIRS, BAMBOO_MOSAIC_STAIRS, PALE_OAK_STAIRS
                    -> Material.OAK_STAIRS;
            case OAK_TRAPDOOR, SPRUCE_TRAPDOOR, BIRCH_TRAPDOOR, JUNGLE_TRAPDOOR, ACACIA_TRAPDOOR, DARK_OAK_TRAPDOOR, MANGROVE_TRAPDOOR, CHERRY_TRAPDOOR,
                 WARPED_TRAPDOOR, CRIMSON_TRAPDOOR, BAMBOO_TRAPDOOR, PALE_OAK_TRAPDOOR
                    -> Material.OAK_TRAPDOOR;
            case OAK_PLANKS, SPRUCE_PLANKS, BIRCH_PLANKS, JUNGLE_PLANKS, ACACIA_PLANKS, DARK_OAK_PLANKS, MANGROVE_PLANKS, CHERRY_PLANKS,
                 WARPED_PLANKS, CRIMSON_PLANKS, BAMBOO_PLANKS, BAMBOO_MOSAIC, PALE_OAK_PLANKS
                    -> Material.OAK_PLANKS;
            default -> mat;
        };
    }

    public static boolean canRepairTool(ItemStack tool, ItemStack sacrificial) {
        if(tool == null) return false;
        if(sacrificial == null) return false;

        CustomItem<?> customItem = CustomItem.getCustomItem(tool);
        if(customItem == null) {
            return !CustomItem.isCustomItem(sacrificial) && tool.isRepairableBy(sacrificial);
        }

        return customItem.repairableBy(sacrificial);
    }
}
