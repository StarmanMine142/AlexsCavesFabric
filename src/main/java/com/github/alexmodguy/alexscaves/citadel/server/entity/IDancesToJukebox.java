package com.github.alexmodguy.alexscaves.citadel.server.entity;

import com.github.alexmodguy.alexscaves.citadel.server.message.DanceJukeboxMessage;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;

public interface IDancesToJukebox {

    void setDancing(boolean dancing);
    void setJukeboxPos(BlockPos pos);

    default void onClientPlayMusicDisc(int entityId, BlockPos pos, boolean dancing) {
        ClientPlayNetworking.send(new DanceJukeboxMessage(entityId, dancing, pos));
        this.setDancing(dancing);
        if (dancing) {
            this.setJukeboxPos(pos);
        } else {
            this.setJukeboxPos(null);
        }
    }
}