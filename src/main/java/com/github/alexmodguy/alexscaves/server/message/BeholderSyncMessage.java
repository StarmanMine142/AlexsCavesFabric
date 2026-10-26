package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.ACPayloadContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class BeholderSyncMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BeholderSyncMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "beholder_sync"));

    public static final StreamCodec<FriendlyByteBuf, BeholderSyncMessage> CODEC =
            StreamCodec.ofMember(BeholderSyncMessage::write, BeholderSyncMessage::read);

    private final int beholderId;
    private final boolean active;
    private final double x, y, z;
    private final float yRot, xRot;
    private final UUID usingPlayerUUID;

    public BeholderSyncMessage(int beholderId, boolean active, double x, double y, double z, float yRot, float xRot, UUID usingPlayerUUID) {
        this.beholderId = beholderId;
        this.active = active;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yRot = yRot;
        this.xRot = xRot;
        this.usingPlayerUUID = usingPlayerUUID;
    }

    public BeholderSyncMessage(int beholderId, boolean active) {
        this(beholderId, active, 0, 0, 0, 0, 0, null);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static BeholderSyncMessage read(FriendlyByteBuf buf) {
        int beholderId = buf.readInt();
        boolean active = buf.readBoolean();
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        float yRot = buf.readFloat();
        float xRot = buf.readFloat();
        UUID uuid = buf.readBoolean() ? buf.readUUID() : null;
        return new BeholderSyncMessage(beholderId, active, x, y, z, yRot, xRot, uuid);
    }

    public static void write(BeholderSyncMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.beholderId);
        buf.writeBoolean(message.active);
        buf.writeDouble(message.x);
        buf.writeDouble(message.y);
        buf.writeDouble(message.z);
        buf.writeFloat(message.yRot);
        buf.writeFloat(message.xRot);
        buf.writeBoolean(message.usingPlayerUUID != null);
        if (message.usingPlayerUUID != null) {
            buf.writeUUID(message.usingPlayerUUID);
        }
    }

    public static void handle(BeholderSyncMessage message, ACPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> {
                AlexsCaves.handleBeholderSync(
                        message.beholderId, message.active,
                        message.x, message.y, message.z,
                        message.yRot, message.xRot, message.usingPlayerUUID
                );
            });
        }
    }
}