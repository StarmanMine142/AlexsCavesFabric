package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

public class ACLootTableRegistry {

    public static final LootItemFunctionType<GummyColorLootFunction> GUMMY_COLORS_LOOT_FUNCTION = Registry.register(
            BuiltInRegistries.LOOT_FUNCTION_TYPE,
            ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "gummy_colors"),
            new LootItemFunctionType<>(GummyColorLootFunction.CODEC)
    );

    public static final ResourceKey<LootTable> ABYSSAL_RUINS_CHEST = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "chests/abyssal_ruins"));
    public static final ResourceKey<LootTable> WITCH_HUT_CHEST = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "chests/witch_hut"));
    public static final ResourceKey<LootTable> LICOWITCH_TOWER_CHEST = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "chests/licowitch_tower"));
    public static final ResourceKey<LootTable> SECRET_LICOWITCH_TOWER_CHEST = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "chests/licowitch_tower_secret"));
    public static final ResourceKey<LootTable> GINGERBREAD_TOWN_CHEST = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "chests/gingerbread_town"));

    public static void registerLootTables() {
    }
}