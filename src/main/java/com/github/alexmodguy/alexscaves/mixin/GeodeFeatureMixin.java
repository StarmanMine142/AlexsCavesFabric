package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.GeodeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to prevent geode (amethyst crystal) generation inside AlexsCaves protected structures.
 * This fixes issues where crystals spawn inside structures like Underground Cabin.
 */
@Mixin(GeodeFeature.class)
public class GeodeFeatureMixin {

    /**
     * Prevent geode generation if the origin is inside an ore-protected structure.
     */
    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void ac_preventGeodeInStructure(FeaturePlaceContext<GeodeConfiguration> context, CallbackInfoReturnable<Boolean> cir) {
        if (alexscaves$isInsideProtectedStructure(context.level(), context.origin())) {
            cir.setReturnValue(false);
        }
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
            // Fail safely - if we can't check, allow geode generation
        }
        return false;
    }
}
