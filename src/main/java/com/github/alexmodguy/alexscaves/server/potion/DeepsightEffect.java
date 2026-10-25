package com.github.alexmodguy.alexscaves.server.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.WeakHashMap;

public class DeepsightEffect extends MobEffect {

    private final Map<LivingEntity, Integer> entityStartDurations = new WeakHashMap<>();

    protected DeepsightEffect() {
        super(MobEffectCategory.BENEFICIAL, 0X002972);
    }

    public int getActiveTime() {
        return 0;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide) {
            return true;
        }
        MobEffectInstance instance = entity.getEffect(ACEffectRegistry.DEEPSIGHT);
        if (instance != null) {
            int duration = instance.getDuration();
            if (!entityStartDurations.containsKey(entity) || duration > entityStartDurations.get(entity)) {
                entityStartDurations.put(entity, duration);
            }
        }
        return true;
    }

    @Override
    public void removeAttributeModifiers(AttributeMap map) {
        super.removeAttributeModifiers(map);
    }

    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        MobEffectInstance instance = entity.getEffect(ACEffectRegistry.DEEPSIGHT);
        if (instance != null) {
            entityStartDurations.put(entity, instance.getDuration());
        }
    }

    public static float getIntensity(Player player, float partialTicks) {
        MobEffectInstance instance = player.getEffect(ACEffectRegistry.DEEPSIGHT);
        if (instance == null) {
            return 0.0F;
        } else if (instance.isInfiniteDuration()) {
            return 1.0F;
        } else {
            DeepsightEffect deepsightEffect = (DeepsightEffect) instance.getEffect().value();
            int duration = instance.getDuration();
            int maxDuration = deepsightEffect.entityStartDurations.getOrDefault(player, duration);
            if (duration > maxDuration) {
                maxDuration = duration;
                deepsightEffect.entityStartDurations.put(player, maxDuration);
            }
            float activeTime = maxDuration - duration + partialTicks;
            return Math.min(20, (Math.min(activeTime, duration + partialTicks))) * 0.05F;
        }
    }
}
