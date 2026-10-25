package com.github.alexmodguy.alexscaves.client.render.entity.layer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.EntityType;

@Environment(EnvType.CLIENT)
public class ClientLayerRegistry {

    public static void register() {
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(ClientLayerRegistry::addLayers);
    }

    public static void addLayers(EntityType<?> entityType,
                                 EntityRenderer<?> entityRenderer,
                                 LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper registrationHelper,
                                 EntityRendererProvider.Context context) {
        if (entityRenderer instanceof LivingEntityRenderer<?, ?> livingRenderer) {
            registrationHelper.register(new ACPotionEffectLayer(livingRenderer));
        }
    }
}