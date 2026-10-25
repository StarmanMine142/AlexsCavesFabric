package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.misc.ACFluidHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelHeightAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.ClientLevelData.class)
public abstract class ClientLevelDataMixin {

    @Inject(method = "getHorizonHeight", at = @At("HEAD"), cancellable = true)
    private void ac_getHorizonHeight(LevelHeightAccessor heightAccessor, CallbackInfoReturnable<Double> cir) {
        Entity camera = Minecraft.getInstance().getCameraEntity();
        if (camera != null && (camera.isEyeInFluid(ACFluidHelper.ACID) || camera.isEyeInFluid(ACFluidHelper.PURPLE_SODA))) {
            return;
        }
        if (!AlexsCaves.CLIENT_CONFIG.biomeSkyOverrides) {
            return;
        }
        cir.setReturnValue((double) -heightAccessor.getMaxBuildHeight());
    }
}