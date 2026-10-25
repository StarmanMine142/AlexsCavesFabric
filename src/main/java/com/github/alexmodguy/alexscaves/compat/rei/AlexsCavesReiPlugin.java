package com.github.alexmodguy.alexscaves.compat.rei;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.NuclearFurnaceBlockEntity;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACDataComponentRegistry;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public class AlexsCavesReiPlugin implements REIClientPlugin {

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new SpelunkeryTableCategory());
        registry.add(new NuclearFurnaceCategory());
        registry.addWorkstations(SpelunkeryTableCategory.CATEGORY,
                EntryStacks.of(ACBlockRegistry.SPELUNKERY_TABLE));
        registry.addWorkstations(NuclearFurnaceCategory.CATEGORY,
                EntryStacks.of(ACBlockRegistry.NUCLEAR_FURNACE_COMPONENT));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        ACBiomeRegistry.ALEXS_CAVES_BIOMES.forEach(biome ->
                registry.add(new SpelunkeryTableDisplay(biome))
        );

        if (Minecraft.getInstance().level != null) {
            for (RecipeHolder<? extends AbstractCookingRecipe> holder : Minecraft.getInstance().level.getRecipeManager()
                    .getAllRecipesFor(NuclearFurnaceBlockEntity.getRecipeType())) {
                registry.add(new NuclearFurnaceDisplay(holder.value()));
            }
        }
    }

    @Override
    public void registerItemComparators(ItemComparatorRegistry registry) {
        registry.register((context, stack) -> {
            if (stack.has(ACDataComponentRegistry.CAVE_BIOME)) {
                var key = stack.get(ACDataComponentRegistry.CAVE_BIOME);
                return key != null ? key.location().hashCode() : 0;
            }
            var customData = stack.get(DataComponents.CUSTOM_DATA);
            if (customData != null) {
                var tag = customData.copyTag();
                if (tag.contains("CaveBiome")) {
                    return tag.getString("CaveBiome").hashCode();
                }
            }
            return 0;
        }, ACItemRegistry.CAVE_TABLET, ACItemRegistry.CAVE_CODEX);

        registry.register((context, stack) -> {
            var potion = stack.get(DataComponents.POTION_CONTENTS);
            return potion != null && potion.potion().isPresent()
                    ? potion.potion().get().getRegisteredName().hashCode()
                    : 0;
        }, ACItemRegistry.JELLY_BEAN);
    }
}