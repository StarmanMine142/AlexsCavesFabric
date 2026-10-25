package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.client.render.entity.LivingEntityRendererAccessor;
import com.github.alexmodguy.alexscaves.client.render.item.RaygunRenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin extends EntityRenderer implements LivingEntityRendererAccessor {

    @Shadow protected abstract void scale(LivingEntity living, PoseStack poseStack, float f);
    @Shadow protected abstract boolean addLayer(RenderLayer<?, ?> renderLayer);

    protected LivingEntityRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    public void scaleForHologram(LivingEntity entity, PoseStack poseStack, float partialTicks) {
        this.scale(entity, poseStack, partialTicks);
    }

    @Inject(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V",
                    shift = At.Shift.AFTER
            )
    )
    private void ac_renderRaygunRays(LivingEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                                     MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        if (!Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
            RaygunRenderHelper.renderRaysFor(entity, entity.getPosition(partialTicks), poseStack, bufferSource,
                    partialTicks, false, 0);
        }
    }

    @Override
    public void addACLayer(RenderLayer<?, ?> layer) {
        this.addLayer(layer);
    }
}