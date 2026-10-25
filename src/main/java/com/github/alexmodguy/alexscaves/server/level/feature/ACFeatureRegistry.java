package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.level.feature.config.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class ACFeatureRegistry {

    public static final Feature<GalenaHexagonFeatureConfiguration> GALENA_HEXAGON = register("galena_hexagon", new GalenaHexagonFeature(GalenaHexagonFeatureConfiguration.CODEC));
    public static final Feature<MagneticNodeFeatureConfiguration> MAGNETIC_NODE = register("magnetic_node", new MagneticNodeFeature(MagneticNodeFeatureConfiguration.CODEC));
    public static final Feature<UndergroundRuinsFeatureConfiguration> UNDERGROUND_RUINS = register("underground_ruins", new UndergroundRuinsFeature(UndergroundRuinsFeatureConfiguration.CODEC));
    public static final Feature<FloatingOrbFeatureConfig> FLOATING_ORB = register("floating_orb", new FloatingOrbFeature(FloatingOrbFeatureConfig.CODEC));
    public static final Feature<NoneFeatureConfiguration> TESLA_BULB = register("tesla_bulb", new TeslaBulbFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<CoveredBlockBlobConfiguration> COVERED_BLOCK_BLOB = register("covered_block_blob", new CoveredBlockBlobFeature(CoveredBlockBlobConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> AMBERSOL = register("ambersol", new AmbersolFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> PEWEN_TREE = register("pewen_tree", new PewenTreeFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> ANCIENT_TREE = register("ancient_tree", new AncientTreeFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> GIANT_ANCIENT_TREE = register("giant_ancient_tree", new GiantAncientTreeFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> CYCAD = register("cycad", new CycadFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> AMBER_MONOLITH = register("amber_monolith", new AmberMonolithFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> SUBTERRANODON_ROOST = register("subterranodon_roost", new SubterranodonRoostFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> VOLCANO_BOULDER = register("volcano_boulder", new VolcanoBoulderFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> ACID_VENT = register("acid_vent", new AcidVentFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> SULFUR_STACK = register("sulfur_stack", new SulfurStackFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> NUCLEAR_SIREN = register("nuclear_siren", new NuclearSirenFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<FillBiomeAboveConfiguration> FILL_BIOME_ABOVE = register("fill_biome_above", new FillBiomeAboveFeature(FillBiomeAboveConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> FILL_IN_BUBBLES_WITH_WATER = register("fill_in_bubbles_with_water", new FillInBubblesWithWaterFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> BLACK_VENT = register("black_vent", new BlackVentFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> TUBE_WORM = register("tube_worm", new TubeWormFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<WhalefallFeatureConfiguration> WHALEFALL = register("whalefall", new WhalefallFeature(WhalefallFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> PING_PONG_SPONGE = register("ping_pong_sponge", new PingPongSpongeFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<AbyssalFloraFeatureConfiguration> ABYSSAL_FLORA = register("abyssal_flora", new AbyssalFloraFeature(AbyssalFloraFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> ABYSSAL_BOULDER = register("abyssal_boulder", new AbyssalBoulderFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> MUSSEL = register("mussel", new MusselFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<UndergroundRuinsFeatureConfiguration> DEEP_ONE_RUINS = register("deep_one_ruins", new DeepOnesRuinsFeature(UndergroundRuinsFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> PEERING_COPROLITH = register("peering_coprolith", new PeeringCoprolithFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> THORNWOOD_TREE = register("thornwood_tree", new ThornwoodTreeFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> THORNWOOD_TREE_WITH_BRANCHES = register("thornwood_tree_with_branches", new ThornwoodTreeWithBranchesFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> THORNWOOD_ROOTS = register("thornwood_roots", new ThornwoodRootsFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> GUANO_PILE = register("guano_pile", new GuanoPileFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<UndergroundRuinsFeatureConfiguration> FORLORN_RUINS = register("forlorn_ruins", new ForlornRuinsFeature(UndergroundRuinsFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> PEPPERMINT_PILE = register("peppermint_patch", new PeppermintPileFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> ENCRUSTED_PEPPERMINT = register("encrusted_peppermint", new EncrustedPeppermintFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> LICOROOT_TREE = register("licoroot_tree", new LicorootTreeFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> LICOROOT_TREE_WITH_SPROUTS = register("licoroot_tree_with_sprouts", new LicorootTreeWithSproutsFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> SPILLED_ICE_CREAM_CONE = register("spilled_ice_cream_cone", new SpilledIceCreamConeFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<IceCreamScoopFeatureConfiguration> ICE_CREAM_SCOOP = register("ice_cream_scoop", new IceCreamScoopFeature(IceCreamScoopFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> CEILING_ICE_CREAM_CONE = register("ceiling_ice_cream_cone", new CeilingIceCreamConeFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> SWEET_PUFF = register("sweet_puff", new SweetPuffFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> SPRINKLES_PILE = register("sprinkles_pile", new SprinklesPileFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> CANDY_CANE = register("candy_cane", new CandyCaneFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> SUNDROP_PATCH = register("sundrop_patch", new SundropPatchFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<LollipopFeatureConfiguration> LOLLIPOP = register("lollipop", new LollipopFeature(LollipopFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> COOKIE_SHELF = register("cookie_shelf", new CookieShelfFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<UndergroundRuinsFeatureConfiguration> CANDY_RUINS = register("candy_ruins", new CandyRuinsFeature(UndergroundRuinsFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> GOBSTOPPER_GEODE = register("gobstopper_geode", new GobstopperGeodeFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> CEILING_FROSTMINT = register("ceiling_frostmint", new CeilingFrostmintFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> FLOATING_GUMMY_RING = register("floating_gummy_ring", new FloatingGummyRingFeature(NoneFeatureConfiguration.CODEC));

    public static void init() {
    }

    private static <C extends FeatureConfiguration, F extends Feature<C>> F register(String name, F feature) {
        Registry.register(BuiltInRegistries.FEATURE, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, name), feature);
        return feature;
    }
}