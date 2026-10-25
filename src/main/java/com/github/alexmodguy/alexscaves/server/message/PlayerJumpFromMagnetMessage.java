package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.fabric.ACPayloadContext;
import com.github.alexmodguy.alexscaves.mixin.LivingEntityAccessor;
import com.github.alexmodguy.alexscaves.server.entity.util.MagnetUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class PlayerJumpFromMagnetMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PlayerJumpFromMagnetMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "player_jump_from_magnet"));

    public static final StreamCodec<FriendlyByteBuf, PlayerJumpFromMagnetMessage> CODEC =
            StreamCodec.ofMember(PlayerJumpFromMagnetMessage::write, PlayerJumpFromMagnetMessage::read);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    private final int entityID;
    private final boolean jumping;

    public PlayerJumpFromMagnetMessage(int entityID, boolean jumping) {
        this.entityID = entityID;
        this.jumping = jumping;
    }

    public static PlayerJumpFromMagnetMessage read(FriendlyByteBuf buf) {
        return new PlayerJumpFromMagnetMessage(buf.readInt(), buf.readBoolean());
    }

    public static void write(PlayerJumpFromMagnetMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.entityID);
        buf.writeBoolean(message.jumping);
    }

    public static void handle(PlayerJumpFromMagnetMessage message, ACPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player != null) {
                Entity entity = player.level().getEntity(message.entityID);
                if (MagnetUtil.isPulledByMagnets(entity) && entity instanceof LivingEntity living) {
                    ((LivingEntityAccessor) living).alexscaves$setJumping(message.jumping);
                }
            }
        });
    }
}