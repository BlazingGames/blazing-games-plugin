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
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentHelper;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentTome;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentWrapper;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentWrappers;
import de.blazemcworld.blazinggames.items.CustomItem;
import de.blazemcworld.blazinggames.userinterfaces.*;
import de.blazemcworld.blazinggames.utils.adapters.TextLocation;
import de.blazemcworld.blazinggames.utils.TomeAltarStorage;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class AltarInterface extends PagedUserInterface {
    private static final InputSlot toolSlot = new SingleInputSlot() {

        @Override
        public boolean filterItem(ItemStack stack) {
            if(!EnchantmentHelper.canEnchantItem(stack)) {
                return false;
            }
            return super.filterItem(stack);
        }
    };
    private static final InputSlot materialSlot = new InputSlot();
    private static final InputSlot lapisSlot = new InputSlot() {

        @Override
        public boolean filterItem(ItemStack stack) {
            if(CustomItem.isCustomItem(stack)) {
                return false;
            }
            if(stack.getType() != Material.LAPIS_LAZULI) {
                return false;
            }
            return super.filterItem(stack);
        }
    };

    private static final PageSlot forward = new PageSlot(PageSlot.Arrow.DOWN, PageSlot.Direction.FORWARD);
    private static final PageSlot backward = new PageSlot(PageSlot.Arrow.UP, PageSlot.Direction.BACKWARD);

    private final BlockState state;
    private final Map<String, ItemStack> altars;
    private int tier;

    public AltarInterface(BlazingGames plugin, BlockState state) {
        super(plugin, "Enchant", 5);
        this.state = state;
        this.tier = 0;
        this.altars = new HashMap<>();
        reloadAltar();
    }

    @Override
    public void preload() {
        StaticUserInterfaceSlot tool = new StaticUserInterfaceSlot(BlazingGames.get().key("tool_slot"));
        StaticUserInterfaceSlot randomMaterial = new StaticUserInterfaceSlot(BlazingGames.get().key("material_slot"));
        StaticUserInterfaceSlot lapisLazuli = new StaticUserInterfaceSlot(BlazingGames.get().key("lapis_lazuli_slot"));

        int index = 0;
        for(int y = 0; y < 5; y++) {
            addSlot(0, y, StaticUserInterfaceSlot.blank);
            addSlot(1, y, StaticUserInterfaceSlot.blank);
            addSlot(2, y, StaticUserInterfaceSlot.blank);

            for(int x = 3; x < 9; x++) {
                addSlot(x, y, new EnchantmentSlot(index));
                index++;
            }
        }

        addSlot(0, 1, tool);
        addSlot(0, 2, lapisLazuli);
        addSlot(0, 3, randomMaterial);

        addSlot(1, 1, toolSlot);
        addSlot(1, 2, lapisSlot);
        addSlot(1, 3, materialSlot);

        addSlot(1, 4, forward);
        addSlot(1, 0, backward);
    }

    @Override
    public void tick(Player p) {
        reloadAltar();

        reload();
    }

    @Override
    public void onClose(Player p) {
        for(Map.Entry<Integer, UserInterfaceSlot> slot : slots.entrySet()) {
            if(slot.getValue() instanceof UsableInterfaceSlot) {
                for(ItemStack overflow : p.getInventory().addItem(getItem(slot.getKey())).values()) {
                    p.getWorld().dropItemNaturally(p.getLocation(), overflow);
                }
            }
        }
    }

    private void reloadAltar() {
        tier = AltarOfEnchanting.altar.match(state.getLocation());

        if(tier <= 0) {
            getInventory().close();
            return;
        }

        List<Location> newAltars = TomeAltarStorage.getNear(state.getLocation(), 5);

        altars.clear();

        for (Location altar : newAltars) {
            ItemStack stack = TomeAltarStorage.getItem(altar);
            altars.put(TextLocation.serializeRounded(altar), stack);
        }

        int maxPage = getMaxPage();

        if(getCurrentPage() > getMaxPage()) {
            changePage(maxPage);
        }
    }

    public List<EnchantmentWrapper> getAvailable() {
        ItemStack tool = getItem(1,1);

        List<EnchantmentWrapper> treasured = new ArrayList<>();

        List<EnchantmentWrapper> result = new ArrayList<>(EnchantmentWrappers.instance.list());

        if(altars != null) {
            for(ItemStack tome : altars.values()) {
                if(CustomItem.getCustomItem(tome) instanceof EnchantmentTome customTome) {
                    treasured.add(customTome.getWrapper());
                }
            }
        }

        result.removeIf((wrapper) -> {
            if(wrapper.getRecipe(1).tier() > tier) {
                return true;
            }

            if(!wrapper.canEnchantItem(tool)) {
                return true;
            }

            if(wrapper.isTreasure()) {
                return !treasured.contains(wrapper);
            }

            return false;
        });

        return result;
    }

    public ItemStack getTool() {
        return getItem(1,1);
    }

    public ItemStack getLapis() {
        return getItem(1,2);
    }

    public ItemStack getMaterial() {
        return getItem(1,3);
    }

    public int getTier() {
        return tier;
    }

    @Override
    public int getMaxPage() {
        int available = getAvailable().size();

        if(available == 0) {
            return 0;
        }

        return (available-1) / indicesPerPage();
    }

    @Override
    public int indicesPerPage() {
        return 5*6;
    }
}
