package com.github.alexmodguy.alexscaves.server.entity;

import com.github.alexmodguy.alexscaves.server.entity.util.GummyColors;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class ACEntityDataRegistry {

    private static boolean fabricSerializersRegistered = false;

    public static final EntityDataSerializer<Optional<Vec3>> OPTIONAL_VEC_3 = EntityDataSerializer.forValueType(ACMath.OPTIONAL_VEC3_STREAM_CODEC);
    public static final EntityDataSerializer<GummyColors> GUMMY_COLOR = EntityDataSerializer.forValueType(GummyColors.STREAM_CODEC);

    public static void register() {
        if (fabricSerializersRegistered) {
            return;
        }
        fabricSerializersRegistered = true;
        EntityDataSerializers.registerSerializer(OPTIONAL_VEC_3);
        EntityDataSerializers.registerSerializer(GUMMY_COLOR);
    }
}