package com.github.alexmodguy.alexscaves.server.level.structure.piece;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class ACStructurePieceRegistry {
    public static final StructurePieceType UNDERGROUND_CABIN = register("underground_cabin", UndergroundCabinStructurePiece::new);
    public static final StructurePieceType FERROCAVE = register("ferrocave", FerrocaveStructurePiece::new);
    public static final StructurePieceType DINO_BOWL = register("dino_bowl", DinoBowlStructurePiece::new);
    public static final StructurePieceType VOLCANO = register("volcano", VolcanoStructurePiece::new);
    public static final StructurePieceType ACID_PIT = register("acid_pit", AcidPitStructurePiece::new);
    public static final StructurePieceType OCEAN_TRENCH = register("ocean_trench", OceanTrenchStructurePiece::new);
    public static final StructurePieceType ABYSSAL_RUINS = register("abyssal_ruins", AbyssalRuinsStructurePiece::new);
    public static final StructurePieceType FORLORN_CANYON = register("forlorn_canyon", ForlornCanyonStructurePiece::new);
    public static final StructurePieceType FORLORN_BRIDGE = register("forlorn_bridge", ForlornBridgeStructurePiece::new);
    public static final StructurePieceType CAKE_CAVE = register("cake_cave", CakeCaveStructurePiece::new);
    public static final StructurePieceType SODA_BOTTLE = register("soda_bottle", SodaBottleStructurePiece::new);
    public static final StructurePieceType DONUT_ARCH = register("donut_arch", DonutArchStructurePiece::new);
    public static final StructurePieceType LICOWITCH_TOWER = register("licowitch_tower", LicowitchTowerStructurePiece::new);
    public static final StructurePieceType GINGERBREAD_HOUSE = register("gingerbread_house", GingerbreadHousePiece::new);
    public static final StructurePieceType GINGERBREAD_ROAD = register("gingerbread_road", GingerbreadRoadPiece::new);

    public static void init() {
    }

    private static StructurePieceType register(String name, StructurePieceType type) {
        return Registry.register(BuiltInRegistries.STRUCTURE_PIECE, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, name), type);
    }
}