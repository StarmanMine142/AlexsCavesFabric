package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.GumWormModel;
import com.github.alexmodguy.alexscaves.server.entity.living.GumWormEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class GumWormRenderer extends MobRenderer<GumWormEntity, GumWormModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gum_worm.png");

    public GumWormRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new GumWormModel(), 1.2F);
    }

    protected float getFlipDegrees(GumWormEntity entity) {
        return 0.0F;
    }

    @Override
    protected void setupRotations(GumWormEntity entity, PoseStack poseStack, float bob, float yawIn, float partialTicks, float scale) {
        if (this.isShaking(entity)) {
            yawIn += (float)(Math.cos((double)entity.tickCount * 3.25D) * Math.PI * (double)0.4F);
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yawIn));
        poseStack.translate(0F, 1F, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(-entity.getViewXRot(partialTicks)));
        poseStack.translate(0F, -1F, 0);

        if (isEntityUpsideDown(entity)) {
            poseStack.translate(0.0F, entity.getBbHeight() + 0.1F, 0.0F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        }
    }

    public ResourceLocation getTextureLocation(GumWormEntity entity) {
        return TEXTURE;
    }
}


