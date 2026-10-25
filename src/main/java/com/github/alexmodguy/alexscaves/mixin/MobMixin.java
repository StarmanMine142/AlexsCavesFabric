package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.entity.util.EntityDropChanceAccessor;
import com.github.alexmodguy.alexscaves.server.entity.util.MobTargetAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity implements EntityDropChanceAccessor, MobTargetAccessor {

    @Shadow
    protected abstract float getEquipmentDropChance(EquipmentSlot slot);

    @Shadow
    public abstract void setDropChance(EquipmentSlot slot, float chance);

    @Shadow
    private boolean canPickUpLoot;

    @Shadow
    protected abstract void dropCustomDeathLoot(ServerLevel serverLevel, DamageSource damageSource, boolean playerKill);

    @Shadow
    protected GoalSelector goalSelector;

    @Shadow
    protected GoalSelector targetSelector;

    public MobMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    public float ac_getEquipmentDropChance(EquipmentSlot equipmentSlot) {
        return this.getEquipmentDropChance(equipmentSlot);
    }

    public void ac_setDropChance(EquipmentSlot equipmentSlot, float chance) {
        this.setDropChance(equipmentSlot, chance);
    }

    public void ac_dropCustomDeathLoot(ServerLevel serverLevel, DamageSource damageSource, boolean playerKill) {
        this.dropCustomDeathLoot(serverLevel, damageSource, playerKill);
    }

    @Override
    public GoalSelector ac_getGoalSelector() {
        return this.goalSelector;
    }

    @Override
    public GoalSelector ac_getTargetSelector() {
        return this.targetSelector;
    }
}