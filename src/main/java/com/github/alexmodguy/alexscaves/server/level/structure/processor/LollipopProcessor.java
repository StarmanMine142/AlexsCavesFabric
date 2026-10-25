package com.github.alexmodguy.alexscaves.server.level.structure.processor;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;

public class LollipopProcessor extends StructureProcessor {

    private static Block[][] BLOCK_COLOR_PALETTES = new Block[][]{
            {ACBlockRegistry.LIGHT_BLUE_ROCK_CANDY, ACBlockRegistry.GREEN_ROCK_CANDY, ACBlockRegistry.ORANGE_ROCK_CANDY, ACBlockRegistry.RED_ROCK_CANDY},
            {ACBlockRegistry.YELLOW_ROCK_CANDY, ACBlockRegistry.PINK_ROCK_CANDY, ACBlockRegistry.BLUE_ROCK_CANDY, ACBlockRegistry.RED_ROCK_CANDY},
            {ACBlockRegistry.WHITE_ROCK_CANDY, ACBlockRegistry.CYAN_ROCK_CANDY, ACBlockRegistry.GREEN_ROCK_CANDY, ACBlockRegistry.YELLOW_ROCK_CANDY},
            {ACBlockRegistry.WHITE_ROCK_CANDY, ACBlockRegistry.CYAN_ROCK_CANDY, ACBlockRegistry.GREEN_ROCK_CANDY, ACBlockRegistry.YELLOW_ROCK_CANDY},
            {ACBlockRegistry.PINK_ROCK_CANDY, ACBlockRegistry.MAGENTA_ROCK_CANDY, ACBlockRegistry.LIGHT_BLUE_ROCK_CANDY, ACBlockRegistry.BLUE_ROCK_CANDY},
            {ACBlockRegistry.RED_ROCK_CANDY, ACBlockRegistry.PINK_ROCK_CANDY, ACBlockRegistry.MAGENTA_ROCK_CANDY, ACBlockRegistry.PURPLE_ROCK_CANDY},
            {ACBlockRegistry.WHITE_ROCK_CANDY, ACBlockRegistry.PINK_ROCK_CANDY, ACBlockRegistry.MAGENTA_ROCK_CANDY, ACBlockRegistry.PURPLE_ROCK_CANDY},
            {ACBlockRegistry.WHITE_ROCK_CANDY, ACBlockRegistry.PINK_ROCK_CANDY, ACBlockRegistry.MAGENTA_ROCK_CANDY, ACBlockRegistry.PURPLE_ROCK_CANDY},
            {ACBlockRegistry.WHITE_ROCK_CANDY, ACBlockRegistry.YELLOW_ROCK_CANDY, ACBlockRegistry.ORANGE_ROCK_CANDY, ACBlockRegistry.RED_ROCK_CANDY},
            {ACBlockRegistry.WHITE_ROCK_CANDY, ACBlockRegistry.CYAN_ROCK_CANDY, ACBlockRegistry.LIGHT_BLUE_ROCK_CANDY, ACBlockRegistry.BLUE_ROCK_CANDY},
            {ACBlockRegistry.WHITE_ROCK_CANDY, ACBlockRegistry.CYAN_ROCK_CANDY, ACBlockRegistry.LIME_ROCK_CANDY, ACBlockRegistry.GREEN_ROCK_CANDY},
            {ACBlockRegistry.YELLOW_ROCK_CANDY, ACBlockRegistry.CYAN_ROCK_CANDY, ACBlockRegistry.PINK_ROCK_CANDY, ACBlockRegistry.PURPLE_ROCK_CANDY},
            {ACBlockRegistry.PINK_ROCK_CANDY, ACBlockRegistry.BLUE_ROCK_CANDY, ACBlockRegistry.PURPLE_ROCK_CANDY, ACBlockRegistry.BLACK_ROCK_CANDY},
    };

    public static final MapCodec<LollipopProcessor> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(Codec.INT.fieldOf("palette_index").forGetter((p_230289_) -> {
            return p_230289_.paletteIndex;
        })).apply(instance, LollipopProcessor::new);
    });

    private final int paletteIndex;

    public LollipopProcessor(int paletteIndex) {
        this.paletteIndex = paletteIndex;
    }

    public LollipopProcessor(RandomSource randomSource){
        this(randomSource.nextInt(BLOCK_COLOR_PALETTES.length - 1));
    }

    @Nullable
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader levelReader, BlockPos blockPosUnused, BlockPos pos, StructureTemplate.StructureBlockInfo relativeInfo, StructureTemplate.StructureBlockInfo info, StructurePlaceSettings settings) {
        BlockState in = info.state();
        int clampedPaletteIndex = Mth.clamp(paletteIndex, 0, BLOCK_COLOR_PALETTES.length);
        if(in.is(ACBlockRegistry.BLACK_ROCK_CANDY)){ // color palette 0
            return new StructureTemplate.StructureBlockInfo(info.pos(), BLOCK_COLOR_PALETTES[clampedPaletteIndex][0].defaultBlockState(), info.nbt());
        }else if(in.is(ACBlockRegistry.GRAY_ROCK_CANDY)){ // color palette 1
            return new StructureTemplate.StructureBlockInfo(info.pos(), BLOCK_COLOR_PALETTES[clampedPaletteIndex][1].defaultBlockState(), info.nbt());
        }else if(in.is(ACBlockRegistry.LIGHT_GRAY_ROCK_CANDY)){ // color palette 2
            return new StructureTemplate.StructureBlockInfo(info.pos(), BLOCK_COLOR_PALETTES[clampedPaletteIndex][2].defaultBlockState(), info.nbt());
        }else if(in.is(ACBlockRegistry.WHITE_ROCK_CANDY)){ // color palette 3
            return new StructureTemplate.StructureBlockInfo(info.pos(), BLOCK_COLOR_PALETTES[clampedPaletteIndex][3].defaultBlockState(), info.nbt());
        }
        return info;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return ACStructureProcessorRegistry.LOLLIPOP;
    }
}
