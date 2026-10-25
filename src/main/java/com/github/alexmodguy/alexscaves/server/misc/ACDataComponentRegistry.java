package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public class ACDataComponentRegistry {

    public static final DataComponentType<ResourceKey<Biome>> CAVE_BIOME = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "cave_biome"),
            DataComponentType.<ResourceKey<Biome>>builder()
                    .persistent(ResourceKey.codec(Registries.BIOME))
                    .networkSynchronized(ResourceKey.streamCodec(Registries.BIOME))
                    .build()
    );

    public static void init() {
    }
}