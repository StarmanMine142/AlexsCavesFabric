package com.github.alexmodguy.alexscaves.citadel.server.message;

import com.github.alexmodguy.alexscaves.citadel.animation.Animation;
import com.github.alexmodguy.alexscaves.citadel.animation.IAnimatedEntity;
import com.github.alexmodguy.alexscaves.ACPayloadContext;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public record AnimationMessage(int entityID, int index) implements CustomPacketPayload {
    public static final Type<AnimationMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("alexscaves", "citadel_animation"));
    public static final StreamCodec<FriendlyByteBuf, AnimationMessage> CODEC = StreamCodec.ofMember(AnimationMessage::write, AnimationMessage::read);

    public static AnimationMessage read(FriendlyByteBuf buf) {
        return new AnimationMessage(buf.readInt(), buf.readInt());
    }

    public static void write(AnimationMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.entityID);
        buf.writeInt(message.index);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(AnimationMessage message, ACPayloadContext context) {
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().level == null) {
                return;
            }
            Entity entity = Minecraft.getInstance().level.getEntity(message.entityID());
            if (!(entity instanceof IAnimatedEntity animated)) {
                return;
            }
            Animation[] animations = animated.getAnimations();
            if (message.index() >= 0 && message.index() < animations.length) {
                animated.setAnimation(animations[message.index()]);
            }
        });
    }
}