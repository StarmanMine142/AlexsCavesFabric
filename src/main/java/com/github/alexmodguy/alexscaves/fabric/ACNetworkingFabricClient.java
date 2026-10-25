package com.github.alexmodguy.alexscaves.fabric;

import com.github.alexmodguy.alexscaves.citadel.server.message.AnimationMessage;
import com.github.alexmodguy.alexscaves.citadel.server.message.PropertiesMessage;
import com.github.alexmodguy.alexscaves.server.message.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public final class ACNetworkingFabricClient {
    private static boolean initialized;

    private ACNetworkingFabricClient() {
    }

    public static void registerClient() {
        if (initialized) {
            return;
        }
        initialized = true;

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
            ACNetworkingFabric.PayloadHandler<T> handler) {
        ClientPlayNetworking.registerGlobalReceiver(type,
                (payload, context) -> handler.handle(payload, new ClientPayloadContext(context))
        );
    }

    private record ClientPayloadContext(ClientPlayNetworking.Context context) implements ACPayloadContext {

        @Override
        public Player player() {
            return context.player();
        }

        @Override
        public <T> CompletableFuture<T> enqueueWork(Callable<T> task) {
            CompletableFuture<T> future = new CompletableFuture<>();
            context.client().execute(() -> {
                try {
                    future.complete(task.call());
                } catch (Throwable t) {
                    future.completeExceptionally(t);
                }
            });
            return future;
        }

        @Override
        public void enqueueWork(Runnable task) {
            context.client().execute(task);
        }

        @Override
        public Flow flow() {
            return CLIENTBOUND_FLOW;
        }

        private static final Flow CLIENTBOUND_FLOW = new Flow() {
            @Override
            public boolean isClientbound() {
                return true;
            }

            @Override
            public boolean isServerbound() {
                return false;
            }
        };
    }
}