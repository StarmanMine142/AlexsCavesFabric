package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

public final class ACLootFunctions {

    public static final LootItemFunctionType<CaveBiomeLootFunction> CAVE_BIOME =
            Registry.register(
                    BuiltInRegistries.LOOT_FUNCTION_TYPE,
                    ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "cave_biome"),
                    new LootItemFunctionType<>(CaveBiomeLootFunction.CODEC)
            );

    private ACLootFunctions() {
    }
}