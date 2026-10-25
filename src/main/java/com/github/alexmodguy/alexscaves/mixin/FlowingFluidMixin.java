package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowingFluid.class)
public abstract class FlowingFluidMixin extends Fluid {

    @Unique
    private static final Direction[] AC_DIRECTIONS = Direction.values();

    @Inject(
            method = "canHoldFluid(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/Fluid;)Z",
            cancellable = true,
            at = @At("HEAD")
    )
    private void ac_canHoldFluid(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, Fluid fluid, CallbackInfoReturnable<Boolean> cir) {
        if (blockState.getBlock() instanceof LiquidBlockContainer
                && this.is(ACTagRegistry.DOES_NOT_FLOW_INTO_WATERLOGGABLE_BLOCKS)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "spreadTo(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/material/FluidState;)V",
            cancellable = true,
            remap = true,
            at = @At("HEAD")
    )
    private void ac_spreadTo(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Direction direction, FluidState fluidState, CallbackInfo ci) {
        if (blockState.getBlock() instanceof LiquidBlockContainer
                && this.is(ACTagRegistry.DOES_NOT_FLOW_INTO_WATERLOGGABLE_BLOCKS)) {
            ci.cancel();
        }
    }

    @Inject(
            method = "tick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/FluidState;)V",
            cancellable = true,
            remap = true,
            at = @At("HEAD")
    )
    private void ac_tick(Level level, BlockPos blockPos, FluidState fluidState, CallbackInfo ci) {
        if (level.isClientSide()) {
            return;
        }

        Fluid selfFluid = fluidState.getType();
        boolean isAcid = selfFluid == ACFluidRegistry.ACID_FLUID_SOURCE
                || selfFluid == ACFluidRegistry.ACID_FLUID_FLOWING;
        boolean isSoda = selfFluid == ACFluidRegistry.PURPLE_SODA_FLUID_SOURCE
                || selfFluid == ACFluidRegistry.PURPLE_SODA_FLUID_FLOWING;

        if ((isAcid || isSoda) && level.dimensionType().ultraWarm()) {
            ac_vaporize(level, blockPos, isAcid);
            ci.cancel();
            return;
        }

        BlockState replacement = ac_getInteractionResult(fluidState, level, blockPos);
        if (replacement != null) {
            level.setBlockAndUpdate(blockPos, replacement);
            ci.cancel();
        }
    }

    @Unique
    private static void ac_vaporize(Level level, BlockPos pos, boolean isAcid) {
        level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS,
                0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);

        if (isAcid) {
            for (int i = 0; i < 8; i++) {
                level.addAlwaysVisibleParticle(ACParticleRegistry.RADGILL_SPLASH,
                        pos.getX() + Math.random(), pos.getY() + Math.random(), pos.getZ() + Math.random(),
                        (Math.random() - 0.5F) * 0.25F, Math.random() * 0.25F, (Math.random() - 0.5F) * 0.25F);
            }
            level.setBlockAndUpdate(pos, ACBlockRegistry.UNREFINED_WASTE.defaultBlockState());
        } else {
            for (int i = 0; i < 8; i++) {
                level.addAlwaysVisibleParticle(ACParticleRegistry.PURPLE_SODA_FIZZ, true,
                        pos.getX() + Math.random(), pos.getY() + Math.random(), pos.getZ() + Math.random(),
                        (Math.random() - 0.5F) * 0.25F, Math.random() * 0.25F, (Math.random() - 0.5F) * 0.25F);
            }
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
    }

    @Unique
    private static BlockState ac_getInteractionResult(FluidState self, Level level, BlockPos pos) {
        FluidState current = level.getFluidState(pos);
        if (current.getType() != self.getType()) {
            return null;
        }

        Fluid selfFluid = self.getType();
        boolean selfIsAcid = selfFluid == ACFluidRegistry.ACID_FLUID_SOURCE
                || selfFluid == ACFluidRegistry.ACID_FLUID_FLOWING;
        boolean selfIsSoda = selfFluid == ACFluidRegistry.PURPLE_SODA_FLUID_SOURCE
                || selfFluid == ACFluidRegistry.PURPLE_SODA_FLUID_FLOWING;
        boolean selfIsWater = self.is(FluidTags.WATER);
        boolean selfIsLava = self.is(FluidTags.LAVA);

        if (!selfIsAcid && !selfIsSoda && !selfIsWater && !selfIsLava) {
            return null;
        }

        for (Direction direction : AC_DIRECTIONS) {
            BlockPos neighborPos = pos.relative(direction);
            FluidState neighborState = level.getFluidState(neighborPos);
            if (neighborState.isEmpty()) {
                continue;
            }

            Fluid neighborFluid = neighborState.getType();
            boolean neighborIsAcid = neighborFluid == ACFluidRegistry.ACID_FLUID_SOURCE
                    || neighborFluid == ACFluidRegistry.ACID_FLUID_FLOWING;
            boolean neighborIsSoda = neighborFluid == ACFluidRegistry.PURPLE_SODA_FLUID_SOURCE
                    || neighborFluid == ACFluidRegistry.PURPLE_SODA_FLUID_FLOWING;
            boolean neighborIsWater = neighborState.is(FluidTags.WATER);
            boolean neighborIsLava = neighborState.is(FluidTags.LAVA);

            if ((selfIsAcid && neighborIsWater) || (neighborIsAcid && selfIsWater)) {
                return Blocks.MUD.defaultBlockState();
            }
            if ((selfIsAcid && neighborIsLava) || (neighborIsAcid && selfIsLava)) {
                return ACBlockRegistry.RADROCK.defaultBlockState();
            }
            if ((selfIsSoda && neighborIsWater) || (neighborIsSoda && selfIsWater)) {
                return ACBlockRegistry.BLUE_ROCK_CANDY.defaultBlockState();
            }
            if ((selfIsSoda && neighborIsLava) || (neighborIsSoda && selfIsLava)) {
                return ACBlockRegistry.ORANGE_ROCK_CANDY.defaultBlockState();
            }
            if ((selfIsSoda && neighborIsAcid) || (neighborIsSoda && selfIsAcid)) {
                return ACBlockRegistry.GREEN_ROCK_CANDY.defaultBlockState();
            }
        }
        return null;
    }
}