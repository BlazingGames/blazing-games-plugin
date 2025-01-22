package de.blazemcworld.blazinggames.items;

import de.blazemcworld.blazinggames.items.contexts.EmptyItemContext;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;

public abstract class ContextlessItem extends CustomItem<EmptyItemContext> {
    public final @NotNull ItemStack create() {
        return create(EmptyItemContext.instance);
    }

    protected final @NotNull ItemStack modifyMaterial(ItemStack stack, EmptyItemContext context) {
        return modifyMaterial(stack);
    }

    @Override
    protected EmptyItemContext parseContext(Player player, String string) throws ParseException {
        return EmptyItemContext.parse(player, string);
    }

    protected @NotNull ItemStack modifyMaterial(ItemStack stack) {
        return stack;
    }
}
