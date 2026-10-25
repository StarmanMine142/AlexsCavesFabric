package com.github.alexmodguy.alexscaves.server.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.item.*;
import com.github.alexmodguy.alexscaves.server.entity.living.*;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.levelgen.Heightmap;

public class ACEntityRegistry {

    public static final MobCategory CAVE_CREATURE = MobCategory.CREATURE;
    public static final MobCategory DEEP_SEA_CREATURE = MobCategory.WATER_CREATURE;

    public static final EntityType<AlexsCavesBoatEntity> BOAT = register("boat",
            EntityType.Builder.of(AlexsCavesBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10));

    public static final EntityType<AlexsCavesChestBoatEntity> CHEST_BOAT = register("chest_boat",
            EntityType.Builder.of(AlexsCavesChestBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10));

    public static final EntityType<MovingMetalBlockEntity> MOVING_METAL_BLOCK = register("moving_metal_block",
            EntityType.Builder.of(MovingMetalBlockEntity::new, MobCategory.MISC)
                    .sized(0.99F, 0.99F).updateInterval(10).clientTrackingRange(20));

    public static final EntityType<TeletorEntity> TELETOR = register("teletor",
            EntityType.Builder.of(TeletorEntity::new, MobCategory.MONSTER).sized(0.99F, 1.99F));

    public static final EntityType<MagneticWeaponEntity> MAGNETIC_WEAPON = register("magnetic_weapon",
            EntityType.Builder.of(MagneticWeaponEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).updateInterval(1).clientTrackingRange(20));

    public static final EntityType<MagnetronEntity> MAGNETRON = register("magnetron",
            EntityType.Builder.of(MagnetronEntity::new, MobCategory.MONSTER).sized(0.8F, 2.0F));

    public static final EntityType<BoundroidEntity> BOUNDROID = register("boundroid",
            EntityType.Builder.of(BoundroidEntity::new, MobCategory.MONSTER).sized(1.4F, 0.75F));

    public static final EntityType<BoundroidWinchEntity> BOUNDROID_WINCH = register("boundroid_winch",
            EntityType.Builder.of(BoundroidWinchEntity::new, MobCategory.MONSTER).sized(0.8F, 1.4F));

    public static final EntityType<FerrouslimeEntity> FERROUSLIME = register("ferrouslime",
            EntityType.Builder.of(FerrouslimeEntity::new, MobCategory.MONSTER).sized(0.85F, 0.85F));

    public static final EntityType<NotorEntity> NOTOR = register("notor",
            EntityType.Builder.of(NotorEntity::new, MobCategory.AMBIENT).sized(0.5F, 0.65F));

    public static final EntityType<QuarrySmasherEntity> QUARRY_SMASHER = register("quarry_smasher",
            EntityType.Builder.of(QuarrySmasherEntity::new, MobCategory.MISC)
                    .sized(0.9F, 1.2F).updateInterval(10).clientTrackingRange(20));

    public static final EntityType<SeekingArrowEntity> SEEKING_ARROW = register("seeking_arrow",
            EntityType.Builder.of(SeekingArrowEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).updateInterval(1));

    public static final EntityType<SubterranodonEntity> SUBTERRANODON = register("subterranodon",
            EntityType.Builder.of(SubterranodonEntity::new, CAVE_CREATURE)
                    .sized(1.75F, 1.2F).clientTrackingRange(12).updateInterval(1));

    public static final EntityType<VallumraptorEntity> VALLUMRAPTOR = register("vallumraptor",
            EntityType.Builder.of(VallumraptorEntity::new, CAVE_CREATURE)
                    .sized(0.8F, 1.5F).clientTrackingRange(8));

    public static final EntityType<GrottoceratopsEntity> GROTTOCERATOPS = register("grottoceratops",
            EntityType.Builder.of(GrottoceratopsEntity::new, CAVE_CREATURE)
                    .sized(2.3F, 2.5F).clientTrackingRange(8));

