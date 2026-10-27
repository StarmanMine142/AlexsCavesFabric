package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.server.entity.util.MultiPartEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderHitbox(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/entity/Entity;FFFF)V",
                    shift = At.Shift.AFTER
            )
    )
    private <E extends Entity> void alexscaves$renderMultipartHitboxes(
            E entity, double x, double y, double z, float yRot, float partialTicks,
            PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        if (!(entity instanceof MultiPartEntity multipart)) return;
        if (!multipart.acIsMultipartEntity()) return;

        Entity[] parts = multipart.getParts();
        if (parts == null || parts.length == 0) return;

        VertexConsumer lines = bufferSource.getBuffer(RenderType.lines());
        for (Entity part : parts) {
            if (part == null || !part.isAlive()) continue;

            poseStack.pushPose();
            poseStack.translate(part.getX() - entity.getX(), part.getY() - entity.getY(), part.getZ() - entity.getZ());
            AABB box = part.getBoundingBox().move(-part.getX(), -part.getY(), -part.getZ());
            LevelRenderer.renderLineBox(poseStack, lines, box, 0.25F, 1.0F, 0.0F, 1.0F);
            poseStack.popPose();
        }
    }
}