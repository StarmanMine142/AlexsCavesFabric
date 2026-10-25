package com.github.alexmodguy.alexscaves.server.block;

import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class CandyCaneBlock extends RotatedPillarBlock {

    public CandyCaneBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(2.0F, 3.0F).sound(ACSoundTypes.HARD_CANDY));
    }
}