    public static final EntityType<TrilocarisEntity> TRILOCARIS = register("trilocaris",
            EntityType.Builder.of(TrilocarisEntity::new, MobCategory.WATER_AMBIENT).sized(0.9F, 0.4F));

    public static final EntityType<TremorsaurusEntity> TREMORSAURUS = register("tremorsaurus",
            EntityType.Builder.of(TremorsaurusEntity::new, CAVE_CREATURE)
                    .sized(2.5F, 3.85F).clientTrackingRange(8));

    public static final EntityType<RelicheirusEntity> RELICHEIRUS = register("relicheirus",
            EntityType.Builder.of(RelicheirusEntity::new, CAVE_CREATURE)
                    .sized(2.65F, 5.9F).clientTrackingRange(9));

    public static final EntityType<LuxtructosaurusEntity> LUXTRUCTOSAURUS = register("luxtructosaurus",
            EntityType.Builder.of(LuxtructosaurusEntity::new, MobCategory.MONSTER)
                    .sized(6.0F, 8.5F).clientTrackingRange(12).fireImmune());

    public static final EntityType<TephraEntity> TEPHRA = register("tephra",
            EntityType.Builder.of(TephraEntity::new, MobCategory.MISC)
                    .sized(0.6F, 0.6F).fireImmune().updateInterval(1));

    public static final EntityType<AtlatitanEntity> ATLATITAN = register("atlatitan",
            EntityType.Builder.of(AtlatitanEntity::new, CAVE_CREATURE)
                    .sized(5.0F, 8.0F).clientTrackingRange(11));

    public static final EntityType<FallingTreeBlockEntity> FALLING_TREE_BLOCK = register("falling_tree_block",
            EntityType.Builder.of(FallingTreeBlockEntity::new, MobCategory.MISC)
                    .sized(0.99F, 0.99F).updateInterval(10).clientTrackingRange(20));

    public static final EntityType<CrushedBlockEntity> CRUSHED_BLOCK = register("crushed_block",
            EntityType.Builder.of(CrushedBlockEntity::new, MobCategory.MISC)
                    .sized(0.99F, 0.99F).updateInterval(10).clientTrackingRange(20));

    public static final EntityType<LimestoneSpearEntity> LIMESTONE_SPEAR = register("limestone_spear",
            EntityType.Builder.of(LimestoneSpearEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).updateInterval(1));

    public static final EntityType<ExtinctionSpearEntity> EXTINCTION_SPEAR = register("extinction_spear",
            EntityType.Builder.of(ExtinctionSpearEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).updateInterval(1).fireImmune());

    public static final EntityType<DinosaurSpiritEntity> DINOSAUR_SPIRIT = register("dinosaur_spirit",
            EntityType.Builder.of(DinosaurSpiritEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F).updateInterval(1).fireImmune());

    public static final EntityType<NuclearExplosionEntity> NUCLEAR_EXPLOSION = register("nuclear_explosion",
            EntityType.Builder.of(NuclearExplosionEntity::new, MobCategory.MISC)
                    .sized(0.99F, 0.99F).updateInterval(10).clientTrackingRange(20));

    public static final EntityType<NuclearBombEntity> NUCLEAR_BOMB = register("nuclear_bomb",
            EntityType.Builder.of(NuclearBombEntity::new, MobCategory.MISC)
                    .sized(0.98F, 0.98F).updateInterval(10).clientTrackingRange(20));

    public static final EntityType<NucleeperEntity> NUCLEEPER = register("nucleeper",
            EntityType.Builder.of(NucleeperEntity::new, MobCategory.MONSTER).sized(0.98F, 3.95F));

    public static final EntityType<RadgillEntity> RADGILL = register("radgill",
            EntityType.Builder.of(RadgillEntity::new, MobCategory.WATER_AMBIENT).sized(0.9F, 0.6F));

    public static final EntityType<BrainiacEntity> BRAINIAC = register("brainiac",
            EntityType.Builder.of(BrainiacEntity::new, MobCategory.MONSTER).sized(1.3F, 2.5F));

