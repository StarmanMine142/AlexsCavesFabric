package com.github.alexmodguy.alexscaves.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderSystem.class, remap = false)
public class RenderSystemThreadBypassMixin {
    @Inject(method = "assertOnRenderThread", at = @At("HEAD"), cancellable = true, remap = false)
    private static void bypassThreadCheck(CallbackInfo ci) {
        if (!RenderSystem.isOnRenderThread()) {
            ci.cancel();
        }
    }
}