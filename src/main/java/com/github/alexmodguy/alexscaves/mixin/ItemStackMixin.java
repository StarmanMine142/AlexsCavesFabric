package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.UnaryOperator;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void ac_applyCustomRarityStyle(CallbackInfoReturnable<Component> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        UnaryOperator<Style> styleModifier = ACItemRegistry.getCustomRarityStyle(stack);
        if (styleModifier != null) {
            cir.setReturnValue(cir.getReturnValue().copy().withStyle(styleModifier));
        }
    }
}