    public static final EntityType<ThrownWasteDrumEntity> THROWN_WASTE_DRUM = register("thrown_waste_drum",
            EntityType.Builder.of(ThrownWasteDrumEntity::new, MobCategory.MISC)
                    .sized(0.98F, 0.98F).updateInterval(10).clientTrackingRange(20));

    public static final EntityType<GammaroachEntity> GAMMAROACH = register("gammaroach",
            EntityType.Builder.of(GammaroachEntity::new, MobCategory.AMBIENT).sized(1.25F, 0.9F));

    public static final EntityType<RaycatEntity> RAYCAT = register("raycat",
            EntityType.Builder.of(RaycatEntity::new, CAVE_CREATURE).sized(0.85F, 0.6F));

    public static final EntityType<CinderBrickEntity> CINDER_BRICK = register("cinder_brick",
            EntityType.Builder.of(CinderBrickEntity::new, MobCategory.MISC)
                    .sized(0.4F, 0.4F).updateInterval(1));

    public static final EntityType<TremorzillaEntity> TREMORZILLA = register("tremorzilla",
            EntityType.Builder.of(TremorzillaEntity::new, CAVE_CREATURE)
                    .sized(4.5F, 11F).clientTrackingRange(11).fireImmune());

    public static final EntityType<LanternfishEntity> LANTERNFISH = register("lanternfish",
            EntityType.Builder.of(LanternfishEntity::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.4F));

    public static final EntityType<SeaPigEntity> SEA_PIG = register("sea_pig",
            EntityType.Builder.of(SeaPigEntity::new, DEEP_SEA_CREATURE).sized(0.5F, 0.65F));

    public static final EntityType<SubmarineEntity> SUBMARINE = register("submarine",
            EntityType.Builder.of(SubmarineEntity::new, MobCategory.MISC)
                    .sized(3.5F, 3.3F).updateInterval(10).clientTrackingRange(20));

    public static final EntityType<HullbreakerEntity> HULLBREAKER = register("hullbreaker",
            EntityType.Builder.of(HullbreakerEntity::new, MobCategory.UNDERGROUND_WATER_CREATURE)
                    .sized(4.65F, 4.5F).clientTrackingRange(20));

    public static final EntityType<GossamerWormEntity> GOSSAMER_WORM = register("gossamer_worm",
            EntityType.Builder.of(GossamerWormEntity::new, DEEP_SEA_CREATURE).sized(1.15F, 0.5F));

    public static final EntityType<TripodfishEntity> TRIPODFISH = register("tripodfish",
            EntityType.Builder.of(TripodfishEntity::new, DEEP_SEA_CREATURE).sized(0.95F, 0.5F));

    public static final EntityType<DeepOneEntity> DEEP_ONE = register("deep_one",
            EntityType.Builder.of(DeepOneEntity::new, MobCategory.MONSTER)
                    .sized(0.9F, 2.2F).clientTrackingRange(12).updateInterval(1));

    public static final EntityType<InkBombEntity> INK_BOMB = register("ink_bomb",
            EntityType.Builder.of(InkBombEntity::new, MobCategory.MISC)
                    .sized(0.6F, 0.6F).updateInterval(1));

    public static final EntityType<DeepOneKnightEntity> DEEP_ONE_KNIGHT = register("deep_one_knight",
            EntityType.Builder.of(DeepOneKnightEntity::new, MobCategory.MONSTER)
                    .sized(1.15F, 2.5F).clientTrackingRange(12).updateInterval(1));

    public static final EntityType<DeepOneMageEntity> DEEP_ONE_MAGE = register("deep_one_mage",
            EntityType.Builder.of(DeepOneMageEntity::new, MobCategory.MONSTER)
                    .sized(1.35F, 2.5F).clientTrackingRange(12).updateInterval(1));

    public static final EntityType<WaterBoltEntity> WATER_BOLT = register("water_bolt",
            EntityType.Builder.of(WaterBoltEntity::new, MobCategory.MISC)
                    .sized(0.6F, 0.6F).updateInterval(1));

