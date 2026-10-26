package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.ACPayloadContext;
import com.github.alexmodguy.alexscaves.server.entity.item.BeholderEyeEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class BeholderRotateMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BeholderRotateMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "beholder_rotate"));

    public static final StreamCodec<FriendlyByteBuf, BeholderRotateMessage> CODEC =
            StreamCodec.ofMember(BeholderRotateMessage::write, BeholderRotateMessage::read);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public int beholderId;
    public float rotX;
    public float rotY;

    public BeholderRotateMessage(int beholderId, float rotX, float rotY) {
        this.beholderId = beholderId;
        this.rotX = rotX;
        this.rotY = rotY;
    }

    public BeholderRotateMessage() {
    }

    public static BeholderRotateMessage read(FriendlyByteBuf buf) {
        return new BeholderRotateMessage(buf.readInt(), buf.readFloat(), buf.readFloat());
    }

    public static void write(BeholderRotateMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.beholderId);
        buf.writeFloat(message.rotX);
        buf.writeFloat(message.rotY);
    }

    public static void handle(BeholderRotateMessage message, ACPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player != null) {
                Level level = player.level();
                Entity watcher = level.getEntity(message.beholderId);
                if (watcher instanceof BeholderEyeEntity beholderEye) {
                    beholderEye.setEyeXRot(message.rotX);
                    beholderEye.setEyeYRot(message.rotY);
                }
            }
        });
    }
}