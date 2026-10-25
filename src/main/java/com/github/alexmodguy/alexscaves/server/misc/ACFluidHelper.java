package com.github.alexmodguy.alexscaves.server.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class ACFluidHelper {

    public static final TagKey<Fluid> ACID = TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath("c", "acid"));
    public static final TagKey<Fluid> PURPLE_SODA = TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath("c", "soda"));

    private ACFluidHelper() {
    }

    public static boolean isAcid(FluidState state) {
        return state.is(ACID);
    }

    public static boolean isPurpleSoda(FluidState state) {
        return state.is(PURPLE_SODA);
    }

    public static boolean isAcid(Fluid fluid) {
        return fluid.defaultFluidState().is(ACID);
    }

    public static boolean isPurpleSoda(Fluid fluid) {
        return fluid.defaultFluidState().is(PURPLE_SODA);
    }

    public static boolean isWaterOrAcid(FluidState state) {
        return state.is(FluidTags.WATER) || isAcid(state);
    }

    public static boolean isWaterOrPurpleSoda(FluidState state) {
        return state.is(FluidTags.WATER) || isPurpleSoda(state);
    }

    public static double getWaterHeight(Entity entity) {
        return getFluidHeight(entity, FluidTags.WATER);
    }

    public static double getAcidHeight(Entity entity) {
        return getFluidHeight(entity, ACID);
    }

    public static double getPurpleSodaHeight(Entity entity) {
        return getFluidHeight(entity, PURPLE_SODA);
    }

    public static boolean isInAcid(Entity entity) {
        return getAcidHeight(entity) > 0.0D;
    }

    public static boolean isInPurpleSoda(Entity entity) {
        return getPurpleSodaHeight(entity) > 0.0D;
    }

    public static boolean isInModFluid(Entity entity) {
        return isInAcid(entity) || isInPurpleSoda(entity);
    }

    public static boolean isInAnyFluid(Entity entity) {
        return entity.isInWaterOrBubble() || isInModFluid(entity) || !entity.level().getFluidState(entity.blockPosition()).isEmpty();
    }

    public static double getMaxFluidHeight(Entity entity) {
        return Math.max(getWaterHeight(entity), Math.max(getAcidHeight(entity), getPurpleSodaHeight(entity)));
    }

    public static double getFluidHeight(Entity entity, TagKey<Fluid> tag) {
        Level level = entity.level();
        AABB box = entity.getBoundingBox().deflate(0.001D);
        int minX = Mth.floor(box.minX);
        int maxX = Mth.ceil(box.maxX);
        int minY = Mth.floor(box.minY);
        int maxY = Mth.ceil(box.maxY);
        int minZ = Mth.floor(box.minZ);
        int maxZ = Mth.ceil(box.maxZ);
        double maxHeight = 0.0D;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int x = minX; x < maxX; ++x) {
            for (int y = minY; y < maxY; ++y) {
                for (int z = minZ; z < maxZ; ++z) {
                    cursor.set(x, y, z);
                    FluidState state = level.getFluidState(cursor);
                    if (!state.is(tag)) {
                        continue;
                    }
                    double surface = y + state.getHeight(level, cursor);
                    if (surface > box.minY) {
                        maxHeight = Math.max(maxHeight, surface - box.minY);
                    }
                }
            }
        }
        return Math.min(maxHeight, box.getYsize());
    }

    public static boolean matches(FluidState state, Fluid fluid) {
        return !state.isEmpty() && state.getType().isSame(fluid);
    }

    public static boolean sameFluid(FluidState first, FluidState second) {
        return !first.isEmpty() && !second.isEmpty() && first.getType().isSame(second.getType());
    }

    public static boolean isAcid(LevelReader level, BlockPos pos) {
        return isAcid(level.getFluidState(pos));
    }

    public static boolean isPurpleSoda(LevelReader level, BlockPos pos) {
        return isPurpleSoda(level.getFluidState(pos));
    }

    public static boolean moveInACFluid(FluidState state, LivingEntity entity, Vec3 movementVector, double gravity) {
        if (state.isEmpty()) {
            return false;
        }
        if (!isAcid(state) && !isPurpleSoda(state)) {
            return false;
        }

        boolean sprinting = entity.isSprinting();
        float slowDown = sprinting ? 0.9F : 0.8F;
        float acceleration = sprinting ? 0.026F : 0.02F;
        double upwardPush = 0.2D;

        entity.moveRelative(acceleration, movementVector);
        entity.move(MoverType.SELF, entity.getDeltaMovement());
        Vec3 newMotion = entity.getDeltaMovement().scale(slowDown);

        if (!entity.isNoGravity() && !sprinting && entity.horizontalCollision) {
            newMotion = new Vec3(newMotion.x, upwardPush, newMotion.z);
        }
        entity.setDeltaMovement(newMotion);
        entity.fallDistance = 0.0F;

        if (movementVector.y > 0.0D && entity.horizontalCollision) {
            entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, upwardPush, 0.0D));
        }

        return true;
    }
}