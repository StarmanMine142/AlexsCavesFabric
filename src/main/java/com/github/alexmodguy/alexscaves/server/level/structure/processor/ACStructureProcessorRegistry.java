package com.github.alexmodguy.alexscaves.server.level.structure.processor;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

public class ACStructureProcessorRegistry {

    public static final StructureProcessorType<UndergroundCabinProcessor> UNDERGROUND_CABIN = register("underground_cabin", () -> UndergroundCabinProcessor.CODEC);
    public static final StructureProcessorType<WhalefallProcessor> WHALEFALL = register("whalefall", () -> WhalefallProcessor.CODEC);
    public static final StructureProcessorType<WhalefallProcessor> WHALEFALL_SKULL = register("whalefall_skull", () -> WhalefallProcessor.CODEC_SKULL);
    public static final StructureProcessorType<LollipopProcessor> LOLLIPOP = register("lollipop", () -> LollipopProcessor.CODEC);
    public static final StructureProcessorType<SodaBottleProcessor> SODA_BOTTLE = register("soda_bottle", () -> SodaBottleProcessor.CODEC);

    public static void init() {
    }

    private static <T extends StructureProcessorType<?>> T register(String name, T type) {
        Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, name), type);
        return type;
    }
}