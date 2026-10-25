package com.github.alexmodguy.alexscaves.server.misc;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public final class ACRuntimeData {
    private static final Map<Entity, CompoundTag> ENTITY_DATA = Collections.synchronizedMap(new WeakHashMap<>());

    private ACRuntimeData() {
    }

    public static CompoundTag getOrCreate(Entity entity) {
        return ENTITY_DATA.computeIfAbsent(entity, ignored -> new CompoundTag());
    }
}
