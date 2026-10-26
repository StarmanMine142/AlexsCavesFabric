package com.github.alexmodguy.alexscaves.server.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.FrogVariant;

public class ACFrogRegistry {

    public static final ResourceKey<FrogVariant> PRIMORDIAL_KEY =
            ResourceKey.create(Registries.FROG_VARIANT,
                    ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "primordial"));

    public static FrogVariant PRIMORDIAL;
    public static Holder<FrogVariant> PRIMORDIAL_HOLDER;

    public static void register() {
        PRIMORDIAL = Registry.register(
                BuiltInRegistries.FROG_VARIANT,
                PRIMORDIAL_KEY,
                new FrogVariant(
                        ResourceLocation.fromNamespaceAndPath(
                                AlexsCaves.MODID,
                                "textures/entity/frog/primordial_frog.png")
                )
        );
        PRIMORDIAL_HOLDER = BuiltInRegistries.FROG_VARIANT.wrapAsHolder(PRIMORDIAL);
    }
}