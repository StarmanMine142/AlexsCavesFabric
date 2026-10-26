package com.github.alexmodguy.alexscaves.fabric;

import com.github.alexmodguy.alexscaves.server.entity.util.ACMultipartEntity;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

public interface MultipartEntityLevel {
    Int2ObjectMap<ACMultipartEntity<?>> alexscaves$getPartEntityMap();
}