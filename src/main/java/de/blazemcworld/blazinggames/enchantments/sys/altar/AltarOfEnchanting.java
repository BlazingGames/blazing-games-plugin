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
package de.blazemcworld.blazinggames.enchantments.sys.altar;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.multiblocks.*;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Material;
import org.bukkit.util.Vector;

public class AltarOfEnchanting {
    //region Altar MultiBlock
    private static final BlockPredicate enchantingTable = new SingleBlockPredicate(Material.ENCHANTING_TABLE);
    private static final BlockPredicate smoothQuartz = new SingleBlockPredicate(Material.SMOOTH_QUARTZ);
    private static final BlockPredicate obsidian = new SingleBlockPredicate(Material.OBSIDIAN);
    private static final BlockPredicate lapisBlock = new SingleBlockPredicate(Material.LAPIS_BLOCK);
    private static final BlockPredicate diamondBlock = new SingleBlockPredicate(Material.DIAMOND_BLOCK);

    private static final BlockPredicate quartzStairs = new ComplexBlockPredicate(new SingleBlockPredicate(Material.QUARTZ_STAIRS),
                                                                                    BisectedBlockPredicate.BOTTOM);

    private static final BlockPredicate quartzStairsEast = new ComplexBlockPredicate(quartzStairs, StairShapeBlockPredicate.STRAIGHT_EAST);
    private static final BlockPredicate quartzStairsWest = new ComplexBlockPredicate(quartzStairs, StairShapeBlockPredicate.STRAIGHT_WEST);
    private static final BlockPredicate quartzStairsNorth = new ComplexBlockPredicate(quartzStairs, StairShapeBlockPredicate.STRAIGHT_NORTH);
    private static final BlockPredicate quartzStairsSouth = new ComplexBlockPredicate(quartzStairs, StairShapeBlockPredicate.STRAIGHT_SOUTH);

    private static final BlockPredicate quartzStairsNorthEast = new ComplexBlockPredicate(quartzStairs, StairShapeBlockPredicate.OUTER_NORTH_EAST);
    private static final BlockPredicate quartzStairsNorthWest = new ComplexBlockPredicate(quartzStairs, StairShapeBlockPredicate.OUTER_NORTH_WEST);
    private static final BlockPredicate quartzStairsSouthEast = new ComplexBlockPredicate(quartzStairs, StairShapeBlockPredicate.OUTER_SOUTH_EAST);
    private static final BlockPredicate quartzStairsSouthWest = new ComplexBlockPredicate(quartzStairs, StairShapeBlockPredicate.OUTER_SOUTH_WEST);

    public static final MultiBlockStructureMetadata altar =
            new MultiBlockStructureMetadata(
                    BlazingGames.get().key("altar_of_enchanting"),
                    "Altar of Enchanting",
                    Style.style(NamedTextColor.DARK_PURPLE),
                    new MultiLevelBlockStructure(
                            new MultiBlockStructure()
                                    .add(new Vector(0, 0, 0), enchantingTable),
                            new MultiBlockStructure()
                                    // base
                                    .addArea(new Vector(-1, -1, -1), new Vector(1, -1, -1), smoothQuartz)
                                    .addArea(new Vector(-1, -1, 1), new Vector(1, -1, 1), smoothQuartz)
                                    .add(new Vector(-1, -1, 0), smoothQuartz)
                                    .add(new Vector(1, -1, 0), smoothQuartz)
                                    // stairs
                                    .addArea(new Vector(-1, -1, -2), new Vector(1, -1, -2), quartzStairsSouth)
                                    .addArea(new Vector(-1, -1, 2), new Vector(1, -1, 2), quartzStairsNorth)
                                    .addArea(new Vector(-2, -1, -1), new Vector(-2, -1, 1), quartzStairsEast)
                                    .addArea(new Vector(2, -1, -1), new Vector(2, -1, 1), quartzStairsWest)
                                    // pillars
                                    .addArea(new Vector(-2, -1, -2), new Vector(-2, 1, -2), obsidian)
                                    .addArea(new Vector(2, -1, 2), new Vector(2, 1, 2), obsidian)
                                    .addArea(new Vector(2, -1, -2), new Vector(2, 1, -2), obsidian)
                                    .addArea(new Vector(-2, -1, 2), new Vector(-2, 1, 2), obsidian),
                            new MultiBlockStructure()
                                    // stairs
                                    .addArea(new Vector(-2, -2, -3), new Vector(2, -2, -3), quartzStairsSouth)
                                    .addArea(new Vector(-2, -2, 3), new Vector(2, -2, 3), quartzStairsNorth)
                                    .addArea(new Vector(-3, -2, -2), new Vector(-3, -2, 2), quartzStairsEast)
                                    .addArea(new Vector(3, -2, -2), new Vector(3, -2, 2), quartzStairsWest)
                                    // lapis lazuli blocks
                                    .add(new Vector(3, -2, 3), lapisBlock)
                                    .add(new Vector(-3, -2, 3), lapisBlock)
                                    .add(new Vector(3, -2, -3), lapisBlock)
                                    .add(new Vector(-3, -2, -3), lapisBlock),
                            new MultiBlockStructure()
                                    // upper stairs
                                    .addArea(new Vector(-3, -3, -4), new Vector(3, -3, -4), quartzStairsSouth)
                                    .addArea(new Vector(-3, -3, 4), new Vector(3, -3, 4), quartzStairsNorth)
                                    .addArea(new Vector(-4, -3, -3), new Vector(-4, -3, 3), quartzStairsEast)
                                    .addArea(new Vector(4, -3, -3), new Vector(4, -3, 3), quartzStairsWest)
                                    // corner stairs
                                    .add(new Vector(4, -3, 4), quartzStairsNorthWest)
                                    .add(new Vector(4, -3, -4), quartzStairsSouthWest)
                                    .add(new Vector(-4, -3, 4), quartzStairsNorthEast)
                                    .add(new Vector(-4, -3, -4), quartzStairsSouthEast)
                                    // lower stairs
                                    .addArea(new Vector(-4, -4, -5), new Vector(4, -4, -5), quartzStairsSouth)
                                    .addArea(new Vector(-4, -4, 5), new Vector(4, -4, 5), quartzStairsNorth)
                                    .addArea(new Vector(-5, -4, -4), new Vector(-5, -4, 4), quartzStairsEast)
                                    .addArea(new Vector(5, -4, -4), new Vector(5, -4, 4), quartzStairsWest)
                                    // pillars
                                    .addArea(new Vector(5, -4, 5), new Vector(5, 2, 5), obsidian)
                                    .addArea(new Vector(5, -4, -5), new Vector(5, 2, -5), obsidian)
                                    .addArea(new Vector(-5, -4, 5), new Vector(-5, 2, 5), obsidian)
                                    .addArea(new Vector(-5, -4, -5), new Vector(-5, 2, -5), obsidian)
                                    // diamond blocks
                                    .add(new Vector(5, 3, 5), diamondBlock)
                                    .add(new Vector(5, 3, -5), diamondBlock)
                                    .add(new Vector(-5, 3, 5), diamondBlock)
                                    .add(new Vector(-5, 3, -5), diamondBlock)
            ));
    //endregion
}
