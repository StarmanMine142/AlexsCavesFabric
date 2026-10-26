package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.AlexsCavesClient;
import com.github.alexmodguy.alexscaves.server.block.EnergizedGalenaBlock;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(value = LevelRenderer.class, priority = 800)
public abstract class LevelRendererMixin {

    @Shadow private ClientLevel level;
    @Shadow private int ticks;
    @Shadow @Nullable private ViewArea viewArea;
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Nullable private VertexBuffer skyBuffer;
    @Shadow @Nullable private VertexBuffer starBuffer;
    @Shadow @Nullable private VertexBuffer darkBuffer;
    @Shadow @Final private static ResourceLocation SUN_LOCATION;
    @Shadow @Final private static ResourceLocation MOON_LOCATION;

    @Unique
    private int aclastCameraChunkX;
    @Unique
    private int aclastCameraChunkY;
    @Unique
    private int aclastCameraChunkZ;

    @Shadow
    protected abstract void renderEndSky(PoseStack p_109781_);

    @Shadow
    protected abstract boolean doesMobEffectBlockSky(Camera p_234311_);

    @Inject(method = "Lnet/minecraft/client/renderer/LevelRenderer;setupRender(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/culling/Frustum;ZZ)V", at = @At(value = "HEAD"), allow = 1)
    private void ac_setupRender(Camera camera, Frustum frustum, boolean b1, boolean b2, CallbackInfo ci) {
        if (Minecraft.getInstance().cameraEntity != null
                && Minecraft.getInstance().cameraEntity != Minecraft.getInstance().player) {
            double d0 = Minecraft.getInstance().cameraEntity.getX();
            double d1 = Minecraft.getInstance().cameraEntity.getY();
            double d2 = Minecraft.getInstance().cameraEntity.getZ();
            int i = SectionPos.posToSectionCoord(d0);
            int j = SectionPos.posToSectionCoord(d1);
            int k = SectionPos.posToSectionCoord(d2);
            if (this.aclastCameraChunkX != i || this.aclastCameraChunkY != j || this.aclastCameraChunkZ != k) {
                this.aclastCameraChunkX = i;
                this.aclastCameraChunkY = j;
                this.aclastCameraChunkZ = k;
                assert viewArea != null;
                viewArea.repositionCamera(d0, d2);
            }
        }
    }

    @Inject(method = "Lnet/minecraft/client/renderer/LevelRenderer;renderSky(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V", at = @At(value = "HEAD"), cancellable = true)
    private void ac_renderSky(Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean foggy,
                              Runnable runnable, CallbackInfo ci) {
        Player player = this.minecraft.player;
        if (player != null) {
            FluidState eyeFluid = this.level.getFluidState(BlockPos.containing(player.getEyePosition(partialTick)));
            Fluid eyeType = eyeFluid.getType();
            boolean inAcid = eyeType == ACFluidRegistry.ACID_FLUID_SOURCE
                    || eyeType == ACFluidRegistry.ACID_FLUID_FLOWING;
            boolean inSoda = eyeType == ACFluidRegistry.PURPLE_SODA_FLUID_SOURCE
                    || eyeType == ACFluidRegistry.PURPLE_SODA_FLUID_FLOWING;
            if (inAcid || inSoda) {
                ci.cancel();
                runnable.run();
                ac_renderSkyCube(camera, inAcid ? 0.0F : 0.6F, inAcid ? 1.0F : 0.1F, inAcid ? 0.0F : 0.85F);
                return;
            }
        }

        float override = AlexsCavesClient.acSkyOverrideAmount;
        float primordialBoss = AlexsCaves.getPrimordialBossActiveAmount(partialTick);
        if (!AlexsCaves.CLIENT_CONFIG.biomeSkyOverrides || override <= 0.0F && primordialBoss <= 0.0F) {
            return;
        }
        ci.cancel();

        runnable.run();
        if (foggy) {
            return;
        }
        FogType fogtype = camera.getFluidInCamera();
        if (fogtype == FogType.POWDER_SNOW || fogtype == FogType.LAVA || this.doesMobEffectBlockSky(camera)) {
            return;
        }
        assert this.minecraft.level != null;
        if (this.minecraft.level.effects().skyType() != DimensionSpecialEffects.SkyType.NORMAL) {
            return;
        }

        Vec3 skyColor = this.level.getSkyColor(this.minecraft.gameRenderer.getMainCamera().getPosition(), partialTick);
        skyColor = AlexsCavesClient.processSkyColor(skyColor, partialTick);
        ac_renderSkyCube(camera, (float) skyColor.x, (float) skyColor.y, (float) skyColor.z);
    }

