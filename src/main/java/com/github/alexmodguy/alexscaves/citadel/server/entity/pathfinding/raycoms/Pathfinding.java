package com.github.alexmodguy.alexscaves.citadel.server.entity.pathfinding.raycoms;

import com.github.alexmodguy.alexscaves.citadel.Citadel;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;

public final class Pathfinding {
    private static final BlockingQueue<Runnable> jobQueue = new LinkedBlockingDeque<>();
    private static ThreadPoolExecutor executor;
    private static MinecraftServer server;

    private Pathfinding() {
    }

    public static void setServer(MinecraftServer server) {
        Pathfinding.server = server;
    }

    public static boolean isDebug() {
        return PathfindingConstants.isDebugMode;
    }

    public static ThreadPoolExecutor getExecutor() {
        if (executor == null) {
            executor = new ThreadPoolExecutor(1, PathfindingConstants.pathfindingThreads, 10, TimeUnit.SECONDS, jobQueue, new CitadelThreadFactory());
        }
        return executor;
    }

    public static class CitadelThreadFactory implements ThreadFactory {
        public static int id;

        @Override
        public Thread newThread(final @NotNull Runnable runnable) throws RuntimeException {
            ClassLoader classLoader;
            if (server == null || server.isSameThread()) {
                classLoader = Thread.currentThread().getContextClassLoader();
            } else {
                classLoader = server.getRunningThread().getContextClassLoader();
            }

            final Thread thread = new Thread(runnable, "Citadel Pathfinding Worker #" + (id++));
            thread.setDaemon(true);
            thread.setPriority(Thread.MAX_PRIORITY);
            if (thread.getContextClassLoader() != classLoader) {
                Citadel.LOGGER.info("Corrected CCL of new Citadel Pathfinding Thread, was: {}", thread.getContextClassLoader().toString());
                thread.setContextClassLoader(classLoader);
            }
            thread.setUncaughtExceptionHandler((thread1, throwable) -> Citadel.LOGGER.error("Citadel Pathfinding Thread errored! ", throwable));
            return thread;
        }
    }
}