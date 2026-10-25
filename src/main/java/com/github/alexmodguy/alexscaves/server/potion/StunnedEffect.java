package com.github.alexmodguy.alexscaves.server.potion;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.util.MobTargetAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;

public class StunnedEffect extends MobEffect {

    protected StunnedEffect() {
        super(MobEffectCategory.HARMFUL, 0XFFFBC5);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.parse("alexscaves:stunned_speed"), (double) -1.0F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.getDeltaMovement().y > 0) {
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(1, 0.1D, 1));
        }
        if (entity.level().random.nextFloat() < entity.getBbWidth() * 0.12F) {
            entity.level().addParticle(ACParticleRegistry.STUN_STAR, entity.getX(), entity.getEyeY(), entity.getZ(), entity.getId(), entity.level().random.nextFloat() * 360, 0);
        }
        if (entity instanceof Mob mob) {
            entity.setXRot(30.0F);
            entity.xRotO = 30.0F;
            if (!mob.level().isClientSide) {
                MobTargetAccessor accessor = (MobTargetAccessor) mob;
                accessor.ac_getGoalSelector().setControlFlag(Goal.Flag.MOVE, false);
                accessor.ac_getGoalSelector().setControlFlag(Goal.Flag.JUMP, false);
                accessor.ac_getGoalSelector().setControlFlag(Goal.Flag.LOOK, false);
            }
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration > 0;
    }
}
