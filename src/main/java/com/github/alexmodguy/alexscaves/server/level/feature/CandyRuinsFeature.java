package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.level.feature.config.UndergroundRuinsFeatureConfiguration;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class CandyRuinsFeature extends UndergroundRuinsFeature {

    public CandyRuinsFeature(Codec<UndergroundRuinsFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    protected boolean canGenerateAt(WorldGenLevel level, BlockPos blockpos) {
        return level.getBlockState(blockpos).is(ACBlockRegistry.BLOCK_OF_CHOCOLATE) || level.getBlockState(blockpos).is(ACBlockRegistry.BLOCK_OF_FROSTED_CHOCOLATE);
    }

    protected int calculateSinkBy(WorldGenLevel level, BlockPos blockpos1, StructureTemplate structuretemplate, int sinkByIn) {
        return 1 + level.getRandom().nextInt(sinkByIn + 1);
    }
}
