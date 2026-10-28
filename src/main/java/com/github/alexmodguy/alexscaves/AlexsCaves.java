package com.github.alexmodguy.alexscaves;

import com.github.alexmodguy.alexscaves.client.AlexsCavesClient;
import com.github.alexmodguy.alexscaves.client.config.ACClientConfig;
import com.github.alexmodguy.alexscaves.config.ACModConfigSpec;
import com.github.alexmodguy.alexscaves.network.server.ACNetworkServer;
import com.github.alexmodguy.alexscaves.multipart.ACMultipartServer;
import com.github.alexmodguy.alexscaves.server.level.ACWorldWorkerManager;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.block.poi.ACPOIRegistry;
import com.github.alexmodguy.alexscaves.server.config.ACServerConfig;
import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationConfig;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityDataRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACFrogRegistry;
import com.github.alexmodguy.alexscaves.server.event.CommonEvents;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.feature.ACFeatureRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.ACStructureRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.piece.ACStructurePieceRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.processor.ACStructureProcessorRegistry;
import com.github.alexmodguy.alexscaves.server.level.surface.ACSurfaceRules;
import com.github.alexmodguy.alexscaves.server.misc.ACAdvancementTriggerRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACCreativeTabRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACDataComponentRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACLoadedMods;
import com.github.alexmodguy.alexscaves.server.misc.ACLootTableRegistry;
import com.github.alexmodguy.alexscaves.server.misc.WebHelper;
import com.github.alexmodguy.alexscaves.server.recipe.ACRecipeRegistry;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AlexsCaves implements ModInitializer {

    public static final String MODID = "alexscaves";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String VERSION = "2.0.2-7-fabric";

    public static final ACServerConfig COMMON_CONFIG;
    public static final ACClientConfig CLIENT_CONFIG;
    public static final List<String> MOD_GENERATION_CONFLICTS = new ArrayList<>();

    private static boolean worldgenBootstrapped;
    private static MinecraftServer currentServer;

    static {
        final Pair<ACServerConfig, ACModConfigSpec> serverPair = new ACModConfigSpec.Builder("alexscaves-server")
                .configure(ACServerConfig::new);
        COMMON_CONFIG = serverPair.getLeft();

        final Pair<ACClientConfig, ACModConfigSpec> clientPair = new ACModConfigSpec.Builder("alexscaves-client")
                .configure(ACClientConfig::new);
        CLIENT_CONFIG = clientPair.getLeft();
    }

    @Override
    public void onInitialize() {
        ACWorldWorkerManager.registerCommon();
        CommonEvents.register();

        ACNetworkServer.registerCommon();
        ACMultipartServer.registerCommon();

        ACLootTableRegistry.registerLootTables();
        ACEntityDataRegistry.register();
        ACPOIRegistry.registerFabricBlockStates();
        ACFeatureRegistry.init();
        ACStructureRegistry.init();
        ACStructurePieceRegistry.init();
        ACStructureProcessorRegistry.init();
        ACDataComponentRegistry.init();
        ACFluidRegistry.postInit();
        ACCreativeTabRegistry.init();
        ACRecipeRegistry.init();
        ACAdvancementTriggerRegistry.init();
        ACFrogRegistry.register();
        ACEntityRegistry.initializeAttributes();
        ACEntityRegistry.spawnPlacements();

        ACFluidRegistry.register();
        AlexsCaves.init();
        ServerLifecycleEvents.SERVER_STARTING.register(CommonEvents::onServerStarting);
        ServerLifecycleEvents.SERVER_STOPPED.register(CommonEvents::onServerStopped);
    }

    private static boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    public static void setCurrentServer(MinecraftServer server) {
        currentServer = server;
    }

    public static MinecraftServer getCurrentServer() {
        return currentServer;
    }

    public static void init() {
        BiomeGenerationConfig.reloadConfig();
        bootstrapWorldgen();
        ACLoadedMods.afterAllModsLoaded();
        initPathfinding();
        readModIncompatibilities();
    }

    public static void initPathfinding() {
    }

    public static void setPrimordialBossActive(Level level, int id, boolean active) {
        if (level.isClientSide() && isClient()) {
            AlexsCavesClient.setPrimordialBossActive(id, active);
        }
    }

    public static boolean isPrimordialBossActive(Level level) {
        return level.isClientSide() && isClient() && AlexsCavesClient.isPrimordialBossActive();
    }

    public static <MSG extends CustomPacketPayload> void sendMSGToServer(MSG message) {
        ClientPlayNetworking.send(message);
    }

    public static <MSG extends CustomPacketPayload> void sendMSGToAll(MSG message) {
        if (currentServer == null) {
            return;
        }
        for (ServerPlayer player : currentServer.getPlayerList().getPlayers()) {
            sendNonLocal(message, player);
        }
    }

    public static <MSG extends CustomPacketPayload> void sendNonLocal(MSG msg, ServerPlayer player) {
        ServerPlayNetworking.send(player, msg);
    }

    public static void playWorldSound(@Nullable Object soundEmitter, byte type) {
        if (!isClient()) return;
        AlexsCavesClient.playWorldSound(soundEmitter, type);
    }

    public static void preScreenRender(float partialTick) {
        if (!isClient()) return;
        AlexsCavesClient.preScreenRender(partialTick);
    }

    public static boolean isFirstPersonPlayer(Entity entity) {
        return isClient() && AlexsCavesClient.isFirstPersonPlayer(entity);
    }

    public static void openBookGUI(ItemStack stack) {
        if (!isClient()) return;
        AlexsCavesClient.openBookGUI(stack);
    }

    public static Vec3 getCameraRotation() {
        return isClient() ? AlexsCavesClient.getCameraRotation() : Vec3.ZERO;
    }

    public static boolean isKeyDown(int keyType) {
        return isClient() && AlexsCavesClient.isKeyDown(keyType);
    }

    public static float getPartialTicks() {
        return isClient() ? AlexsCavesClient.getPartialTicks() : 0f;
    }

    public static void setSpelunkeryTutorialComplete(boolean done) {
        if (!isClient()) return;
        AlexsCavesClient.setSpelunkeryTutorialComplete(done);
    }

    public static boolean isSpelunkeryTutorialComplete() {
        return isClient() && AlexsCavesClient.isSpelunkeryTutorialComplete();
    }

    public static void setRenderViewEntity(Player player, Entity entity) {
        if (!isClient()) return;
        AlexsCavesClient.setRenderViewEntity(player, entity);
    }

    public static void resetRenderViewEntity(Player player) {
        if (!isClient()) return;
        AlexsCavesClient.resetRenderViewEntity(player);
    }

    public static boolean hasBubbledEffectVisual(int entityId) {
        return isClient() && AlexsCavesClient.hasBubbledEffectVisual(entityId);
    }

    public static void setBubbledEffectTicks(int entityId, int ticks) {
        if (!isClient()) return;
        AlexsCavesClient.setBubbledEffectTicks(entityId, ticks);
    }

    public static void handleBeholderSync(int beholderId, boolean active, double x, double y, double z,
                                          float yRot, float xRot, UUID usingPlayerUUID) {
        if (!isClient()) return;
        AlexsCavesClient.handleBeholderSync(beholderId, active, x, y, z, yRot, xRot, usingPlayerUUID);
    }

    public static void clearSoundCacheFor(Entity entity) {
        if (!isClient()) return;
        AlexsCavesClient.clearSoundCacheFor(entity);
    }

    public static void clearSoundCacheFor(BlockEntity entity) {
        if (!isClient()) return;
        AlexsCavesClient.clearSoundCacheFor(entity);
    }

    public static Vec3 getDarknessTrailPosFor(LivingEntity living, int pointer, float partialTick) {
        return isClient() ? AlexsCavesClient.getDarknessTrailPosFor(living, pointer, partialTick) : living.position();
    }

    public static int getPlayerTime() {
        return isClient() ? AlexsCavesClient.getPlayerTime() : 0;
    }

    public static void playWorldEvent(int messageId, Level level, BlockPos pos) {
        if (!isClient()) return;
        AlexsCavesClient.playWorldEvent(messageId, level, pos);
    }

    public static void blockRenderingEntity(UUID id) {
        if (!isClient()) return;
        AlexsCavesClient.blockRenderingEntity(id);
    }

    public static void releaseRenderingEntity(UUID id) {
        if (!isClient()) return;
        AlexsCavesClient.releaseRenderingEntity(id);
    }

    public static float getNukeFlashAmount(float partialTicks) {
        return isClient() ? AlexsCavesClient.getNukeFlashAmount(partialTicks) : 0f;
    }

    public static float getPrimordialBossActiveAmount(float partialTicks) {
        return isClient() ? AlexsCavesClient.getPrimordialBossActiveAmount(partialTicks) : 0f;
    }

    public static float getPossessionStrengthAmount(float partialTicks) {
        return isClient() ? AlexsCavesClient.getPossessionStrengthAmount(partialTicks) : 0f;
    }

    public static boolean checkIfParticleAt(SimpleParticleType type, BlockPos at) {
        return isClient() && AlexsCavesClient.checkIfParticleAt(type, at);
    }

    public static void removeParticleAt(BlockPos at) {
        if (!isClient()) return;
        AlexsCavesClient.removeParticleAt(at);
    }

    public static Player getClientSidePlayer() {
        return isClient() ? AlexsCavesClient.getClientSidePlayer() : null;
    }

    public static Object getArmorProperties() {
        return isClient() ? AlexsCavesClient.getArmorProperties() : null;
    }

    public static boolean isTickRateModificationActive(Level level) {
        return isClient() && AlexsCavesClient.isTickRateModificationActive(level);
    }

    public static boolean isFarFromCamera(double x, double y, double z) {
        return isClient() && AlexsCavesClient.isFarFromCamera(x, y, z);
    }

    public static void renderVanillaMapDecoration(MapDecoration decoration, int index) {
        if (!isClient()) return;
        AlexsCavesClient.renderVanillaMapDecoration(decoration, index);
    }

    public static void setBossBarRender(UUID bossBar, int renderType) {
        if (!isClient()) return;
        AlexsCavesClient.setBossBarRender(bossBar, renderType);
    }

    public static void removeBossBarRender(UUID bossBar) {
        if (!isClient()) return;
        AlexsCavesClient.removeBossBarRender(bossBar);
    }

    private static void readModIncompatibilities() {
        MOD_GENERATION_CONFLICTS.clear();
        BufferedReader urlContents = WebHelper.getURLContents(
                "https://raw.githubusercontent.com/AlexModGuy/AlexsCaves/main/src/main/resources/assets/alexscaves/warning/mod_generation_conflicts.txt",
                "assets/alexscaves/warning/mod_generation_conflicts.txt"
        );
        if (urlContents == null) {
            LOGGER.warn("Failed to load mod conflicts");
            return;
        }

        try {
            String line;
            while ((line = urlContents.readLine()) != null) {
                MOD_GENERATION_CONFLICTS.add(line);
            }
        } catch (IOException e) {
            LOGGER.warn("Failed to load mod conflicts", e);
        }
    }

    private static void bootstrapWorldgen() {
        if (worldgenBootstrapped) {
            return;
        }
        worldgenBootstrapped = true;
        ACBiomeRegistry.init();
        ACSurfaceRules.setup();
    }
}