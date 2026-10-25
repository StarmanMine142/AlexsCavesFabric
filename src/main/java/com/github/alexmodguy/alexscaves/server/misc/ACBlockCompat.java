package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.server.block.RadrockUraniumOreBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class ACBlockCompat {

    private ACBlockCompat() {
    }

    public static void explodeBlock(Level level, BlockPos pos, BlockState state, Explosion explosion) {
        level.removeBlock(pos, false);
        state.getBlock().wasExploded(level, pos, explosion);
    }

    public static void awardExperience(ServerLevel level, BlockPos pos, int amount) {
        if (amount > 0) {
            ExperienceOrb.award(level, Vec3.atCenterOf(pos), amount);
        }
    }

    public static int getExpDrop(BlockState state, Level level, BlockPos pos, Entity breaker, ItemStack tool) {
        if (state.getBlock() instanceof RadrockUraniumOreBlock oreBlock) {
            return oreBlock.getExpDrop(state, level, pos, level.getBlockEntity(pos), breaker, tool);
        }
        return 0;
    }

    public static boolean isStickyBlock(BlockState state) {
        return state.is(Blocks.SLIME_BLOCK) || state.is(Blocks.HONEY_BLOCK);
    }

    public static boolean canStickTo(BlockState state, BlockState other) {
        if (state.is(Blocks.SLIME_BLOCK) && other.is(Blocks.HONEY_BLOCK)) {
            return false;
        }
        if (state.is(Blocks.HONEY_BLOCK) && other.is(Blocks.SLIME_BLOCK)) {
            return false;
        }
        return isStickyBlock(state);
    }
}
