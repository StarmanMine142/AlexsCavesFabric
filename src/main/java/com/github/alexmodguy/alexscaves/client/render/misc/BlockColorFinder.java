package com.github.alexmodguy.alexscaves.client.render.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class BlockColorFinder {

    public static final Object2IntMap<String> TEXTURES_TO_COLOR = new Object2IntOpenHashMap<>();

    public static int getBlockColor(BlockState blockState, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos) {
        String blockName = blockState.toString();
        int colorizer = -1;
        if(!blockState.is(ACBlockRegistry.BLOCK_OF_FROSTED_CHOCOLATE)){
            try{
                colorizer = Minecraft.getInstance().getBlockColors().getColor(blockState, level, pos, 0);
            }catch (Exception e){
                AlexsCaves.LOGGER.warn("Another mod did not use block colorizers correctly.");
            }
        }
        if (TEXTURES_TO_COLOR.containsKey(blockName)) {
            if(colorizer == -1){
                return TEXTURES_TO_COLOR.getInt(blockName);
            }else{
                return colorizer;
            }
        } else {
            int color = 0XFFFFFF;
            if(colorizer == -1){
                color = 0XFFFFFF;
            }else{
                color = colorizer;
            }
            TEXTURES_TO_COLOR.put(blockName, color);
            return color;
        }
    }
}
