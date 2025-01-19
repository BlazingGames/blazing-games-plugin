package de.blazemcworld.blazinggames.items;

import de.blazemcworld.blazinggames.items.contexts.EmptyItemContext;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class ContextlessItem extends CustomItem<EmptyItemContext> {
    public final @NotNull ItemStack create() {
        return create(EmptyItemContext.instance);
    }

    protected final @NotNull ItemStack modifyMaterial(ItemStack stack, EmptyItemContext context) {
        return modifyMaterial(stack);
    }

    protected @NotNull ItemStack modifyMaterial(ItemStack stack) {
        return stack;
    }
}
