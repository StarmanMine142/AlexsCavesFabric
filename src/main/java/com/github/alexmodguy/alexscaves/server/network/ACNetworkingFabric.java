package com.github.alexmodguy.alexscaves.server.network;

import com.github.alexmodguy.alexscaves.citadel.server.message.AnimationMessage;
import com.github.alexmodguy.alexscaves.citadel.server.message.PropertiesMessage;
import com.github.alexmodguy.alexscaves.ACPayloadContext;
import com.github.alexmodguy.alexscaves.server.message.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public final class ACNetworkingFabric {
    private static boolean initialized;

    private ACNetworkingFabric() {
    }

    public static void registerCommon() {
        if (initialized) {
            return;
        }
        initialized = true;

        registerC2S(ArmorKeyMessage.TYPE, ArmorKeyMessage.CODEC, ArmorKeyMessage::handle);
        registerC2S(BeholderRotateMessage.TYPE, BeholderRotateMessage.CODEC, BeholderRotateMessage::handle);
        registerC2S(MountedEntityKeyMessage.TYPE, MountedEntityKeyMessage.CODEC, MountedEntityKeyMessage::handle);
        registerC2S(MultipartEntityMessage.TYPE, MultipartEntityMessage.CODEC, MultipartEntityMessage::handle);
        registerC2S(PlayerJumpFromMagnetMessage.TYPE, PlayerJumpFromMagnetMessage.CODEC, PlayerJumpFromMagnetMessage::handle);
        registerC2S(PossessionKeyMessage.TYPE, PossessionKeyMessage.CODEC, PossessionKeyMessage::handle);
        registerC2S(SpelunkeryTableChangeMessage.TYPE, SpelunkeryTableChangeMessage.CODEC, SpelunkeryTableChangeMessage::handle);

        registerS2CType(BeholderSyncMessage.TYPE, BeholderSyncMessage.CODEC);
        registerS2CType(SpelunkeryTableCompleteTutorialMessage.TYPE, SpelunkeryTableCompleteTutorialMessage.CODEC);
        registerS2CType(SundropRainbowMessage.TYPE, SundropRainbowMessage.CODEC);
        registerS2CType(UpdateBossBarMessage.TYPE, UpdateBossBarMessage.CODEC);
        registerS2CType(UpdateBossEruptionStatus.TYPE, UpdateBossEruptionStatus.CODEC);
        registerS2CType(UpdateCaveBiomeMapTagMessage.TYPE, UpdateCaveBiomeMapTagMessage.CODEC);
        registerS2CType(UpdateEffectVisualityEntityMessage.TYPE, UpdateEffectVisualityEntityMessage.CODEC);
        registerS2CType(UpdateMagneticDataMessage.TYPE, UpdateMagneticDataMessage.CODEC);
        registerS2CType(WorldEventMessage.TYPE, WorldEventMessage.CODEC);
        registerS2CType(AnimationMessage.TYPE, AnimationMessage.CODEC);

        registerBidirectional(PropertiesMessage.TYPE, PropertiesMessage.CODEC, PropertiesMessage::handle);
        registerBidirectional(UpdateItemTagMessage.TYPE, UpdateItemTagMessage.CODEC, UpdateItemTagMessage::handle);
    }

    private static <T extends CustomPacketPayload> void registerC2S(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            PayloadHandler<T> handler) {
        PayloadTypeRegistry.playC2S().register(type, codec);
        ServerPlayNetworking.registerGlobalReceiver(type,
                (payload, context) -> handler.handle(payload, new ServerPayloadContext(context))
        );
    }

    private static <T extends CustomPacketPayload> void registerS2CType(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        PayloadTypeRegistry.playS2C().register(type, codec);
    }

    private static <T extends CustomPacketPayload> void registerBidirectional(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            PayloadHandler<T> handler) {
        registerC2S(type, codec, handler);
        registerS2CType(type, codec);
    }

    @FunctionalInterface
    public interface PayloadHandler<T extends CustomPacketPayload> {
        void handle(T payload, ACPayloadContext context);
    }
}