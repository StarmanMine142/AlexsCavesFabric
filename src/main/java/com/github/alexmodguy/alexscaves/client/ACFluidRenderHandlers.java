package com.github.alexmodguy.alexscaves.client;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.resources.ResourceLocation;

public class ACFluidRenderHandlers {

    public static void register() {
        FluidRenderHandlerRegistry.INSTANCE.register(
                ACFluidRegistry.ACID_FLUID_SOURCE,
                ACFluidRegistry.ACID_FLUID_FLOWING,
                new SimpleFluidRenderHandler(
                        ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "block/acid_still"),
                        ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "block/acid_flowing")
                )
        );
        FluidRenderHandlerRegistry.INSTANCE.register(
                ACFluidRegistry.PURPLE_SODA_FLUID_SOURCE,
                ACFluidRegistry.PURPLE_SODA_FLUID_FLOWING,
                new SimpleFluidRenderHandler(
                        ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "block/purple_soda_still"),
                        ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "block/purple_soda_flowing")
                )
        );
    }
}