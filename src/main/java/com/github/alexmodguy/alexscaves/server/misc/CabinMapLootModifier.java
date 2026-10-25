package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ExplorationMapFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;

public final class CabinMapLootModifier {

    private CabinMapLootModifier() {
    }

    public static void register() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (source.isBuiltin() && BuiltInLootTables.ABANDONED_MINESHAFT.equals(key)) {
                LootPool.Builder pool = LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.MAP)
                                .apply(ExplorationMapFunction.makeExplorationMap()
                                        .setDestination(ACTagRegistry.ON_UNDERGROUND_CABIN_MAPS)
                                        .setMapDecoration(ACVanillaMapUtil.getUndergroundCabinDecoration())
                                        .setZoom((byte) 2)
                                        .setSearchRadius(100)
                                        .setSkipKnownStructures(true)
                                )
                        )
                        .when(LootItemRandomChanceCondition.randomChance(
                                (float) AlexsCaves.COMMON_CONFIG.cabinMapLootChance
                        ));
                tableBuilder.withPool(pool);
            }
        });
    }
}