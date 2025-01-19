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
package de.blazemcworld.blazinggames.commands;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.multiblocks.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class SetAltar implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command,
                             @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player p)) {
            commandSender.sendMessage(Component.text("Only players can use this command!")
                    .color(NamedTextColor.RED));
            return true;
        }

        if(strings.length != 1) {
            CommandHelper.sendUsage(commandSender, command);
            return true;
        }

        int level;
        try {
            level = Integer.parseInt(strings[0]);
        } catch (NumberFormatException e) {
            commandSender.sendMessage(Component.text("Unknown structure level: " + strings[0] + "!").color(NamedTextColor.RED));
            return true;
        }

        MultiBlockStructureMetadata structureMetadata = MultiBlockStructures.getByKey(BlazingGames.get().key("altar_of_enchanting"));

        Location oldLoc = p.getLocation();
        assert structureMetadata != null;
        int index = 0;
        for (MultiBlockStructure structure : ((MultiLevelBlockStructure) structureMetadata.getStructure()).getLevels()) {
            if (index >= level) break;
            index += 1;
            for (Map.Entry<Vector, BlockPredicate> entry : structure.getPredicates().entrySet()) {
                Vector vector = entry.getKey();
                BlockPredicate predicate = entry.getValue();
                Location loc = oldLoc.clone().add(vector);
                if (predicate instanceof SingleBlockPredicate) {
                    loc.getBlock().setType(((SingleBlockPredicate) predicate).getMaterial());
                } else if (predicate instanceof ChoiceBlockPredicate) {
                    loc.getBlock().setType(((ChoiceBlockPredicate) predicate).getRandomMaterial());
                } else if (predicate instanceof ComplexBlockPredicate complexBlockPredicate) {
                    StairShapeBlockPredicate shape = (StairShapeBlockPredicate) complexBlockPredicate.getPredicates().get(1);

                    loc.getBlock().setType(Material.QUARTZ_STAIRS);
                    Stairs data = (Stairs) loc.getBlock().getBlockData();
                    data.setShape((shape.name().startsWith("STRAIGHT") ? Stairs.Shape.STRAIGHT : Stairs.Shape.OUTER_LEFT));
                    data.setHalf(Stairs.Half.BOTTOM);
                    data.setFacing(switch (shape.name()) {
                        case "STRAIGHT_SOUTH" -> BlockFace.SOUTH;
                        case "STRAIGHT_EAST", "INNER_NORTH_EAST", "INNER_SOUTH_EAST", "OUTER_NORTH_EAST",
                             "OUTER_SOUTH_EAST" -> BlockFace.EAST;
                        case "STRAIGHT_WEST", "INNER_NORTH_WEST", "INNER_SOUTH_WEST", "OUTER_NORTH_WEST",
                             "OUTER_SOUTH_WEST" -> BlockFace.WEST;
                        default -> BlockFace.NORTH;
                    });
                    loc.getBlock().setBlockData(data);
                }
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command,
                                                @NotNull String s, @NotNull String[] strings) {
        return List.of("1", "2", "3", "4", "5");
    }
}
