package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.block.grower.AncientTreeGrower;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

/**
 * Custom sapling block for Ancient Trees that supports 3x3 mega tree growth.
 * When 9 saplings are placed in a 3x3 pattern and bonemealed, a giant ancient tree will grow.
 */
public class AncientSaplingBlock extends SaplingBlock {

    public AncientSaplingBlock(TreeGrower grower, Properties properties) {
        super(grower, properties);
    }

    @Override
    public void advanceTree(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        if (state.getValue(STAGE) == 0) {
            level.setBlock(pos, state.cycle(STAGE), 4);
        } else {
            // First, try to grow a 3x3 mega tree
            if (!tryGrowThreeByThreeMegaTree(level, pos, state, random)) {
                // If 3x3 pattern not found, fall back to normal tree growth
                this.treeGrower.growTree(level, level.getChunkSource().getGenerator(), pos, state, random);
            }
        }
    }

    /**
     * Attempts to grow a 3x3 mega tree. Checks all 9 possible positions where
     * this sapling could be part of a 3x3 pattern.
     *
     * @return true if a mega tree was successfully grown, false otherwise
     */
    private boolean tryGrowThreeByThreeMegaTree(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        Holder<ConfiguredFeature<?, ?>> holder = level.registryAccess()
                .registryOrThrow(Registries.CONFIGURED_FEATURE)
                .getHolder(AncientTreeGrower.GIANT_ANCIENT_TREE)
                .orElse(null);

        if (holder == null) {
            return false;
        }

        ChunkGenerator chunkGenerator = level.getChunkSource().getGenerator();

        // Check all 9 possible corner positions where this sapling could be the origin
        // The origin is the corner with the smallest x and z coordinates
        for (int xOffset = 0; xOffset >= -2; xOffset--) {
            for (int zOffset = 0; zOffset >= -2; zOffset--) {
                if (isThreeByThreeSapling(state, level, pos, xOffset, zOffset)) {
                    BlockPos originPos = pos.offset(xOffset, 0, zOffset);
                    ConfiguredFeature<?, ?> configuredFeature = holder.value();

                    // Clear all 9 saplings
                    BlockState airState = Blocks.AIR.defaultBlockState();
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            level.setBlock(originPos.offset(i, 0, j), airState, 4);
                        }
                    }

                    // Try to place the mega tree at the center of the 3x3 area
                    BlockPos treePos = originPos.offset(1, 0, 1);
                    if (configuredFeature.place(level, chunkGenerator, random, treePos)) {
                        return true;
                    }

                    // If tree generation failed, restore all saplings
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            level.setBlock(originPos.offset(i, 0, j), state, 4);
                        }
                    }
                    return false;
                }
            }
        }

        return false;
    }

    /**
     * Checks if there is a 3x3 pattern of the same sapling starting at the offset position.
     *
     * @param state   The block state of the sapling
     * @param level   The block getter
     * @param pos     The position of the bonemealed sapling
     * @param xOffset X offset to the potential origin corner
     * @param zOffset Z offset to the potential origin corner
     * @return true if a 3x3 pattern exists
     */
    private static boolean isThreeByThreeSapling(BlockState state, BlockGetter level, BlockPos pos, int xOffset, int zOffset) {
        Block block = state.getBlock();
        BlockPos originPos = pos.offset(xOffset, 0, zOffset);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (!level.getBlockState(originPos.offset(i, 0, j)).is(block)) {
                    return false;
                }
            }
        }
        return true;
    }
}
