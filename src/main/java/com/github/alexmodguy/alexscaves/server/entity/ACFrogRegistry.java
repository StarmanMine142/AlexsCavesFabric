package com.github.alexmodguy.alexscaves.server.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.FrogVariant;

public class ACFrogRegistry {

    public static final Holder<FrogVariant> PRIMORDIAL = Registry.registerForHolder(
            BuiltInRegistries.FROG_VARIANT,
            ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "primordial"),
            new FrogVariant(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/primordial_frog.png"))
    );
}