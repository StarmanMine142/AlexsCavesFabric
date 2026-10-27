package com.github.alexmodguy.alexscaves.multipart;

import com.github.alexmodguy.alexscaves.server.entity.util.ACMultipartEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.MultiPartEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public final class ACMultipartServer {

    private ACMultipartServer() {
    }

    public static void registerCommon() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> addParts(world, entity));
        ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> removeParts(world, entity));
    }

    static void addParts(Level level, Entity entity) {
        if (!(entity instanceof MultiPartEntity multipart) || !multipart.acIsMultipartEntity()) {
            return;
        }
        Entity[] parts = multipart.getParts();
        if (parts == null) {
            return;
        }
        var partEntityMap = ((MultipartEntityLevel) level).alexscaves$getPartEntityMap();
        for (Entity part : parts) {
            if (part instanceof ACMultipartEntity<?> acPart) {
                partEntityMap.put(acPart.getId(), acPart);
            }
        }
    }

    static void removeParts(Level level, Entity entity) {
        if (!(entity instanceof MultiPartEntity multipart) || !multipart.acIsMultipartEntity()) {
            return;
        }
        Entity[] parts = multipart.getParts();
        if (parts == null) {
            return;
        }
        var partEntityMap = ((MultipartEntityLevel) level).alexscaves$getPartEntityMap();
        for (Entity part : parts) {
            partEntityMap.remove(part.getId());
        }
    }
}