    public static final EntityType<WaveEntity> WAVE = register("wave",
            EntityType.Builder.of(WaveEntity::new, MobCategory.MISC)
                    .sized(0.9F, 0.9F).updateInterval(1));

    public static final EntityType<MineGuardianEntity> MINE_GUARDIAN = register("mine_guardian",
            EntityType.Builder.of(MineGuardianEntity::new, MobCategory.MONSTER)
                    .sized(1.3F, 1.3F).updateInterval(1));

    public static final EntityType<MineGuardianAnchorEntity> MINE_GUARDIAN_ANCHOR = register("mine_guardian_anchor",
            EntityType.Builder.of(MineGuardianAnchorEntity::new, MobCategory.MISC).sized(0.6F, 1.35F));

    public static final EntityType<DepthChargeEntity> DEPTH_CHARGE = register("depth_charge",
            EntityType.Builder.of(DepthChargeEntity::new, MobCategory.MISC)
                    .sized(0.7F, 0.7F).updateInterval(1));

    public static final EntityType<FloaterEntity> FLOATER = register("floater",
            EntityType.Builder.of(FloaterEntity::new, MobCategory.MISC)
                    .sized(0.98F, 0.98F).updateInterval(10).clientTrackingRange(20));

    public static final EntityType<GuanoEntity> GUANO = register("guano",
            EntityType.Builder.of(GuanoEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).updateInterval(1));

    public static final EntityType<FallingGuanoEntity> FALLING_GUANO = register("falling_guano",
            EntityType.Builder.of(FallingGuanoEntity::new, MobCategory.MISC)
                    .sized(0.8F, 0.9F).updateInterval(10).clientTrackingRange(20));

    public static final EntityType<GloomothEntity> GLOOMOTH = register("gloomoth",
            EntityType.Builder.of(GloomothEntity::new, MobCategory.AMBIENT)
                    .sized(0.99F, 0.99F).clientTrackingRange(12).updateInterval(1));

    public static final EntityType<UnderzealotEntity> UNDERZEALOT = register("underzealot",
            EntityType.Builder.of(UnderzealotEntity::new, MobCategory.MONSTER).sized(0.8F, 1.2F));

    public static final EntityType<WatcherEntity> WATCHER = register("watcher",
            EntityType.Builder.of(WatcherEntity::new, MobCategory.MONSTER).sized(0.9F, 1.9F));

    public static final EntityType<CorrodentEntity> CORRODENT = register("corrodent",
            EntityType.Builder.of(CorrodentEntity::new, MobCategory.MONSTER).sized(0.9F, 0.9F));

    public static final EntityType<VesperEntity> VESPER = register("vesper",
            EntityType.Builder.of(VesperEntity::new, MobCategory.MONSTER)
                    .sized(1.2F, 1.65F).clientTrackingRange(12).updateInterval(1));

    public static final EntityType<ForsakenEntity> FORSAKEN = register("forsaken",
            EntityType.Builder.of(ForsakenEntity::new, MobCategory.MONSTER)
                    .sized(3F, 3.5F).clientTrackingRange(12).updateInterval(1));

    public static final EntityType<BeholderEyeEntity> BEHOLDER_EYE = register("beholder_eye",
            EntityType.Builder.of(BeholderEyeEntity::new, MobCategory.MISC).sized(0.3F, 0.3F));

    public static final EntityType<DesolateDaggerEntity> DESOLATE_DAGGER = register("desolate_dagger",
            EntityType.Builder.of(DesolateDaggerEntity::new, MobCategory.MISC)
                    .sized(0.6F, 0.6F).updateInterval(1));

    public static final EntityType<BurrowingArrowEntity> BURROWING_ARROW = register("burrowing_arrow",
            EntityType.Builder.of(BurrowingArrowEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).updateInterval(1));

    public static final EntityType<DarkArrowEntity> DARK_ARROW = register("dark_arrow",
            EntityType.Builder.of(DarkArrowEntity::new, MobCategory.MISC)
                    .sized(1.1F, 0.5F).updateInterval(1));

