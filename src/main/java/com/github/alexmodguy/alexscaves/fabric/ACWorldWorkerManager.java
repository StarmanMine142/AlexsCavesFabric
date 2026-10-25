package com.github.alexmodguy.alexscaves.fabric;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class ACWorldWorkerManager {

    private static final List<TickableWorker> WORKERS = new ArrayList<>();
    private static boolean initialized = false;

    private ACWorldWorkerManager() {
    }

    public static void registerCommon() {
        if (initialized) return;
        initialized = true;
        ServerTickEvents.END_SERVER_TICK.register(server -> tick());
    }

    public static void addWorker(TickableWorker worker) {
        WORKERS.add(worker);
    }

    private static void tick() {
        Iterator<TickableWorker> iter = WORKERS.iterator();
        while (iter.hasNext()) {
            TickableWorker worker = iter.next();
            try {
                if (worker.hasWork()) {
                    worker.doWork();
                }
                if (!worker.hasWork()) {
                    iter.remove();
                }
            } catch (Throwable t) {
                AlexsCaves.LOGGER.error("World worker failed", t);
                iter.remove();
            }
        }
    }

    public interface TickableWorker {
        boolean hasWork();

        boolean doWork();
    }
}