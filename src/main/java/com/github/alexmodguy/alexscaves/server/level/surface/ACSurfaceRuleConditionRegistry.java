package com.github.alexmodguy.alexscaves.server.level.surface;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.level.levelgen.AlexsCavesSimplexConditionSource;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class ACSurfaceRuleConditionRegistry {

    public static final MapCodec<AlexsCavesSimplexConditionSource> AC_SIMPLEX_CONDITION = register("ac_simplex", AlexsCavesSimplexConditionSource.CODEC.codec());

    public static SurfaceRules.ConditionSource simplexCondition(float noiseMin, float noiseMax, float noiseScale, float yScale, int offsetType) {
        return new AlexsCavesSimplexConditionSource(noiseMin, noiseMax, noiseScale, yScale, offsetType);
    }

    private static <T extends SurfaceRules.ConditionSource> MapCodec<T> register(String name, MapCodec<T> codec) {
        Registry.register(BuiltInRegistries.MATERIAL_CONDITION, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, name), codec);
        return codec;
    }
}