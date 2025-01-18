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

import org.bukkit.Material;
import org.bukkit.block.BlockState;

import java.util.List;

public class ChoiceBlockPredicate implements BlockPredicate {
    List<Material> possibleMaterials;

    public ChoiceBlockPredicate(Material... blockMaterial) {
        this.possibleMaterials = List.of(blockMaterial);
    }

    public Material getFirstMaterial() {
        return possibleMaterials.getFirst();
    }

    public Material getRandomMaterial() {
        return possibleMaterials.get((int) (Math.random() * possibleMaterials.size()));
    }

    public Material getMaterial(int index) {
        return possibleMaterials.get(index);
    }

    public List<Material> getPossibleMaterials() {
        return possibleMaterials;
    }

    @Override
    public boolean matchBlock(BlockState block, int direction) {
        return possibleMaterials.contains(block.getType());
    }

    @Override
    public String toString() {
        return possibleMaterials.toString();
    }
}
