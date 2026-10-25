package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.citadel.item.BlockItemWithSupplier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Supplier;

public class BlockItemWithSupplierLore extends BlockItemWithSupplier {

    private final Supplier<Block> blockSupplier;

    public BlockItemWithSupplierLore(Supplier<Block> blockSupplier, Properties props) {
        super(blockSupplier, props);
        this.blockSupplier = blockSupplier;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        Block block = blockSupplier.get();
        if (block != null) {
            ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
            String blockName = blockId.getNamespace() + "." + blockId.getPath();
            tooltip.add(Component.translatable("block." + blockName + ".desc").withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, context, tooltip, flagIn);
    }
}