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
