package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.network.ACPayloadContext;
import com.github.alexmodguy.alexscaves.server.entity.util.ACAttachmentRegistry;
import com.github.alexmodguy.alexscaves.server.entity.util.MagneticEntityData;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/**
 * Message to sync magnetic entity data from server to client.
 * This replaces the automatic SynchedEntityData sync from 1.20.
 */
public class UpdateMagneticDataMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UpdateMagneticDataMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "update_magnetic_data"));

    public static final StreamCodec<FriendlyByteBuf, UpdateMagneticDataMessage> CODEC =
            StreamCodec.ofMember(UpdateMagneticDataMessage::write, UpdateMagneticDataMessage::read);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    private final int entityId;
    private final float deltaX;
    private final float deltaY;
    private final float deltaZ;
    private final Direction attachmentDirection;

    public UpdateMagneticDataMessage(int entityId, float deltaX, float deltaY, float deltaZ, Direction attachmentDirection) {
        this.entityId = entityId;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.deltaZ = deltaZ;
        this.attachmentDirection = attachmentDirection;
    }

    public UpdateMagneticDataMessage(Entity entity, MagneticEntityData data) {
        this(entity.getId(), data.getDeltaX(), data.getDeltaY(), data.getDeltaZ(), data.getAttachmentDirection());
    }

    public static UpdateMagneticDataMessage read(FriendlyByteBuf buf) {
        int entityId = buf.readInt();
        float deltaX = buf.readFloat();
        float deltaY = buf.readFloat();
        float deltaZ = buf.readFloat();
        Direction dir = buf.readEnum(Direction.class);
        return new UpdateMagneticDataMessage(entityId, deltaX, deltaY, deltaZ, dir);
    }

    public static void write(UpdateMagneticDataMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.entityId);
        buf.writeFloat(message.deltaX);
        buf.writeFloat(message.deltaY);
        buf.writeFloat(message.deltaZ);
        buf.writeEnum(message.attachmentDirection);
    }

    public static void handle(UpdateMagneticDataMessage message, ACPayloadContext context) {
        if (!context.flow().isClientbound()) {
            return;
        }
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player != null) {
                Entity entity = player.level().getEntity(message.entityId);
                if (entity != null) {
                    MagneticEntityData data = ACAttachmentRegistry.getMagneticData(entity);
                    if (data != null) {
                        data.setDeltaX(message.deltaX);
                        data.setDeltaY(message.deltaY);
                        data.setDeltaZ(message.deltaZ);
                        data.setAttachmentDirection(message.attachmentDirection);
                    }
                }
            }
        });
    }
}