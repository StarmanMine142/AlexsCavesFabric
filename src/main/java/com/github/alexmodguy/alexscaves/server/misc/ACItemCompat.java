package com.github.alexmodguy.alexscaves.server.misc;

import net.minecraft.world.item.ItemStack;

public final class ACItemCompat {

    private ACItemCompat() {
    }

    public static ItemStack getCraftingRemainingItem(ItemStack stack) {
        return stack.getRecipeRemainder();
    }
}