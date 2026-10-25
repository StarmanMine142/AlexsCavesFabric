package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.SweetishFishEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.FrostmintExplosion;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.Vec3;

public class PurpleSodaBlock extends LiquidBlock {

    public PurpleSodaBlock(FlowingFluid flowingFluid, BlockBehaviour.Properties properties) {
        super(flowingFluid, properties);
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource) {
        if (randomSource.nextInt(400) == 0) {
            level.playLocalSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, ACSoundRegistry.PURPLE_SODA_IDLE, SoundSource.BLOCKS, 0.5F, randomSource.nextFloat() * 0.4F + 0.8F, false);
        }
        if (randomSource.nextInt(400) == 0 && level.getBlockState(pos.below()).isSolid()) {
            level.addParticle(ACParticleRegistry.PURPLE_SODA_BUBBLE_EMITTER, pos.getX() + randomSource.nextFloat(), pos.getY(), pos.getZ() + randomSource.nextFloat(), 0F, 0F, 0F);
        } else if (randomSource.nextInt(150) == 0) {
            level.addParticle(ACParticleRegistry.PURPLE_SODA_BUBBLE, pos.getX() + randomSource.nextFloat(), pos.getY(), pos.getZ() + randomSource.nextFloat(), 0F, 0F, 0F);
        }
    }

    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos pos, Entity entity) {
        entity.fallDistance = 0.0F;
    }

    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, worldIn, pos, oldState, isMoving);
        if (!worldIn.isClientSide) {
            handleUpdate(worldIn, pos);
        }
    }

    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
        for (Direction direction : ACMath.HORIZONTAL_DIRECTIONS) {
            BlockPos offset = pos.relative(direction);
            BlockState state1 = worldIn.getBlockState(offset);
            if (state1.is(ACBlockRegistry.FROSTMINT)) {
                worldIn.setBlockAndUpdate(offset, Blocks.AIR.defaultBlockState());
                FrostmintExplosion explosion = new FrostmintExplosion(worldIn, null, offset.getX() + 0.5F, offset.getY() + 0.5F, offset.getZ() + 0.5F, 4.0F, Explosion.BlockInteraction.DESTROY_WITH_DECAY, false);
                explosion.explode();
                explosion.finalizeExplosion(true);
            }
        }
        if (!worldIn.isClientSide) {
            handleUpdate(worldIn, pos);
        }
    }

    private void handleUpdate(Level level, BlockPos pos) {
        if (!level.getBlockState(pos).is(this)) {
            return;
        }

        if (level.dimension() == Level.NETHER) {
            evaporateInNether(level, pos);
            return;
        }

        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        Fluid belowType = belowState.getFluidState().getType();

        if (belowType == Fluids.WATER || belowType == Fluids.FLOWING_WATER) {
            level.setBlockAndUpdate(below, ACBlockRegistry.BLUE_ROCK_CANDY.defaultBlockState());
            spawnInteractionEffects(level, below, true);
            return;
        }
        if (belowType == Fluids.LAVA || belowType == Fluids.FLOWING_LAVA) {
            level.setBlockAndUpdate(below, ACBlockRegistry.ORANGE_ROCK_CANDY.defaultBlockState());
            spawnInteractionEffects(level, below, false);
            return;
        }
        if (belowType == ACFluidRegistry.ACID_FLUID_SOURCE || belowType == ACFluidRegistry.ACID_FLUID_FLOWING) {
            level.setBlockAndUpdate(below, ACBlockRegistry.GREEN_ROCK_CANDY.defaultBlockState());
            spawnInteractionEffects(level, below, false);
        }
    }

    private void evaporateInNether(Level level, BlockPos pos) {
        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());

        if (level instanceof ServerLevel serverLevel) {
            RandomSource random = serverLevel.random;
            for (int i = 0; i < 32; i++) {
                serverLevel.sendParticles(ACParticleRegistry.PURPLE_SODA_BUBBLE,
                        pos.getX() + random.nextDouble(),
                        pos.getY() + random.nextDouble() * 0.9D,
                        pos.getZ() + random.nextDouble(),
                        1, 0.0D, 0.18D, 0.0D, 0.06D);
            }
            serverLevel.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 1.2F, 0.8F);
        }
    }

    private void spawnInteractionEffects(Level level, BlockPos pos, boolean withWater) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        RandomSource random = serverLevel.random;
        if (withWater) {
            for (int i = 0; i < 20; i++) {
                serverLevel.sendParticles(ParticleTypes.SPLASH,
                        pos.getX() + random.nextDouble(),
                        pos.getY() + 1.0D,
                        pos.getZ() + random.nextDouble(),
                        1, 0.0D, 0.15D, 0.0D, 0.05D);
            }
            for (int i = 0; i < 12; i++) {
                serverLevel.sendParticles(ACParticleRegistry.PURPLE_SODA_BUBBLE,
                        pos.getX() + random.nextDouble(),
                        pos.getY() + 1.0D,
                        pos.getZ() + random.nextDouble(),
                        1, 0.0D, 0.15D, 0.0D, 0.05D);
            }
            serverLevel.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
        } else {
            for (int i = 0; i < 12; i++) {
                serverLevel.sendParticles(ACParticleRegistry.PURPLE_SODA_BUBBLE,
                        pos.getX() + random.nextDouble(),
                        pos.getY() + 1.0D,
                        pos.getZ() + random.nextDouble(),
                        1, 0.0D, 0.15D, 0.0D, 0.05D);
            }
            serverLevel.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 0.8F);
        }
    }
}