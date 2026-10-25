package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.SubterranodonEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Custom attack goal for Subterranodon that handles both flying and ground attacks.
 * When the entity has a target, it will take off and fly towards the target to attack.
 */
public class SubterranodonAttackGoal extends Goal {

    private final SubterranodonEntity entity;
    private final double speedModifier;
    private int ticksUntilNextAttack;
    private int ticksUntilNextPathRecalculation;
    private static final int ATTACK_INTERVAL = 20;

    public SubterranodonAttackGoal(SubterranodonEntity entity, double speedModifier) {
        this.entity = entity;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = entity.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        if (entity.isPassenger() || entity.isDancing() || entity.isInSittingPose()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = entity.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        if (entity.isPassenger() || entity.isDancing() || entity.isInSittingPose()) {
            return false;
        }
        return true;
    }

    @Override
    public void start() {
        // Take off when starting to attack
        if (!entity.isFlying() && entity.onGround()) {
            entity.setFlying(true);
            // Give a small upward boost
            entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0.5, 0));
        }
        this.ticksUntilNextPathRecalculation = 0;
        this.ticksUntilNextAttack = 0;
    }

    @Override
    public void stop() {
        entity.getNavigation().stop();
    }

    @Override
    public void tick() {
        LivingEntity target = entity.getTarget();
        if (target == null) {
            return;
        }

        // Ensure flying when attacking
        if (!entity.isFlying() && !entity.onGround()) {
            entity.setFlying(true);
        } else if (!entity.isFlying() && entity.onGround()) {
            // Take off from ground
            entity.setFlying(true);
            entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0.4, 0));
        }

        // Look at target
        entity.getLookControl().setLookAt(target, 30.0F, 30.0F);

        double distanceToTarget = entity.distanceToSqr(target);
        double attackReach = getAttackReachSqr(target);

        // Recalculate path periodically
        this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);

        if (this.ticksUntilNextPathRecalculation <= 0) {
            this.ticksUntilNextPathRecalculation = 4 + entity.getRandom().nextInt(7);
            
            // Fly towards target - aim slightly above them for better attack angle
            Vec3 targetPos = target.position().add(0, target.getBbHeight() * 0.5, 0);
            
            if (entity.isFlying()) {
                // Use move control directly for flying movement
                entity.getMoveControl().setWantedPosition(targetPos.x, targetPos.y, targetPos.z, speedModifier);
            } else {
                // Fallback to navigation for ground movement
                entity.getNavigation().moveTo(target, speedModifier);
            }
        }

        // Attack when in range
        this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);

        if (distanceToTarget <= attackReach && this.ticksUntilNextAttack <= 0) {
            this.ticksUntilNextAttack = ATTACK_INTERVAL;
            entity.swing(InteractionHand.MAIN_HAND);
            entity.doHurtTarget(target);
        }
    }

    protected double getAttackReachSqr(LivingEntity target) {
        float reach = entity.getBbWidth() * 2.0F + target.getBbWidth();
        return (double)(reach * reach);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
