package com.github.alexmodguy.alexscaves.server.recipe;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

public class ACRecipeRegistry {

    public static final RecipeType<NuclearFurnaceRecipe> NUCLEAR_FURNACE_TYPE = Registry.register(
            BuiltInRegistries.RECIPE_TYPE,
            ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "nuclear_furnace"),
            new RecipeType<>() {
            }
    );

    public static final RecipeSerializer<RecipeCaveMap> CAVE_MAP = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "cave_map"),
            new SimpleCraftingRecipeSerializer<>(RecipeCaveMap::new)
    );

    public static final RecipeSerializer<NuclearFurnaceRecipe> NUCLEAR_FURNACE = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "nuclear_furnace"),
            new SimpleCookingSerializer<>(NuclearFurnaceRecipe::new, 100)
    );

    public static void init() {
    }
}