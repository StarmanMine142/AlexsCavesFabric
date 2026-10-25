package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.util.GummyColors;
import com.github.alexmodguy.alexscaves.server.entity.util.HasGummyColors;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GummyColorLootFunction extends LootItemConditionalFunction {

    public static final MapCodec<GummyColorLootFunction> CODEC = RecordCodecBuilder.mapCodec(instance ->
        commonFields(instance).apply(instance, GummyColorLootFunction::new)
    );

    protected GummyColorLootFunction(List<LootItemCondition> conditions) {
        super(conditions);
    }

    @Override
    protected ItemStack run(ItemStack stack, @NotNull LootContext context) {
        if(stack.is(ACTagRegistry.GUMMY_ITEMS) && context.hasParam(LootContextParams.THIS_ENTITY) && context.getParam(LootContextParams.THIS_ENTITY) instanceof HasGummyColors hasGummyColors){
            GummyColors color = hasGummyColors.getGummyColor();
            Item replaceItem = stack.getItem();
            if(stack.is(ACItemRegistry.SWEETISH_FISH_RED_BUCKET) || stack.is(ACItemRegistry.SWEETISH_FISH_GREEN_BUCKET) || stack.is(ACItemRegistry.SWEETISH_FISH_YELLOW_BUCKET) || stack.is(ACItemRegistry.SWEETISH_FISH_BLUE_BUCKET) || stack.is(ACItemRegistry.SWEETISH_FISH_PINK_BUCKET)){
                switch (color){
                    case RED:
                        replaceItem = ACItemRegistry.SWEETISH_FISH_RED_BUCKET;
                        break;
                    case GREEN:
                        replaceItem = ACItemRegistry.SWEETISH_FISH_GREEN_BUCKET;
                        break;
                    case YELLOW:
                        replaceItem = ACItemRegistry.SWEETISH_FISH_YELLOW_BUCKET;
                        break;
                    case BLUE:
                        replaceItem = ACItemRegistry.SWEETISH_FISH_BLUE_BUCKET;
                        break;
                    case PINK:
                        replaceItem = ACItemRegistry.SWEETISH_FISH_PINK_BUCKET;
                        break;
                }
            }else if(stack.is(ACItemRegistry.SWEETISH_FISH_RED) || stack.is(ACItemRegistry.SWEETISH_FISH_GREEN) || stack.is(ACItemRegistry.SWEETISH_FISH_YELLOW) || stack.is(ACItemRegistry.SWEETISH_FISH_BLUE) || stack.is(ACItemRegistry.SWEETISH_FISH_PINK)){
                switch (color){
                    case RED:
                        replaceItem = ACItemRegistry.SWEETISH_FISH_RED;
                        break;
                    case GREEN:
                        replaceItem = ACItemRegistry.SWEETISH_FISH_GREEN;
                        break;
                    case YELLOW:
                        replaceItem = ACItemRegistry.SWEETISH_FISH_YELLOW;
                        break;
                    case BLUE:
                        replaceItem = ACItemRegistry.SWEETISH_FISH_BLUE;
                        break;
                    case PINK:
                        replaceItem = ACItemRegistry.SWEETISH_FISH_PINK;
                        break;
                }
            }else if(stack.is(ACItemRegistry.GELATIN_RED) || stack.is(ACItemRegistry.GELATIN_GREEN) || stack.is(ACItemRegistry.GELATIN_YELLOW) || stack.is(ACItemRegistry.GELATIN_BLUE) || stack.is(ACItemRegistry.GELATIN_PINK)){
                switch (color){
                    case RED:
                        replaceItem = ACItemRegistry.GELATIN_RED;
                        break;
                    case GREEN:
                        replaceItem = ACItemRegistry.GELATIN_GREEN;
                        break;
                    case YELLOW:
                        replaceItem = ACItemRegistry.GELATIN_YELLOW;
                        break;
                    case BLUE:
                        replaceItem = ACItemRegistry.GELATIN_BLUE;
                        break;
                    case PINK:
                        replaceItem = ACItemRegistry.GELATIN_PINK;
                        break;
                }
            }else if(stack.is(ACBlockRegistry.GUMMY_RING_RED.asItem()) || stack.is(ACBlockRegistry.GUMMY_RING_GREEN.asItem()) || stack.is(ACBlockRegistry.GUMMY_RING_YELLOW.asItem()) || stack.is(ACBlockRegistry.GUMMY_RING_BLUE.asItem()) || stack.is(ACBlockRegistry.GUMMY_RING_PINK.asItem())){
                switch (color){
                    case RED:
                        replaceItem = ACBlockRegistry.GUMMY_RING_RED.asItem();
                        break;
                    case GREEN:
                        replaceItem = ACBlockRegistry.GUMMY_RING_GREEN.asItem();
                        break;
                    case YELLOW:
                        replaceItem = ACBlockRegistry.GUMMY_RING_YELLOW.asItem();
                        break;
                    case BLUE:
                        replaceItem = ACBlockRegistry.GUMMY_RING_BLUE.asItem();
                        break;
                    case PINK:
                        replaceItem = ACBlockRegistry.GUMMY_RING_PINK.asItem();
                        break;
                }
            }
            return new ItemStack(replaceItem, stack.getCount());
        }
        return stack;
    }


    @Override
    public @NotNull LootItemFunctionType<GummyColorLootFunction> getType() {
        return ACLootTableRegistry.GUMMY_COLORS_LOOT_FUNCTION;
    }
}
