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
package de.blazemcworld.blazinggames.items.predicates;

import de.blazemcworld.blazinggames.items.CustomItem;
import de.blazemcworld.blazinggames.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ColorlessItemPredicate implements ItemPredicate {
    private final Material material;

    public ColorlessItemPredicate(Material material) {
        this.material = ItemUtils.getUncoloredType(material);
    }

    @Override
    public boolean matchItem(ItemStack stack) {
        if(CustomItem.isCustomItem(stack)) {
            return false;
        }

        return ItemUtils.getUncoloredType(stack) == material;
    }

    @Override
    public Component getDescription() {
        return Component.text(switch(ItemUtils.getUncoloredType(material)) {
            case WHITE_WOOL -> "Any Wool";
            case WHITE_BED -> "Any Bed";
            case OAK_BOAT -> "Any Boat or Raft";
            default -> "Unknown Colorless Material!";
        });
    }
}
