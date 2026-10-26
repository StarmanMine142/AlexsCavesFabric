package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.ACPayloadContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class UpdateBossEruptionStatus implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UpdateBossEruptionStatus> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "update_boss_eruption_status"));

    public static final StreamCodec<FriendlyByteBuf, UpdateBossEruptionStatus> CODEC =
            StreamCodec.ofMember(UpdateBossEruptionStatus::write, UpdateBossEruptionStatus::read);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    private final int entityId;
    private final boolean erupting;

    public UpdateBossEruptionStatus(int entityId, boolean erupting) {
        this.entityId = entityId;
        this.erupting = erupting;
    }

    public static UpdateBossEruptionStatus read(FriendlyByteBuf buf) {
        return new UpdateBossEruptionStatus(buf.readInt(), buf.readBoolean());
    }

    public static void write(UpdateBossEruptionStatus message, FriendlyByteBuf buf) {
        buf.writeInt(message.entityId);
        buf.writeBoolean(message.erupting);
    }

    public static void handle(UpdateBossEruptionStatus message, ACPayloadContext context) {
        if (!context.flow().isClientbound()) {
            return;
        }
        context.enqueueWork(() -> {
            Player playerSided = context.player();
            if (playerSided != null) {
                AlexsCaves.setPrimordialBossActive(playerSided.level(), message.entityId, message.erupting);
            }
        });
    }
}