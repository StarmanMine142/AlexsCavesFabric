package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.GummyRingBlock;
import com.github.alexmodguy.alexscaves.server.misc.ACFluidHelper;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class FloatingGummyRingFeature extends Feature<NoneFeatureConfiguration> {

    public FloatingGummyRingFeature(Codec<NoneFeatureConfiguration> config) {
        super(config);
    }

    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel worldgenlevel = context.level();
        RandomSource randomsource = context.random();
        boolean aboveSoda = false;
        BlockPos.MutableBlockPos scanPos = new BlockPos.MutableBlockPos();
        for (scanPos.set(context.origin()); scanPos.getY() > worldgenlevel.getMinBuildHeight(); scanPos.move(0, -1, 0)) {
            if (ACFluidHelper.isPurpleSoda(worldgenlevel, scanPos)) {
                aboveSoda = true;
                break;
            }
        }
        if (aboveSoda) {
            boolean sunk = randomsource.nextBoolean();
            if(sunk){
                while(scanPos.getY() > worldgenlevel.getMinBuildHeight() && ACFluidHelper.isPurpleSoda(worldgenlevel, scanPos)){
                    scanPos.move(0, -1, 0);
                }
            }
            scanPos.move(0, 1, 0);
            if(worldgenlevel.getBlockState(scanPos).canBeReplaced()){
                BlockState state;
                switch (randomsource.nextInt(4)){
                    case 4:
                        state = ACBlockRegistry.GUMMY_RING_PINK.defaultBlockState();
                        break;
                    case 3:
                        state = ACBlockRegistry.GUMMY_RING_BLUE.defaultBlockState();
                        break;
                    case 2:
                        state = ACBlockRegistry.GUMMY_RING_YELLOW.defaultBlockState();
                        break;
                    case 1:
                        state = ACBlockRegistry.GUMMY_RING_GREEN.defaultBlockState();
                        break;
                    default:
                        state = ACBlockRegistry.GUMMY_RING_RED.defaultBlockState();
                        break;
                }
                if(sunk){
                    state = state.setValue(GummyRingBlock.LIQUID_LOGGED, 2);
                }else{
                    state = state.setValue(GummyRingBlock.FLOATING, true);
                }
                worldgenlevel.setBlock(scanPos, state, 3);
                return true;
            }
        }
        return false;
    }

}
