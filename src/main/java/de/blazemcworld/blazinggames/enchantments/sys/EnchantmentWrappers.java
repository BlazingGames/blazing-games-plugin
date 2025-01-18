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
package de.blazemcworld.blazinggames.enchantments.sys;

import de.blazemcworld.blazinggames.enchantments.sys.VanillaEnchantmentWrapper.AltarTiers;
import de.blazemcworld.blazinggames.enchantments.sys.VanillaEnchantmentWrapper.Warning;
import de.blazemcworld.blazinggames.enchantments.sys.altar.AltarRecipe;
import de.blazemcworld.blazinggames.items.predicates.ColorlessItemPredicate;
import de.blazemcworld.blazinggames.items.CustomItems;
import de.blazemcworld.blazinggames.items.predicates.MaterialItemPredicate;
import de.blazemcworld.blazinggames.items.predicates.PotionItemPredicate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnchantmentWrappers {
    public static VanillaEnchantmentWrapper MULTISHOT =
            new VanillaEnchantmentWrapper(Enchantment.MULTISHOT, () -> new ItemStack(Material.PRISMARINE_CRYSTALS),
                    new AltarTiers(0, 1),
                    new AltarRecipe(1, 4, 16, new MaterialItemPredicate(Material.ARROW))
            );
    public static VanillaEnchantmentWrapper PIERCING =
            new VanillaEnchantmentWrapper(Enchantment.PIERCING, () -> new ItemStack(Material.SPECTRAL_ARROW),
                    new AltarTiers(1, 2, 3, 4),
                    new AltarRecipe(1, 2, 32, new MaterialItemPredicate(Material.ROTTEN_FLESH)),
                    new AltarRecipe(2, 4, 32, new MaterialItemPredicate(Material.BONE)),
                    new AltarRecipe(3, 6, 32, new MaterialItemPredicate(Material.SPIDER_EYE)),
                    new AltarRecipe(4, 8, 32, new MaterialItemPredicate(Material.GUNPOWDER))
            );
    public static VanillaEnchantmentWrapper SILK_TOUCH =
            new VanillaEnchantmentWrapper(Enchantment.SILK_TOUCH, () -> new ItemStack(Material.STRING),
                    new AltarTiers(0, 0, 1, 1, 2),
                    List.of(
                            new Warning("Might break pickaxe when used on spawners.\nAlways breaks iron pickaxes and lower.\nUses 2/3rd of durability on diamond pickaxes.\nUses half of durability on netherite pickaxes.", 2)
                    ),
                    new AltarRecipe(1, 15, 32, new MaterialItemPredicate(Material.STRING)),
                    new AltarRecipe(2, 30, 1, new MaterialItemPredicate(Material.NETHERITE_INGOT))
            );
    public static VanillaEnchantmentWrapper VANISHING_CURSE =
            new VanillaEnchantmentWrapper(Enchantment.VANISHING_CURSE, () -> new ItemStack(Material.GLASS),
                    new AltarTiers(0, 1),
                    new AltarRecipe(1, 8, new PotionItemPredicate(PotionEffectType.INVISIBILITY))
            );
    public static VanillaEnchantmentWrapper FROST_WALKER =
            new VanillaEnchantmentWrapper(Enchantment.FROST_WALKER, () -> new ItemStack(Material.BLUE_ICE),
                    new AltarTiers(0, 1, 2),
                    new AltarRecipe(1, 2, 16, new MaterialItemPredicate(Material.BLUE_ICE)),
                    new AltarRecipe(2, 4, 16, new MaterialItemPredicate(Material.PRISMARINE_CRYSTALS))
            );
    public static VanillaEnchantmentWrapper FORTUNE =
            new VanillaEnchantmentWrapper(Enchantment.FORTUNE, () -> new ItemStack(Material.EMERALD),
                    new AltarTiers(0, 1, 2, 3),
                    new AltarRecipe(1, 2, 16, new MaterialItemPredicate(Material.EMERALD)),
                    new AltarRecipe(2, 4, 16, new MaterialItemPredicate(Material.DIAMOND)),
                    new AltarRecipe(3, 8, new MaterialItemPredicate(Material.RABBIT_FOOT))
            );
    public static VanillaEnchantmentWrapper BINDING_CURSE =
            new VanillaEnchantmentWrapper(Enchantment.BINDING_CURSE, () -> new ItemStack(Material.CHAIN),
                    new AltarTiers(0, 1),
                    new AltarRecipe(1, 8, 16, new MaterialItemPredicate(Material.COBWEB))
            );
    public static VanillaEnchantmentWrapper SHARPNESS =
            new VanillaEnchantmentWrapper(Enchantment.SHARPNESS, () -> new ItemStack(Material.DIAMOND_SWORD),
                    new AltarTiers(1, 2, 3, 5),
                    new AltarRecipe(1, 2, 32, new MaterialItemPredicate(Material.REDSTONE)),
                    new AltarRecipe(2, 4, 16, new MaterialItemPredicate(Material.REDSTONE_BLOCK)),
                    new AltarRecipe(3, 6, 16, new MaterialItemPredicate(Material.BLAZE_POWDER)),
                    new AltarRecipe(4, 8, 8, new MaterialItemPredicate(Material.DIAMOND)),
                    new AltarRecipe(5, 10, new PotionItemPredicate(PotionEffectType.STRENGTH))
            );
    public static VanillaEnchantmentWrapper SWEEPING_EDGE =
            new VanillaEnchantmentWrapper(Enchantment.SWEEPING_EDGE, () -> new ItemStack(Material.FEATHER),
                    new AltarTiers(1, 2, 3),
                    new AltarRecipe(1, 1, 32, new MaterialItemPredicate(Material.ROTTEN_FLESH)),
                    new AltarRecipe(2, 2, 32, new MaterialItemPredicate(Material.BONE)),
                    new AltarRecipe(3, 4, 32, new MaterialItemPredicate(Material.STRING))
            );
    public static VanillaEnchantmentWrapper RIPTIDE =
            new VanillaEnchantmentWrapper(Enchantment.RIPTIDE, () -> new ItemStack(Material.NAUTILUS_SHELL),
                    new AltarTiers(0, 0, 1, 3),
                    new AltarRecipe(1, 4, 16, new MaterialItemPredicate(Material.CHAIN)),
                    new AltarRecipe(2, 6, 16, new MaterialItemPredicate(Material.PISTON)),
                    new AltarRecipe(3, 8, 16, new MaterialItemPredicate(Material.TNT))
            );
    public static VanillaEnchantmentWrapper QUICK_CHARGE =
            new VanillaEnchantmentWrapper(Enchantment.QUICK_CHARGE, () -> new ItemStack(Material.REDSTONE),
                    new AltarTiers(0, 1, 2, 3),
                    new AltarRecipe(1, 4, 16, new MaterialItemPredicate(Material.LEATHER)),
                    new AltarRecipe(2, 6, 16, new MaterialItemPredicate(Material.STRING)),
                    new AltarRecipe(3, 8, new PotionItemPredicate(PotionEffectType.SPEED))
            );
    public static VanillaEnchantmentWrapper IMPALING =
            new VanillaEnchantmentWrapper(Enchantment.IMPALING, () -> new ItemStack(Material.TRIDENT),
                    new AltarTiers(1, 2, 3, 5),
                    new AltarRecipe(1, 1, new MaterialItemPredicate(Material.WATER_BUCKET)),
                    new AltarRecipe(2, 2, 32, new MaterialItemPredicate(Material.ROTTEN_FLESH)),
                    new AltarRecipe(3, 3, 16, new MaterialItemPredicate(Material.COD)),
                    new AltarRecipe(4, 4, 16, new MaterialItemPredicate(Material.SALMON)),
                    new AltarRecipe(5, 5, 16, new MaterialItemPredicate(Material.PUFFERFISH))
            );
    public static VanillaEnchantmentWrapper FIRE_PROTECTION =
            new VanillaEnchantmentWrapper(Enchantment.FIRE_PROTECTION, () -> new ItemStack(Material.MAGMA_CREAM),
                    new AltarTiers(1, 2, 3, 4),
                    new AltarRecipe(1, 1, 32, new MaterialItemPredicate(Material.COPPER_INGOT)),
                    new AltarRecipe(2, 2, 32, new MaterialItemPredicate(Material.IRON_INGOT)),
                    new AltarRecipe(3, 3, 16, new MaterialItemPredicate(Material.BLAZE_POWDER)),
                    new AltarRecipe(4, 4, 32, new MaterialItemPredicate(Material.BLAZE_POWDER))
            );
    public static VanillaEnchantmentWrapper MENDING =
            new VanillaEnchantmentWrapper(Enchantment.MENDING, () -> new ItemStack(Material.SHULKER_SHELL),
                    new AltarTiers(0, 0, 0, 1),
                    new AltarRecipe(10, 15, CustomItems.NETHER_STAR_CHUNK)
            );
    public static VanillaEnchantmentWrapper LOYALTY =
            new VanillaEnchantmentWrapper(Enchantment.LOYALTY, () -> new ItemStack(Material.STICK),
                    new AltarTiers(0, 1, 2, 3),
                    new AltarRecipe(1, 2, 16, new MaterialItemPredicate(Material.BONE)),
                    new AltarRecipe(2, 3, 16, new MaterialItemPredicate(Material.COD)),
                    new AltarRecipe(3, 4, 16, new MaterialItemPredicate(Material.CHAIN))
            );
    public static VanillaEnchantmentWrapper LUCK_OF_THE_SEA =
            new VanillaEnchantmentWrapper(Enchantment.LUCK_OF_THE_SEA, () -> new ItemStack(Material.HEART_OF_THE_SEA),
                    new AltarTiers(1, 2, 3),
                    new AltarRecipe(1, 1, 16, new MaterialItemPredicate(Material.COD)),
                    new AltarRecipe(2, 2, 8, new MaterialItemPredicate(Material.CHEST)),
                    new AltarRecipe(3, 4, new MaterialItemPredicate(Material.HEART_OF_THE_SEA))
            );
    public static VanillaEnchantmentWrapper RESPIRATION =
            new VanillaEnchantmentWrapper(Enchantment.RESPIRATION, () -> new ItemStack(Material.PUFFERFISH),
                    new AltarTiers(1, 2, 3),
                    new AltarRecipe(1, 1, new MaterialItemPredicate(Material.WATER_BUCKET)),
                    new AltarRecipe(2, 2, new MaterialItemPredicate(Material.TURTLE_HELMET)),
                    new AltarRecipe(3, 4, new PotionItemPredicate(PotionEffectType.WATER_BREATHING))
            );
    public static VanillaEnchantmentWrapper FLAME =
            new VanillaEnchantmentWrapper(Enchantment.FLAME, () -> new ItemStack(Material.BLAZE_POWDER),
                    new AltarTiers(0, 1),
                    new AltarRecipe(1, 4, 16, new MaterialItemPredicate(Material.BLAZE_POWDER))
            );
    public static VanillaEnchantmentWrapper PUNCH =
            new VanillaEnchantmentWrapper(Enchantment.PUNCH, () -> new ItemStack(Material.PISTON),
                    new AltarTiers(0, 1, 2),
                    new AltarRecipe(1, 2, 10, new MaterialItemPredicate(Material.SLIME_BLOCK)),
                    new AltarRecipe(2, 4, 10, new MaterialItemPredicate(Material.PISTON))
            );
    public static VanillaEnchantmentWrapper BLAST_PROTECTION =
            new VanillaEnchantmentWrapper(Enchantment.BLAST_PROTECTION, () -> new ItemStack(Material.TNT),
                    new AltarTiers(1, 2, 3, 4),
                    new AltarRecipe(1, 1, 32, new MaterialItemPredicate(Material.COPPER_INGOT)),
                    new AltarRecipe(2, 2, 32, new MaterialItemPredicate(Material.IRON_INGOT)),
                    new AltarRecipe(3, 3, 16, new MaterialItemPredicate(Material.TNT)),
                    new AltarRecipe(4, 4, 32, new MaterialItemPredicate(Material.TNT))
            );
    public static VanillaEnchantmentWrapper PROJECTILE_PROTECTION =
            new VanillaEnchantmentWrapper(Enchantment.PROJECTILE_PROTECTION, () -> new ItemStack(Material.ARROW),
                    new AltarTiers(1, 2, 3, 4),
                    new AltarRecipe(1, 1, 32, new MaterialItemPredicate(Material.COPPER_INGOT)),
                    new AltarRecipe(2, 2, 32, new MaterialItemPredicate(Material.IRON_INGOT)),
                    new AltarRecipe(3, 3, 16, new MaterialItemPredicate(Material.ARROW)),
                    new AltarRecipe(4, 4, 32, new MaterialItemPredicate(Material.ARROW))
            );
    public static VanillaEnchantmentWrapper PROTECTION =
            new VanillaEnchantmentWrapper(Enchantment.PROTECTION, () -> new ItemStack(Material.DIAMOND_CHESTPLATE),
                    new AltarTiers(2, 3, 4, 4),
                    new AltarRecipe(1, 2, 32, new MaterialItemPredicate(Material.COPPER_INGOT)),
                    new AltarRecipe(2, 4, 32, new MaterialItemPredicate(Material.IRON_INGOT)),
                    new AltarRecipe(3, 6, 32, new MaterialItemPredicate(Material.GOLD_INGOT)),
                    new AltarRecipe(4, 8, 16, new MaterialItemPredicate(Material.DIAMOND))
            );
    public static VanillaEnchantmentWrapper AQUA_AFFINITY =
            new VanillaEnchantmentWrapper(Enchantment.AQUA_AFFINITY, () -> new ItemStack(Material.PRISMARINE),
                    new AltarTiers(1),
                    new AltarRecipe(1, 4, 16, new MaterialItemPredicate(Material.PRISMARINE_SHARD))
            );
    public static VanillaEnchantmentWrapper SMITE =
            new VanillaEnchantmentWrapper(Enchantment.SMITE, () -> new ItemStack(Material.ROTTEN_FLESH),
                    new AltarTiers(1, 2, 3, 5),
                    new AltarRecipe(1, 1, 10, new MaterialItemPredicate(Material.ROTTEN_FLESH)),
                    new AltarRecipe(2, 2, 20, new MaterialItemPredicate(Material.ROTTEN_FLESH)),
                    new AltarRecipe(3, 3, 30, new MaterialItemPredicate(Material.ROTTEN_FLESH)),
                    new AltarRecipe(4, 4, 40, new MaterialItemPredicate(Material.ROTTEN_FLESH)),
                    new AltarRecipe(5, 5, 50, new MaterialItemPredicate(Material.ROTTEN_FLESH))
            );
    public static VanillaEnchantmentWrapper POWER =
            new VanillaEnchantmentWrapper(Enchantment.POWER, () -> new ItemStack(Material.BOW),
                    new AltarTiers(1, 2, 3, 5),
                    new AltarRecipe(1, 2, 32, new MaterialItemPredicate(Material.REDSTONE)),
                    new AltarRecipe(2, 4, 16, new MaterialItemPredicate(Material.REDSTONE_BLOCK)),
                    new AltarRecipe(3, 6, 16, new MaterialItemPredicate(Material.BLAZE_POWDER)),
                    new AltarRecipe(4, 8, 8, new MaterialItemPredicate(Material.DIAMOND)),
                    new AltarRecipe(5, 10, new PotionItemPredicate(PotionEffectType.STRENGTH))
            );
    public static VanillaEnchantmentWrapper THORNS =
            new VanillaEnchantmentWrapper(Enchantment.THORNS, () -> new ItemStack(Material.PUFFERFISH),
                    new AltarTiers(0, 1, 2, 3),
                    new AltarRecipe(1, 1, 16, new MaterialItemPredicate(Material.CACTUS)),
                    new AltarRecipe(2, 2, 16, new MaterialItemPredicate(Material.SWEET_BERRIES)),
                    new AltarRecipe(3, 4, new MaterialItemPredicate(Material.DIAMOND_SWORD))
            );
    public static VanillaEnchantmentWrapper LOOTING =
            new VanillaEnchantmentWrapper(Enchantment.LOOTING, () -> new ItemStack(Material.EMERALD),
                    new AltarTiers(0, 1, 2, 3),
                    new AltarRecipe(1, 2, 16, new MaterialItemPredicate(Material.CHEST)),
                    new AltarRecipe(2, 4, 16, new MaterialItemPredicate(Material.EMERALD)),
                    new AltarRecipe(3, 8, 10, new MaterialItemPredicate(Material.DIAMOND))
            );
    public static VanillaEnchantmentWrapper FEATHER_FALLING =
            new VanillaEnchantmentWrapper(Enchantment.FEATHER_FALLING, () -> new ItemStack(Material.FEATHER),
                    new AltarTiers(0, 1, 2, 4),
                    new AltarRecipe(1, 2, 10, new MaterialItemPredicate(Material.FEATHER)),
                    new AltarRecipe(2, 4, 5, new MaterialItemPredicate(Material.HAY_BLOCK)),
                    new AltarRecipe(3, 6, new ColorlessItemPredicate(Material.WHITE_BED)),
                    new AltarRecipe(4, 8, 32, new ColorlessItemPredicate(Material.WHITE_WOOL))
            );
    public static VanillaEnchantmentWrapper FIRE_ASPECT =
            new VanillaEnchantmentWrapper(Enchantment.FIRE_ASPECT, () -> new ItemStack(Material.BLAZE_POWDER),
                    new AltarTiers(0, 1, 2),
                    new AltarRecipe(1, 4, 16, new MaterialItemPredicate(Material.BLAZE_POWDER)),
                    new AltarRecipe(2, 8, 32, new MaterialItemPredicate(Material.BLAZE_POWDER))
            );
    public static VanillaEnchantmentWrapper UNBREAKING =
            new VanillaEnchantmentWrapper(Enchantment.UNBREAKING, () -> new ItemStack(Material.BEDROCK),
                    new AltarTiers(0, 1, 2, 3),
                    new AltarRecipe(1, 2, 32, new MaterialItemPredicate(Material.IRON_INGOT)),
                    new AltarRecipe(2, 4, 32, new MaterialItemPredicate(Material.OBSIDIAN)),
                    new AltarRecipe(3, 8, 2, new MaterialItemPredicate(Material.NETHERITE_SCRAP))
            );
    public static VanillaEnchantmentWrapper CHANNELING =
            new VanillaEnchantmentWrapper(Enchantment.CHANNELING, () -> new ItemStack(Material.LIGHTNING_ROD),
                    new AltarTiers(0, 0, 1),
                    new AltarRecipe(1, 8, 1, new MaterialItemPredicate(Material.LIGHTNING_ROD))
            );
    public static VanillaEnchantmentWrapper LURE =
            new VanillaEnchantmentWrapper(Enchantment.LURE, () -> new ItemStack(Material.TRIPWIRE_HOOK),
                    new AltarTiers(1, 2, 3),
                    new AltarRecipe(1, 1, 16, new MaterialItemPredicate(Material.STRING)),
                    new AltarRecipe(2, 2, 16, new MaterialItemPredicate(Material.COD)),
                    new AltarRecipe(3, 4, 16, new MaterialItemPredicate(Material.PUFFERFISH))
            );
    public static VanillaEnchantmentWrapper INFINITY =
            new VanillaEnchantmentWrapper(Enchantment.INFINITY, () -> new ItemStack(Material.CHORUS_FLOWER),
                    new AltarTiers(0, 0, 0, 1),
                    new AltarRecipe(1, 8, 64, new MaterialItemPredicate(Material.ARROW))
            );
    public static VanillaEnchantmentWrapper EFFICIENCY =
            new VanillaEnchantmentWrapper(Enchantment.EFFICIENCY, () -> new ItemStack(Material.REDSTONE),
                    new AltarTiers(1, 2, 3, 5),
                    new AltarRecipe(1, 2, 32, new MaterialItemPredicate(Material.SUGAR)),
                    new AltarRecipe(2, 4, 32, new MaterialItemPredicate(Material.REDSTONE)),
                    new AltarRecipe(3, 6, 16, new MaterialItemPredicate(Material.REDSTONE_BLOCK)),
                    new AltarRecipe(4, 8, 32, new MaterialItemPredicate(Material.GOLD_INGOT)),
                    new AltarRecipe(5, 10, new PotionItemPredicate(PotionEffectType.SPEED))
            );
    public static VanillaEnchantmentWrapper DEPTH_STRIDER =
            new VanillaEnchantmentWrapper(Enchantment.DEPTH_STRIDER, () -> new ItemStack(Material.COD),
                    new AltarTiers(0, 1, 2, 3),
                    new AltarRecipe(1, 1, new ColorlessItemPredicate(Material.OAK_BOAT)),
                    new AltarRecipe(2, 2, new MaterialItemPredicate(Material.MINECART)),
                    new AltarRecipe(3, 4, 3, new MaterialItemPredicate(Material.RABBIT_FOOT))
            );
    public static VanillaEnchantmentWrapper KNOCKBACK =
            new VanillaEnchantmentWrapper(Enchantment.KNOCKBACK, () -> new ItemStack(Material.PISTON),
                    new AltarTiers(0, 1, 2),
                    new AltarRecipe(1, 2, 10, new MaterialItemPredicate(Material.SLIME_BLOCK)),
                    new AltarRecipe(2, 4, 10, new MaterialItemPredicate(Material.PISTON))
            );
    public static VanillaEnchantmentWrapper SOUL_SPEED =
            new VanillaEnchantmentWrapper(Enchantment.SOUL_SPEED, () -> new ItemStack(Material.SOUL_SAND),
                    new AltarTiers(0, 1, 2, 3),
                    new AltarRecipe(1, 1, 16, new MaterialItemPredicate(Material.SOUL_SAND)),
                    new AltarRecipe(2, 2, 16, new MaterialItemPredicate(Material.SOUL_SOIL)),
                    new AltarRecipe(3, 4, 32, new MaterialItemPredicate(Material.SOUL_LANTERN))
            );
    public static VanillaEnchantmentWrapper SWIFT_SNEAK =
            new VanillaEnchantmentWrapper(Enchantment.SWIFT_SNEAK, () -> new ItemStack(Material.ECHO_SHARD),
                    new AltarTiers(0, 1, 2, 3),
                    new AltarRecipe(1, 1, 16, new ColorlessItemPredicate(Material.WHITE_WOOL)),
                    new AltarRecipe(2, 2, 16, new MaterialItemPredicate(Material.SCULK)),
                    new AltarRecipe(3, 4, 4, new MaterialItemPredicate(Material.ECHO_SHARD))
            );
    public static VanillaEnchantmentWrapper BANE_OF_ARTHROPODS =
            new VanillaEnchantmentWrapper(Enchantment.BANE_OF_ARTHROPODS, () -> new ItemStack(Material.SPIDER_EYE),
                    new AltarTiers(1, 2, 3, 5),
                    new AltarRecipe(1, 1, 10, new MaterialItemPredicate(Material.SPIDER_EYE)),
                    new AltarRecipe(2, 2, 20, new MaterialItemPredicate(Material.SPIDER_EYE)),
                    new AltarRecipe(3, 3, 30, new MaterialItemPredicate(Material.SPIDER_EYE)),
                    new AltarRecipe(4, 4, 40, new MaterialItemPredicate(Material.SPIDER_EYE)),
                    new AltarRecipe(5, 5, 50, new MaterialItemPredicate(Material.SPIDER_EYE))
            );
    public static VanillaEnchantmentWrapper WIND_BURST =
            new VanillaEnchantmentWrapper(Enchantment.WIND_BURST, () -> new ItemStack(Material.WIND_CHARGE),
                    new AltarTiers(0, 1, 2, 3),
                    new AltarRecipe(1, 1, 16, new MaterialItemPredicate(Material.WIND_CHARGE)),
                    new AltarRecipe(2, 2, 32, new MaterialItemPredicate(Material.BREEZE_ROD)),
                    new AltarRecipe(3, 4, new PotionItemPredicate(PotionEffectType.WIND_CHARGED))
            );
    public static VanillaEnchantmentWrapper BREACH =
            new VanillaEnchantmentWrapper(Enchantment.BREACH, () -> new ItemStack(Material.GRINDSTONE),
                    new AltarTiers(1, 2, 3, 4),
                    new AltarRecipe(1, 2, 16, new MaterialItemPredicate(Material.GRINDSTONE)),
                    new AltarRecipe(2, 4, 8, new MaterialItemPredicate(Material.ANVIL)),
                    new AltarRecipe(3, 6, new PotionItemPredicate(PotionEffectType.STRENGTH)),
                    new AltarRecipe(4, 8, new MaterialItemPredicate(Material.HEAVY_CORE))
            );
    public static VanillaEnchantmentWrapper DENSITY =
            new VanillaEnchantmentWrapper(Enchantment.DENSITY, () -> new ItemStack(Material.MACE),
                    new AltarTiers(1, 2, 3, 5),
                    new AltarRecipe(1, 1, 2, new MaterialItemPredicate(Material.ANVIL)),
                    new AltarRecipe(2, 2, 4, new MaterialItemPredicate(Material.ANVIL)),
                    new AltarRecipe(3, 3, 6, new MaterialItemPredicate(Material.ANVIL)),
                    new AltarRecipe(4, 4, 8, new MaterialItemPredicate(Material.ANVIL)),
                    new AltarRecipe(5, 5, new MaterialItemPredicate(Material.HEAVY_CORE))
            );

    private static Set<VanillaEnchantmentWrapper> vanilla() {
        return Set.of(
                MULTISHOT, PIERCING, SILK_TOUCH, VANISHING_CURSE, FROST_WALKER, FORTUNE, BINDING_CURSE, SHARPNESS, SWEEPING_EDGE,
                RIPTIDE, QUICK_CHARGE, IMPALING, FIRE_PROTECTION, LOYALTY, LUCK_OF_THE_SEA, RESPIRATION, FLAME, PUNCH,
                BLAST_PROTECTION, PROJECTILE_PROTECTION, PROTECTION, AQUA_AFFINITY, SMITE, POWER, THORNS, LOOTING,
                FEATHER_FALLING, FIRE_ASPECT, UNBREAKING, CHANNELING, LURE, INFINITY, EFFICIENCY, DEPTH_STRIDER, KNOCKBACK,
                SOUL_SPEED, SWIFT_SNEAK, BANE_OF_ARTHROPODS, WIND_BURST, BREACH, DENSITY
        );
    }

    public static Set<EnchantmentWrapper> list() {
        Set<EnchantmentWrapper> wrappers = new HashSet<>(CustomEnchantments.list());

        wrappers.addAll(vanilla());

        wrappers.removeIf(EnchantmentWrapper::isTreasure);

        return wrappers;
    }

    public static @Nullable EnchantmentWrapper getByKey(NamespacedKey key) {
        for(EnchantmentWrapper curr : list()) {
            if(curr.getKey().equals(key)) {
                return curr;
            }
        }
        return null;
    }
}
