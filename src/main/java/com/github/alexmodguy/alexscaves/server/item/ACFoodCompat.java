package com.github.alexmodguy.alexscaves.server.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

public final class ACFoodCompat {

    private ACFoodCompat() {
    }

    public static FoodProperties getFoodProperties(ItemStack stack) {
        return stack.get(DataComponents.FOOD);
    }
}
