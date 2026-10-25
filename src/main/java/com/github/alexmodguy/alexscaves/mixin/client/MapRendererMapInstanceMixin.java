package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.client.event.ClientEvents;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapRenderer.class)
public class MapRendererMapInstanceMixin {

    @Inject(method = "render", at = @At(value = "HEAD"))
    private void ac_render(PoseStack poseStack, MultiBufferSource multiBufferSource, MapId mapId, MapItemSavedData data, boolean inFrame, int packedLighting, CallbackInfo ci) {
        ClientEvents.lastVanillaMapPoseStack = poseStack;
        ClientEvents.lastVanillaMapRenderBuffer = multiBufferSource;
        ClientEvents.lastVanillaMapRenderPackedLight = packedLighting;
    }
}
