package com.github.alexmodguy.alexscaves.mixin;

import com.mojang.datafixers.DSL;
import net.minecraft.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Util.class)
public class UtilMixin {

    @Inject(method = "fetchChoiceType", at = @At("HEAD"), cancellable = true)
    private static void alexscaves$skipMissingDataFixerLog(DSL.TypeReference typeReference, String id, CallbackInfoReturnable<com.mojang.datafixers.types.Type<?>> cir) {
        if (id != null && id.startsWith("alexscaves:")) {
            cir.setReturnValue(null);
        }
    }
}
