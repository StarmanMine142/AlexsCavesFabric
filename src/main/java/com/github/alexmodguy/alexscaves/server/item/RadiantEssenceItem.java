package com.github.alexmodguy.alexscaves.server.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class RadiantEssenceItem extends Item {

    public RadiantEssenceItem() {
        super(new Item.Properties().rarity(ACItemRegistry.getRarityRainbow()));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        if(this == ACItemRegistry.LICOWITCH_RADIANT_ESSENCE){
            tooltip.add(Component.translatable("item.alexscaves.licowitch_radiant_essence.desc").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
        }
        super.appendHoverText(stack, context, tooltip, flagIn);
    }
}
