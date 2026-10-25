package com.github.alexmodguy.alexscaves.server.entity.util;

import net.minecraft.world.entity.Entity;

public interface MultiPartEntity {

    default boolean isMultipartEntity() {
        return false;
    }

    default Entity[] getParts() {
        return null;
    }
}