package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.citadel.item.BlockItemWithSupplier;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class BlockItemWithISTER extends BlockItemWithSupplier {

    public BlockItemWithISTER(Supplier<Block> blockSupplier, Properties props) {
        super(blockSupplier, props);
    }
}