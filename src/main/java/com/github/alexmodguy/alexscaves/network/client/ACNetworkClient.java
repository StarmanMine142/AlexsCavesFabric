package com.github.alexmodguy.alexscaves.network.client;

import com.github.alexmodguy.alexscaves.citadel.server.entity.CitadelEntityData;
import com.github.alexmodguy.alexscaves.citadel.server.message.AnimationMessage;
import com.github.alexmodguy.alexscaves.citadel.server.message.PropertiesMessage;
import com.github.alexmodguy.alexscaves.network.server.ACNetworkServer;
import com.github.alexmodguy.alexscaves.server.message.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public final class ACNetworkClient {
    private static boolean initialized;

    private ACNetworkClient() {
    }

    public static void registerClient() {
        if (initialized) {
            return;
        }
        initialized = true;

        PayloadTypeRegistry.playS2C().register(CaveBookProgressSyncMessage.TYPE, CaveBookProgressSyncMessage.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(CaveBookProgressSyncMessage.TYPE, (msg, ctx) -> {
            ctx.client().execute(() -> {
                Player player = Minecraft.getInstance().player;
                if (player != null) {
                    CitadelEntityData.setCitadelTag(player, msg.tag());
                            CitadelEntityData.getOrCreateCitadelTag(player);
                }
            });
        });

        registerReceiver(BeholderSyncMessage.TYPE, BeholderSyncMessage::handle);
        registerReceiver(SpelunkeryTableCompleteTutorialMessage.TYPE, SpelunkeryTableCompleteTutorialMessage::handle);
        registerReceiver(SundropRainbowMessage.TYPE, SundropRainbowMessage::handle);
        registerReceiver(UpdateBossBarMessage.TYPE, UpdateBossBarMessage::handle);
        registerReceiver(UpdateBossEruptionStatus.TYPE, UpdateBossEruptionStatus::handle);
        registerReceiver(UpdateCaveBiomeMapTagMessage.TYPE, UpdateCaveBiomeMapTagMessage::handle);
        registerReceiver(UpdateEffectVisualityEntityMessage.TYPE, UpdateEffectVisualityEntityMessage::handle);
        registerReceiver(UpdateItemTagMessage.TYPE, UpdateItemTagMessage::handle);
        registerReceiver(UpdateMagneticDataMessage.TYPE, UpdateMagneticDataMessage::handle);
        registerReceiver(PropertiesMessage.TYPE, PropertiesMessage::handle);
        registerReceiver(WorldEventMessage.TYPE, WorldEventMessage::handle);
        registerReceiver(AnimationMessage.TYPE, AnimationMessage::handle);
    }

    private static <T extends CustomPacketPayload> void registerReceiver(
            CustomPacketPayload.Type<T> type,
            ACNetworkServer.PayloadHandler<T> handler) {
        ClientPlayNetworking.registerGlobalReceiver(type,
                (payload, context) -> handler.handle(payload, new ClientPayloadContext(context))
        );
    }
}