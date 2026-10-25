package com.github.alexmodguy.alexscaves.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;

public interface LivingEntityRendererAccessor {
    void scaleForHologram(LivingEntity entity, PoseStack poseStack, float partialTicks);

    void addACLayer(RenderLayer<?, ?> layer);
}
