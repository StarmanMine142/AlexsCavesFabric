package com.github.alexmodguy.alexscaves.client.sound;

import com.github.alexmodguy.alexscaves.server.entity.living.TremorzillaEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class TremorzillaBeamSound extends AbstractTickableSoundInstance {
    private final TremorzillaEntity tremorzilla;

    public TremorzillaBeamSound(TremorzillaEntity tremorzilla) {
        super(ACSoundRegistry.TREMORZILLA_BEAM_LOOP, SoundSource.HOSTILE, SoundInstance.createUnseededRandom());
        this.tremorzilla = tremorzilla;
        this.attenuation = Attenuation.LINEAR;
        this.x = ((float)this.tremorzilla.getX());
        this.y = ((float)this.tremorzilla.getEyeY());
        this.z = ((float)this.tremorzilla.getZ());
        this.looping = true;
        this.delay = 0;
        this.volume = 0.0F;
    }

    public boolean canPlaySound() {
        return this.tremorzilla.isAlive() && !this.tremorzilla.isSilent() && tremorzilla.getBeamProgress(1.0F) > 0;
    }

    public void tick() {
        if (this.tremorzilla.isAlive() && this.tremorzilla.getBeamProgress(1.0F) > 0) {
            this.x = ((float)this.tremorzilla.getX());
            this.y = ((float)this.tremorzilla.getEyeY());
            this.z = ((float)this.tremorzilla.getZ());
            this.volume = tremorzilla.getBeamProgress(1.0F);
        } else {
            this.volume = 0;
            this.stop();
        }
    }

    public boolean canStartSilent() {
        return true;
    }

    public boolean isSameEntity(TremorzillaEntity tremorzilla) {
        return this.tremorzilla.isAlive() && this.tremorzilla.getId() == tremorzilla.getId();
    }
}
