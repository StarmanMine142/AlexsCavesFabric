package com.github.alexmodguy.alexscaves.server.misc;

import net.fabricmc.loader.api.FabricLoader;

public class ACLoadedMods {

    private static boolean distantHorizonsLoaded;
    private static boolean entityCullingLoaded;

    public static void afterAllModsLoaded(){
        distantHorizonsLoaded = FabricLoader.getInstance().isModLoaded("distanthorizons");
        entityCullingLoaded = FabricLoader.getInstance().isModLoaded("entityculling");
    }

    public static boolean isDistantHorizonsLoaded() {
        return distantHorizonsLoaded;
    }

    public static boolean isEntityCullingLoaded() {
        return entityCullingLoaded;
    }
}
