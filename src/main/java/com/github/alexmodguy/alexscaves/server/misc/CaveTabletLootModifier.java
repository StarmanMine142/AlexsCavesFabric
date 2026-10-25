package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;

import java.util.List;
import java.util.function.DoubleSupplier;

public final class CaveTabletLootModifier {

    private CaveTabletLootModifier() {
    }

    public static void register() {
        ACLootFunctions.CAVE_BIOME.getClass();

        register(BuiltInLootTables.BASTION_TREASURE,
                ACBiomeRegistry.MAGNETIC_CAVES,
                () -> AlexsCaves.COMMON_CONFIG.magneticTabletLootChance);

        for (ResourceKey<LootTable> archaeology : List.of(
                BuiltInLootTables.DESERT_WELL_ARCHAEOLOGY,
                BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY,
                BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_COMMON,
                BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_RARE,
                BuiltInLootTables.OCEAN_RUIN_WARM_ARCHAEOLOGY,
                BuiltInLootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY
        )) {
            register(archaeology,
                    ACBiomeRegistry.PRIMORDIAL_CAVES,
                    () -> AlexsCaves.COMMON_CONFIG.primordialTabletLootChance);
        }

        register(BuiltInLootTables.JUNGLE_TEMPLE,
                ACBiomeRegistry.TOXIC_CAVES,
                () -> AlexsCaves.COMMON_CONFIG.toxicTabletLootChance);

        register(BuiltInLootTables.UNDERWATER_RUIN_BIG,
                ACBiomeRegistry.ABYSSAL_CHASM,
                () -> AlexsCaves.COMMON_CONFIG.abyssalTabletLootChance);
        register(BuiltInLootTables.UNDERWATER_RUIN_SMALL,
                ACBiomeRegistry.ABYSSAL_CHASM,
                () -> AlexsCaves.COMMON_CONFIG.abyssalTabletLootChance);

        register(BuiltInLootTables.WOODLAND_MANSION,
                ACBiomeRegistry.FORLORN_HOLLOWS,
                () -> AlexsCaves.COMMON_CONFIG.forlornTabletLootChance);
    }

    private static void register(ResourceKey<LootTable> lootTable,
                                 ResourceKey<Biome> biome,
                                 DoubleSupplier chance) {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (source.isBuiltin() && key.equals(lootTable)) {
                addTablet(tableBuilder, biome, (float) chance.getAsDouble());
            }
        });
    }

    private static void addTablet(LootTable.Builder builder,
                                  ResourceKey<Biome> biome,
                                  float chance) {
        LootPool.Builder pool = LootPool.lootPool()
                .add(LootItem.lootTableItem(ACItemRegistry.CAVE_TABLET)
                        .apply(CaveBiomeLootFunction.create(biome)))
                .when(LootItemRandomChanceCondition.randomChance(chance));
        builder.withPool(pool);
    }
}