    @Unique
    private void ac_renderSkyCube(Camera camera, float r, float g, float b) {
        Vec3 camPos = camera.getPosition();
        float cx = (float) camPos.x;
        float cy = (float) camPos.y;
        float cz = (float) camPos.z;
        float size = 100.0F;

        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f mat = new PoseStack().last().pose();

        float x0 = cx - size, x1 = cx + size;
        float y0 = cy - size, y1 = cy + size;
        float z0 = cz - size, z1 = cz + size;

        buffer.addVertex(mat, x0, y1, z0).setColor(r, g, b, 1.0F);
        buffer.addVertex(mat, x0, y1, z1).setColor(r, g, b, 1.0F);
        buffer.addVertex(mat, x1, y1, z1).setColor(r, g, b, 1.0F);
        buffer.addVertex(mat, x1, y1, z0).setColor(r, g, b, 1.0F);

        buffer.addVertex(mat, x0, y0, z0).setColor(r, g, b, 1.0F);
        buffer.addVertex(mat, x1, y0, z0).setColor(r, g, b, 1.0F);
        buffer.addVertex(mat, x1, y0, z1).setColor(r, g, b, 1.0F);
        buffer.addVertex(mat, x0, y0, z1).setColor(r, g, b, 1.0F);

        buffer.addVertex(mat, x0, y0, z0).setColor(r, g, b, 1.0F);
        buffer.addVertex(mat, x0, y0, z1).setColor(r, g, b, 1.0F);
        buffer.addVertex(mat, x0, y1, z1).setColor(r, g, b, 1.0F);
        buffer.addVertex(mat, x0, y1, z0).setColor(r, g, b, 1.0F);

        buffer.addVertex(mat, x1, y0, z0).setColor(r, g, b, 1.0F);
        buffer.addVertex(mat, x1, y1, z0).setColor(r, g, b, 1.0F);
        buffer.addVertex(mat, x1, y1, z1).setColor(r, g, b, 1.0F);
        buffer.addVertex(mat, x1, y0, z1).setColor(r, g, b, 1.0F);

        buffer.addVertex(mat, x0, y0, z0).setColor(r, g, b, 1.0F);
        buffer.addVertex(mat, x0, y1, z0).setColor(r, g, b, 1.0F);
        buffer.addVertex(mat, x1, y1, z0).setColor(r, g, b, 1.0F);
        buffer.addVertex(mat, x1, y0, z0).setColor(r, g, b, 1.0F);

        buffer.addVertex(mat, x0, y0, z1).setColor(r, g, b, 1.0F);
        buffer.addVertex(mat, x1, y0, z1).setColor(r, g, b, 1.0F);
        buffer.addVertex(mat, x1, y1, z1).setColor(r, g, b, 1.0F);
        buffer.addVertex(mat, x0, y1, z1).setColor(r, g, b, 1.0F);

        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.enableCull();
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Inject(method = "Lnet/minecraft/client/renderer/LevelRenderer;getLightColor(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)I", at = @At("HEAD"), cancellable = true)
    private static void ac_getLightColor(BlockAndTintGetter level, BlockState state, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (state.getBlock() instanceof EnergizedGalenaBlock) {
            int i = level.getBrightness(LightLayer.SKY, pos);
            int j = level.getBrightness(LightLayer.BLOCK, pos);
            int k = state.getLightEmission() - 1;
            if (j < k) {
                j = k;
            }
            cir.setReturnValue(i << 20 | (j) << 4);
        }
    }
}