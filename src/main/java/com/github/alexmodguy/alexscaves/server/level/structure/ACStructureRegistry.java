package com.github.alexmodguy.alexscaves.server.level.structure;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.StructureType;

public class ACStructureRegistry {

    public static final StructureType<UndergroundCabinStructure> UNDERGROUND_CABIN = register("underground_cabin", () -> UndergroundCabinStructure.CODEC);
    public static final StructureType<FerrocaveStructure> FERROCAVE = register("ferrocave", () -> FerrocaveStructure.CODEC);
    public static final StructureType<VolcanoStructure> VOLCANO = register("volcano", () -> VolcanoStructure.CODEC);
    public static final StructureType<DinoBowlStructure> DINO_BOWL = register("dino_bowl", () -> DinoBowlStructure.CODEC);
    public static final StructureType<AcidPitStructure> ACID_PIT = register("acid_pit", () -> AcidPitStructure.CODEC);
    public static final StructureType<OceanTrenchStructure> OCEAN_TRENCH = register("ocean_trench", () -> OceanTrenchStructure.CODEC);
    public static final StructureType<AbyssalRuinsStructure> ABYSSAL_RUINS = register("abyssal_ruins", () -> AbyssalRuinsStructure.CODEC);
    public static final StructureType<ForlornCanyonStructure> FORLORN_CANYON = register("forlorn_canyon", () -> ForlornCanyonStructure.CODEC);
    public static final StructureType<ForlornBridgeStructure> FORLORN_BRIDGE = register("forlorn_bridge", () -> ForlornBridgeStructure.CODEC);
    public static final StructureType<CakeCaveStructure> CAKE_CAVE = register("cake_cave", () -> CakeCaveStructure.CODEC);
    public static final StructureType<SodaBottleStructure> SODA_BOTTLE = register("soda_bottle", () -> SodaBottleStructure.CODEC);
    public static final StructureType<DonutArchStructure> DONUT_ARCH = register("donut_arch", () -> DonutArchStructure.CODEC);
    public static final StructureType<LicowitchTowerStructure> LICOWITCH_TOWER = register("licowitch_tower", () -> LicowitchTowerStructure.CODEC);
    public static final StructureType<GingerbreadTownStructure> GINGERBREAD_TOWN = register("gingerbread_town", () -> GingerbreadTownStructure.CODEC);

    public static void init() {
    }

    private static <T extends StructureType<?>> T register(String name, T type) {
        Registry.register(BuiltInRegistries.STRUCTURE_TYPE, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, name), type);
        return type;
    }
}