package com.github.alexmodguy.alexscaves.server.block.blockentity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ACBlockEntityRegistry {

    public static final BlockEntityType<VolcanicCoreBlockEntity> VOLCANIC_CORE = registerBlockEntity("volcanic_core",
            BlockEntityType.Builder.of(VolcanicCoreBlockEntity::new, ACBlockRegistry.VOLCANIC_CORE));

    public static final BlockEntityType<MagnetBlockEntity> MAGNET = registerBlockEntity("magnet",
            BlockEntityType.Builder.of(MagnetBlockEntity::new, ACBlockRegistry.SCARLET_MAGNET, ACBlockRegistry.AZURE_MAGNET));

    public static final BlockEntityType<TeslaBulbBlockEntity> TESLA_BULB = registerBlockEntity("tesla_bulb",
            BlockEntityType.Builder.of(TeslaBulbBlockEntity::new, ACBlockRegistry.TESLA_BULB));

    public static final BlockEntityType<HologramProjectorBlockEntity> HOLOGRAM_PROJECTOR = registerBlockEntity("hologram_projector",
            BlockEntityType.Builder.of(HologramProjectorBlockEntity::new, ACBlockRegistry.HOLOGRAM_PROJECTOR));

    public static final BlockEntityType<QuarryBlockEntity> QUARRY = registerBlockEntity("quarry",
            BlockEntityType.Builder.of(QuarryBlockEntity::new, ACBlockRegistry.QUARRY));

    public static final BlockEntityType<AmbersolBlockEntity> AMBERSOL = registerBlockEntity("ambersol",
            BlockEntityType.Builder.of(AmbersolBlockEntity::new, ACBlockRegistry.AMBERSOL));

    public static final BlockEntityType<AmberMonolithBlockEntity> AMBER_MONOLITH = registerBlockEntity("amber_monolith",
            BlockEntityType.Builder.of(AmberMonolithBlockEntity::new, ACBlockRegistry.AMBER_MONOLITH));

    public static final BlockEntityType<GeothermalVentBlockEntity> GEOTHERMAL_VENT = registerBlockEntity("geothermal_vent",
            BlockEntityType.Builder.of(GeothermalVentBlockEntity::new, ACBlockRegistry.GEOTHERMAL_VENT, ACBlockRegistry.GEOTHERMAL_VENT_MEDIUM, ACBlockRegistry.GEOTHERMAL_VENT_THIN));

    public static final BlockEntityType<NuclearFurnaceBlockEntity> NUCLEAR_FURNACE = registerBlockEntity("nuclear_furnace",
            BlockEntityType.Builder.of(NuclearFurnaceBlockEntity::new, ACBlockRegistry.NUCLEAR_FURNACE));

    public static final BlockEntityType<SirenLightBlockEntity> SIREN_LIGHT = registerBlockEntity("siren_light",
            BlockEntityType.Builder.of(SirenLightBlockEntity::new, ACBlockRegistry.SIREN_LIGHT));

    public static final BlockEntityType<NuclearSirenBlockEntity> NUCLEAR_SIREN = registerBlockEntity("nuclear_siren",
            BlockEntityType.Builder.of(NuclearSirenBlockEntity::new, ACBlockRegistry.NUCLEAR_SIREN));

    public static final BlockEntityType<MetalBarrelBlockEntity> METAL_BARREL = registerBlockEntity("metal_barrel",
            BlockEntityType.Builder.of(MetalBarrelBlockEntity::new, ACBlockRegistry.METAL_BARREL, ACBlockRegistry.RUSTY_BARREL));

    public static final BlockEntityType<AbyssalAltarBlockEntity> ABYSSAL_ALTAR = registerBlockEntity("abyssal_altar",
            BlockEntityType.Builder.of(AbyssalAltarBlockEntity::new, ACBlockRegistry.ABYSSAL_ALTAR));

    public static final BlockEntityType<CopperValveBlockEntity> COPPER_VALVE = registerBlockEntity("copper_valve",
            BlockEntityType.Builder.of(CopperValveBlockEntity::new, ACBlockRegistry.COPPER_VALVE));

    public static final BlockEntityType<EnigmaticEngineBlockEntity> ENIGMATIC_ENGINE = registerBlockEntity("enigmatic_engine",
            BlockEntityType.Builder.of(EnigmaticEngineBlockEntity::new, ACBlockRegistry.ENIGMATIC_ENGINE));

    public static final BlockEntityType<BeholderBlockEntity> BEHOLDER = registerBlockEntity("beholder",
            BlockEntityType.Builder.of(BeholderBlockEntity::new, ACBlockRegistry.BEHOLDER));

    public static final BlockEntityType<GobthumperBlockEntity> GOBTHUMPER = registerBlockEntity("gobthumper",
            BlockEntityType.Builder.of(GobthumperBlockEntity::new, ACBlockRegistry.GOBTHUMPER));

    public static final BlockEntityType<ConversionCrucibleBlockEntity> CONVERSION_CRUCIBLE = registerBlockEntity("conversion_crucible",
            BlockEntityType.Builder.of(ConversionCrucibleBlockEntity::new, ACBlockRegistry.CONVERSION_CRUCIBLE));

    public static final BlockEntityType<GingerbarrelBlockEntity> GINGERBARREL = registerBlockEntity("gingerbarrel",
            BlockEntityType.Builder.of(GingerbarrelBlockEntity::new, ACBlockRegistry.GINGERBARREL));

    public static final BlockEntityType<ConfectionOvenBlockEntity> CONFECTION_OVEN = registerBlockEntity("confection_oven",
            BlockEntityType.Builder.of(ConfectionOvenBlockEntity::new, ACBlockRegistry.CONFECTION_OVEN));

    public static void register() {
    }

    public static void expandVanillaDefinitions() {
        // Vanilla 1.21.1 keeps the sign valid-block set private. Revisit with a mixin or access widener.
    }

    private static <T extends net.minecraft.world.level.block.entity.BlockEntity> BlockEntityType<T> registerBlockEntity(
            String name, BlockEntityType.Builder<T> builder) {
        return Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, name),
                builder.build(null)
        );
    }
}