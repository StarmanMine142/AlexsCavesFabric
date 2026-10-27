package com.github.alexmodguy.alexscaves.client;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.citadel.client.shader.PostEffectRegistry;
import com.github.alexmodguy.alexscaves.citadel.client.tick.ClientTickRateTracker;
import com.github.alexmodguy.alexscaves.client.event.ClientEvents;
import com.github.alexmodguy.alexscaves.client.gui.NuclearFurnaceScreen;
import com.github.alexmodguy.alexscaves.client.gui.SpelunkeryTableScreen;
import com.github.alexmodguy.alexscaves.client.gui.book.CaveBookScreen;
import com.github.alexmodguy.alexscaves.client.model.baked.BakedModelShadeLayerFullbright;
import com.github.alexmodguy.alexscaves.client.model.layered.ACModelLayers;
import com.github.alexmodguy.alexscaves.client.particle.*;
import com.github.alexmodguy.alexscaves.client.render.ACBlockRenderLayerRegistry;
import com.github.alexmodguy.alexscaves.client.render.ACInternalShaders;
import com.github.alexmodguy.alexscaves.client.render.blockentity.*;
import com.github.alexmodguy.alexscaves.client.render.entity.*;
import com.github.alexmodguy.alexscaves.client.render.entity.layer.ClientLayerRegistry;
import com.github.alexmodguy.alexscaves.client.render.item.ACArmorRenderProperties;
import com.github.alexmodguy.alexscaves.client.render.item.ACItemstackRenderer;
import com.github.alexmodguy.alexscaves.client.render.item.RaygunRenderHelper;
import com.github.alexmodguy.alexscaves.client.render.item.tooltip.ClientSackOfSatingTooltip;
import com.github.alexmodguy.alexscaves.client.sound.*;
import com.github.alexmodguy.alexscaves.multipart.ACMultipartClient;
import com.github.alexmodguy.alexscaves.network.client.ACNetworkClient;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.AcidBlock;
import com.github.alexmodguy.alexscaves.server.block.ActivatedByAltar;
import com.github.alexmodguy.alexscaves.server.block.FrostedChocolateBlock;
import com.github.alexmodguy.alexscaves.server.block.blockentity.*;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.BeholderEyeEntity;
import com.github.alexmodguy.alexscaves.server.entity.item.QuarrySmasherEntity;
import com.github.alexmodguy.alexscaves.server.entity.item.SubmarineEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.*;
import com.github.alexmodguy.alexscaves.server.inventory.ACMenuRegistry;
import com.github.alexmodguy.alexscaves.server.item.*;
import com.github.alexmodguy.alexscaves.server.item.tooltip.SackOfSatingTooltip;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.BiomeSampler;
import com.github.alexmodguy.alexscaves.server.misc.ACKeybindRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class AlexsCavesClient implements ClientModInitializer {

    private static final List<String> FULLBRIGHTS = ImmutableList.of("alexscaves:ambersol#",
            "alexscaves:radrock_uranium_ore#", "alexscaves:acidic_radrock#", "alexscaves:uranium_rod#axis=x",
            "alexscaves:uranium_rod#axis=y", "alexscaves:uranium_rod#axis=z", "alexscaves:block_of_uranium#",
            "alexscaves:abyssal_altar#active=true", "alexscaves:abyssmarine_", "alexscaves:peering_coprolith#",
            "alexscaves:forsaken_idol#", "alexscaves:magnetic_light#", "alexscaves:tremorzilla_egg#");
    public static final ResourceLocation BOMB_FLASH = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID,
            "textures/misc/bomb_flash.png");
    public static final ResourceLocation WATCHER_EFFECT = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID,
            "textures/misc/watcher_effect.png");
    public static final ResourceLocation IRRADIATED_SHADER = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID,
            "shaders/post/irradiated.json");
    public static final ResourceLocation HOLOGRAM_SHADER = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID,
            "shaders/post/hologram.json");
    public static final ResourceLocation PURPLE_WITCH_SHADER = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID,
            "shaders/post/purple_witch.json");
    public static final ResourceLocation SUGAR_RUSH_SHADER = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID,
            "shaders/post/sugar_rush.json");
    public static final RandomSource random = RandomSource.create();
    public static int lastTremorTick = -1;
    public static float[] randomTremorOffsets = new float[3];
    public static List<UUID> blockedEntityRenders = new ArrayList<>();
    public static Map<ClientLevel, List<BlockPos>> blockedParticleLocations = new HashMap<>();
    public static Map<LivingEntity, Vec3[]> darknessTrailPosMap = new HashMap<>();
    public static Map<LivingEntity, Integer> darknessTrailPointerMap = new HashMap<>();
    public static int muteNonNukeSoundsFor = 0;
    public static int renderNukeFlashFor = 0;
    public static boolean primordialBossActive = false;
    public static float prevPrimordialBossActiveAmount = 0;
    public static float primordialBossActiveAmount = 0;
    public static float prevNukeFlashAmount = 0;
    public static float nukeFlashAmount = 0;
    public static float prevPossessionStrengthAmount = 0;
    public static float possessionStrengthAmount = 0;
    public static int renderNukeSkyDarkFor = 0;
    public static int renderNukeShakeFor = 0;
    public static float masterVolumeNukeModifier = 0.0F;
    private static final Int2IntMap BUBBLED_EFFECT_TICKS = new Int2IntOpenHashMap();
    public static final Int2ObjectMap<AbstractTickableSoundInstance> ENTITY_SOUND_INSTANCE_MAP = new Int2ObjectOpenHashMap<>();
    public static final Map<BlockEntity, AbstractTickableSoundInstance> BLOCK_ENTITY_SOUND_INSTANCE_MAP = new HashMap<>();
    private static final ACArmorRenderProperties ARMOR_PROPERTIES = new ACArmorRenderProperties();
    @FunctionalInterface
    private interface ShaderRegistrar {
        void register(ResourceLocation id, VertexFormat vertexFormat, Consumer<ShaderInstance> consumer)
                throws IOException;
    }
    public static boolean spelunkeryTutorialComplete;
    public static boolean hasACSplashText = false;
    public static CameraType lastPOV = CameraType.FIRST_PERSON;
    public static int shaderLoadAttemptCooldown = 0;
    public static Vec3 lastBiomeLightColor = new Vec3(1.0D, 1.0D, 1.0D);
    public static float lastBiomeAmbientLightAmount = 0;
    public static Vec3 lastBiomeLightColorPrev = new Vec3(1.0D, 1.0D, 1.0D);
    public static float lastBiomeAmbientLightAmountPrev = 0;
    public static Map<UUID, Integer> bossBarRenderTypes = new HashMap<>();
    private static Entity lastCameraEntity;
    public static float acSkyOverrideAmount;
    public static Vec3 acSkyOverrideColor = Vec3.ZERO;
    public static boolean disabledBiomeAmbientLightByOtherMod = false;
    private static float lastSampledFogNearness = 1.0F;
    private static float lastSampledWaterFogFarness = 1.0F;
    private static Vec3 lastSampledFogColor = Vec3.ZERO;
    private static Vec3 lastSampledWaterFogColor = Vec3.ZERO;
    private static final ACItemstackRenderer FABRIC_ITEM_RENDERER = new ACItemstackRenderer();

    @Override
    public void onInitializeClient() {
        ACNetworkClient.registerClient();
        ACMultipartClient.registerClient();

        ACModelLayers.registerFabric();
        ClientLayerRegistry.register();
        registerFabricClientLifecycle();
        registerFabricBuiltinItemRenderers();
        registerFabricFluidRendering();

        commonInit();
        clientInit();

        ACFluidRenderHandlers.register();
    }

    public static void commonInit() {
        setupParticles();
        registerKeybinds();
        onItemColors();
        onBlockColors();
        onRegisterTooltips();
        onRegisterMenuScreens();
    }

    public static void clientInit() {
        ClientEvents.register();
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(ClientLayerRegistry::addLayers);
        bakeModels();
        registerFabricShaders();
        EntityRendererRegistry.register(ACEntityRegistry.BOAT, (context) -> new AlexsCavesBoatRenderer(context, false));
        EntityRendererRegistry.register(ACEntityRegistry.CHEST_BOAT, (context) -> new AlexsCavesBoatRenderer(context, true));
        BlockEntityRenderers.register(ACBlockEntityRegistry.MAGNET, MagnetBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.TESLA_BULB, TelsaBulbBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.HOLOGRAM_PROJECTOR, HologramProjectorBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.QUARRY, QuarryBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.AMBERSOL, AmbersolBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.AMBER_MONOLITH, AmberMonolithBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.NUCLEAR_FURNACE, NuclearFurnaceBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.SIREN_LIGHT, SirenLightBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.ABYSSAL_ALTAR, AbyssalAltarBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.COPPER_VALVE, CopperValveBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.BEHOLDER, BeholderBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.GOBTHUMPER, GobthumperBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.CONVERSION_CRUCIBLE, ConversionCrucibleBlockRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.MOVING_METAL_BLOCK, MovingMetalBlockRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.TELETOR, TeletorRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.MAGNETIC_WEAPON, MagneticWeaponRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.MAGNETRON, MagnetronRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.BOUNDROID, BoundroidRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.BOUNDROID_WINCH, BoundroidWinchRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.FERROUSLIME, FerrouslimeRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.NOTOR, NotorRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.QUARRY_SMASHER, QuarrySmasherRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.SEEKING_ARROW, SeekingArrowRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.SUBTERRANODON, SubterranodonRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.VALLUMRAPTOR, VallumraptorRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.GROTTOCERATOPS, GrottoceratopsRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.TRILOCARIS, TrilocarisRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.TREMORSAURUS, TremorsaurusRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.RELICHEIRUS, RelicheirusRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.FALLING_TREE_BLOCK, FallingTreeBlockRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.CRUSHED_BLOCK, CrushedBlockRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.LIMESTONE_SPEAR, LimestoneSpearRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.EXTINCTION_SPEAR, ExtinctionSpearRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.DINOSAUR_SPIRIT, DinosaurSpiritRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.LUXTRUCTOSAURUS, LuxtructosaurusRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.TEPHRA, TephraRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.ATLATITAN, AtlatitanRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.NUCLEAR_EXPLOSION, EmptyRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.NUCLEAR_BOMB, NuclearBombRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.NUCLEEPER, NucleeperRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.RADGILL, RadgillRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.BRAINIAC, BrainiacRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.THROWN_WASTE_DRUM, ThrownWasteDrumEntityRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.GAMMAROACH, GammaroachRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.RAYCAT, RaycatRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.CINDER_BRICK, (context) -> new ThrownItemRenderer<>(context, 1.25F, false));
        EntityRendererRegistry.register(ACEntityRegistry.TREMORZILLA, TremorzillaRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.LANTERNFISH, LanternfishRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.SEA_PIG, SeaPigRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.SUBMARINE, SubmarineRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.HULLBREAKER, HullbreakerRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.GOSSAMER_WORM, GossamerWormRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.TRIPODFISH, TripodfishRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.DEEP_ONE, DeepOneRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.INK_BOMB, (context) -> new ThrownItemRenderer<>(context, 1.25F, false));
        EntityRendererRegistry.register(ACEntityRegistry.DEEP_ONE_KNIGHT, DeepOneKnightRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.DEEP_ONE_MAGE, DeepOneMageRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.WATER_BOLT, WaterBoltRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.WAVE, WaveRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.MINE_GUARDIAN, MineGuardianRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.MINE_GUARDIAN_ANCHOR, MineGuardianAnchorRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.DEPTH_CHARGE, (context) -> new ThrownItemRenderer<>(context, 1.75F, true));
        EntityRendererRegistry.register(ACEntityRegistry.FLOATER, FloaterRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.GUANO, (context) -> new ThrownItemRenderer<>(context, 1.25F, false));
        EntityRendererRegistry.register(ACEntityRegistry.FALLING_GUANO, FallingBlockRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.GLOOMOTH, GloomothRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.UNDERZEALOT, UnderzealotRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.WATCHER, WatcherRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.CORRODENT, CorrodentRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.VESPER, VesperRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.FORSAKEN, ForsakenRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.BEHOLDER_EYE, EmptyRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.DESOLATE_DAGGER, DesolateDaggerRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.BURROWING_ARROW, BurrowingArrowRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.DARK_ARROW, DarkArrowRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.SWEETISH_FISH, SweetishFishRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.CANIAC, CaniacRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.GUMBEEPER, GumbeeperRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.GUMBALL, GumballRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.CANDICORN, CandicornRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.GUM_WORM, GumWormRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.GUM_WORM_SEGMENT, GumWormSegmentRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.CARAMEL_CUBE, CaramelCubeRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.MELTED_CARAMEL, MeltedCaramelRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.GUMMY_BEAR, GummyBearRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.LICOWITCH, LicowitchRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.SPINNING_PEPPERMINT, SpinningPeppermintRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.SUGAR_STAFF_HEX, SugarStaffHexRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.GINGERBREAD_MAN, GingerbreadManRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.FALLING_FROSTMINT, FallingBlockRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.CANDY_CANE_HOOK, CandyCaneHookRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.SODA_BOTTLE_ROCKET, (render) -> new ThrownItemRenderer<>(render, 1.25F, true));
        EntityRendererRegistry.register(ACEntityRegistry.FROSTMINT_SPEAR, FrostmintSpearRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.THROWN_ICE_CREAM_SCOOP, (context) -> new ThrownItemRenderer<>(context, 1.25F, false));
        ItemProperties.register(ACItemRegistry.HOLOCODER, ResourceLocation.withDefaultNamespace("bound"),
                (stack, level, living, j) -> HolocoderItem.isBound(stack) ? 1.0F : 0.0F);
        ItemProperties.register(ACItemRegistry.DINOSAUR_NUGGET, ResourceLocation.withDefaultNamespace("nugget"),
                (stack, level, living, j) -> (stack.getCount() % 4) / 4F);
        ItemProperties.register(ACItemRegistry.LIMESTONE_SPEAR, ResourceLocation.withDefaultNamespace("throwing"),
                (stack, level, living, j) -> living != null && living.isUsingItem() && living.getUseItem() == stack ? 1.0F : 0.0F);
        ItemProperties.register(ACItemRegistry.EXTINCTION_SPEAR, ResourceLocation.withDefaultNamespace("throwing"),
                (stack, level, living, j) -> living != null && living.isUsingItem() && living.getUseItem() == stack ? 1.0F : 0.0F);
        ItemProperties.register(ACItemRegistry.REMOTE_DETONATOR, ResourceLocation.withDefaultNamespace("active"),
                (stack, level, living, j) -> RemoteDetonatorItem.isActive(stack) ? 1.0F : 0.0F);
        ItemProperties.register(ACItemRegistry.MAGIC_CONCH, ResourceLocation.withDefaultNamespace("tooting"),
                (stack, level, living, j) -> living != null && living.isUsingItem() && living.getUseItem() == stack ? 1.0F : 0.0F);
        ItemProperties.register(ACItemRegistry.ORTHOLANCE, ResourceLocation.withDefaultNamespace("charging"),
                (stack, level, living, j) -> living != null && living.isUsingItem() && living.getUseItem() == stack ? 1.0F : 0.0F);
        ItemProperties.register(ACItemRegistry.TOTEM_OF_POSSESSION, ResourceLocation.withDefaultNamespace("totem"),
                (stack, level, living, j) -> TotemOfPossessionItem.isBound(stack)
                        ? living != null && living.isUsingItem() && living.getUseItem() == stack ? 1.0F : 0.5F : 0.0F);
        ItemProperties.register(ACItemRegistry.CANDY_CANE_HOOK, ResourceLocation.withDefaultNamespace("cast"),
                (stack, level, holder, i) -> holder != null && CandyCaneHookItem.isActive(stack) ? 1.0F : 0.0F);
        ItemProperties.register(ACItemRegistry.SACK_OF_SATING, ResourceLocation.withDefaultNamespace("open"),
                (stack, level, living, j) -> level != null && SackOfSatingItem.isChewing(stack, level.getGameTime()) ? 1.0F
                        : stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).isEmpty()
                        || living instanceof Player player && player.containerMenu != null
                        && SackOfSatingItem.calculateWholeStackHungerValue(player.containerMenu.getCarried(), player) > 0 ? 0.5F : 0.0F);
        ItemProperties.register(ACItemRegistry.FROSTMINT_SPEAR, ResourceLocation.withDefaultNamespace("throwing"),
                (stack, level, living, j) -> living != null && living.isUsingItem() && living.getUseItem() == stack ? 1.0F : 0.0F);
        blockedParticleLocations.clear();
        PostEffectRegistry.registerEffect(IRRADIATED_SHADER);
        PostEffectRegistry.registerEffect(HOLOGRAM_SHADER);
        PostEffectRegistry.registerEffect(PURPLE_WITCH_SHADER);
        PostEffectRegistry.registerEffect(SUGAR_RUSH_SHADER);
        ACBlockRenderLayerRegistry.register();
        hasACSplashText = random.nextInt(300) == 0;
        BlockRenderLayerMap.INSTANCE.putFluids(RenderType.cutoutMipped(), ACFluidRegistry.ACID_FLUID_SOURCE, ACFluidRegistry.ACID_FLUID_FLOWING);
        BlockRenderLayerMap.INSTANCE.putFluids(RenderType.translucent(), ACFluidRegistry.PURPLE_SODA_FLUID_SOURCE, ACFluidRegistry.PURPLE_SODA_FLUID_FLOWING);
    }

    public static void tickBubbledEffects() {
        if (BUBBLED_EFFECT_TICKS.isEmpty()) return;
        var iterator = BUBBLED_EFFECT_TICKS.int2IntEntrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            int newValue = entry.getIntValue() - 1;
            if (newValue <= 0) {
                iterator.remove();
            } else {
                entry.setValue(newValue);
            }
        }
    }

    public static void setPrimordialBossActive(int id, boolean active) {
        primordialBossActive = active;
        if (!active && id != -1) {
            Minecraft.getInstance().getMusicManager().stopPlaying(ACMusics.LUXTRUCTOSAURUS_BOSS_MUSIC);
        }
    }

    public static boolean isPrimordialBossActive() {
        return primordialBossActive;
    }

    public static void registerFabricShaders() {
        CoreShaderRegistrationCallback.EVENT.register(context -> registerInternalShaders(context::register));
    }

    public static void registerFabricClientLifecycle() {
        ClientTickEvents.END_CLIENT_TICK.register(AlexsCavesClient::onFabricClientTick);
        WorldRenderEvents.BEFORE_ENTITIES.register(AlexsCavesClient::onFabricBeforeEntities);
        WorldRenderEvents.AFTER_ENTITIES.register(AlexsCavesClient::onFabricAfterEntities);
    }

    public static void registerFabricBuiltinItemRenderers() {
        registerBuiltinItemRenderer(ACItemRegistry.CAVE_MAP);
        registerBuiltinItemRenderer(ACItemRegistry.DREADBOW);
        registerBuiltinItemRenderer(ACItemRegistry.RAYGUN);
        registerBuiltinItemRenderer(ACItemRegistry.PRIMITIVE_CLUB);
        registerBuiltinItemRenderer(ACItemRegistry.LIMESTONE_SPEAR);
        registerBuiltinItemRenderer(ACItemRegistry.EXTINCTION_SPEAR);
        registerBuiltinItemRenderer(ACItemRegistry.FROSTMINT_SPEAR);
        registerBuiltinItemRenderer(ACItemRegistry.SEA_STAFF);
        registerBuiltinItemRenderer(ACItemRegistry.ORTHOLANCE);
        registerBuiltinItemRenderer(ACItemRegistry.GALENA_GAUNTLET);
        registerBuiltinItemRenderer(ACItemRegistry.RESISTOR_SHIELD);
        registerBuiltinItemRenderer(ACItemRegistry.SHOT_GUM);
        registerBuiltinItemRenderer(ACItemRegistry.SUGAR_STAFF);
        registerBuiltinItemRenderer(ACBlockRegistry.SIREN_LIGHT);
        registerBuiltinItemRenderer(ACBlockRegistry.COPPER_VALVE);
        registerBuiltinItemRenderer(ACBlockRegistry.BEHOLDER);
        registerBuiltinItemRenderer(ACBlockRegistry.GOBTHUMPER);
    }

    public static void registerFabricFluidRendering() {
        FluidRenderHandlerRegistry.INSTANCE.register(
                ACFluidRegistry.ACID_FLUID_SOURCE,
                ACFluidRegistry.ACID_FLUID_FLOWING,
                new SimpleFluidRenderHandler(
                        ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "block/acid_still"),
                        ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "block/acid_flowing")
                )
        );
        FluidRenderHandlerRegistry.INSTANCE.register(
                ACFluidRegistry.PURPLE_SODA_FLUID_SOURCE,
                ACFluidRegistry.PURPLE_SODA_FLUID_FLOWING,
                new SimpleFluidRenderHandler(
                        ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "block/purple_soda_still"),
                        ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "block/purple_soda_flowing")
                )
        );
    }

    private static void registerBuiltinItemRenderer(net.minecraft.world.level.ItemLike itemLike) {
        BuiltinItemRendererRegistry.INSTANCE.register(itemLike.asItem(), (stack, mode, matrices, vertexConsumers, light, overlay) ->
                FABRIC_ITEM_RENDERER.render(stack, mode, matrices, vertexConsumers, light, overlay));
    }

    private static void onFabricClientTick(Minecraft minecraft) {
        Entity cameraEntity = minecraft.getCameraEntity();
        tickBubbledEffects();
        if (shaderLoadAttemptCooldown > 0) shaderLoadAttemptCooldown--;
        if (renderNukeSkyDarkFor > 0) renderNukeSkyDarkFor--;
        if (renderNukeShakeFor > 0) renderNukeShakeFor--;
        if (renderNukeFlashFor > 0) renderNukeFlashFor--;
        if (muteNonNukeSoundsFor > 0) muteNonNukeSoundsFor--;
        prevNukeFlashAmount = nukeFlashAmount;
        nukeFlashAmount = Mth.approach(nukeFlashAmount, renderNukeFlashFor > 0 ? 1.0F : 0.0F, renderNukeFlashFor > 0 ? 0.25F : 0.08F);
        masterVolumeNukeModifier = Mth.approach(masterVolumeNukeModifier, muteNonNukeSoundsFor > 0 ? 1.0F : 0.0F, 0.08F);
        if (cameraEntity == null || minecraft.level == null) {
            acSkyOverrideAmount = 0.0F;
            acSkyOverrideColor = Vec3.ZERO;
            lastBiomeLightColorPrev = lastBiomeLightColor;
            lastBiomeLightColor = new Vec3(1.0D, 1.0D, 1.0D);
            lastBiomeAmbientLightAmountPrev = lastBiomeAmbientLightAmount;
            lastBiomeAmbientLightAmount = 0.0F;
            lastSampledFogNearness = 1.0F;
            lastSampledWaterFogFarness = 1.0F;
            lastSampledFogColor = Vec3.ZERO;
            lastSampledWaterFogColor = Vec3.ZERO;
            return;
        }
        acSkyOverrideAmount = ACBiomeRegistry.calculateBiomeSkyOverride(cameraEntity);
        if (acSkyOverrideAmount > 0.0F) {
            acSkyOverrideColor = BiomeSampler.sampleBiomesVec3(minecraft.level, cameraEntity.position(),
                    biomeHolder -> Vec3.fromRGB24(biomeHolder.value().getSkyColor()));
        } else {
            acSkyOverrideColor = Vec3.ZERO;
        }
        lastBiomeLightColorPrev = lastBiomeLightColor;
        lastBiomeLightColor = calculateBiomeLightColor(cameraEntity);
        lastBiomeAmbientLightAmountPrev = lastBiomeAmbientLightAmount;
        lastBiomeAmbientLightAmount = calculateBiomeAmbientLight(cameraEntity);
        lastSampledFogNearness = calculateBiomeFogNearness(cameraEntity);
        lastSampledWaterFogFarness = calculateBiomeWaterFogFarness(cameraEntity);
        if (cameraEntity.level() instanceof ClientLevel) {
            lastSampledFogColor = calculateBiomeFogColor(cameraEntity);
            lastSampledWaterFogColor = calculateBiomeWaterFogColor(cameraEntity);
        } else {
            lastSampledFogColor = Vec3.ZERO;
            lastSampledWaterFogColor = Vec3.ZERO;
        }
    }

    private static void onFabricBeforeEntities(WorldRenderContext context) {
        renderFirstPersonRaygunRays(context, 2);
    }

    private static void onFabricAfterEntities(WorldRenderContext context) {
        renderFirstPersonRaygunRays(context, 1);
    }

    private static void renderFirstPersonRaygunRays(WorldRenderContext context, int firstPersonPass) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.options.getCameraType().isFirstPerson() || context.matrixStack() == null || context.consumers() == null) {
            return;
        }
        if (!(minecraft.getCameraEntity() instanceof LivingEntity living)) {
            return;
        }
        float partialTick = minecraft.getTimer().getGameTimeDeltaPartialTick(true);
        RaygunRenderHelper.renderRaysFor(living, minecraft.gameRenderer.getMainCamera().getPosition(), context.matrixStack(), context.consumers(), partialTick, true, firstPersonPass);
    }

    private static float calculateBiomeAmbientLight(Entity player) {
        int blendRadius = Minecraft.getInstance().options.biomeBlendRadius().get();
        if (blendRadius == 0) {
            return ACBiomeRegistry.getBiomeAmbientLight(player.level().getBiome(player.blockPosition()));
        }
        return BiomeSampler.sampleBiomesFloat(player.level(), player.position(), ACBiomeRegistry::getBiomeAmbientLight);
    }

    private static Vec3 calculateBiomeLightColor(Entity player) {
        int blendRadius = Minecraft.getInstance().options.biomeBlendRadius().get();
        if (blendRadius == 0) {
            return ACBiomeRegistry.getBiomeLightColorOverride(player.level().getBiome(player.blockPosition()));
        }
        return BiomeSampler.sampleBiomesVec3(player.level(), player.position(), ACBiomeRegistry::getBiomeLightColorOverride);
    }

    private static float calculateBiomeFogNearness(Entity player) {
        int blendRadius = Minecraft.getInstance().options.biomeBlendRadius().get();
        if (blendRadius == 0) {
            return ACBiomeRegistry.getBiomeFogNearness(player.level().getBiome(player.blockPosition()));
        }
        return BiomeSampler.sampleBiomesFloat(player.level(), player.position(), ACBiomeRegistry::getBiomeFogNearness);
    }

    private static float calculateBiomeWaterFogFarness(Entity player) {
        int blendRadius = Minecraft.getInstance().options.biomeBlendRadius().get();
        if (blendRadius == 0) {
            return ACBiomeRegistry.getBiomeWaterFogFarness(player.level().getBiome(player.blockPosition()));
        }
        return BiomeSampler.sampleBiomesFloat(player.level(), player.position(), ACBiomeRegistry::getBiomeWaterFogFarness);
    }

    private static Vec3 calculateBiomeFogColor(Entity player) {
        ClientLevel level = (ClientLevel) player.level();
        int blendRadius = Minecraft.getInstance().options.biomeBlendRadius().get();
        if (blendRadius == 0) {
            return level.effects().getBrightnessDependentFogColor(
                    Vec3.fromRGB24(level.getBiomeManager().getNoiseBiomeAtPosition(player.blockPosition()).value().getFogColor()), 1.0F);
        }
        return level.effects().getBrightnessDependentFogColor(
                BiomeSampler.sampleBiomesVec3(player.level(), player.position(), biomeHolder -> Vec3.fromRGB24(biomeHolder.value().getFogColor())), 1.0F);
    }

    private static Vec3 calculateBiomeWaterFogColor(Entity player) {
        ClientLevel level = (ClientLevel) player.level();
        int blendRadius = Minecraft.getInstance().options.biomeBlendRadius().get();
        if (blendRadius == 0) {
            return level.effects().getBrightnessDependentFogColor(
                    Vec3.fromRGB24(level.getBiomeManager().getNoiseBiomeAtPosition(player.blockPosition()).value().getWaterFogColor()), 1.0F);
        }
        return level.effects().getBrightnessDependentFogColor(
                BiomeSampler.sampleBiomesVec3(player.level(), player.position(), biomeHolder -> Vec3.fromRGB24(biomeHolder.value().getWaterFogColor())), 1.0F);
    }

    public static float getLastSampledFogNearness() { return lastSampledFogNearness; }
    public static float getLastSampledWaterFogFarness() { return lastSampledWaterFogFarness; }
    public static Vec3 getLastSampledFogColor() { return lastSampledFogColor; }
    public static Vec3 getLastSampledWaterFogColor() { return lastSampledWaterFogColor; }

    public static void onRegisterMenuScreens() {
        MenuScreens.register(ACMenuRegistry.SPELUNKERY_TABLE_MENU, SpelunkeryTableScreen::new);
        MenuScreens.register(ACMenuRegistry.NUCLEAR_FURNACE_MENU, NuclearFurnaceScreen::new);
    }

    public static void setupParticles() {
        ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();
        registry.register(ACParticleRegistry.SCARLET_MAGNETIC_ORBIT, new MagneticOrbitParticle.ScarletFactory());
        registry.register(ACParticleRegistry.AZURE_MAGNETIC_ORBIT, new MagneticOrbitParticle.AzureFactory());
        registry.register(ACParticleRegistry.SCARLET_MAGNETIC_FLOW, new MagneticFlowParticle.ScarletFactory());
        registry.register(ACParticleRegistry.AZURE_MAGNETIC_FLOW, new MagneticFlowParticle.AzureFactory());
        registry.register(ACParticleRegistry.TESLA_BULB_LIGHTNING, new TeslaBulbLightningParticle.Factory());
        registry.register(ACParticleRegistry.MAGNET_LIGHTNING, new MagnetLightningParticle.Factory());
        registry.register(ACParticleRegistry.GALENA_DEBRIS, GalenaDebrisParticle.Factory::new);
        registry.register(ACParticleRegistry.MAGNETIC_CAVES_AMBIENT, new MagneticCavesAmbientParticle.Factory());
        registry.register(ACParticleRegistry.FERROUSLIME, FerrouslimeParticle.Factory::new);
        registry.register(ACParticleRegistry.QUARRY_BORDER_LIGHTING, new QuarryBorderLightningParticle.Factory());
        registry.register(ACParticleRegistry.AZURE_SHIELD_LIGHTNING, new ResistorShieldLightningParticle.AzureFactory());
        registry.register(ACParticleRegistry.SCARLET_SHIELD_LIGHTNING, new ResistorShieldLightningParticle.ScarletFactory());
        registry.register(ACParticleRegistry.FLY, FlyParticle.Factory::new);
        registry.register(ACParticleRegistry.WATER_TREMOR, WaterTremorParticle.Factory::new);
        registry.register(ACParticleRegistry.AMBER_MONOLITH, AmberMonolithParticle.Factory::new);
        registry.register(ACParticleRegistry.AMBER_EXPLOSION, SmallExplosionParticle.AmberFactory::new);
        registry.register(ACParticleRegistry.DINOSAUR_TRANSFORMATION_AMBER, new DinosaurTransformParticle.AmberFactory());
        registry.register(ACParticleRegistry.DINOSAUR_TRANSFORMATION_TECTONIC, new DinosaurTransformParticle.TectonicFactory());
        registry.register(ACParticleRegistry.STUN_STAR, new StunStarParticle.Factory());
        registry.register(ACParticleRegistry.TEPHRA, TephraParticle.Factory::new);
        registry.register(ACParticleRegistry.TEPHRA_SMALL, TephraParticle.SmallFactory::new);
        registry.register(ACParticleRegistry.TEPHRA_FLAME, TephraParticle.FlameFactory::new);
        registry.register(ACParticleRegistry.LUXTRUCTOSAURUS_SPIT, LuxtructosaurusSpitParticle.Factory::new);
        registry.register(ACParticleRegistry.LUXTRUCTOSAURUS_ASH, LuxtructosaurusAshParticle.Factory::new);
        registry.register(ACParticleRegistry.HAPPINESS, HappinessParticle.Factory::new);
        registry.register(ACParticleRegistry.ACID_BUBBLE, AcidBubbleParticle.Factory::new);
        registry.register(ACParticleRegistry.BLACK_VENT_SMOKE, VentSmokeParticle.BlackFactory::new);
        registry.register(ACParticleRegistry.WHITE_VENT_SMOKE, VentSmokeParticle.WhiteFactory::new);
        registry.register(ACParticleRegistry.GREEN_VENT_SMOKE, VentSmokeParticle.GreenFactory::new);
        registry.register(ACParticleRegistry.RED_VENT_SMOKE, VentSmokeParticle.RedFactory::new);
        registry.register(ACParticleRegistry.MUSHROOM_CLOUD, new MushroomCloudParticle.Factory());
        registry.register(ACParticleRegistry.MUSHROOM_CLOUD_SMOKE, SmallExplosionParticle.NukeFactory::new);
        registry.register(ACParticleRegistry.MUSHROOM_CLOUD_EXPLOSION, SmallExplosionParticle.NukeFactory::new);
        registry.register(ACParticleRegistry.PROTON, new ProtonParticle.Factory());
        registry.register(ACParticleRegistry.FALLOUT, FalloutParticle.Factory::new);
        registry.register(ACParticleRegistry.GAMMAROACH, GammaroachParticle.Factory::new);
        registry.register(ACParticleRegistry.HAZMAT_BREATHE, HazmatBreatheParticle.Factory::new);
        registry.register(ACParticleRegistry.BLUE_HAZMAT_BREATHE, HazmatBreatheParticle.BlueFactory::new);
        registry.register(ACParticleRegistry.RADGILL_SPLASH, RadgillSplashParticle.Factory::new);
        registry.register(ACParticleRegistry.ACID_DROP, AcidDropParticle.Factory::new);
        registry.register(ACParticleRegistry.NUCLEAR_SIREN_SONAR, NuclearSirenSonarParticle.Factory::new);
        registry.register(ACParticleRegistry.RAYGUN_EXPLOSION, SmallExplosionParticle.RaygunFactory::new);
        registry.register(ACParticleRegistry.BLUE_RAYGUN_EXPLOSION, SmallExplosionParticle.BlueRaygunFactory::new);
        registry.register(ACParticleRegistry.RAYGUN_BLAST, RaygunBlastParticle.Factory::new);
        registry.register(ACParticleRegistry.TREMORZILLA_EXPLOSION, SmallExplosionParticle.TremorzillaFactory::new);
        registry.register(ACParticleRegistry.TREMORZILLA_RETRO_EXPLOSION, SmallExplosionParticle.TremorzillaRetroFactory::new);
        registry.register(ACParticleRegistry.TREMORZILLA_TECTONIC_EXPLOSION, SmallExplosionParticle.TremorzillaTectonicFactory::new);
        registry.register(ACParticleRegistry.TREMORZILLA_PROTON, new TremorzillaProtonParticle.Factory());
        registry.register(ACParticleRegistry.TREMORZILLA_RETRO_PROTON, new TremorzillaProtonParticle.RetroFactory());
        registry.register(ACParticleRegistry.TREMORZILLA_TECTONIC_PROTON, new TremorzillaProtonParticle.TectonicFactory());
        registry.register(ACParticleRegistry.TREMORZILLA_LIGHTNING, TremorzillaLightningParticle.Factory::new);
        registry.register(ACParticleRegistry.TREMORZILLA_RETRO_LIGHTNING, TremorzillaLightningParticle.Factory::new);
        registry.register(ACParticleRegistry.TREMORZILLA_TECTONIC_LIGHTNING, TremorzillaLightningParticle.Factory::new);
        registry.register(ACParticleRegistry.TREMORZILLA_BLAST, RaygunBlastParticle.TremorzillaFactory::new);
        registry.register(ACParticleRegistry.TREMORZILLA_STEAM, TremorzillaSteamParticle.Factory::new);
        registry.register(ACParticleRegistry.TUBE_WORM, new TubeWormParticle.Factory());
        registry.register(ACParticleRegistry.DEEP_ONE_MAGIC, DeepOneMagicParticle.Factory::new);
        registry.register(ACParticleRegistry.WATER_FOAM, WaterFoamParticle.Factory::new);
        registry.register(ACParticleRegistry.BIG_SPLASH, new BigSplashParticle.Factory());
        registry.register(ACParticleRegistry.BIG_SPLASH_EFFECT, BigSplashEffectParticle.Factory::new);
        registry.register(ACParticleRegistry.MINE_EXPLOSION, SmallExplosionParticle.MineFactory::new);
        registry.register(ACParticleRegistry.BIO_POP, BioPopParticle.Factory::new);
        registry.register(ACParticleRegistry.WATCHER_APPEARANCE, new WatcherAppearanceParticle.Factory());
        registry.register(ACParticleRegistry.VOID_BEING_CLOUD, new VoidBeingCloudParticle.Factory());
        registry.register(ACParticleRegistry.VOID_BEING_TENDRIL, new VoidBeingTendrilParticle.Factory());
        registry.register(ACParticleRegistry.VOID_BEING_EYE, new VoidBeingEyeParticle.Factory());
        registry.register(ACParticleRegistry.UNDERZEALOT_MAGIC, UnderzealotMagicParticle.Factory::new);
        registry.register(ACParticleRegistry.UNDERZEALOT_EXPLOSION, SmallExplosionParticle.UnderzealotFactory::new);
        registry.register(ACParticleRegistry.FALLING_GUANO, FallingGuanoParticle.Factory::new);
        registry.register(ACParticleRegistry.MOTH_DUST, MothDustParticle.Factory::new);
        registry.register(ACParticleRegistry.FORSAKEN_SPIT, ForsakenSpitParticle.Factory::new);
        registry.register(ACParticleRegistry.FORSAKEN_SONAR, ForsakenSonarParticle.Factory::new);
        registry.register(ACParticleRegistry.FORSAKEN_SONAR_LARGE, ForsakenSonarParticle.LargeFactory::new);
        registry.register(ACParticleRegistry.TOTEM_EXPLOSION, SmallExplosionParticle.TotemFactory::new);
        registry.register(ACParticleRegistry.ICE_CREAM_DRIP, IceCreamDripParticle.Factory::new);
        registry.register(ACParticleRegistry.ICE_CREAM_SPLASH, IceCreamSplashParticle.Factory::new);
        registry.register(ACParticleRegistry.PURPLE_SODA_BUBBLE, PurpleSodaBubbleParticle.Factory::new);
        registry.register(ACParticleRegistry.PURPLE_SODA_BUBBLE_EMITTER, PurpleSodaBubbleEmitterParticle.Factory::new);
        registry.register(ACParticleRegistry.PURPLE_SODA_FIZZ, PurpleSodaFizzParticle.Factory::new);
        registry.register(ACParticleRegistry.SUNDROP, SundropParticle.Factory::new);
        registry.register(ACParticleRegistry.RAINBOW, new RainbowParticle.Factory());
        registry.register(ACParticleRegistry.PLAYER_RAINBOW, new PlayerRainbowParticle.Factory());
        registry.register(ACParticleRegistry.CANDICORN_CHARGE, CandicornChargeParticle.Factory::new);
        registry.register(ACParticleRegistry.BIG_BLOCK_DUST, BigBlockDustParticle.Factory::new);
        registry.register(ACParticleRegistry.CARAMEL_DROP, CaramelDropParticle.Factory::new);
        registry.register(ACParticleRegistry.JELLY_BEAN_EAT, new JellyBeanEatParticle.Factory());
        registry.register(ACParticleRegistry.SLEEP, SleepParticle.Factory::new);
        registry.register(ACParticleRegistry.WITCH_COOKIE, WitchCookieParticle.Factory::new);
        registry.register(ACParticleRegistry.PURPLE_WITCH_MAGIC, new PurpleWitchMagicParticle.Factory());
        registry.register(ACParticleRegistry.PURPLE_WITCH_EXPLOSION, SmallExplosionParticle.PurpleWitchFactory::new);
        registry.register(ACParticleRegistry.GOBTHUMPER, GobthumperParticle.Factory::new);
        registry.register(ACParticleRegistry.COLORED_DUST, ColoredDustParticle.Factory::new);
        registry.register(ACParticleRegistry.SMALL_COLORED_DUST, ColoredDustParticle.SmallFactory::new);
        registry.register(ACParticleRegistry.CONVERSION_CRUCIBLE_EXPLOSION, SmallExplosionParticle.ConversionCrucibleFactory::new);
        registry.register(ACParticleRegistry.FROSTMINT_EXPLOSION, SmallExplosionParticle.FrostmintFactory::new);
        registry.register(ACParticleRegistry.SUGAR_FLAKE, SugarFlakeParticle.Factory::new);
    }

    public static void onItemColors() {
        ColorProviderRegistry.ITEM.register((stack, colorIn) -> colorIn != 1 ? -1
                : CaveInfoItem.getBiomeColorOf(Minecraft.getInstance().level, stack, false) | 0xFF000000, ACItemRegistry.CAVE_TABLET);
        ColorProviderRegistry.ITEM.register((stack, colorIn) -> colorIn != 1 ? -1
                : CaveInfoItem.getBiomeColorOf(Minecraft.getInstance().level, stack, false) | 0xFF000000, ACItemRegistry.CAVE_CODEX);
        ColorProviderRegistry.ITEM.register((stack, colorIn) -> colorIn != 0 ? -1 : GazingPearlItem.getPearlColor(stack), ACItemRegistry.GAZING_PEARL);
        ColorProviderRegistry.ITEM.register((stack, colorIn) -> colorIn != 0 ? -1 : JellyBeanItem.getBeanColor(stack), ACItemRegistry.JELLY_BEAN);
        ColorProviderRegistry.ITEM.register((stack, colorIn) -> colorIn != 1 ? -1
                : BiomeTreatItem.getBiomeTreatColorOf(Minecraft.getInstance().level, stack) | 0xFF000000, ACItemRegistry.BIOME_TREAT);
    }

    public static void onBlockColors() {
        ColorProviderRegistry.BLOCK.register((blockState, blockAndTintGetter, blockPos, colorIn) -> colorIn != 0 ? -1
                : FrostedChocolateBlock.calculateFrostingColor(blockPos), ACBlockRegistry.BLOCK_OF_FROSTED_CHOCOLATE);
        ColorProviderRegistry.BLOCK.register((blockState, blockAndTintGetter, blockPos, colorIn) -> colorIn != 0 ? -1
                : FrostedChocolateBlock.calculateFrostingColor(blockPos), ACBlockRegistry.BLOCK_OF_FROSTING);
    }

    private static void onRegisterTooltips() {
        TooltipComponentCallback.EVENT.register(component -> {
            if (component instanceof SackOfSatingTooltip tooltip) {
                return new ClientSackOfSatingTooltip(tooltip);
            }
            return null;
        });
    }

    private static void bakeModels() {
        ModelLoadingPlugin.register(pluginContext -> {
            pluginContext.modifyModelAfterBake().register((model, context) -> {
                if (AlexsCaves.CLIENT_CONFIG.emissiveBlockModels) {
                    var id = context.topLevelId();
                    if (id != null) {
                        String identifierStr = id.toString();
                        for (String fullbright : FULLBRIGHTS) {
                            if (identifierStr.startsWith(fullbright)) {
                                return new BakedModelShadeLayerFullbright(model);
                            }
                        }
                    }
                }
                return model;
            });
        });
    }

    private static void registerInternalShaders(ShaderRegistrar registrar) throws IOException {
        registrar.register(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "rendertype_ferrouslime_gel"), DefaultVertexFormat.NEW_ENTITY, ACInternalShaders::setRenderTypeFerrouslimeGelShader);
        registrar.register(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "rendertype_hologram"), DefaultVertexFormat.POSITION_COLOR, ACInternalShaders::setRenderTypeHologramShader);
        registrar.register(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "rendertype_irradiated"), DefaultVertexFormat.NEW_ENTITY, ACInternalShaders::setRenderTypeIrradiatedShader);
        registrar.register(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "rendertype_blue_irradiated"), DefaultVertexFormat.NEW_ENTITY, ACInternalShaders::setRenderTypeBlueIrradiatedShader);
        registrar.register(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "rendertype_bubbled"), DefaultVertexFormat.NEW_ENTITY, ACInternalShaders::setRenderTypeBubbledShader);
        registrar.register(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "rendertype_sepia"), DefaultVertexFormat.NEW_ENTITY, ACInternalShaders::setRenderTypeSepiaShader);
        registrar.register(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "rendertype_red_ghost"), DefaultVertexFormat.NEW_ENTITY, ACInternalShaders::setRenderTypeRedGhostShader);
        registrar.register(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "rendertype_purple_witch"), DefaultVertexFormat.NEW_ENTITY, ACInternalShaders::setRenderTypePurpleWitchShader);
        registrar.register(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "rendertype_entity_unlit_translucent"), DefaultVertexFormat.NEW_ENTITY, ACInternalShaders::setRenderTypeUnlitTranslucentShader);
        AlexsCaves.LOGGER.info("registered internal shaders");
    }

    private static void registerKeybinds() {
        KeyBindingHelper.registerKeyBinding(ACKeybindRegistry.KEY_SPECIAL_ABILITY);
    }

    public static Player getClientSidePlayer() { return Minecraft.getInstance().player; }
    public static void blockRenderingEntity(UUID id) { blockedEntityRenders.add(id); }
    public static void releaseRenderingEntity(UUID id) { blockedEntityRenders.remove(id); }
    public static Object getArmorProperties() { return ARMOR_PROPERTIES; }

    public static float getNukeFlashAmount(float partialTicks) {
        return prevNukeFlashAmount + (nukeFlashAmount - prevNukeFlashAmount) * partialTicks;
    }

    public static float getPrimordialBossActiveAmount(float partialTicks) {
        return prevPrimordialBossActiveAmount + (primordialBossActiveAmount - prevPrimordialBossActiveAmount) * partialTicks;
    }

    public static float getPossessionStrengthAmount(float partialTicks) {
        return prevPossessionStrengthAmount + (possessionStrengthAmount - prevPossessionStrengthAmount) * partialTicks;
    }

    public static boolean checkIfParticleAt(SimpleParticleType simpleParticleType, BlockPos at) {
        if (!blockedParticleLocations.containsKey(Minecraft.getInstance().level)) {
            blockedParticleLocations.clear();
            blockedParticleLocations.put(Minecraft.getInstance().level, new ArrayList<>());
        }
        List<BlockPos> blocked = blockedParticleLocations.get(Minecraft.getInstance().level);
        if (blocked.contains(at)) {
            return false;
        } else {
            blocked.add(new BlockPos(at));
            return true;
        }
    }

    public static void removeParticleAt(BlockPos at) {
        if (!blockedParticleLocations.containsKey(Minecraft.getInstance().level)) {
            blockedParticleLocations.clear();
            blockedParticleLocations.put(Minecraft.getInstance().level, new ArrayList<>());
        }
        blockedParticleLocations.get(Minecraft.getInstance().level).remove(at);
    }

    public static boolean isKeyDown(int keyType) {
        if (keyType == -1) {
            return Minecraft.getInstance().options.keyLeft.isDown() || Minecraft.getInstance().options.keyRight.isDown()
                    || Minecraft.getInstance().options.keyUp.isDown() || Minecraft.getInstance().options.keyDown.isDown()
                    || Minecraft.getInstance().options.keyJump.isDown();
        }
        if (keyType == 0) return Minecraft.getInstance().options.keyJump.isDown();
        if (keyType == 1) return Minecraft.getInstance().options.keySprint.isDown();
        if (keyType == 2) return ACKeybindRegistry.KEY_SPECIAL_ABILITY.isDown();
        if (keyType == 3) return Minecraft.getInstance().options.keyAttack.isDown();
        if (keyType == 4) return Minecraft.getInstance().options.keyShift.isDown();
        return false;
    }

    public static float getPartialTicks() {
        return Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
    }

    public static void setSpelunkeryTutorialComplete(boolean completedTutorial) { spelunkeryTutorialComplete = completedTutorial; }
    public static boolean isSpelunkeryTutorialComplete() { return spelunkeryTutorialComplete; }

    public static void setRenderViewEntity(Player player, Entity entity) {
        if (player == Minecraft.getInstance().player && Minecraft.getInstance().getCameraEntity() == Minecraft.getInstance().player) {
            lastPOV = Minecraft.getInstance().options.getCameraType();
            Minecraft.getInstance().setCameraEntity(entity);
            Minecraft.getInstance().options.setCameraType(CameraType.FIRST_PERSON);
        }
        if (lastCameraEntity != Minecraft.getInstance().getCameraEntity()) {
            Minecraft.getInstance().levelRenderer.allChanged();
            lastCameraEntity = Minecraft.getInstance().getCameraEntity();
        }
    }

    public static void resetRenderViewEntity(Player player) {
        if (player == Minecraft.getInstance().player) {
            Minecraft.getInstance().level = (ClientLevel) Minecraft.getInstance().player.level();
            Minecraft.getInstance().setCameraEntity(Minecraft.getInstance().player);
            Minecraft.getInstance().options.setCameraType(lastPOV);
        }
        if (lastCameraEntity != Minecraft.getInstance().getCameraEntity()) {
            Minecraft.getInstance().levelRenderer.allChanged();
            lastCameraEntity = Minecraft.getInstance().getCameraEntity();
        }
    }

    public static boolean hasBubbledEffectVisual(int entityId) { return BUBBLED_EFFECT_TICKS.getOrDefault(entityId, 0) > 0; }

    public static void setBubbledEffectTicks(int entityId, int ticks) {
        if (ticks <= 0) BUBBLED_EFFECT_TICKS.remove(entityId);
        else BUBBLED_EFFECT_TICKS.put(entityId, ticks);
    }

    public static void handleBeholderSync(int beholderId, boolean active, double x, double y, double z, float yRot, float xRot, UUID usingPlayerUUID) {
        Player playerSided = getClientSidePlayer();
        if (playerSided != null && playerSided.level() instanceof ClientLevel clientLevel) {
            Entity watcher = clientLevel.getEntity(beholderId);
            if (watcher == null && active && usingPlayerUUID != null) {
                BeholderEyeEntity beholderEye = ACEntityRegistry.BEHOLDER_EYE.create(clientLevel);
                if (beholderEye != null) {
                    beholderEye.setId(beholderId);
                    beholderEye.setPos(x, y, z);
                    beholderEye.setEyeYRot(yRot);
                    beholderEye.setEyeXRot(xRot);
                    beholderEye.setUsingPlayerUUID(usingPlayerUUID);
                    beholderEye.hasTakenFullControlOfCamera = true;
                    clientLevel.addEntity(beholderEye);
                    watcher = beholderEye;
                }
            }
            if (watcher instanceof BeholderEyeEntity beholderEye) {
                Entity beholderEyePlayer = beholderEye.getUsingPlayer();
                beholderEye.hasTakenFullControlOfCamera = true;
                if (beholderEyePlayer instanceof Player && beholderEyePlayer.equals(playerSided)) {
                    if (active) setRenderViewEntity(playerSided, beholderEye);
                    else resetRenderViewEntity(playerSided);
                }
            }
        }
    }

    public static void clearSoundCacheFor(Entity entity) { ENTITY_SOUND_INSTANCE_MAP.remove(entity.getId()); }
    public static void clearSoundCacheFor(BlockEntity entity) { BLOCK_ENTITY_SOUND_INSTANCE_MAP.remove(entity); }

    public static Vec3 getDarknessTrailPosFor(LivingEntity living, int pointer, float partialTick) {
        if (living.isRemoved()) partialTick = 1.0F;
        Vec3[] trailPositions = darknessTrailPosMap.get(living);
        if (trailPositions == null || !darknessTrailPointerMap.containsKey(living)) return living.position();
        int trailPointer = darknessTrailPointerMap.get(living);
        int i = trailPointer - pointer & 63;
        int j = trailPointer - pointer - 1 & 63;
        Vec3 d0 = trailPositions[j];
        Vec3 d1 = trailPositions[i].subtract(d0);
        return d0.add(d1.scale(partialTick));
    }

    public static int getPlayerTime() { return Minecraft.getInstance().player == null ? 0 : Minecraft.getInstance().player.tickCount; }

    public static void playWorldSound(@Nullable Object soundEmitter, byte type) {
        if (soundEmitter instanceof Entity entity && !entity.level().isClientSide) return;
        switch (type) {
            case 0: if (soundEmitter instanceof NuclearSirenBlockEntity s) { playBlock(s, new NuclearSirenSound(s)); } break;
            case 1: if (soundEmitter instanceof NucleeperEntity e) { playEntity(e, new NucleeperSound(e)); } break;
            case 2: if (soundEmitter instanceof NotorEntity e) { playEntity(e, new NotorHologramSound(e)); } break;
            case 3: if (soundEmitter instanceof HologramProjectorBlockEntity b) { playBlock(b, new HologramProjectorSound(b)); } break;
            case 4: if (soundEmitter instanceof MagnetBlockEntity b) { playBlock(b, new MagnetSound(b)); } break;
            case 5: if (soundEmitter instanceof UnderzealotEntity e) { playEntity(e, new UnderzealotSound(e)); } break;
            case 6: if (soundEmitter instanceof CorrodentEntity e) { playEntity(e, new CorrodentSound(e)); } break;
            case 7: if (soundEmitter instanceof NuclearFurnaceBlockEntity b) { playBlock(b, new NuclearFurnaceSound(b)); } break;
            case 8: if (soundEmitter instanceof LivingEntity e) { playEntity(e, new RaygunSound(e)); } break;
            case 9: if (soundEmitter instanceof LivingEntity e) { playEntity(e, new ResistorShieldSound(e, false)); } break;
            case 10: if (soundEmitter instanceof LivingEntity e) { playEntity(e, new ResistorShieldSound(e, true)); } break;
            case 11: if (soundEmitter instanceof LivingEntity e) { playEntity(e, new GalenaGauntletSound(e)); } break;
            case 12: if (soundEmitter instanceof BoundroidEntity e) { playEntity(e, new BoundroidSound(e)); } break;
            case 13: if (soundEmitter instanceof FerrouslimeEntity e) { playEntity(e, new FerrouslimeSound(e)); } break;
            case 14: if (soundEmitter instanceof QuarrySmasherEntity e) { playEntity(e, new QuarrySmasherSound(e)); } break;
            case 15: if (soundEmitter instanceof SubmarineEntity e) { playEntity(e, new SubmarineSound(e)); } break;
            case 16: if (soundEmitter instanceof TremorzillaEntity e) { playEntity(e, new TremorzillaBeamSound(e)); } break;
            case 17: if (soundEmitter instanceof GumWormEntity e) { playEntity(e, new GumWormSound(e)); } break;
            case 18: if (soundEmitter instanceof LivingEntity e) { playEntity(e, new SugarRushSound(e)); } break;
            case 19: if (soundEmitter instanceof CandicornEntity e) { playEntity(e, new CandicornSound(e)); } break;
        }
    }

    private static void playEntity(Entity e, AbstractTickableSoundInstance sound) {
        AbstractTickableSoundInstance old = ENTITY_SOUND_INSTANCE_MAP.get(e.getId());
        if (old != null && old.getClass() == sound.getClass()) return;
        ENTITY_SOUND_INSTANCE_MAP.put(e.getId(), sound);
        if (!Minecraft.getInstance().getSoundManager().isActive(sound) && sound.canPlaySound()) {
            Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
        }
    }

    private static void playBlock(BlockEntity be, AbstractTickableSoundInstance sound) {
        AbstractTickableSoundInstance old = BLOCK_ENTITY_SOUND_INSTANCE_MAP.get(be);
        if (old != null && old.getClass() == sound.getClass() && !old.isStopped()) return;
        BLOCK_ENTITY_SOUND_INSTANCE_MAP.put(be, sound);
        if (!Minecraft.getInstance().getSoundManager().isActive(sound) && sound.canPlaySound()) {
            Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
        }
    }

    public static void playWorldEvent(int messageId, Level level, BlockPos pos) {
        if (messageId == 0 && AcidBlock.doesBlockCorrode(level.getBlockState(pos))) {
            level.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, ACSoundRegistry.ACID_CORROSION, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.4F + 0.8F, false);
        }
        if (messageId == 1 && level.getBlockState(pos).getBlock() instanceof ActivatedByAltar) {
            level.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, ACSoundRegistry.ABYSSMARINE_GLOW_ON, SoundSource.BLOCKS, 1.5F, level.random.nextFloat() * 0.4F + 0.8F, false);
        }
        if (messageId == 2 && level.getBlockState(pos).getBlock() instanceof ActivatedByAltar) {
            level.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, ACSoundRegistry.ABYSSMARINE_GLOW_OFF, SoundSource.BLOCKS, 1.5F, level.random.nextFloat() * 0.4F + 0.8F, false);
        }
        if (messageId == 3 && level.getBlockState(pos).is(ACBlockRegistry.DRAIN)) {
            level.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, ACSoundRegistry.DRAIN_START, SoundSource.BLOCKS, 1.5F, level.random.nextFloat() * 0.4F + 0.8F, false);
        }
        if (messageId == 4 && level.getBlockState(pos).is(ACBlockRegistry.DRAIN)) {
            level.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, ACSoundRegistry.DRAIN_STOP, SoundSource.BLOCKS, 1.5F, level.random.nextFloat() * 0.4F + 0.8F, false);
        }
        if (messageId == 5 && level.getBlockState(pos).is(ACBlockRegistry.SPELUNKERY_TABLE)) {
            level.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, ACSoundRegistry.SPELUNKERY_TABLE_FAIL, SoundSource.BLOCKS, 1.5F, level.random.nextFloat() * 0.4F + 0.8F, false);
            BlockParticleOption opt = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState());
            for (int i = 0; i < 8; i++) {
                level.addParticle(opt, pos.getX() + level.random.nextFloat(), pos.getY() + 1.0F, pos.getZ() + level.random.nextFloat(), 0, 0, 0);
            }
        }
        if (messageId == 6 && level.getBlockState(pos).is(ACBlockRegistry.ABYSSAL_ALTAR)
                && level.getBlockEntity(pos) instanceof AbyssalAltarBlockEntity altarBlock) {
            altarBlock.resetSlideAnimation();
        }
        if (messageId == 7) {
            for (int i = 0; i < 8; i++) {
                level.addParticle(ACParticleRegistry.PURPLE_WITCH_EXPLOSION, pos.getX() + level.random.nextFloat(), pos.getY() + level.random.nextFloat(), pos.getZ() + level.random.nextFloat(), 0, 0, 0);
            }
        }
        if (messageId == 8) {
            for (int i = 0; i < 15; i++) {
                level.addAlwaysVisibleParticle(random.nextInt(5) == 0 ? ACParticleRegistry.FROSTMINT_EXPLOSION : ParticleTypes.SNOWFLAKE,
                        true, pos.getX() + random.nextFloat() * 8 - 4, pos.getY() + random.nextFloat() * 8 - 4, pos.getZ() + random.nextFloat() * 8 - 4, 0, 0, 0);
            }
        }
        if (messageId == 9) {
            for (int i = 0; i < 30; i++) {
                level.addAlwaysVisibleParticle(ParticleTypes.SNOWFLAKE, true, pos.getX() + random.nextFloat() * 4 - 2, pos.getY() + random.nextFloat() * 4 - 2, pos.getZ() + random.nextFloat() * 4 - 2, 0, 0, 0);
            }
        }
    }

    public static void preScreenRender(float partialTick) {
        if (!RenderSystem.isOnRenderThread()) {
            Minecraft.getInstance().execute(() -> preScreenRender(partialTick));
            return;
        }
        float screenEffectIntensity = Minecraft.getInstance().options.screenEffectScale().get().floatValue();
        float watcherPossessionStrength = getPossessionStrengthAmount(partialTick);
        float nukeFlash = getNukeFlashAmount(partialTick);
        if (nukeFlash > 0 && AlexsCaves.CLIENT_CONFIG.nuclearBombFlash) {
            int w = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            int h = Minecraft.getInstance().getWindow().getGuiScaledHeight();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, nukeFlash * screenEffectIntensity);
            RenderSystem.setShaderTexture(0, BOMB_FLASH);
            BufferBuilder bb = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bb.addVertex(0.0F, (float) h, -90.0F).setUv(0.0F, 1.0F);
            bb.addVertex((float) w, (float) h, -90.0F).setUv(1.0F, 1.0F);
            bb.addVertex((float) w, 0.0F, -90.0F).setUv(1.0F, 0.0F);
            bb.addVertex(0.0F, 0.0F, -90.0F).setUv(0.0F, 0.0F);
            BufferUploader.drawWithShader(bb.buildOrThrow());
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
        if (watcherPossessionStrength > 0) {
            int w = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            int h = Minecraft.getInstance().getWindow().getGuiScaledHeight();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, watcherPossessionStrength * screenEffectIntensity);
            RenderSystem.setShaderTexture(0, WATCHER_EFFECT);
            BufferBuilder bb = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bb.addVertex(0.0F, (float) h, -90.0F).setUv(0.0F, 1.0F);
            bb.addVertex((float) w, (float) h, -90.0F).setUv(1.0F, 1.0F);
            bb.addVertex((float) w, 0.0F, -90.0F).setUv(1.0F, 0.0F);
            bb.addVertex(0.0F, 0.0F, -90.0F).setUv(0.0F, 0.0F);
            BufferUploader.drawWithShader(bb.buildOrThrow());
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public static boolean isFirstPersonPlayer(Entity entity) {
        return entity.equals(Minecraft.getInstance().getCameraEntity())
                && Minecraft.getInstance().options.getCameraType().isFirstPerson();
    }

    public static void openBookGUI(ItemStack itemStackIn) {
        Minecraft.getInstance().setScreen(new CaveBookScreen());
    }

    public static Vec3 getCameraRotation() {
        return Vec3.ZERO;
    }

    public static Vec3 processSkyColor(Vec3 colorIn, float partialTick) {
        float primordialAmount = getPrimordialBossActiveAmount(partialTick);
        if (primordialAmount > 0.0F) {
            Vec3 targetColor = new Vec3(0.2F, 0.15F, 0.1F);
            colorIn = colorIn.add(targetColor.subtract(colorIn).scale(primordialAmount));
        }
        return colorIn;
    }

    public static void removeBossBarRender(UUID bossBar) { bossBarRenderTypes.remove(bossBar); }
    public static void setBossBarRender(UUID bossBar, int renderType) { bossBarRenderTypes.put(bossBar, renderType); }

    public static boolean isTickRateModificationActive(Level level) {
        return ClientTickRateTracker.getForClient(Minecraft.getInstance()).getClientTickRate() != 50;
    }

    public static boolean isFarFromCamera(double x, double y, double z) {
        return Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().distanceToSqr(x, y, z) >= 256.0D;
    }

    public static void renderVanillaMapDecoration(MapDecoration mapDecoration, int index) {
        ClientEvents.renderVanillaMapDecoration(mapDecoration, index + 1);
    }
}