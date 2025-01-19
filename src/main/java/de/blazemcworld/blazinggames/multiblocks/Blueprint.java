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
package de.blazemcworld.blazinggames.multiblocks;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.items.ContextlessItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Blueprint extends ContextlessItem {
    @Override
    public @NotNull NamespacedKey getKey() {
        return BlazingGames.get().key("blueprint");
    }

    @Override
    protected @NotNull Component itemName() {
        return Component.text("Blueprint").color(NamedTextColor.BLUE);
    }

    public void outputMultiBlockProgress(Player player, Location location) {
        List<Component> output = new ArrayList<>();

        for(MultiBlockStructureMetadata data : MultiBlockStructures.list()) {
            if(data.matchTarget(location)) {
                output.addAll(data.getProgress(location));
            }
        }

        if(output.isEmpty()) {
            output.add(Component.text("No Multi-Block Structure Found!").color(NamedTextColor.RED));
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, SoundCategory.PLAYERS, 2, 1);
        }
        else {
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, SoundCategory.PLAYERS, 2, 1);
        }

        for(Component component : output) {
            player.sendMessage(component);
        }
    }

    public Map<NamespacedKey, Recipe> getRecipes() {
        ShapedRecipe wandRecipe = new ShapedRecipe(getKey(), create());
        wandRecipe.shape(
                "PPP",
                "PBP",
                "PPP"
        );
        wandRecipe.setIngredient('P', Material.PAPER);
        wandRecipe.setIngredient('B', Material.BLUE_DYE);

        return Map.of(
            getKey(), wandRecipe
        );
    }

    @Override
    protected int stackSize() {
        return 1;
    }
}

