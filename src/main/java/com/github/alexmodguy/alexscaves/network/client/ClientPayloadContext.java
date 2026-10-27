package com.github.alexmodguy.alexscaves.network.client;

import com.github.alexmodguy.alexscaves.network.ACPayloadContext;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

record ClientPayloadContext(ClientPlayNetworking.Context context) implements ACPayloadContext {

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
