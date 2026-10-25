package com.github.alexmodguy.alexscaves.server.level.levelgen;

import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.SurfaceRules;

public record AlexsCavesSimplexConditionSource(float noiseMin, float noiseMax, float noiseScale, float yScale, int offsetType) implements SurfaceRules.ConditionSource {
    public static final KeyDispatchDataCodec<AlexsCavesSimplexConditionSource> CODEC = KeyDispatchDataCodec.of(
        RecordCodecBuilder.mapCodec(group -> group.group(
            Codec.floatRange(-1F, 1F).fieldOf("noise_min").forGetter(AlexsCavesSimplexConditionSource::noiseMin),
            Codec.floatRange(-1F, 1F).fieldOf("noise_max").forGetter(AlexsCavesSimplexConditionSource::noiseMax),
            Codec.floatRange(1F, 10000F).fieldOf("noise_scale").forGetter(AlexsCavesSimplexConditionSource::noiseScale),
            Codec.floatRange(0F, 10000F).fieldOf("y_scale").forGetter(AlexsCavesSimplexConditionSource::yScale),
            Codec.intRange(0, 128).fieldOf("offset_type").forGetter(AlexsCavesSimplexConditionSource::offsetType)
        ).apply(group, AlexsCavesSimplexConditionSource::new))
    );

    @Override
    public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
        return CODEC;
    }

    @Override
    public SurfaceRules.Condition apply(final SurfaceRules.Context context) {
        return () -> {
            double noise = ACMath.sampleNoise3D(
                context.blockX + (offsetType * 1000),
                (int) (context.blockY * yScale + offsetType * 2000),
                context.blockZ - (offsetType * 3000),
                noiseScale
            );
            return noise > noiseMin && noise <= noiseMax;
        };
    }
}
