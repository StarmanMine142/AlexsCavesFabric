package com.github.alexmodguy.alexscaves.client.sound;

import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.Music;

public class ACMusics {

    public static final Music LUXTRUCTOSAURUS_BOSS_MUSIC = new Music(
            BuiltInRegistries.SOUND_EVENT.wrapAsHolder(ACSoundRegistry.LUXTRUCTOSAURUS_BOSS_MUSIC),
            0, 0, true
    );
}