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
package de.blazemcworld.blazinggames.items;

import com.google.common.collect.ImmutableSet;
import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.builderwand.BuilderWand;
import de.blazemcworld.blazinggames.crates.DeathCrateKey;
import de.blazemcworld.blazinggames.crates.SkeletonKey;
import de.blazemcworld.blazinggames.crates.ToGoBoxItem;
import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantments;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentTome;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentWrappers;
import de.blazemcworld.blazinggames.multiblocks.Blueprint;
import org.bukkit.NamespacedKey;

import javax.annotation.Nullable;
import java.util.*;

public class CustomItems implements ItemProvider {
    public static final CustomSlabs CUSTOM_SLABS = new CustomSlabs();

    public static final BuilderWand BUILDER_WAND = new BuilderWand();
    public static final PortableCraftingTable PORTABLE_CRAFTING_TABLE = new PortableCraftingTable();
    public static final TeleportAnchor TELEPORT_ANCHOR = new TeleportAnchor();
    public static final Blueprint BLUEPRINT = new Blueprint();
    public static final TomeAltar TOME_ALTAR = new TomeAltar();
    public static final DeathCrateKey DEATH_CRATE_KEY = new DeathCrateKey();
    public static final SkeletonKey SKELETON_KEY = new SkeletonKey();
    public static final ToGoBoxItem TO_GO_BOX = new ToGoBoxItem();
    public static final NetherStarChunk NETHER_STAR_CHUNK = new NetherStarChunk();

    public static final EnchantmentTome FUSE_TOME = new EnchantmentTome(BlazingGames.get().key("fuse_tome"), "Fuse Tome", EnchantmentWrappers.MENDING);
    public static final EnchantmentTome BIND_TOME = new EnchantmentTome(BlazingGames.get().key("bind_tome"), "Bind Tome", EnchantmentWrappers.BINDING_CURSE);
    public static final EnchantmentTome VANISH_TOME = new EnchantmentTome(BlazingGames.get().key("vanish_tome"), "Vanish Tome", EnchantmentWrappers.VANISHING_CURSE);
    public static final EnchantmentTome CHILL_TOME = new EnchantmentTome(BlazingGames.get().key("chill_tome"), "Chill Tome", EnchantmentWrappers.FROST_WALKER);
    public static final EnchantmentTome NETHER_TOME = new EnchantmentTome(BlazingGames.get().key("nether_tome"), "Nether Tome", EnchantmentWrappers.SOUL_SPEED);
    public static final EnchantmentTome ECHO_TOME = new EnchantmentTome(BlazingGames.get().key("echo_tome"), "Echo Tome", EnchantmentWrappers.SWIFT_SNEAK);
    public static final EnchantmentTome STORM_TOME = new EnchantmentTome(BlazingGames.get().key("storm_tome"), "Storm Tome", EnchantmentWrappers.WIND_BURST);
    public static final EnchantmentTome BLACK_TOME = new EnchantmentTome(BlazingGames.get().key("black_tome"), "Black Tome", CustomEnchantments.CAPTURING);
    public static final EnchantmentTome GUST_TOME = new EnchantmentTome(BlazingGames.get().key("gust_tome"), "Gust Tome", CustomEnchantments.UPDRAFT);
    public static final EnchantmentTome GREED_TOME = new EnchantmentTome(BlazingGames.get().key("greed_tome"), "Greed Tome", CustomEnchantments.SCAVENGER);
    public static final EnchantmentTome DIM_TOME = new EnchantmentTome(BlazingGames.get().key("dim_tome"), "Dim Tome", CustomEnchantments.UNSHINY);

    @Override
    public Set<CustomItem<?>> getItems() {
        return Set.of(
                BUILDER_WAND,
                PORTABLE_CRAFTING_TABLE,
                TELEPORT_ANCHOR,
                BLUEPRINT,
                TOME_ALTAR,
                DEATH_CRATE_KEY,
                SKELETON_KEY,
                TO_GO_BOX,
                NETHER_STAR_CHUNK,
                FUSE_TOME,
                BIND_TOME,
                VANISH_TOME,
                CHILL_TOME,
                NETHER_TOME,
                ECHO_TOME,
                STORM_TOME,
                BLACK_TOME,
                GUST_TOME,
                GREED_TOME,
                DIM_TOME
        );
    }

    private static Set<ItemProvider> getItemProviders() {
        ImmutableSet.Builder<ItemProvider> providers = new ImmutableSet.Builder<>();

        providers.add(new CustomItems());
        providers.add(CUSTOM_SLABS);

        return providers.build();
    }

    public static Set<CustomItem<?>> getAllItems() {
        Set<CustomItem<?>> items = new HashSet<>();

        for(ItemProvider provider : getItemProviders()) {
            items.addAll(provider.getItems());
        }

        return items;
    }

    public static @Nullable CustomItem<?> getByKey(NamespacedKey key) {
        for(CustomItem<?> curr : getAllItems()) {
            if(curr.getKey().equals(key)) {
                return curr;
            }
        }
        return null;
    }
}
