package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.RadgillEntity;
import com.github.alexmodguy.alexscaves.server.item.HazmatArmorItem;
import com.github.alexmodguy.alexscaves.server.message.WorldEventMessage;
import com.github.alexmodguy.alexscaves.server.misc.*;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.Vec3;

import java.util.Map;

public class AcidBlock extends LiquidBlock {

    private static Map<Block, Block> CORRODES_INTERACTIONS;

    public AcidBlock(FlowingFluid flowingFluid, BlockBehaviour.Properties properties) {
        super(flowingFluid, properties);
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource) {
        if (randomSource.nextInt(400) == 0) {
            level.playLocalSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, ACSoundRegistry.ACID_IDLE, SoundSource.BLOCKS, 0.5F, randomSource.nextFloat() * 0.4F + 0.8F, false);
        }
        boolean top = level.getFluidState(pos.above()).isEmpty();
        if (randomSource.nextInt(top ? 10 : 40) == 0) {
            float height = top ? state.getFluidState().getHeight(level, pos) : randomSource.nextFloat();
            level.addParticle(ACParticleRegistry.ACID_BUBBLE, pos.getX() + randomSource.nextFloat(), pos.getY() + height, pos.getZ() + randomSource.nextFloat(), (randomSource.nextFloat() - 0.5F) * 0.1F, 0.05F + randomSource.nextFloat() * 0.1F, (randomSource.nextFloat() - 0.5F) * 0.1F);
        }
    }

    public void entityInside(BlockState blockState, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof ItemEntity itemEntity && !level.isClientSide) {
            ItemStack stack = itemEntity.getItem();
            if (!stack.isEmpty()) {
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.playSound(null, itemEntity.blockPosition(), SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.6F, 1.2F);
                }
                itemEntity.discard();
            }
            return;
        }

        if (!entity.getType().is(ACTagRegistry.RESISTS_ACID) && ACFluidHelper.getAcidHeight(entity) > 0.1) {
            boolean armor = false;
            boolean hurtSound = false;
            float dmgMultiplier = 1.0F;
            if (entity instanceof LivingEntity living) {
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    if (slot.isArmor()) {
                        ItemStack item = living.getItemBySlot(slot);
                        if (item != null && item.isDamageableItem() && !(item.getItem() instanceof HazmatArmorItem)) {
                            armor = true;
                            if (living.getRandom().nextFloat() < 0.05F && !(entity instanceof Player player && player.isCreative())) {
                                item.hurtAndBreak(1, living, slot);
                            }
                        }
                    }
                }
                dmgMultiplier = 1.0F - (HazmatArmorItem.getWornAmount(living) / 4F);
            }
            if (armor) {
                ACAdvancementTriggerRegistry.ENTER_ACID_WITH_ARMOR.triggerForEntity(entity);
            }
            if (level.random.nextFloat() < dmgMultiplier) {
                float golemAddition = entity.getType().is(ACTagRegistry.WEAK_TO_ACID) ? 10.0F : 0.0F;
                hurtSound = entity.hurt(ACDamageTypes.causeAcidDamage(level.registryAccess()), dmgMultiplier * (float) (armor ? 0.01D : 1.0D) + golemAddition);
            }
            if (hurtSound) {
                entity.playSound(ACSoundRegistry.ACID_BURN);
            }
        }
        if (entity instanceof LivingEntity living && !(entity instanceof RadgillEntity)) {
            Vec3 vec3 = entity.getDeltaMovement();
            float f1 = Math.min(1.0F, (float) vec3.length());
            if (f1 > 0.1F) {
                entity.playSound(ACSoundRegistry.ACID_SWIM, f1, 1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);
            }
        }
    }

    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState state2, boolean isMoving) {
        super.onPlace(state, worldIn, pos, state2, isMoving);
        if (!worldIn.isClientSide) {
            handleUpdate(worldIn, pos);
        }
    }

    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
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
            level.setBlockAndUpdate(below, Blocks.MUD.defaultBlockState());
            spawnInteractionEffects(level, below, true);
            return;
        }
        if (belowType == Fluids.LAVA || belowType == Fluids.FLOWING_LAVA) {
            level.setBlockAndUpdate(below, ACBlockRegistry.RADROCK.defaultBlockState());
            spawnInteractionEffects(level, below, false);
            return;
        }
        if (belowType == ACFluidRegistry.PURPLE_SODA_FLUID_SOURCE || belowType == ACFluidRegistry.PURPLE_SODA_FLUID_FLOWING) {
            level.setBlockAndUpdate(below, ACBlockRegistry.GREEN_ROCK_CANDY.defaultBlockState());
            spawnInteractionEffects(level, below, false);
            return;
        }

        tickCorrosion(level, pos);
    }

    private void evaporateInNether(Level level, BlockPos pos) {
        level.setBlockAndUpdate(pos, ACBlockRegistry.UNREFINED_WASTE.defaultBlockState());

        if (level instanceof ServerLevel serverLevel) {
            RandomSource random = serverLevel.random;
            for (int i = 0; i < 24; i++) {
                serverLevel.sendParticles(ACParticleRegistry.ACID_BUBBLE,
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
                serverLevel.sendParticles(ACParticleRegistry.ACID_BUBBLE,
                        pos.getX() + random.nextDouble(),
                        pos.getY() + 1.0D,
                        pos.getZ() + random.nextDouble(),
                        1, 0.0D, 0.15D, 0.0D, 0.05D);
            }
            serverLevel.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
        } else {
            for (int i = 0; i < 12; i++) {
                serverLevel.sendParticles(ACParticleRegistry.ACID_BUBBLE,
                        pos.getX() + random.nextDouble(),
                        pos.getY() + 1.0D,
                        pos.getZ() + random.nextDouble(),
                        1, 0.0D, 0.15D, 0.0D, 0.05D);
            }
            serverLevel.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 0.8F);
        }
    }

    public void tickCorrosion(Level worldIn, BlockPos pos) {
        initCorrosion();
        for (Direction direction : ACMath.HORIZONTAL_DIRECTIONS) {
            BlockPos offset = pos.relative(direction);
            BlockState state1 = worldIn.getBlockState(offset);
            if (CORRODES_INTERACTIONS.containsKey(state1.getBlock())) {
                AlexsCaves.sendMSGToAll(new WorldEventMessage(0, offset.getX(), offset.getY(), offset.getZ()));
                BlockState transform = CORRODES_INTERACTIONS.get(state1.getBlock()).defaultBlockState();
                for (Property prop : state1.getProperties()) {
                    transform = transform.hasProperty(prop) ? transform.setValue(prop, state1.getValue(prop)) : transform;
                }
                worldIn.setBlockAndUpdate(offset, transform);
                Vec3 vec3 = offset.getCenter();
                Player player = worldIn.getNearestPlayer(vec3.x, vec3.y, vec3.z, 8, false);
                if (player != null) {
                    ACAdvancementTriggerRegistry.ACID_CREATE_RUST.triggerForEntity(player);
                }
            }
        }
    }

    public static boolean doesBlockCorrode(BlockState state) {
        initCorrosion();
        return CORRODES_INTERACTIONS.containsKey(state.getBlock());
    }

    private static void initCorrosion() {
        if (CORRODES_INTERACTIONS != null) {
            return;
        }
        CORRODES_INTERACTIONS = Util.make(Maps.newHashMap(), (map) -> {
            map.put(Blocks.COPPER_BLOCK, Blocks.WEATHERED_COPPER);
            map.put(Blocks.WEATHERED_COPPER, Blocks.EXPOSED_COPPER);
            map.put(Blocks.EXPOSED_COPPER, Blocks.OXIDIZED_COPPER);
            map.put(Blocks.CUT_COPPER, Blocks.WEATHERED_CUT_COPPER);
            map.put(Blocks.WEATHERED_CUT_COPPER, Blocks.EXPOSED_CUT_COPPER);
            map.put(Blocks.EXPOSED_CUT_COPPER, Blocks.OXIDIZED_CUT_COPPER);
            map.put(Blocks.CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER_SLAB);
            map.put(Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.EXPOSED_CUT_COPPER_SLAB);
            map.put(Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.OXIDIZED_CUT_COPPER_SLAB);
            map.put(Blocks.CUT_COPPER_STAIRS, Blocks.WEATHERED_CUT_COPPER_STAIRS);
            map.put(Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.EXPOSED_CUT_COPPER_STAIRS);
            map.put(Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_CUT_COPPER_STAIRS);
            map.put(Blocks.CHISELED_COPPER, Blocks.WEATHERED_CHISELED_COPPER);
            map.put(Blocks.WEATHERED_CHISELED_COPPER, Blocks.EXPOSED_CHISELED_COPPER);
            map.put(Blocks.EXPOSED_CHISELED_COPPER, Blocks.OXIDIZED_CHISELED_COPPER);
            map.put(Blocks.COPPER_GRATE, Blocks.WEATHERED_COPPER_GRATE);
            map.put(Blocks.WEATHERED_COPPER_GRATE, Blocks.EXPOSED_COPPER_GRATE);
            map.put(Blocks.EXPOSED_COPPER_GRATE, Blocks.OXIDIZED_COPPER_GRATE);
            map.put(Blocks.COPPER_BULB, Blocks.WEATHERED_COPPER_BULB);
            map.put(Blocks.WEATHERED_COPPER_BULB, Blocks.EXPOSED_COPPER_BULB);
            map.put(Blocks.EXPOSED_COPPER_BULB, Blocks.OXIDIZED_COPPER_BULB);
            map.put(Blocks.COPPER_DOOR, Blocks.WEATHERED_COPPER_DOOR);
            map.put(Blocks.WEATHERED_COPPER_DOOR, Blocks.EXPOSED_COPPER_DOOR);
            map.put(Blocks.EXPOSED_COPPER_DOOR, Blocks.OXIDIZED_COPPER_DOOR);
            map.put(Blocks.COPPER_TRAPDOOR, Blocks.WEATHERED_COPPER_TRAPDOOR);
            map.put(Blocks.WEATHERED_COPPER_TRAPDOOR, Blocks.EXPOSED_COPPER_TRAPDOOR);
            map.put(Blocks.EXPOSED_COPPER_TRAPDOOR, Blocks.OXIDIZED_COPPER_TRAPDOOR);
            map.put(ACBlockRegistry.SCRAP_METAL, ACBlockRegistry.RUSTY_SCRAP_METAL);
            map.put(ACBlockRegistry.SCRAP_METAL_PLATE, ACBlockRegistry.RUSTY_SCRAP_METAL_PLATE);
            map.put(ACBlockRegistry.METAL_BARREL, ACBlockRegistry.RUSTY_BARREL);
            map.put(ACBlockRegistry.METAL_SCAFFOLDING, ACBlockRegistry.RUSTY_SCAFFOLDING);
            map.put(ACBlockRegistry.METAL_REBAR, ACBlockRegistry.RUSTY_REBAR);
        });
    }
}