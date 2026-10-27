package com.github.alexmodguy.alexscaves.network;

import net.minecraft.world.entity.player.Player;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public interface ACPayloadContext {
    Player player();

    <T> CompletableFuture<T> enqueueWork(Callable<T> task);

    void enqueueWork(Runnable task);

    Flow flow();

    interface Flow {
        boolean isClientbound();
        boolean isServerbound();
    }
}