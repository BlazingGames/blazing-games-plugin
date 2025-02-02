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
import com.google.gson.JsonObject;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.builderwand.BuilderWand;
import de.blazemcworld.blazinggames.builderwand.BuilderWandMode;
import de.blazemcworld.blazinggames.crates.DeathCrateKey;
import de.blazemcworld.blazinggames.crates.SkeletonKey;
import de.blazemcworld.blazinggames.crates.ToGoBoxItem;
import de.blazemcworld.blazinggames.enchantments.sys.CustomEnchantments;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentTome;
import de.blazemcworld.blazinggames.enchantments.sys.EnchantmentWrappers;
import de.blazemcworld.blazinggames.multiblocks.Blueprint;
import de.blazemcworld.blazinggames.packs.HookContext;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import javax.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;

public class CustomItems implements ItemProvider {
    public static final CustomSlabs CUSTOM_SLABS = new CustomSlabs();

    public static final PortableCraftingTable PORTABLE_CRAFTING_TABLE = new PortableCraftingTable();
    public static final TeleportAnchor TELEPORT_ANCHOR = new TeleportAnchor();
    public static final Blueprint BLUEPRINT = new Blueprint();
    public static final TomeAltar TOME_ALTAR = new TomeAltar();
    public static final DeathCrateKey DEATH_CRATE_KEY = new DeathCrateKey();
    public static final SkeletonKey SKELETON_KEY = new SkeletonKey();
    public static final ToGoBoxItem TO_GO_BOX = new ToGoBoxItem();
    public static final NetherStarChunk NETHER_STAR_CHUNK = new NetherStarChunk();

    public static final BuilderWand WOODEN_BUILDER_WAND = new BuilderWand(
            BlazingGames.get().key("wooden_builder_wand"),
            Component.text("Wooden Builder's Wand"),
            16, 256,
            Material.FLINT, Material.STICK,
            BuilderWandMode.NO_LOCK
    );
    public static final BuilderWand STORM_BUILDER_WAND = new BuilderWand(
            BlazingGames.get().key("storm_builder_wand"),
            Component.text("Storm Builder's Wand").color(NamedTextColor.BLUE),
            64, 2048,
            Material.DIAMOND, Material.BREEZE_ROD,
            BuilderWandMode.NO_LOCK, BuilderWandMode.HORIZONTAL, BuilderWandMode.VERTICAL
    );
    public static final BuilderWand BLAZING_BUILDER_WAND = new BuilderWand(
            BlazingGames.get().key("blazing_builder_wand"),
            Component.text("Blazing Builder's Wand").color(NamedTextColor.GOLD),
            128, 16384,
            Material.NETHER_STAR, Material.BLAZE_ROD, CustomItems.NETHER_STAR_CHUNK,
            BuilderWandMode.NO_LOCK, BuilderWandMode.HORIZONTAL, BuilderWandMode.VERTICAL,
            BuilderWandMode.NORTH_SOUTH, BuilderWandMode.NORTH_SOUTH_VERTICAL,
            BuilderWandMode.EAST_WEST, BuilderWandMode.EAST_WEST_VERTICAL
    );

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
                PORTABLE_CRAFTING_TABLE,
                TELEPORT_ANCHOR,
                BLUEPRINT,
                TOME_ALTAR,
                DEATH_CRATE_KEY,
                SKELETON_KEY,
                TO_GO_BOX,
                NETHER_STAR_CHUNK,
                WOODEN_BUILDER_WAND,
                STORM_BUILDER_WAND,
                BLAZING_BUILDER_WAND,
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

    public static Set<ItemProvider> getItemProviders() {
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

    @Override
    public void runHook(Logger logger, HookContext context) {
        for (CustomItem<?> item : getItems()) {
            // install texture
            try (InputStream stream = item.getClass().getResourceAsStream("/customitems/" + item.getKey().getKey() + ".png")) {
                if (stream != null) context.installTexture(item.getKey(), "item", stream.readAllBytes());
            } catch (IOException e) {
                BlazingGames.get().log(e);
            }

            // install animation options
            try (InputStream stream = item.getClass().getResourceAsStream("/customitems/" + item.getKey().getKey() + ".png.mcmeta")) {
                if (stream != null) context.installTextureAnimationData(item.getKey(), "item", stream.readAllBytes());
            } catch (IOException e) {
                BlazingGames.get().log(e);
            }

            // install model
            try (InputStream stream = item.getClass().getResourceAsStream("/customitems/" + item.getKey().getKey() + ".json")) {
                if (stream != null) context.installModel(item.getKey(), stream.readAllBytes());
            } catch (IOException e) {
                BlazingGames.get().log(e);
            }

            // create items data
            JsonObject root = new JsonObject();
            JsonObject model = new JsonObject();
            model.addProperty("type", "minecraft:model");
            model.addProperty("model", item.getKey().toString());
            root.add("model", model);
            context.writeFile("/assets/" + item.getKey().getNamespace() + "/items/" + item.getKey().getKey() + ".json", root);
        }
    }
}