    public static final EntityType<SweetishFishEntity> SWEETISH_FISH = register("sweetish_fish",
            EntityType.Builder.of(SweetishFishEntity::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.45F));

    public static final EntityType<CaniacEntity> CANIAC = register("caniac",
            EntityType.Builder.of(CaniacEntity::new, MobCategory.MONSTER)
                    .sized(0.9F, 2.3F).clientTrackingRange(12).updateInterval(1));

    public static final EntityType<GumbeeperEntity> GUMBEEPER = register("gumbeeper",
            EntityType.Builder.of(GumbeeperEntity::new, MobCategory.MONSTER).sized(0.8F, 1.6F));

    public static final EntityType<GumballEntity> GUMBALL = register("gumball",
            EntityType.Builder.of(GumballEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).updateInterval(1));

    public static final EntityType<CandicornEntity> CANDICORN = register("candicorn",
            EntityType.Builder.of(CandicornEntity::new, CAVE_CREATURE)
                    .sized(1.7F, 2.25F).updateInterval(1));

    public static final EntityType<GumWormEntity> GUM_WORM = register("gum_worm",
            EntityType.Builder.of(GumWormEntity::new, MobCategory.MONSTER)
                    .sized(3.5F, 2.7F).clientTrackingRange(14).updateInterval(1).fireImmune());

    public static final EntityType<GumWormSegmentEntity> GUM_WORM_SEGMENT = register("gum_worm_segment",
            EntityType.Builder.of(GumWormSegmentEntity::new, MobCategory.MISC)
                    .sized(2.0F, 2.0F).clientTrackingRange(14).updateInterval(1).fireImmune());

    public static final EntityType<CaramelCubeEntity> CARAMEL_CUBE = register("caramel_cube",
            EntityType.Builder.of(CaramelCubeEntity::new, MobCategory.MONSTER).sized(0.8F, 0.8F));

    public static final EntityType<MeltedCaramelEntity> MELTED_CARAMEL = register("melted_caramel",
            EntityType.Builder.of(MeltedCaramelEntity::new, MobCategory.MISC).sized(0.99F, 0.1F));

    public static final EntityType<GummyBearEntity> GUMMY_BEAR = register("gummy_bear",
            EntityType.Builder.of(GummyBearEntity::new, CAVE_CREATURE).sized(1.45F, 1.2F));

    public static final EntityType<LicowitchEntity> LICOWITCH = register("licowitch",
            EntityType.Builder.of(LicowitchEntity::new, MobCategory.MONSTER).sized(0.8F, 1.9F));

    public static final EntityType<SpinningPeppermintEntity> SPINNING_PEPPERMINT = register("spinning_peppermint",
            EntityType.Builder.of(SpinningPeppermintEntity::new, MobCategory.MISC).sized(0.8F, 0.4F));

    public static final EntityType<SugarStaffHexEntity> SUGAR_STAFF_HEX = register("sugar_staff_hex",
            EntityType.Builder.of(SugarStaffHexEntity::new, MobCategory.MISC).sized(4.0F, 0.25F));

    public static final EntityType<GingerbreadManEntity> GINGERBREAD_MAN = register("gingerbread_man",
            EntityType.Builder.of(GingerbreadManEntity::new, MobCategory.MONSTER).sized(0.5F, 0.9F));

    public static final EntityType<ThrownIceCreamScoopEntity> THROWN_ICE_CREAM_SCOOP = register("thrown_ice_cream_scoop",
            EntityType.Builder.of(ThrownIceCreamScoopEntity::new, MobCategory.MISC)
                    .sized(0.4F, 0.4F).updateInterval(1));

    public static final EntityType<FallingFrostmintEntity> FALLING_FROSTMINT = register("falling_frostmint",
            EntityType.Builder.of(FallingFrostmintEntity::new, MobCategory.MISC)
                    .sized(0.8F, 0.9F).updateInterval(10).clientTrackingRange(20));

