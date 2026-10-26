package com.github.alexmodguy.alexscaves.fabric;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;

public final class ACMultipartFabricClient {

    private ACMultipartFabricClient() {
    }

    public static void registerClient() {
        ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> ACMultipartFabric.addParts(world, entity));
        ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> ACMultipartFabric.removeParts(world, entity));
    }
}
