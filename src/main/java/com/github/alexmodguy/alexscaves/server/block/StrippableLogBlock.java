package com.github.alexmodguy.alexscaves.server.block;

import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.world.level.block.RotatedPillarBlock;

public class StrippableLogBlock extends RotatedPillarBlock {

    public StrippableLogBlock(Properties properties) {
        super(properties);
    }

    public static void registerStrippables() {
        StrippableBlockRegistry.register(ACBlockRegistry.PEWEN_LOG, ACBlockRegistry.STRIPPED_PEWEN_LOG);
        StrippableBlockRegistry.register(ACBlockRegistry.PEWEN_WOOD, ACBlockRegistry.STRIPPED_PEWEN_WOOD);
        StrippableBlockRegistry.register(ACBlockRegistry.THORNWOOD_LOG, ACBlockRegistry.STRIPPED_THORNWOOD_LOG);
        StrippableBlockRegistry.register(ACBlockRegistry.THORNWOOD_WOOD, ACBlockRegistry.STRIPPED_THORNWOOD_WOOD);
    }
}