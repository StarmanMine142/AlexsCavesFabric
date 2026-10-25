package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Mixin to prevent ore generation inside AlexsCaves protected structures.
 * This fixes issues where ores (coal, emerald, etc.) and crystals spawn inside
 * structures like Underground Cabin, replacing functional blocks.
 * 
 * Performance optimization: Caches structure check results per chunk section
 * to avoid repeated expensive structure lookups during ore vein generation.
 */
@Mixin(OreFeature.class)
public class OreFeatureMixin {

    @Unique
    private static final ThreadLocal<WorldGenLevel> alexscaves$currentLevel = new ThreadLocal<>();
    
    /**
     * Cache for structure check results. Key is the chunk section position (packed long),
     * value is whether that section intersects with a protected structure.
     * This dramatically reduces the number of structure lookups since ore veins
     * typically span only 1-2 chunk sections.
     */
    @Unique
    private static final ThreadLocal<Map<Long, Boolean>> alexscaves$sectionCache = ThreadLocal.withInitial(HashMap::new);

    /**
     * Capture the WorldGenLevel at the start of the place method.
     */
    @Inject(method = "place", at = @At("HEAD"))
    private void ac_captureLevel(FeaturePlaceContext<OreConfiguration> context, CallbackInfoReturnable<Boolean> cir) {
        alexscaves$currentLevel.set(context.level());
        alexscaves$sectionCache.get().clear(); // Clear cache for each new ore vein
    }

    /**
     * Clear the captured level after place method completes.
     */
    @Inject(method = "place", at = @At("RETURN"))
    private void ac_clearLevel(FeaturePlaceContext<OreConfiguration> context, CallbackInfoReturnable<Boolean> cir) {
        alexscaves$currentLevel.remove();
        alexscaves$sectionCache.get().clear();
    }

    /**
     * Wrap canPlaceOre to check structure protection before allowing ore placement.
     * This is called for each potential ore position with the actual BlockPos.
     * canPlaceOre is a static method, so we use a static wrapper method.
     */
    @WrapOperation(
        method = "doPlace",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/levelgen/feature/OreFeature;canPlaceOre(Lnet/minecraft/world/level/block/state/BlockState;Ljava/util/function/Function;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/level/levelgen/feature/configurations/OreConfiguration;Lnet/minecraft/world/level/levelgen/feature/configurations/OreConfiguration$TargetBlockState;Lnet/minecraft/core/BlockPos$MutableBlockPos;)Z"
        )
    )
    private boolean ac_wrapCanPlaceOre(
        BlockState state,
        Function<BlockPos, BlockState> adjacentStateAccessor,
        RandomSource random,
        OreConfiguration config,
        OreConfiguration.TargetBlockState targetState,
        BlockPos.MutableBlockPos mutablePos,
        Operation<Boolean> original
    ) {
        // First check if the original would allow placement
        boolean canPlace = original.call(state, adjacentStateAccessor, random, config, targetState, mutablePos);
        
        if (!canPlace) {
            return false;
        }
        
        // Check if inside protected structure (with caching)
        WorldGenLevel level = alexscaves$currentLevel.get();
        if (level != null && alexscaves$isInsideProtectedStructureCached(level, mutablePos)) {
            return false;
        }
        
        return true;
    }

    /**
     * Check if a position is inside an ore-protected structure, with caching per chunk section.
     * Chunk sections are 16x16x16 blocks, so positions within the same section share the cache.
     */
    @Unique
    private static boolean alexscaves$isInsideProtectedStructureCached(WorldGenLevel level, BlockPos pos) {
        // Use chunk section position as cache key for better granularity
        long sectionKey = SectionPos.asLong(pos);
        Map<Long, Boolean> cache = alexscaves$sectionCache.get();
        
        Boolean cached = cache.get(sectionKey);
        if (cached != null) {
            return cached;
        }
        
        // Perform the actual structure check
        boolean result = alexscaves$isInsideProtectedStructure(level, pos);
        cache.put(sectionKey, result);
        return result;
    }

    /**
     * Check if a position is inside an ore-protected structure.
     */
    @Unique
    private static boolean alexscaves$isInsideProtectedStructure(WorldGenLevel level, BlockPos pos) {
        try {
            StructureManager structureManager = null;
            
            if (level instanceof WorldGenRegion worldGenRegion) {
                ServerLevel serverLevel = worldGenRegion.getLevel();
                structureManager = serverLevel.structureManager().forWorldGenRegion(worldGenRegion);
            } else if (level instanceof ServerLevel serverLevel) {
                structureManager = serverLevel.structureManager();
            }
            
            if (structureManager != null) {
                StructureStart structureStart = structureManager.getStructureWithPieceAt(pos, ACTagRegistry.ORE_PROTECTED);
                return structureStart.isValid();
            }
        } catch (Exception e) {
            // Fail safely - if we can't check, allow ore generation
        }
        return false;
    }
}


