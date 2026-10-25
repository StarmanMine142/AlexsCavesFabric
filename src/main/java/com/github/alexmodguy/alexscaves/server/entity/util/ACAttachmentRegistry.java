package com.github.alexmodguy.alexscaves.server.entity.util;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class ACAttachmentRegistry {

    private static final Map<Entity, MagneticEntityData> MAGNETIC_DATA_STORE = Collections.synchronizedMap(new WeakHashMap<>());

    public static final AttachmentType<MagneticEntityData> MAGNETIC_DATA = AttachmentRegistry.create(
            ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "magnetic_data"),
            builder -> builder
                    .persistent(MagneticEntityData.CODEC)
                    .copyOnDeath()
                    .initializer(() -> new MagneticEntityData(0f, 0f, 0f, Direction.DOWN))
    );

    public static MagneticEntityData getMagneticData(Entity entity) {
        return MAGNETIC_DATA_STORE.computeIfAbsent(entity, ignored -> MagneticEntityData.DEFAULT.copy());
    }
}