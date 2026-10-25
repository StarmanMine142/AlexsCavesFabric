package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.NuclearSirenBlockEntity;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class NuclearBombEntity extends Entity {

    private static final EntityDataAccessor<Integer> TIME = SynchedEntityData.defineId(NuclearBombEntity.class, EntityDataSerializers.INT);
    public static final int MAX_TIME = 300;
    private boolean spawnedExplosion = false;

    public NuclearBombEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public NuclearBombEntity(Level level, double x, double y, double z) {
        this(ACEntityRegistry.NUCLEAR_BOMB, level);
        this.setPos(x, y, z);
        double d0 = level.random.nextDouble() * (double)((float)Math.PI * 2F);
        this.setDeltaMovement(-Math.sin(d0) * 0.02D, (double)0.2F, -Math.cos(d0) * 0.02D);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    public void tick() {
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, -0.7, 0.7D));
        }
        if((tickCount + this.getId()) % 10 == 0 && level() instanceof ServerLevel serverLevel){
            NuclearSirenBlockEntity.getNearbySirens(serverLevel, this.blockPosition(), 256).forEach(this::activateSiren);
        }
        int i = this.getTime() + 1;
        if (i > MAX_TIME) {
            this.discard();
            if (!this.level().isClientSide && !spawnedExplosion) {
                this.explode();
                spawnedExplosion = true;
            }
        } else {
            this.setTime(i);
            this.updateInWaterStateAndDoFluidPushing();
            if (this.level().isClientSide && MAX_TIME - i > 10 && random.nextFloat() < 0.3F && this.onGround()) {
                Vec3 center = this.getEyePosition();
                level().addParticle(ACParticleRegistry.PROTON, center.x, center.y, center.z, center.x, center.y, center.z);
            }
        }
    }

    private void activateSiren(BlockPos pos) {
        if(level().getBlockEntity(pos) instanceof NuclearSirenBlockEntity nuclearSirenBlock){
            nuclearSirenBlock.setNearestNuclearBomb(this);
        }
    }

    private void explode() {
        NuclearExplosionEntity explosion = ACEntityRegistry.NUCLEAR_EXPLOSION.create(level());
        explosion.copyPosition(this);
        explosion.setSize((float) AlexsCaves.COMMON_CONFIG.nukeExplosionSizeModifier);
        level().addFreshEntity(explosion);
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, net.minecraft.world.level.block.state.BlockState state, BlockPos pos) {
        if (onGround && this.fallDistance > 20.0F) {
            this.discard();
            if (!this.level().isClientSide) {
                this.explode();
            }
        }
        super.checkFallDamage(y, onGround, state, pos);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(TIME, 0);
    }

    public int getTime() {
        return this.entityData.get(TIME);
    }

    public void setTime(int time) {
        this.entityData.set(TIME, time);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

    }

    public boolean isPickable() {
        return !this.isRemoved();
    }

    public boolean shouldBeSaved() {
        return !this.isRemoved();
    }

    public boolean isAttackable() {
        return false;
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(ACBlockRegistry.NUCLEAR_BOMB);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(Items.SHEARS)) {
            player.swing(hand);
            this.playSound(ACSoundRegistry.NUCLEAR_BOMB_DEFUSE);
            this.remove(RemovalReason.KILLED);
            this.spawnAtLocation(new ItemStack(ACBlockRegistry.NUCLEAR_BOMB));
            if (!player.getAbilities().instabuild) {
                itemStack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            }
            return InteractionResult.SUCCESS;
        } else if (player.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        } else if (!this.level().isClientSide) {
            return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
        } else {
            return InteractionResult.SUCCESS;
        }
    }

    public void positionRider(Entity passenger, MoveFunction moveFunction) {
        if (this.isPassengerOfSameVehicle(passenger) && passenger instanceof LivingEntity living && !this.touchingUnloadedChunk()) {
            float progress = this.getTime() / (float) MAX_TIME;
            float expandScale = 1F + (float) Math.sin(progress * progress * Math.PI) * 0.5F;
            float f1 = -(this.getXRot() / 40F);
            float j = expandScale - progress * 0.3F;
            Vec3 attachPoint = passenger.getVehicleAttachmentPoint(this);
            double d0 = this.getY() + j - attachPoint.y;
            moveFunction.accept(passenger, this.getX(), d0, this.getZ());
            passenger.fallDistance = 0.0F;
        } else {
            super.positionRider(passenger, moveFunction);
        }
    }

    public boolean causeFallDamage(float f, float f1, DamageSource damageSource) {
        return false;
    }
}
