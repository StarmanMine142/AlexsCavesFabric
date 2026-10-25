package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.feature.FeaturePositionValidator;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LakeFeature.class)
public class LakeFeatureMixin {

    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void ac_place(FeaturePlaceContext context, CallbackInfoReturnable<Boolean> cir) {
        if (FeaturePositionValidator.isBiome(context, ACBiomeRegistry.ABYSSAL_CHASM)) {
            cir.setReturnValue(false);
        }
    }

    /**
     * Fix for vanilla LakeFeature accessing chunks outside WorldGenRegion bounds.
     * In WorldGenRegion, always use getUncachedNoiseBiome() to avoid chunk access issues.
     */
    @WrapOperation(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/WorldGenLevel;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;"))
    private Holder<Biome> ac_wrapGetBiome(WorldGenLevel level, BlockPos pos, Operation<Holder<Biome>> original) {
        if (level instanceof WorldGenRegion) {
            // Use noise biome directly - doesn't require chunk access and is accurate for freeze checks
            return level.getUncachedNoiseBiome(pos.getX() >> 2, pos.getY() >> 2, pos.getZ() >> 2);
        }
        return original.call(level, pos);
    }

    /**
     * Serene Seasons redirects Biome.shouldFreeze() through a hook that calls level.getBiome(pos),
     * which is not safe during lake placement in a WorldGenRegion. Recreate the vanilla freeze
     * check locally so worldgen never needs to query neighboring chunks here.
     */
    @WrapOperation(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Z)Z"))
    private boolean ac_wrapShouldFreeze(Biome biome, net.minecraft.world.level.LevelReader level, BlockPos pos, boolean mustBeAtEdge, Operation<Boolean> original) {
        if (level instanceof WorldGenRegion) {
            return ac_shouldFreezeWithoutChunkAccess(biome, level, pos, mustBeAtEdge);
        }
        return original.call(biome, level, pos, mustBeAtEdge);
    }

    private static boolean ac_shouldFreezeWithoutChunkAccess(Biome biome, net.minecraft.world.level.LevelReader level, BlockPos pos, boolean mustBeAtEdge) {
        if (biome.warmEnoughToRain(pos)) {
            return false;
        }
        if (pos.getY() < level.getMinBuildHeight() || pos.getY() >= level.getMaxBuildHeight()) {
            return false;
        }
        if (level.getBrightness(LightLayer.BLOCK, pos) >= 10) {
            return false;
        }

        BlockState state = level.getBlockState(pos);
        if (level.getFluidState(pos).getType() != Fluids.WATER || !(state.getBlock() instanceof LiquidBlock)) {
            return false;
        }
        if (!mustBeAtEdge) {
            return true;
        }

        return !(level.isWaterAt(pos.west()) && level.isWaterAt(pos.east()) && level.isWaterAt(pos.north()) && level.isWaterAt(pos.south()));
    }
}
