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

import de.blazemcworld.blazinggames.enchantments.sys.VanillaEnchantmentWrapper.Warning;
import de.blazemcworld.blazinggames.enchantments.sys.altar.AltarRecipe;
import de.blazemcworld.blazinggames.items.predicates.BreakableItemPredicate;
import de.blazemcworld.blazinggames.items.predicates.ColorlessItemPredicate;
import de.blazemcworld.blazinggames.items.CustomItems;
import de.blazemcworld.blazinggames.items.predicates.MaterialItemPredicate;
import de.blazemcworld.blazinggames.items.predicates.PotionItemPredicate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnchantmentWrappers {
    public static VanillaEnchantmentWrapper MULTISHOT =
            new VanillaEnchantmentWrapper(Enchantment.MULTISHOT, Material.PRISMARINE_CRYSTALS,
                    new AltarRecipe(2,1, 4, 16, new MaterialItemPredicate(Material.ARROW))
            );
    public static VanillaEnchantmentWrapper PIERCING =
            new VanillaEnchantmentWrapper(Enchantment.PIERCING, Material.SPECTRAL_ARROW,
                    new AltarRecipe(1,1, 2, 32, new MaterialItemPredicate(Material.ROTTEN_FLESH)),
                    new AltarRecipe(2,2, 4, 32, new MaterialItemPredicate(Material.BONE)),
                    new AltarRecipe(3,3, 6, 32, new MaterialItemPredicate(Material.SPIDER_EYE)),
                    new AltarRecipe(4,4, 8, 32, new MaterialItemPredicate(Material.GUNPOWDER))
            );
    public static VanillaEnchantmentWrapper SILK_TOUCH =
            new VanillaEnchantmentWrapper(Enchantment.SILK_TOUCH, Material.STRING,
                    List.of(
                            new Warning("Might break pickaxe when used on spawners.\nAlways breaks iron pickaxes and lower.\nUses 2/3rd of durability on diamond pickaxes.\nUses half of durability on netherite pickaxes.", 2)
                    ),
                    new AltarRecipe(3,1, 15, 32, new MaterialItemPredicate(Material.STRING)),
                    new AltarRecipe(5,2, 30, 1, new MaterialItemPredicate(Material.NETHERITE_INGOT))
            );
    public static VanillaEnchantmentWrapper VANISHING_CURSE =
            new VanillaEnchantmentWrapper(Enchantment.VANISHING_CURSE, Material.GLASS,
                    new AltarRecipe(2,1, 8, new PotionItemPredicate(PotionEffectType.INVISIBILITY))
            );
    public static VanillaEnchantmentWrapper FROST_WALKER =
            new VanillaEnchantmentWrapper(Enchantment.FROST_WALKER, Material.BLUE_ICE,
                    new AltarRecipe(2,1, 2, 16, new MaterialItemPredicate(Material.BLUE_ICE)),
                    new AltarRecipe(3,2, 4, 16, new MaterialItemPredicate(Material.PRISMARINE_CRYSTALS))
            );
    public static VanillaEnchantmentWrapper FORTUNE =
            new VanillaEnchantmentWrapper(Enchantment.FORTUNE, Material.EMERALD,
                    new AltarRecipe(2,1, 2, 16, new MaterialItemPredicate(Material.EMERALD)),
                    new AltarRecipe(3,2, 4, 16, new MaterialItemPredicate(Material.DIAMOND)),
                    new AltarRecipe(4,3, 8, new MaterialItemPredicate(Material.RABBIT_FOOT))
            );
    public static VanillaEnchantmentWrapper BINDING_CURSE =
            new VanillaEnchantmentWrapper(Enchantment.BINDING_CURSE, Material.CHAIN,
                    new AltarRecipe(2,1, 8, 16, new MaterialItemPredicate(Material.COBWEB))
            );
    public static VanillaEnchantmentWrapper SHARPNESS =
            new VanillaEnchantmentWrapper(Enchantment.SHARPNESS, Material.DIAMOND_SWORD,
                    new AltarRecipe(1,1, 2, 32, new MaterialItemPredicate(Material.REDSTONE)),
                    new AltarRecipe(2,2, 4, 16, new MaterialItemPredicate(Material.REDSTONE_BLOCK)),
                    new AltarRecipe(3,3, 6, 16, new MaterialItemPredicate(Material.BLAZE_POWDER)),
                    new AltarRecipe(4,4, 8, 8, new MaterialItemPredicate(Material.DIAMOND)),
                    new AltarRecipe(4,5, 10, new PotionItemPredicate(PotionEffectType.STRENGTH))
            );
    public static VanillaEnchantmentWrapper SWEEPING_EDGE =
            new VanillaEnchantmentWrapper(Enchantment.SWEEPING_EDGE, Material.FEATHER,
                    new AltarRecipe(1,1, 1, 32, new MaterialItemPredicate(Material.ROTTEN_FLESH)),
                    new AltarRecipe(2,2, 2, 32, new MaterialItemPredicate(Material.BONE)),
                    new AltarRecipe(3,3, 4, 32, new MaterialItemPredicate(Material.STRING))
            );
    public static VanillaEnchantmentWrapper RIPTIDE =
            new VanillaEnchantmentWrapper(Enchantment.RIPTIDE, Material.NAUTILUS_SHELL,
                    new AltarRecipe(3,1, 4, 16, new MaterialItemPredicate(Material.CHAIN)),
                    new AltarRecipe(4,2, 6, 16, new MaterialItemPredicate(Material.PISTON)),
                    new AltarRecipe(4,3, 8, 16, new MaterialItemPredicate(Material.TNT))
            );
    public static VanillaEnchantmentWrapper QUICK_CHARGE =
            new VanillaEnchantmentWrapper(Enchantment.QUICK_CHARGE, Material.REDSTONE,
                    new AltarRecipe(2,1, 4, 16, new MaterialItemPredicate(Material.LEATHER)),
                    new AltarRecipe(3,2, 6, 16, new MaterialItemPredicate(Material.STRING)),
                    new AltarRecipe(4,3, 8, new PotionItemPredicate(PotionEffectType.SPEED))
            );
    public static VanillaEnchantmentWrapper IMPALING =
            new VanillaEnchantmentWrapper(Enchantment.IMPALING, Material.TRIDENT,
                    new AltarRecipe(1,1, 1, new MaterialItemPredicate(Material.WATER_BUCKET)),
                    new AltarRecipe(2,2, 2, 32, new MaterialItemPredicate(Material.ROTTEN_FLESH)),
                    new AltarRecipe(3,3, 3, 16, new MaterialItemPredicate(Material.COD)),
                    new AltarRecipe(4,4, 4, 16, new MaterialItemPredicate(Material.SALMON)),
                    new AltarRecipe(5,5, 5, 16, new MaterialItemPredicate(Material.PUFFERFISH))
            );
    public static VanillaEnchantmentWrapper FIRE_PROTECTION =
            new VanillaEnchantmentWrapper(Enchantment.FIRE_PROTECTION, Material.MAGMA_CREAM,
                    new AltarRecipe(1,1, 1, 32, new MaterialItemPredicate(Material.COPPER_INGOT)),
                    new AltarRecipe(2,2, 2, 32, new MaterialItemPredicate(Material.IRON_INGOT)),
                    new AltarRecipe(3,3, 3, 16, new MaterialItemPredicate(Material.BLAZE_POWDER)),
                    new AltarRecipe(4,4, 4, 32, new MaterialItemPredicate(Material.BLAZE_POWDER))
            );
    public static VanillaEnchantmentWrapper MENDING =
            new OverridableVanillaEnchantmentWrapper(Enchantment.MENDING, Material.SHULKER_SHELL,
                    new OverridableVanillaEnchantmentWrapper.VanillaEnchantmentOverrides().target(BreakableItemPredicate.instance)
                            .conflicts(),
                    new AltarRecipe(4,10, 15, CustomItems.NETHER_STAR_CHUNK)
            );
    public static VanillaEnchantmentWrapper LOYALTY =
            new VanillaEnchantmentWrapper(Enchantment.LOYALTY, Material.STICK,
                    new AltarRecipe(2,1, 2, 16, new MaterialItemPredicate(Material.BONE)),
                    new AltarRecipe(3,2, 3, 16, new MaterialItemPredicate(Material.COD)),
                    new AltarRecipe(4,3, 4, 16, new MaterialItemPredicate(Material.CHAIN))
            );
    public static VanillaEnchantmentWrapper LUCK_OF_THE_SEA =
            new VanillaEnchantmentWrapper(Enchantment.LUCK_OF_THE_SEA, Material.HEART_OF_THE_SEA,
                    new AltarRecipe(1,1, 1, 16, new MaterialItemPredicate(Material.COD)),
                    new AltarRecipe(2,2, 2, 8, new MaterialItemPredicate(Material.CHEST)),
                    new AltarRecipe(3,3, 4, new MaterialItemPredicate(Material.HEART_OF_THE_SEA))
            );
    public static VanillaEnchantmentWrapper RESPIRATION =
            new VanillaEnchantmentWrapper(Enchantment.RESPIRATION, Material.PUFFERFISH,
                    new AltarRecipe(1,1, 1, new MaterialItemPredicate(Material.WATER_BUCKET)),
                    new AltarRecipe(2,2, 2, new MaterialItemPredicate(Material.TURTLE_HELMET)),
                    new AltarRecipe(3,3, 4, new PotionItemPredicate(PotionEffectType.WATER_BREATHING))
            );
    public static VanillaEnchantmentWrapper FLAME =
            new VanillaEnchantmentWrapper(Enchantment.FLAME, Material.BLAZE_POWDER,
                    new AltarRecipe(2,1, 4, 16, new MaterialItemPredicate(Material.BLAZE_POWDER))
            );
    public static VanillaEnchantmentWrapper PUNCH =
            new VanillaEnchantmentWrapper(Enchantment.PUNCH, Material.PISTON,
                    new AltarRecipe(2,1, 2, 10, new MaterialItemPredicate(Material.SLIME_BLOCK)),
                    new AltarRecipe(3,2, 4, 10, new MaterialItemPredicate(Material.PISTON))
            );
    public static VanillaEnchantmentWrapper BLAST_PROTECTION =
            new VanillaEnchantmentWrapper(Enchantment.BLAST_PROTECTION, Material.TNT,
                    new AltarRecipe(1,1, 1, 32, new MaterialItemPredicate(Material.COPPER_INGOT)),
                    new AltarRecipe(2,2, 2, 32, new MaterialItemPredicate(Material.IRON_INGOT)),
                    new AltarRecipe(3,3, 3, 16, new MaterialItemPredicate(Material.TNT)),
                    new AltarRecipe(4,4, 4, 32, new MaterialItemPredicate(Material.TNT))
            );
    public static VanillaEnchantmentWrapper PROJECTILE_PROTECTION =
            new VanillaEnchantmentWrapper(Enchantment.PROJECTILE_PROTECTION, Material.ARROW,
                    new AltarRecipe(1,1, 1, 32, new MaterialItemPredicate(Material.COPPER_INGOT)),
                    new AltarRecipe(2,2, 2, 32, new MaterialItemPredicate(Material.IRON_INGOT)),
                    new AltarRecipe(3,3, 3, 16, new MaterialItemPredicate(Material.ARROW)),
                    new AltarRecipe(4,4, 4, 32, new MaterialItemPredicate(Material.ARROW))
            );
    public static VanillaEnchantmentWrapper PROTECTION =
            new VanillaEnchantmentWrapper(Enchantment.PROTECTION, Material.DIAMOND_CHESTPLATE,
                    new AltarRecipe(1,1, 2, 32, new MaterialItemPredicate(Material.COPPER_INGOT)),
                    new AltarRecipe(1,2, 4, 32, new MaterialItemPredicate(Material.IRON_INGOT)),
                    new AltarRecipe(2,3, 6, 32, new MaterialItemPredicate(Material.GOLD_INGOT)),
                    new AltarRecipe(3,4, 8, 16, new MaterialItemPredicate(Material.DIAMOND))
            );
    public static VanillaEnchantmentWrapper AQUA_AFFINITY =
            new VanillaEnchantmentWrapper(Enchantment.AQUA_AFFINITY, Material.PRISMARINE,
                    new AltarRecipe(1,1, 4, 16, new MaterialItemPredicate(Material.PRISMARINE_SHARD))
            );
    public static VanillaEnchantmentWrapper SMITE =
            new VanillaEnchantmentWrapper(Enchantment.SMITE, Material.ROTTEN_FLESH,
                    new AltarRecipe(1,1, 1, 10, new MaterialItemPredicate(Material.ROTTEN_FLESH)),
                    new AltarRecipe(2,2, 2, 20, new MaterialItemPredicate(Material.ROTTEN_FLESH)),
                    new AltarRecipe(3,3, 3, 30, new MaterialItemPredicate(Material.ROTTEN_FLESH)),
                    new AltarRecipe(4,4, 4, 40, new MaterialItemPredicate(Material.ROTTEN_FLESH)),
                    new AltarRecipe(4,5, 5, 50, new MaterialItemPredicate(Material.ROTTEN_FLESH))
            );
    public static VanillaEnchantmentWrapper POWER =
            new VanillaEnchantmentWrapper(Enchantment.POWER, Material.BOW,
                    new AltarRecipe(1,1, 2, 32, new MaterialItemPredicate(Material.REDSTONE)),
                    new AltarRecipe(2,2, 4, 16, new MaterialItemPredicate(Material.REDSTONE_BLOCK)),
                    new AltarRecipe(3,3, 6, 16, new MaterialItemPredicate(Material.BLAZE_POWDER)),
                    new AltarRecipe(4,4, 8, 8, new MaterialItemPredicate(Material.DIAMOND)),
                    new AltarRecipe(4,5, 10, new PotionItemPredicate(PotionEffectType.STRENGTH))
            );
    public static VanillaEnchantmentWrapper THORNS =
            new VanillaEnchantmentWrapper(Enchantment.THORNS, Material.PUFFERFISH,
                    new AltarRecipe(2,1, 1, 16, new MaterialItemPredicate(Material.CACTUS)),
                    new AltarRecipe(3,2, 2, 16, new MaterialItemPredicate(Material.SWEET_BERRIES)),
                    new AltarRecipe(4,3, 4, new MaterialItemPredicate(Material.DIAMOND_SWORD))
            );
    public static VanillaEnchantmentWrapper LOOTING =
            new VanillaEnchantmentWrapper(Enchantment.LOOTING, Material.EMERALD,
                    new AltarRecipe(2,1, 2, 16, new MaterialItemPredicate(Material.CHEST)),
                    new AltarRecipe(3,2, 4, 16, new MaterialItemPredicate(Material.EMERALD)),
                    new AltarRecipe(4,3, 8, 10, new MaterialItemPredicate(Material.DIAMOND))
            );
    public static VanillaEnchantmentWrapper FEATHER_FALLING =
            new VanillaEnchantmentWrapper(Enchantment.FEATHER_FALLING, Material.FEATHER,
                    new AltarRecipe(2,1, 2, 10, new MaterialItemPredicate(Material.FEATHER)),
                    new AltarRecipe(3,2, 4, 5, new MaterialItemPredicate(Material.HAY_BLOCK)),
                    new AltarRecipe(4,3, 6, new ColorlessItemPredicate(Material.WHITE_BED)),
                    new AltarRecipe(4,4, 8, 32, new ColorlessItemPredicate(Material.WHITE_WOOL))
            );
    public static VanillaEnchantmentWrapper FIRE_ASPECT =
            new VanillaEnchantmentWrapper(Enchantment.FIRE_ASPECT, Material.BLAZE_POWDER,
                    new AltarRecipe(2,1, 4, 16, new MaterialItemPredicate(Material.BLAZE_POWDER)),
                    new AltarRecipe(3,2, 8, 32, new MaterialItemPredicate(Material.BLAZE_POWDER))
            );
    public static VanillaEnchantmentWrapper UNBREAKING =
            new OverridableVanillaEnchantmentWrapper(Enchantment.UNBREAKING, Material.BEDROCK,
                    new OverridableVanillaEnchantmentWrapper.VanillaEnchantmentOverrides().target(BreakableItemPredicate.instance),
                    new AltarRecipe(2,1, 2, 32, new MaterialItemPredicate(Material.IRON_INGOT)),
                    new AltarRecipe(3,2, 4, 32, new MaterialItemPredicate(Material.OBSIDIAN)),
                    new AltarRecipe(4,3, 8, 2, new MaterialItemPredicate(Material.NETHERITE_SCRAP))
            );
    public static VanillaEnchantmentWrapper CHANNELING =
            new VanillaEnchantmentWrapper(Enchantment.CHANNELING, Material.LIGHTNING_ROD,
                    new AltarRecipe(3,1, 8, 1, new MaterialItemPredicate(Material.LIGHTNING_ROD))
            );
    public static VanillaEnchantmentWrapper LURE =
            new VanillaEnchantmentWrapper(Enchantment.LURE, Material.TRIPWIRE_HOOK,
                    new AltarRecipe(1,1, 1, 16, new MaterialItemPredicate(Material.STRING)),
                    new AltarRecipe(2,2, 2, 16, new MaterialItemPredicate(Material.COD)),
                    new AltarRecipe(3,3, 4, 16, new MaterialItemPredicate(Material.PUFFERFISH))
            );
    public static VanillaEnchantmentWrapper INFINITY =
            new OverridableVanillaEnchantmentWrapper(Enchantment.INFINITY, Material.CHORUS_FLOWER,
                    new OverridableVanillaEnchantmentWrapper.VanillaEnchantmentOverrides().target(BlazingEnchantmentTarget.BOW_ROCKET)
                            .conflicts(),
                    new AltarRecipe(4,1, 8, 64, new MaterialItemPredicate(Material.ARROW))
            );
    public static VanillaEnchantmentWrapper EFFICIENCY =
            new VanillaEnchantmentWrapper(Enchantment.EFFICIENCY, Material.REDSTONE,
                    new AltarRecipe(1,1, 2, 32, new MaterialItemPredicate(Material.SUGAR)),
                    new AltarRecipe(2,2, 4, 32, new MaterialItemPredicate(Material.REDSTONE)),
                    new AltarRecipe(3,3, 6, 16, new MaterialItemPredicate(Material.REDSTONE_BLOCK)),
                    new AltarRecipe(4,4, 8, 32, new MaterialItemPredicate(Material.GOLD_INGOT)),
                    new AltarRecipe(4,5, 10, new PotionItemPredicate(PotionEffectType.SPEED))
            );
    public static VanillaEnchantmentWrapper DEPTH_STRIDER =
            new VanillaEnchantmentWrapper(Enchantment.DEPTH_STRIDER, Material.COD,
                    new AltarRecipe(2,1, 1, new ColorlessItemPredicate(Material.OAK_BOAT)),
                    new AltarRecipe(3,2, 2, new MaterialItemPredicate(Material.MINECART)),
                    new AltarRecipe(4,3, 4, 3, new MaterialItemPredicate(Material.RABBIT_FOOT))
            );
    public static VanillaEnchantmentWrapper KNOCKBACK =
            new VanillaEnchantmentWrapper(Enchantment.KNOCKBACK, Material.PISTON,
                    new AltarRecipe(2,1, 2, 10, new MaterialItemPredicate(Material.SLIME_BLOCK)),
                    new AltarRecipe(3,2, 4, 10, new MaterialItemPredicate(Material.PISTON))
            );
    public static VanillaEnchantmentWrapper SOUL_SPEED =
            new VanillaEnchantmentWrapper(Enchantment.SOUL_SPEED, Material.SOUL_SAND,
                    new AltarRecipe(2,1, 1, 16, new MaterialItemPredicate(Material.SOUL_SAND)),
                    new AltarRecipe(3,2, 2, 16, new MaterialItemPredicate(Material.SOUL_SOIL)),
                    new AltarRecipe(4,3, 4, 32, new MaterialItemPredicate(Material.SOUL_LANTERN))
            );
    public static VanillaEnchantmentWrapper SWIFT_SNEAK =
            new VanillaEnchantmentWrapper(Enchantment.SWIFT_SNEAK, Material.ECHO_SHARD,
                    new AltarRecipe(2,1, 1, 16, new ColorlessItemPredicate(Material.WHITE_WOOL)),
                    new AltarRecipe(3,2, 2, 16, new MaterialItemPredicate(Material.SCULK)),
                    new AltarRecipe(4,3, 4, 4, new MaterialItemPredicate(Material.ECHO_SHARD))
            );
    public static VanillaEnchantmentWrapper BANE_OF_ARTHROPODS =
            new VanillaEnchantmentWrapper(Enchantment.BANE_OF_ARTHROPODS, Material.SPIDER_EYE,
                    new AltarRecipe(1,1, 1, 10, new MaterialItemPredicate(Material.SPIDER_EYE)),
                    new AltarRecipe(2,2, 2, 20, new MaterialItemPredicate(Material.SPIDER_EYE)),
                    new AltarRecipe(3,3, 3, 30, new MaterialItemPredicate(Material.SPIDER_EYE)),
                    new AltarRecipe(4,4, 4, 40, new MaterialItemPredicate(Material.SPIDER_EYE)),
                    new AltarRecipe(4,5, 5, 50, new MaterialItemPredicate(Material.SPIDER_EYE))
            );
    public static VanillaEnchantmentWrapper WIND_BURST =
            new VanillaEnchantmentWrapper(Enchantment.WIND_BURST, Material.WIND_CHARGE,
                    new AltarRecipe(2,1, 1, 16, new MaterialItemPredicate(Material.WIND_CHARGE)),
                    new AltarRecipe(3,2, 2, 32, new MaterialItemPredicate(Material.BREEZE_ROD)),
                    new AltarRecipe(4,3, 4, new PotionItemPredicate(PotionEffectType.WIND_CHARGED))
            );
    public static VanillaEnchantmentWrapper BREACH =
            new VanillaEnchantmentWrapper(Enchantment.BREACH, Material.GRINDSTONE,
                    new AltarRecipe(1,1, 2, 16, new MaterialItemPredicate(Material.GRINDSTONE)),
                    new AltarRecipe(2,2, 4, 8, new MaterialItemPredicate(Material.ANVIL)),
                    new AltarRecipe(3,3, 6, new PotionItemPredicate(PotionEffectType.STRENGTH)),
                    new AltarRecipe(4,4, 8, new MaterialItemPredicate(Material.HEAVY_CORE))
            );
    public static VanillaEnchantmentWrapper DENSITY =
            new VanillaEnchantmentWrapper(Enchantment.DENSITY, Material.MACE,
                    new AltarRecipe(1,1, 1, 2, new MaterialItemPredicate(Material.ANVIL)),
                    new AltarRecipe(2,2, 2, 4, new MaterialItemPredicate(Material.ANVIL)),
                    new AltarRecipe(3,3, 3, 6, new MaterialItemPredicate(Material.ANVIL)),
                    new AltarRecipe(4,4, 4, 8, new MaterialItemPredicate(Material.ANVIL)),
                    new AltarRecipe(4,5, 5, new MaterialItemPredicate(Material.HEAVY_CORE))
            );

    private static Set<VanillaEnchantmentWrapper> vanilla() {
        return Set.of(
                MULTISHOT, PIERCING, SILK_TOUCH, VANISHING_CURSE, FROST_WALKER, FORTUNE, BINDING_CURSE, SHARPNESS, SWEEPING_EDGE,
                RIPTIDE, QUICK_CHARGE, IMPALING, FIRE_PROTECTION, MENDING, LOYALTY, LUCK_OF_THE_SEA, RESPIRATION, FLAME, PUNCH,
                BLAST_PROTECTION, PROJECTILE_PROTECTION, PROTECTION, AQUA_AFFINITY, SMITE, POWER, THORNS, LOOTING,
                FEATHER_FALLING, FIRE_ASPECT, UNBREAKING, CHANNELING, LURE, INFINITY, EFFICIENCY, DEPTH_STRIDER, KNOCKBACK,
                SOUL_SPEED, SWIFT_SNEAK, BANE_OF_ARTHROPODS, WIND_BURST, BREACH, DENSITY
        );
    }

    public static Set<EnchantmentWrapper> list(boolean removeTreasure) {
        Set<EnchantmentWrapper> wrappers = new HashSet<>(CustomEnchantments.list());

        wrappers.addAll(vanilla());

        if(removeTreasure) {
            wrappers.removeIf(EnchantmentWrapper::isTreasure);
        }

        return wrappers;
    }

    public static @Nullable EnchantmentWrapper getByKey(NamespacedKey key) {
        for(EnchantmentWrapper curr : list(false)) {
            if(curr.getKey().equals(key)) {
                return curr;
            }
        }
        return null;
    }
}
