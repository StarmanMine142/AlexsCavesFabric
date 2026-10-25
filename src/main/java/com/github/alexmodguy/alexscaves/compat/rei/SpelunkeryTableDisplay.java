package com.github.alexmodguy.alexscaves.compat.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.List;

public class SpelunkeryTableDisplay implements Display {

    private final ResourceKey<Biome> biome;
    private final EntryIngredient inputTablet;
    private final EntryIngredient inputPaper;
    private final EntryIngredient outputCodex;

    public SpelunkeryTableDisplay(ResourceKey<Biome> biome) {
        this.biome = biome;
        this.inputTablet = EntryIngredient.of(me.shedaniel.rei.api.common.util.EntryStacks.of(
                com.github.alexmodguy.alexscaves.server.item.CaveInfoItem.create(
                        com.github.alexmodguy.alexscaves.server.item.ACItemRegistry.CAVE_TABLET, biome)));
        this.inputPaper = EntryIngredient.of(me.shedaniel.rei.api.common.util.EntryStacks.of(
                new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.PAPER)));
        this.outputCodex = EntryIngredient.of(me.shedaniel.rei.api.common.util.EntryStacks.of(
                com.github.alexmodguy.alexscaves.server.item.CaveInfoItem.create(
                        com.github.alexmodguy.alexscaves.server.item.ACItemRegistry.CAVE_CODEX, biome)));
    }

    public ResourceKey<Biome> getBiome() {
        return biome;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(inputTablet, inputPaper);
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(outputCodex);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return SpelunkeryTableCategory.CATEGORY;
    }
}