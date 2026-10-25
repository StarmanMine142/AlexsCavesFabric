package com.github.alexmodguy.alexscaves.server.misc;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public class CaveBiomeLootFunction extends LootItemConditionalFunction {

    public static final MapCodec<CaveBiomeLootFunction> CODEC =
            RecordCodecBuilder.mapCodec(inst ->
                    commonFields(inst).and(
                            ResourceKey.codec(Registries.BIOME).fieldOf("biome").forGetter(f -> f.biome)
                    ).apply(inst, CaveBiomeLootFunction::new));

    private final ResourceKey<Biome> biome;

    protected CaveBiomeLootFunction(List<LootItemCondition> conditions, ResourceKey<Biome> biome) {
        super(conditions);
        this.biome = biome;
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        CompoundTag tag = new CompoundTag();
        tag.putString("CaveBiome", biome.location().toString());
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, CustomData.of(tag));
        stack.set(com.github.alexmodguy.alexscaves.server.misc.ACDataComponentRegistry.CAVE_BIOME, biome);
        return stack;
    }

    @Override
    public LootItemFunctionType<CaveBiomeLootFunction> getType() {
        return ACLootFunctions.CAVE_BIOME;
    }

    public static LootItemConditionalFunction.Builder<?> create(ResourceKey<Biome> biome) {
        return simpleBuilder(conditions -> new CaveBiomeLootFunction(conditions, biome));
    }
}