package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.fabric.ACPayloadContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class SpelunkeryTableCompleteTutorialMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SpelunkeryTableCompleteTutorialMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "spelunkery_table_complete_tutorial"));

    public static final StreamCodec<FriendlyByteBuf, SpelunkeryTableCompleteTutorialMessage> CODEC =
            StreamCodec.ofMember(SpelunkeryTableCompleteTutorialMessage::write, SpelunkeryTableCompleteTutorialMessage::read);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public boolean completedTutorial;

    public SpelunkeryTableCompleteTutorialMessage(boolean completedTutorial) {
        this.completedTutorial = completedTutorial;
    }

    public SpelunkeryTableCompleteTutorialMessage() {
    }

    public static SpelunkeryTableCompleteTutorialMessage read(FriendlyByteBuf buf) {
        return new SpelunkeryTableCompleteTutorialMessage(buf.readBoolean());
    }

    public static void write(SpelunkeryTableCompleteTutorialMessage message, FriendlyByteBuf buf) {
        buf.writeBoolean(message.completedTutorial);
    }

    public static void handle(SpelunkeryTableCompleteTutorialMessage message, ACPayloadContext context) {
        // This packet is sent from server to client
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> {
                Player player = context.player();
                if (player != null) {
                    AlexsCaves.PROXY.setSpelunkeryTutorialComplete(message.completedTutorial);
                }
            });
        }
    }
}