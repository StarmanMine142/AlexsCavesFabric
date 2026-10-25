package com.github.alexmodguy.alexscaves.server.level.carver;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldCarver;

public class ACCarverRegistry {

    //Unused for now.
    public static WaterBubbleCarver WATER_BUBBLE_CARVER = register("water_bubble_carver", new WaterBubbleCarver(CaveCarverConfiguration.CODEC));

    private static <T extends WorldCarver<?>> T register(String name, T carver) {
        Registry.register(BuiltInRegistries.CARVER, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, name), carver);
        return carver;
    }
}