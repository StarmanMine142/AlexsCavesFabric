package com.github.alexmodguy.alexscaves.client.event;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.misc.ACVanillaMapUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import org.joml.Matrix4f;

public class ClientEvents {

    public static PoseStack lastVanillaMapPoseStack;
    public static MultiBufferSource lastVanillaMapRenderBuffer;
    public static int lastVanillaMapRenderPackedLight;
    private static final RenderType UNDERGROUND_CABIN_MAP_ICONS = RenderType.text(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/misc/underground_cabin_map_icons.png")
    );

    public static void renderVanillaMapDecoration(MapDecoration mapDecoration, int index) {
        if (!ACVanillaMapUtil.isUndergroundCabinDecoration(mapDecoration.type())) {
            return;
        }
        MultiBufferSource multiBufferSource = lastVanillaMapRenderBuffer == null
                ? Minecraft.getInstance().renderBuffers().bufferSource()
                : lastVanillaMapRenderBuffer;
        PoseStack poseStack = lastVanillaMapPoseStack == null ? new PoseStack() : lastVanillaMapPoseStack;
        poseStack.pushPose();
        poseStack.translate((float) mapDecoration.x() / 2.0F + 64.0F, (float) mapDecoration.y() / 2.0F + 64.0F, -0.02F);
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) (mapDecoration.rot() * 360) / 16.0F));
        poseStack.scale(4.0F, 4.0F, 3.0F);
        poseStack.translate(-0.125F, 0.125F, 0.0F);
        byte icon = ACVanillaMapUtil.getMapIconRenderOrdinal(mapDecoration.type());
        float u0 = (float) (icon % 16) / 16.0F;
        float v0 = (float) (icon / 16) / 16.0F;
        float u1 = (float) (icon % 16 + 1) / 16.0F;
        float v1 = (float) (icon / 16 + 1) / 16.0F;
        Matrix4f pose = poseStack.last().pose();
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(UNDERGROUND_CABIN_MAP_ICONS);
        vertexConsumer.addVertex(pose, -1.0F, 1.0F, (float) index * -0.001F).setColor(255, 255, 255, 255).setUv(u0, v0).setLight(lastVanillaMapRenderPackedLight);
        vertexConsumer.addVertex(pose, 1.0F, 1.0F, (float) index * -0.001F).setColor(255, 255, 255, 255).setUv(u1, v0).setLight(lastVanillaMapRenderPackedLight);
        vertexConsumer.addVertex(pose, 1.0F, -1.0F, (float) index * -0.001F).setColor(255, 255, 255, 255).setUv(u1, v1).setLight(lastVanillaMapRenderPackedLight);
        vertexConsumer.addVertex(pose, -1.0F, -1.0F, (float) index * -0.001F).setColor(255, 255, 255, 255).setUv(u0, v1).setLight(lastVanillaMapRenderPackedLight);
        poseStack.popPose();

        mapDecoration.name().ifPresent(component -> {
            Font font = Minecraft.getInstance().font;
            float width = font.width(component);
            float scale = Mth.clamp(25.0F / width, 0.0F, 6.0F / 9.0F);
            poseStack.pushPose();
            poseStack.translate((float) mapDecoration.x() / 2.0F + 64.0F - width * scale / 2.0F, (float) mapDecoration.y() / 2.0F + 68.0F, -0.025F);
            poseStack.scale(scale, scale, 1.0F);
            poseStack.translate(0.0F, 0.0F, -0.1F);
            font.drawInBatch(component, 0.0F, 0.0F, -1, false, poseStack.last().pose(), multiBufferSource, Font.DisplayMode.NORMAL, Integer.MIN_VALUE, lastVanillaMapRenderPackedLight);
            poseStack.popPose();
        });
    }

    public static void register() {

    }
}
