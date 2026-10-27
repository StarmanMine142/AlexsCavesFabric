package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CaveBookProgressSyncMessage(CompoundTag tag) implements CustomPacketPayload {

    public static final Type<CaveBookProgressSyncMessage> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "cave_book_progress"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CaveBookProgressSyncMessage> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.COMPOUND_TAG,
                    CaveBookProgressSyncMessage::tag,
                    CaveBookProgressSyncMessage::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}