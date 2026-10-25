package com.github.alexmodguy.alexscaves.citadel.animation;

import com.github.alexmodguy.alexscaves.citadel.server.message.AnimationMessage;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author iLexiconn
 * @since 1.0.0
 */
public enum AnimationHandler {
    INSTANCE;

    /**
     * Fired before an animation is applied, allows listeners to cancel or replace the animation.
     */
    public static final Event<AnimationStartCallback> ANIMATION_START = EventFactory.createArrayBacked(
            AnimationStartCallback.class,
            listeners -> event -> {
                for (AnimationStartCallback listener : listeners) {
                    listener.onStart(event);
                }
            }
    );

    /**
     * Fired on every tick of an active animation.
     */
    public static final Event<AnimationTickCallback> ANIMATION_TICK = EventFactory.createArrayBacked(
            AnimationTickCallback.class,
            listeners -> event -> {
                for (AnimationTickCallback listener : listeners) {
                    listener.onTick(event);
                }
            }
    );

    @FunctionalInterface
    public interface AnimationStartCallback {
        void onStart(AnimationEvent.Start<?> event);
    }

    @FunctionalInterface
    public interface AnimationTickCallback {
        void onTick(AnimationEvent.Tick<?> event);
    }

    /**
     * Sends an animation packet to all clients, notifying them of a changed animation
     *
     * @param entity    the entity with an animation to be updated
     * @param animation the animation to be updated
     * @param <T>       the entity type
     */
    public <T extends Entity & IAnimatedEntity> void sendAnimationMessage(T entity, Animation animation) {
        if (entity.level().isClientSide) {
            return;
        }
        entity.setAnimation(animation);
        AnimationMessage msg = new AnimationMessage(entity.getId(), ArrayUtils.indexOf(entity.getAnimations(), animation));
        if (entity.level().getServer() != null) {
            for (ServerPlayer player : PlayerLookup.all(entity.level().getServer())) {
                ServerPlayNetworking.send(player, msg);
            }
        }
    }

    /**
     * Updates all animations for a given entity
     *
     * @param entity the entity with an animation to be updated
     * @param <T>    the entity type
     */
    public <T extends Entity & IAnimatedEntity> void updateAnimations(T entity) {
        if (entity.getAnimation() == null) {
            entity.setAnimation(IAnimatedEntity.NO_ANIMATION);
        } else {
            if (entity.getAnimation() != IAnimatedEntity.NO_ANIMATION) {
                if (entity.getAnimationTick() == 0) {
                    AnimationEvent.Start<T> event = new AnimationEvent.Start<>(entity, entity.getAnimation());
                    ANIMATION_START.invoker().onStart(event);
                    if (!event.isCanceled()) {
                        this.sendAnimationMessage(entity, event.getAnimation());
                    }
                }
                if (entity.getAnimationTick() < entity.getAnimation().getDuration()) {
                    entity.setAnimationTick(entity.getAnimationTick() + 1);
                    ANIMATION_TICK.invoker().onTick(new AnimationEvent.Tick<>(entity, entity.getAnimation(), entity.getAnimationTick()));
                }
                if (entity.getAnimationTick() == entity.getAnimation().getDuration()) {
                    entity.setAnimationTick(0);
                    entity.setAnimation(IAnimatedEntity.NO_ANIMATION);
                }
            }
        }
    }
}