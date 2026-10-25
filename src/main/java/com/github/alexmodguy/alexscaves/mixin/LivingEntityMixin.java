package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.citadel.server.entity.ICitadelDataEntity;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.entity.util.*;
import com.github.alexmodguy.alexscaves.server.misc.ACRuntimeData;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements HeadRotationEntityAccessor, WatcherPossessionAccessor, DarknessIncarnateUserAccessor, EntityDropChanceAccessor, FrostmintFreezableAccessor, ICitadelDataEntity {

    @Unique
    private static final String CITADEL_DATA_TAG = "CitadelData";

    @Unique
    private CompoundTag citadelEntityData = new CompoundTag();

    @Shadow public abstract float getYHeadRot();
    @Shadow public float yHeadRotO;
    @Shadow public abstract boolean hasEffect(Holder<MobEffect> effect);
    @Shadow @Final public WalkAnimationState walkAnimation;
    @Shadow public float yHeadRot;
    @Shadow public abstract boolean addEffect(MobEffectInstance instance);
    @Shadow public abstract boolean canFreeze();
    @Shadow protected abstract boolean isAffectedByFluids();
    @Shadow public abstract boolean canStandOnFluid(FluidState fluidState);
    @Shadow public abstract void jumpFromGround();
    @Shadow protected abstract float getWaterSlowDown();
    @Shadow protected int noJumpDelay;
    @Shadow protected boolean jumping;

    @Unique private float prevHeadYaw;
    @Unique private float prevHeadYaw0;
    @Unique private float prevHeadPitch;
    @Unique private float prevHeadPitch0;

    @Unique private boolean watcherPossessionFlag;
    @Unique private boolean slowFallingFlag;
    @Unique private boolean frostmintFreezingFlag;

    @Unique private int acFluidSoundCooldown = 0;
    @Unique private double acLastFluidDist = 0.0D;

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void ac_addCitadelData(CompoundTag compoundTag, CallbackInfo ci) {
        if (!this.citadelEntityData.isEmpty()) {
            compoundTag.put(CITADEL_DATA_TAG, this.citadelEntityData.copy());
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void ac_readCitadelData(CompoundTag compoundTag, CallbackInfo ci) {
        this.citadelEntityData = compoundTag.contains(CITADEL_DATA_TAG, 10)
                ? compoundTag.getCompound(CITADEL_DATA_TAG).copy()
                : new CompoundTag();
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/LivingEntity;calculateEntityAnimation(Z)V"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    public void ac_calculateEntityAnimation(boolean b, CallbackInfo ci) {
        if (MagnetUtil.isPulledByMagnets(this) && ((MagneticEntityAccessor) this).getMagneticAttachmentFace() != Direction.DOWN) {
            ci.cancel();
            float f1 = (float) Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
            float f2 = Math.min(f1 * 6, 1.0F);
            this.walkAnimation.update(f2, 0.4F);
        }
    }

    @Inject(method = "baseTick", at = @At("TAIL"))
    private void ac_customFluidPush(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (self.isSprinting() && ac_isInACFluid(self)) {
            self.setSprinting(false);
        }

        if (self.touchingUnloadedChunk() || !self.isPushedByFluid()) {
            return;
        }

        if (this.acFluidSoundCooldown > 0) {
            this.acFluidSoundCooldown--;
        }
        this.acLastFluidDist = Mth.length(self.getX() - self.xo, self.getY() - self.yo, self.getZ() - self.zo);

        AABB box = self.getBoundingBox().deflate(0.001D);
        double fluidHeight = 0.0D;
        Vec3 flow = Vec3.ZERO;
        int count = 0;
        BlockPos.MutableBlockPos mut = new BlockPos.MutableBlockPos();

        for (int x = Mth.floor(box.minX); x < Mth.ceil(box.maxX); x++) {
            for (int y = Mth.floor(box.minY); y < Mth.ceil(box.maxY); y++) {
                for (int z = Mth.floor(box.minZ); z < Mth.ceil(box.maxZ); z++) {
                    mut.set(x, y, z);
                    FluidState fs = self.level().getFluidState(mut);
                    if (!isACFluid(fs)) {
                        continue;
                    }
                    double surface = (double) y + fs.getHeight(self.level(), mut);
                    if (surface >= box.minY) {
                        fluidHeight = Math.max(surface - box.minY, fluidHeight);
                        Vec3 f = fs.getFlow(self.level(), mut);
                        if (fluidHeight < 0.4D) {
                            f = f.scale(fluidHeight);
                        }
                        flow = flow.add(f);
                        count++;
                    }
                }
            }
        }

        if (flow.length() > 0.0D && count > 0) {
            flow = flow.scale(1.0D / count).normalize().scale(0.013D);
            self.setDeltaMovement(self.getDeltaMovement().add(flow));
        }

        if (this.acFluidSoundCooldown <= 0) {
            FluidState fs = self.level().getFluidState(self.blockPosition());
            boolean inAcid = fs.getType() == ACFluidRegistry.ACID_FLUID_SOURCE
                    || fs.getType() == ACFluidRegistry.ACID_FLUID_FLOWING;
            boolean inSoda = fs.getType() == ACFluidRegistry.PURPLE_SODA_FLUID_SOURCE
                    || fs.getType() == ACFluidRegistry.PURPLE_SODA_FLUID_FLOWING;
            if ((inAcid || inSoda) && this.acLastFluidDist > 0.1D) {
                this.acFluidSoundCooldown = 12 + self.getRandom().nextInt(6);
                SoundEvent sound = inAcid ? ACSoundRegistry.ACID_SWIM : ACSoundRegistry.PURPLE_SODA_SWIM;
                float volume = (float) Math.min(1.0F, this.acLastFluidDist);
                float pitch = 1.0F + (self.getRandom().nextFloat() - self.getRandom().nextFloat()) * 0.4F;
                self.playSound(sound, volume, pitch);
            }
        }
    }

    @Unique
    private static boolean isACFluid(FluidState fs) {
        Fluid type = fs.getType();
        return type == ACFluidRegistry.ACID_FLUID_SOURCE
                || type == ACFluidRegistry.ACID_FLUID_FLOWING
                || type == ACFluidRegistry.PURPLE_SODA_FLUID_SOURCE
                || type == ACFluidRegistry.PURPLE_SODA_FLUID_FLOWING;
    }

    @Unique
    private static boolean ac_isInACFluid(LivingEntity entity) {
        AABB box = entity.getBoundingBox().deflate(0.001D);
        BlockPos.MutableBlockPos mut = new BlockPos.MutableBlockPos();
        for (int x = Mth.floor(box.minX); x < Mth.ceil(box.maxX); x++) {
            for (int y = Mth.floor(box.minY); y < Mth.ceil(box.maxY); y++) {
                for (int z = Mth.floor(box.minZ); z < Mth.ceil(box.maxZ); z++) {
                    mut.set(x, y, z);
                    if (isACFluid(entity.level().getFluidState(mut))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Inject(method = "setSprinting", at = @At("HEAD"), cancellable = true)
    private void ac_preventSprintStart(boolean sprinting, CallbackInfo ci) {
        if (sprinting) {
            LivingEntity self = (LivingEntity) (Object) this;
            if (ac_isInACFluid(self)) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void ac_travelInCustomFluid(Vec3 movementVector, CallbackInfo ci) {
        LivingEntity living = (LivingEntity) (Object) this;
        if (!living.isControlledByLocalInstance()) {
            return;
        }

        FluidState fluidState = living.level().getFluidState(living.blockPosition());
        if (!this.isAffectedByFluids()
                || this.canStandOnFluid(fluidState)
                || fluidState.is(FluidTags.WATER)
                || fluidState.is(FluidTags.LAVA)) {
            return;
        }

        if (!isACFluid(fluidState)) {
            return;
        }

        double startY = living.getY();
        double gravity = living.getAttributeValue(Attributes.GRAVITY);
        boolean falling = living.getDeltaMovement().y <= 0.0D;
        float friction = living.isSprinting() ? 0.9F : this.getWaterSlowDown();
        float acceleration = 0.02F;
        float waterMovementEfficiency = (float) living.getAttributeValue(Attributes.WATER_MOVEMENT_EFFICIENCY);
        if (!living.onGround()) {
            waterMovementEfficiency *= 0.5F;
        }
        if (waterMovementEfficiency > 0.0F) {
            friction += (0.54600006F - friction) * waterMovementEfficiency;
            acceleration += (living.getSpeed() - acceleration) * waterMovementEfficiency;
        }
        if (living.hasEffect(MobEffects.DOLPHINS_GRACE)) {
            friction = 0.96F;
        }

        living.moveRelative(acceleration, movementVector);
        living.move(MoverType.SELF, living.getDeltaMovement());
        Vec3 motion = living.getDeltaMovement();
        if (living.horizontalCollision && living.onClimbable()) {
            motion = new Vec3(motion.x, 0.2D, motion.z);
        }
        living.setDeltaMovement(motion.multiply(friction, 0.8F, friction));

        Vec3 adjusted = living.getFluidFallingAdjustedMovement(gravity, falling, living.getDeltaMovement());
        living.setDeltaMovement(adjusted);
        if (living.horizontalCollision && living.isFree(adjusted.x, adjusted.y + 0.6D - living.getY() + startY, adjusted.z)) {
            living.setDeltaMovement(adjusted.x, 0.3D, adjusted.z);
        }

        if (this.jumping) {
            Vec3 current = living.getDeltaMovement();
            if (current.y < 0.04D) {
                living.setDeltaMovement(current.x, 0.04D, current.z);
            }
        }

        living.calculateEntityAnimation(living instanceof FlyingAnimal);
        ci.cancel();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void ac_acidDamage(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level().isClientSide || !self.isAlive()) {
            return;
        }
        FluidState fluidState = self.level().getFluidState(self.blockPosition());
        boolean inAcid = fluidState.getType() == ACFluidRegistry.ACID_FLUID_SOURCE
                || fluidState.getType() == ACFluidRegistry.ACID_FLUID_FLOWING;
        if (!inAcid) {
            return;
        }
        if (self.tickCount % 10 == 0) {
            self.hurt(self.damageSources().magic(), 4.0F);
        }
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/LivingEntity;increaseAirSupply(I)I"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    protected void ac_increaseAirSupply(int air, CallbackInfoReturnable<Integer> cir) {
        if (this.hasEffect(ACEffectRegistry.BUBBLED)) {
            cir.setReturnValue(air);
        }
    }

    @Inject(method = "setLastHurtByMob", remap = true, at = @At("TAIL"))
    private void ac_onSetLastHurtByMob(@Nullable LivingEntity attacker, CallbackInfo ci) {
        if (attacker instanceof Raider atk) {
            var self = (LivingEntity) (Object) this;
            if (self instanceof Raider || self instanceof AbstractIllager) {
                if (isPossessed(atk)) {
                    if (self instanceof Mob mob) {
                        mob.setTarget(atk);
                    }
                }
            }
        }
    }

    @Inject(method = "heal", at = @At("HEAD"), cancellable = true)
    private void ac_heal(float amount, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.hasEffect(ACEffectRegistry.IRRADIATED) && !self.getType().is(ACTagRegistry.RESISTS_RADIATION)) {
            ci.cancel();
        }
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void ac_blockJumpInFluid(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (this.jumping && ac_isInACFluid(self)) {
            this.noJumpDelay = 10;
        }
    }

    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    private void ac_blockGroundJumpInCustomFluid(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (ac_isInACFluid(self)) {
            ci.cancel();
        }
    }

    public void setMagnetHeadRotation() {
        prevHeadYaw = this.getYHeadRot();
        prevHeadYaw0 = this.yHeadRotO;
        prevHeadPitch = this.getXRot();
        prevHeadPitch0 = this.xRotO;
        MagnetUtil.rotateHead((LivingEntity) (Entity) this);
    }

    public void resetMagnetHeadRotation() {
        this.yHeadRot = prevHeadYaw;
        this.yHeadRotO = prevHeadYaw0;
        setXRot(prevHeadPitch);
        this.xRotO = prevHeadPitch0;
    }

    public void setPossessedByWatcher(boolean possessedByWatcher) {
        this.watcherPossessionFlag = possessedByWatcher;
    }

    public boolean isPossessedByWatcher() {
        return watcherPossessionFlag;
    }

    public void setSlowFallingFlag(boolean slowFallingFlag) {
        this.slowFallingFlag = slowFallingFlag;
    }

    public boolean hasSlowFallingFlag() {
        return slowFallingFlag;
    }

    public void setFrostmintFreezing(boolean frostmintFreezingFlag) {
        this.frostmintFreezingFlag = frostmintFreezingFlag;
    }

    public boolean isFreezingFromFrostmint() {
        return frostmintFreezingFlag;
    }

    @Unique
    private static boolean isPossessed(Entity e) {
        return ACRuntimeData.getOrCreate(e).getBoolean("TotemPossessed");
    }

    @Override
    public CompoundTag getCitadelEntityData() {
        return this.citadelEntityData;
    }

    @Override
    public void setCitadelEntityData(CompoundTag nbt) {
        this.citadelEntityData = nbt == null ? new CompoundTag() : nbt.copy();
    }
}