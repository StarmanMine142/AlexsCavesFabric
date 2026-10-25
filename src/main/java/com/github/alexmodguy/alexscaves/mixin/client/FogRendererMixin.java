package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexmodguy.alexscaves.server.potion.DeepsightEffect;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {

    @Shadow private static float fogRed;
    @Shadow private static float fogGreen;
    @Shadow private static float fogBlue;

    @Unique
    private static boolean ac_isCameraInAcid(ClientLevel level, Camera camera) {
        FluidState fs = level.getFluidState(camera.getBlockPosition());
        return fs.getType() == ACFluidRegistry.ACID_FLUID_SOURCE
                || fs.getType() == ACFluidRegistry.ACID_FLUID_FLOWING;
    }

    @Unique
    private static boolean ac_isCameraInSoda(ClientLevel level, Camera camera) {
        FluidState fs = level.getFluidState(camera.getBlockPosition());
        return fs.getType() == ACFluidRegistry.PURPLE_SODA_FLUID_SOURCE
                || fs.getType() == ACFluidRegistry.PURPLE_SODA_FLUID_FLOWING;
    }

    @Inject(method = "setupColor", at = @At("TAIL"))
    private static void ac_setupColor(Camera camera, float partialTick, ClientLevel level, int renderDistanceChunks,
                                      float bossColorModifier, CallbackInfo ci) {
        Entity cameraEntity = camera.getEntity();
        if (cameraEntity == null) {
            return;
        }
        BlockState blockState = level.getBlockState(camera.getBlockPosition());
        if (blockState.is(ACBlockRegistry.PRIMAL_MAGMA) || blockState.is(ACBlockRegistry.FISSURE_PRIMAL_MAGMA)) {
            fogRed = 1.0F;
            fogGreen = 0.4F;
            fogBlue = 0.0F;
            return;
        }
        if (ac_isCameraInAcid(level, camera)) {
            fogRed = 0.0F;
            fogGreen = 1.0F;
            fogBlue = 0.0F;
            return;
        }
        if (ac_isCameraInSoda(level, camera)) {
            fogRed = 0.6F;
            fogGreen = 0.1F;
            fogBlue = 0.85F;
            return;
        }
        if (camera.getFluidInCamera() == FogType.NONE && AlexsCaves.CLIENT_CONFIG.biomeSkyFogOverrides) {
            float override = ClientProxy.acSkyOverrideAmount;
            float setR = fogRed;
            float setG = fogGreen;
            float setB = fogBlue;
            boolean changed = false;
            if (override > 0.0F) {
                Vec3 sampledFog = ClientProxy.getLastSampledFogColor();
                setR += (float) ((sampledFog.x - setR) * override);
                setG += (float) ((sampledFog.y - setG) * override);
                setB += (float) ((sampledFog.z - setB) * override);
                changed = true;
            }
            float primordialBossAmount = AlexsCaves.PROXY.getPrimordialBossActiveAmount(partialTick);
            if (primordialBossAmount > 0.0F) {
                setR += (0.8F - setR) * primordialBossAmount;
                setG += (0.2F - setG) * primordialBossAmount;
                setB += (0.15F - setB) * primordialBossAmount;
                changed = true;
            }
            if (changed) {
                fogRed = setR;
                fogGreen = setG;
                fogBlue = setB;
            }
            return;
        }
        if (camera.getFluidInCamera() == FogType.WATER && AlexsCaves.CLIENT_CONFIG.biomeWaterFogOverrides) {
            float override = ClientProxy.acSkyOverrideAmount;
            if (override > 0.0F) {
                Vec3 sampledWaterFog = ClientProxy.getLastSampledWaterFogColor();
                fogRed += (float) ((sampledWaterFog.x - fogRed) * override);
                fogGreen += (float) ((sampledWaterFog.y - fogGreen) * override);
                fogBlue += (float) ((sampledWaterFog.z - fogBlue) * override);
            }
        }
    }

    @Inject(method = "setupFog", at = @At("TAIL"))
    private static void ac_setupFog(Camera camera, FogRenderer.FogMode mode, float farPlaneDistance,
                                    boolean thickFog, float partialTick, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        Entity cameraEntity = minecraft.getCameraEntity();
        if (cameraEntity == null || minecraft.level == null) {
            return;
        }
        float defaultFarPlaneDistance = RenderSystem.getShaderFogEnd();
        float defaultNearPlaneDistance = RenderSystem.getShaderFogStart();
        BlockState blockState = minecraft.level.getBlockState(camera.getBlockPosition());
        if (ac_isCameraInAcid(minecraft.level, camera)) {
            float farness = 10.0F;
            if (minecraft.player != null && minecraft.player.hasEffect(ACEffectRegistry.DEEPSIGHT)) {
                farness *= 1.0F + 1.5F * DeepsightEffect.getIntensity(minecraft.player, partialTick);
            }
            RenderSystem.setShaderFogStart(0.0F);
            RenderSystem.setShaderFogEnd(farness);
            return;
        }
        if (ac_isCameraInSoda(minecraft.level, camera)) {
            float farness = 20.0F;
            float nearness = -8.0F;
            if (minecraft.player != null && minecraft.player.hasEffect(ACEffectRegistry.DEEPSIGHT)) {
                float deepsight = DeepsightEffect.getIntensity(minecraft.player, partialTick);
                farness *= 1.0F + 1.5F * deepsight;
                nearness *= 1.0F - deepsight;
            }
            RenderSystem.setShaderFogStart(nearness);
            RenderSystem.setShaderFogEnd(farness);
            return;
        }
        if (blockState.is(ACBlockRegistry.PRIMAL_MAGMA) || blockState.is(ACBlockRegistry.FISSURE_PRIMAL_MAGMA)) {
            float farness = 2.0F;
            if (minecraft.player != null && minecraft.player.hasEffect(ACEffectRegistry.DEEPSIGHT)) {
                farness *= 1.0F + 1.5F * DeepsightEffect.getIntensity(minecraft.player, partialTick);
            }
            RenderSystem.setShaderFogStart(0.0F);
            RenderSystem.setShaderFogEnd(farness);
            return;
        }
        if (camera.getFluidInCamera() == FogType.WATER && AlexsCaves.CLIENT_CONFIG.biomeWaterFogOverrides) {
            float farness = ClientProxy.getLastSampledWaterFogFarness();
            if (minecraft.player != null && minecraft.player.hasEffect(ACEffectRegistry.DEEPSIGHT)) {
                farness *= 1.0F + 1.5F * DeepsightEffect.getIntensity(minecraft.player, partialTick);
            }
            if (Math.abs(farness - 1.0F) > 0.01F) {
                RenderSystem.setShaderFogEnd(defaultFarPlaneDistance * farness);
            }
            return;
        }
        if (mode == FogRenderer.FogMode.FOG_TERRAIN && camera.getFluidInCamera() == FogType.NONE
                && AlexsCaves.CLIENT_CONFIG.biomeSkyFogOverrides) {
            float nearness = ClientProxy.getLastSampledFogNearness();
            float primordialBossAmount = AlexsCaves.PROXY.getPrimordialBossActiveAmount(partialTick);
            if (primordialBossAmount > 0.0F) {
                nearness *= 1.0F - primordialBossAmount * 0.75F;
            }
            if (Math.abs(nearness - 1.0F) > 0.01F || primordialBossAmount > 0.0F) {
                RenderSystem.setShaderFogStart(defaultNearPlaneDistance * nearness);
            }
        }
    }
}