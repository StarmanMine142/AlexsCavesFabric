package com.github.alexmodguy.alexscaves.compat.jei;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import org.jetbrains.annotations.Nullable;

public class JellyBeanSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
    public static final JellyBeanSubtypeInterpreter INSTANCE = new JellyBeanSubtypeInterpreter();

    private JellyBeanSubtypeInterpreter() {
    }

    @Nullable
    @Override
    public Object getSubtypeData(ItemStack itemStack, UidContext context) {
        PotionContents potionContents = itemStack.get(DataComponents.POTION_CONTENTS);
        if (potionContents != null && potionContents.potion().isPresent()) {
            return potionContents.potion().get().getRegisteredName();
        }
        return null;
    }

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack itemStack, UidContext context) {
        Object data = getSubtypeData(itemStack, context);
        return data != null ? data.toString() : "";
    }
}