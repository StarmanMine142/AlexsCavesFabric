package com.github.alexmodguy.alexscaves.compat.rei;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;

import java.util.ArrayList;
import java.util.List;

public class NuclearFurnaceDisplay implements Display {

    private final AbstractCookingRecipe recipe;
    private final List<EntryIngredient> inputs;
    private final List<EntryIngredient> outputs;

    public NuclearFurnaceDisplay(AbstractCookingRecipe recipe) {
        this.recipe = recipe;

        List<EntryIngredient> in = new ArrayList<>();
        in.add(EntryIngredients.ofIngredient(
                net.minecraft.world.item.crafting.Ingredient.of(ACTagRegistry.NUCLEAR_FURNACE_RODS)));
        in.add(EntryIngredients.ofIngredient(
                net.minecraft.world.item.crafting.Ingredient.of(ACTagRegistry.NUCLEAR_FURNACE_BARRELS)));
        if (!recipe.getIngredients().isEmpty()) {
            in.add(EntryIngredients.ofIngredient(recipe.getIngredients().get(0)));
        }
        this.inputs = in;

        List<EntryIngredient> out = new ArrayList<>();
        out.add(EntryIngredient.of(EntryStacks.of(new ItemStack(ACBlockRegistry.WASTE_DRUM))));
        ItemStack result = recipe.getResultItem(Minecraft.getInstance().level.registryAccess());
        out.add(EntryIngredient.of(EntryStacks.of(result)));
        this.outputs = out;
    }

    public AbstractCookingRecipe getRecipe() {
        return recipe;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return inputs;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return outputs;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return NuclearFurnaceCategory.CATEGORY;
    }
}