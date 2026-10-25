package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {

    private static final ResourceLocation ACID_OVERLAY =
            ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/misc/under_acid.png");
    private static final ResourceLocation PURPLE_SODA_OVERLAY =
            ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/misc/under_purple_soda.png");

    @Inject(
            method = "renderScreenEffect(Lnet/minecraft/client/Minecraft;Lcom/mojang/blaze3d/vertex/PoseStack;)V",
            at = @At("RETURN")
    )
    private static void ac_renderFluidScreenOverlays(Minecraft minecraft, PoseStack poseStack, CallbackInfo ci) {
        LocalPlayer player = minecraft.player;
        if (player == null || player.isSpectator()) {
            return;
        }

        FluidState eyeFluid = player.level().getFluidState(BlockPos.containing(player.getEyePosition()));
        Fluid eyeType = eyeFluid.getType();
        boolean inAcid = eyeType == ACFluidRegistry.ACID_FLUID_SOURCE
                || eyeType == ACFluidRegistry.ACID_FLUID_FLOWING;
        boolean inSoda = eyeType == ACFluidRegistry.PURPLE_SODA_FLUID_SOURCE
                || eyeType == ACFluidRegistry.PURPLE_SODA_FLUID_FLOWING;

        float alpha = 0.13F;

        if (inAcid) {
            renderFluidOverlay(minecraft, poseStack, ACID_OVERLAY, alpha);
        } else if (inSoda) {
            renderFluidOverlay(minecraft, poseStack, PURPLE_SODA_OVERLAY, alpha);
        }
    }

    private static void renderFluidOverlay(Minecraft minecraft, PoseStack poseStack, ResourceLocation texture, float alpha) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(0.7F, 0.7F, 0.7F, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);

        float yawOffset = -minecraft.player.getYRot() / 64.0F;
        float pitchOffset = minecraft.player.getXRot() / 64.0F;
        Matrix4f matrix4f = poseStack.last().pose();

        BufferBuilder bufferBuilder = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.addVertex(matrix4f, -1.0F, -1.0F, -0.5F).setUv(4.0F + yawOffset, 4.0F + pitchOffset);
        bufferBuilder.addVertex(matrix4f, 1.0F, -1.0F, -0.5F).setUv(0.0F + yawOffset, 4.0F + pitchOffset);
        bufferBuilder.addVertex(matrix4f, 1.0F, 1.0F, -0.5F).setUv(0.0F + yawOffset, 0.0F + pitchOffset);
        bufferBuilder.addVertex(matrix4f, -1.0F, 1.0F, -0.5F).setUv(4.0F + yawOffset, 0.0F + pitchOffset);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}