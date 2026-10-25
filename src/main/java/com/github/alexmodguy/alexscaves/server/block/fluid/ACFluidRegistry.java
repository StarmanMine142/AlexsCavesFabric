package com.github.alexmodguy.alexscaves.server.block.fluid;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

public class ACFluidRegistry {

    public static final FlowingFluid ACID_FLUID_SOURCE = new AcidFluid.Source();
    public static final FlowingFluid ACID_FLUID_FLOWING = new AcidFluid.Flowing();

    public static final FlowingFluid PURPLE_SODA_FLUID_SOURCE = new PurpleSodaFluid.Source();
    public static final FlowingFluid PURPLE_SODA_FLUID_FLOWING = new PurpleSodaFluid.Flowing();

    public static void register() {
        registerFluid("acid", ACID_FLUID_SOURCE);
        registerFluid("acid_flowing", ACID_FLUID_FLOWING);
        registerFluid("purple_soda", PURPLE_SODA_FLUID_SOURCE);
        registerFluid("purple_soda_flowing", PURPLE_SODA_FLUID_FLOWING);
    }

    private static void registerFluid(String name, Fluid fluid) {
        Registry.register(BuiltInRegistries.FLUID, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, name), fluid);
    }

    public static void postInit() {
    }
}