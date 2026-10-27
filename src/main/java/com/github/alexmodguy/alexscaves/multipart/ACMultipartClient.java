package com.github.alexmodguy.alexscaves.multipart;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;

public final class ACMultipartClient {

    private ACMultipartClient() {
    }

    public static void registerClient() {
        ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> ACMultipartServer.addParts(world, entity));
        ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> ACMultipartServer.removeParts(world, entity));
    }
}
