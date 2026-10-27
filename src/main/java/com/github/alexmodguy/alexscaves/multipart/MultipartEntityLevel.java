package com.github.alexmodguy.alexscaves.multipart;

import com.github.alexmodguy.alexscaves.server.entity.util.ACMultipartEntity;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

public interface MultipartEntityLevel {
    Int2ObjectMap<ACMultipartEntity<?>> alexscaves$getPartEntityMap();
}