    public static final EntityType<CandyCaneHookEntity> CANDY_CANE_HOOK = register("candy_cane_hook",
            EntityType.Builder.of(CandyCaneHookEntity::new, MobCategory.MISC)
                    .sized(0.6F, 0.6F).updateInterval(10).clientTrackingRange(20));

    public static final EntityType<SodaBottleRocketEntity> SODA_BOTTLE_ROCKET = register("soda_bottle_rocket",
            EntityType.Builder.of(SodaBottleRocketEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).updateInterval(1));

    public static final EntityType<FrostmintSpearEntity> FROSTMINT_SPEAR = register("frostmint_spear",
            EntityType.Builder.of(FrostmintSpearEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).updateInterval(1));

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder builder) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, name);
        return (EntityType<T>) Registry.register(BuiltInRegistries.ENTITY_TYPE, id, builder.build(id.toString()));
    }

    public static void initializeAttributes() {
        FabricDefaultAttributeRegistry.register(TELETOR, TeletorEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(MAGNETRON, MagnetronEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(BOUNDROID, BoundroidEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(BOUNDROID_WINCH, BoundroidEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(FERROUSLIME, FerrouslimeEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(NOTOR, NotorEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(SUBTERRANODON, SubterranodonEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(VALLUMRAPTOR, VallumraptorEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(GROTTOCERATOPS, GrottoceratopsEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(TRILOCARIS, TrilocarisEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(TREMORSAURUS, TremorsaurusEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(RELICHEIRUS, RelicheirusEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(LUXTRUCTOSAURUS, LuxtructosaurusEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ATLATITAN, AtlatitanEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(NUCLEEPER, NucleeperEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(RADGILL, RadgillEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(BRAINIAC, BrainiacEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(GAMMAROACH, GammaroachEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(RAYCAT, RaycatEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(TREMORZILLA, TremorzillaEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(LANTERNFISH, LanternfishEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(SEA_PIG, SeaPigEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(HULLBREAKER, HullbreakerEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(GOSSAMER_WORM, GossamerWormEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(TRIPODFISH, TripodfishEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(DEEP_ONE, DeepOneEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(DEEP_ONE_KNIGHT, DeepOneKnightEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(DEEP_ONE_MAGE, DeepOneMageEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(MINE_GUARDIAN, MineGuardianEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(GLOOMOTH, GloomothEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(UNDERZEALOT, UnderzealotEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(WATCHER, WatcherEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(CORRODENT, CorrodentEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(VESPER, VesperEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(FORSAKEN, ForsakenEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(SWEETISH_FISH, SweetishFishEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(CANIAC, CaniacEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(GUMBEEPER, GumbeeperEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(CANDICORN, CandicornEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(GUM_WORM, GumWormEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(CARAMEL_CUBE, CaramelCubeEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(GUMMY_BEAR, GummyBearEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(LICOWITCH, LicowitchEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(GINGERBREAD_MAN, GingerbreadManEntity.createAttributes());
    }

    public static void spawnPlacements() {
        SpawnPlacements.register(TELETOR, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TeletorEntity::checkMagneticCaveSpawnRules);
        SpawnPlacements.register(MAGNETRON, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MagnetronEntity::checkMagneticCaveSpawnRules);
        SpawnPlacements.register(BOUNDROID, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BoundroidEntity::checkMagneticCaveSpawnRules);
        SpawnPlacements.register(FERROUSLIME, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, FerrouslimeEntity::checkMagneticCaveSpawnRules);
        SpawnPlacements.register(NOTOR, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, NotorEntity::checkNotorSpawnRules);
        SpawnPlacements.register(SUBTERRANODON, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SubterranodonEntity::checkSubterranodonSpawnRules);
        SpawnPlacements.register(VALLUMRAPTOR, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, VallumraptorEntity::checkPrehistoricSpawnRules);
        SpawnPlacements.register(GROTTOCERATOPS, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GrottoceratopsEntity::checkPrehistoricSpawnRules);
        SpawnPlacements.register(TRILOCARIS, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TrilocarisEntity::checkTrilocarisSpawnRules);
        SpawnPlacements.register(TREMORSAURUS, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TremorsaurusEntity::checkPrehistoricSpawnRules);
        SpawnPlacements.register(RELICHEIRUS, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, RelicheirusEntity::checkPrehistoricSpawnRules);
        SpawnPlacements.register(LUXTRUCTOSAURUS, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, LuxtructosaurusEntity::checkPrehistoricSpawnRules);
        SpawnPlacements.register(ATLATITAN, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AtlatitanEntity::checkPrehistoricPostBossSpawnRules);
        SpawnPlacements.register(NUCLEEPER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, NucleeperEntity::checkMonsterSpawnRules);
        SpawnPlacements.register(RADGILL, ACSpawnPlacementTypes.IN_ACID, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, RadgillEntity::checkRadgillSpawnRules);
        SpawnPlacements.register(BRAINIAC, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BrainiacEntity::checkMonsterSpawnRules);
        SpawnPlacements.register(GAMMAROACH, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GammaroachEntity::checkGammaroachSpawnRules);
        SpawnPlacements.register(RAYCAT, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, RaycatEntity::checkRaycatSpawnRules);
        SpawnPlacements.register(LANTERNFISH, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, LanternfishEntity::checkLanternfishSpawnRules);
        SpawnPlacements.register(SEA_PIG, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SeaPigEntity::checkSeaPigSpawnRules);
        SpawnPlacements.register(HULLBREAKER, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, HullbreakerEntity::checkHullbreakerSpawnRules);
        SpawnPlacements.register(GOSSAMER_WORM, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GossamerWormEntity::checkGossamerWormSpawnRules);
        SpawnPlacements.register(TRIPODFISH, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TripodfishEntity::checkTripodfishSpawnRules);
        SpawnPlacements.register(DEEP_ONE, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DeepOneBaseEntity::checkDeepOneSpawnRules);
        SpawnPlacements.register(DEEP_ONE_KNIGHT, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DeepOneBaseEntity::checkDeepOneSpawnRules);
        SpawnPlacements.register(DEEP_ONE_MAGE, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DeepOneBaseEntity::checkDeepOneSpawnRules);
        SpawnPlacements.register(MINE_GUARDIAN, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MineGuardianEntity::checkMineGuardianSpawnRules);
        SpawnPlacements.register(GLOOMOTH, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GloomothEntity::checkGloomothSpawnRules);
        SpawnPlacements.register(UNDERZEALOT, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, UnderzealotEntity::checkUnderzealotSpawnRules);
        SpawnPlacements.register(WATCHER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WatcherEntity::checkWatcherSpawnRules);
        SpawnPlacements.register(CORRODENT, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CorrodentEntity::checkCorrodentSpawnRules);
        SpawnPlacements.register(VESPER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, VesperEntity::checkVesperSpawnRules);
        SpawnPlacements.register(FORSAKEN, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ForsakenEntity::checkForsakenSpawnRules);
        SpawnPlacements.register(SWEETISH_FISH, ACSpawnPlacementTypes.IN_PURPLE_SODA, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SweetishFishEntity::checkSweetishFishSpawnRules);
        SpawnPlacements.register(CANIAC, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CaniacEntity::checkCaniacSpawnRules);
        SpawnPlacements.register(GUMBEEPER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GumbeeperEntity::checkGumbeeperSpawnRules);
        SpawnPlacements.register(CANDICORN, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CandicornEntity::checkCandicornSpawnRules);
        SpawnPlacements.register(GUM_WORM, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GumWormEntity::checkGumWormSpawnRules);
        SpawnPlacements.register(CARAMEL_CUBE, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CaramelCubeEntity::checkCaramelCubeSpawnRules);
        SpawnPlacements.register(GUMMY_BEAR, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GummyBearEntity::checkGummyBearSpawnRules);
        SpawnPlacements.register(LICOWITCH, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, LicowitchEntity::checkLicowitchSpawnRules);
        SpawnPlacements.register(GINGERBREAD_MAN, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GingerbreadManEntity::checkGingerbreadManSpawnRules);
    }
}