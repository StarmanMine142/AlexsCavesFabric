package com.github.alexmodguy.alexscaves.server.block.poi;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Set;

public class ACPOIRegistry {

    public static final PoiType ATTRACTING_MAGNETS = register("attracting_magnets", new PoiType(getAllAttractingMagnets(), 32, 6));
    public static final PoiType REPELLING_MAGNETS = register("repelling_magnets", new PoiType(getAllRepellingMagnets(), 32, 6));
    public static final PoiType NUCLEAR_SIREN = register("nuclear_siren", new PoiType(getAllStatesOf(ACBlockRegistry.NUCLEAR_SIREN), 0, 6));
    public static final PoiType NUCLEAR_FURNACE = register("nuclear_furnace", new PoiType(getAllStatesOf(ACBlockRegistry.NUCLEAR_FURNACE), 0, 6));
    public static final PoiType ABYSSAL_ALTAR = register("abyssal_altar", new PoiType(getAllStatesOf(ACBlockRegistry.ABYSSAL_ALTAR), 0, 6));
    public static final PoiType MOTH_BALL = register("moth_ball", new PoiType(getAllStatesOf(ACBlockRegistry.MOTH_BALL), 32, 6));
    public static final PoiType SUNDROP = register("sundrop", new PoiType(getAllStatesOf(ACBlockRegistry.SUNDROP), 32, 6));
    public static final PoiType CONVERSION_CRUCIBLE = register("conversion_crucible", new PoiType(getAllStatesOf(ACBlockRegistry.CONVERSION_CRUCIBLE), 0, 6));
    public static final PoiType GINGERBARREL = register("gingerbarrel", new PoiType(getAllStatesOf(ACBlockRegistry.GINGERBARREL), 0, 6));

    private static final List<PoiType> ALL = List.of(
            ATTRACTING_MAGNETS,
            REPELLING_MAGNETS,
            NUCLEAR_SIREN,
            NUCLEAR_FURNACE,
            ABYSSAL_ALTAR,
            MOTH_BALL,
            SUNDROP,
            CONVERSION_CRUCIBLE,
            GINGERBARREL
    );

    private static PoiType register(String name, PoiType poiType) {
        return Registry.register(BuiltInRegistries.POINT_OF_INTEREST_TYPE, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, name), poiType);
    }

    private static boolean fabricBlockStatesRegistered = false;

    public static void registerFabricBlockStates() {
        if (fabricBlockStatesRegistered) {
            return;
        }
        fabricBlockStatesRegistered = true;
        for (PoiType poiType : ALL) {
            Holder<PoiType> holder = BuiltInRegistries.POINT_OF_INTEREST_TYPE.wrapAsHolder(poiType);
            PoiTypes.registerBlockStates(holder, poiType.matchingStates());
        }
    }

    public static void register() {
        registerFabricBlockStates();
    }

    private static Set<BlockState> getAllAttractingMagnets() {
        ImmutableSet.Builder<BlockState> builder = ImmutableSet.builder();
        builder.addAll(getAllStatesOf(ACBlockRegistry.SCARLET_NEODYMIUM_NODE));
        builder.addAll(getAllStatesOf(ACBlockRegistry.SCARLET_NEODYMIUM_PILLAR));
        builder.addAll(getAllStatesOf(ACBlockRegistry.BLOCK_OF_SCARLET_NEODYMIUM));
        return builder.build();
    }

    private static Set<BlockState> getAllRepellingMagnets() {
        ImmutableSet.Builder<BlockState> builder = ImmutableSet.builder();
        builder.addAll(getAllStatesOf(ACBlockRegistry.AZURE_NEODYMIUM_NODE));
        builder.addAll(getAllStatesOf(ACBlockRegistry.AZURE_NEODYMIUM_PILLAR));
        builder.addAll(getAllStatesOf(ACBlockRegistry.BLOCK_OF_AZURE_NEODYMIUM));
        return builder.build();
    }

    private static Set<BlockState> getAllStatesOf(Block block) {
        return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
    }
}