package de.blazemcworld.blazinggames.items;

import de.blazemcworld.blazinggames.BlazingGames;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NetherStarChunk extends ContextlessItem {
    @Override
    public @NotNull NamespacedKey getKey() {
        return BlazingGames.get().key("nether_star_chunk");
    }

    @Override
    protected @NotNull Component itemName() {
        return Component.text("Nether Star Chunk");
    }

    @Override
    protected @NotNull ItemStack modifyMaterial(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.setEnchantmentGlintOverride(true);
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Used to enchant mending").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public Map<NamespacedKey, Recipe> getRecipes() {
        ItemStack result = create();
        result.setAmount(4);
        ShapelessRecipe fragment = new ShapelessRecipe(getKey(), result);
        fragment.addIngredient(Material.NETHER_STAR);

        ShapedRecipe netherstar = new ShapedRecipe(BlazingGames.get().key("nether_star"), new ItemStack(Material.NETHER_STAR));
        netherstar.shape(
                "   ",
                " II",
                " II"
        );
        netherstar.setIngredient('I', create());

        return Map.of(
                getKey(), fragment,
                BlazingGames.get().key("nether_star"), netherstar
        );
    }
}
