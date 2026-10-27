package com.github.alexmodguy.alexscaves.network.server;

import com.github.alexmodguy.alexscaves.network.ACPayloadContext;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

record ServerPayloadContext(ServerPlayNetworking.Context context) implements ACPayloadContext {

    @Override
    public Player player() {
        return context.player();
    }

    @Override
    public <T> CompletableFuture<T> enqueueWork(Callable<T> task) {
        CompletableFuture<T> future = new CompletableFuture<>();
        context.server().execute(() -> {
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
        context.server().execute(task);
    }

    @Override
    public Flow flow() {
        return SERVERBOUND;
    }

    private static final Flow SERVERBOUND = new Flow() {
        @Override public boolean isClientbound() { return false; }
        @Override public boolean isServerbound() { return true; }
    };
}