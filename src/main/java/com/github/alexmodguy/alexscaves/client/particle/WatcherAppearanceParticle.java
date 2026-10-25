package com.github.alexmodguy.alexscaves.client.particle;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.WatcherModel;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

public class WatcherAppearanceParticle extends Particle {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/watcher_appearance.png");
    private final WatcherModel model = new WatcherModel();
    private final RenderType renderType = RenderType.entityTranslucent(TEXTURE);

    private WatcherAppearanceParticle(ClientLevel lvl, double x, double y, double z) {
        super(lvl, x, y, z);
        this.setSize(12, 12);
        this.gravity = 0.0F;
        this.lifetime = 30;
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        float fogBefore = RenderSystem.getShaderFogEnd();
        RenderSystem.setShaderFogEnd(40);
        float age = this.age + partialTick;
        float f = (age - 5) / (float) (this.lifetime - 5);
        float initalFlip = Math.min(f, 0.1F) / 0.1F;
        float scale = 1;
        float alpha = Mth.clamp(1 - f * f, 0F, 1F);
        int color = FastColor.ARGB32.colorFromFloat(alpha, 1.0F, 1.0F, 1.0F);
        
        PoseStack posestack = new PoseStack();
        posestack.pushPose();
        posestack.mulPose(camera.rotation());
        posestack.mulPose(Axis.YP.rotationDegrees(180F));
        posestack.translate(0.0D, 0F, -1.2F);
        posestack.scale(-scale, -scale, scale);
        posestack.translate(0.0D, 0.5F, 2 + (1F - initalFlip));
        
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vertexconsumer = multibuffersource$buffersource.getBuffer(this.renderType);
        this.model.positionForParticle(partialTick, age);
        this.model.renderToBuffer(posestack, vertexconsumer, 240, OverlayTexture.NO_OVERLAY, color);
        multibuffersource$buffersource.endBatch();
        posestack.popPose();
        RenderSystem.setShaderFogEnd(fogBefore);
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new WatcherAppearanceParticle(worldIn, x, y, z);
        }
    }
}
