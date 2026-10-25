package com.github.alexmodguy.alexscaves.client.model.layered;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.resources.ResourceLocation;

public class ACModelLayers {

    public static final ModelLayerLocation PRIMORDIAL_ARMOR = createLocation("primordial_armor", "main");
    public static final ModelLayerLocation HAZMAT_ARMOR = createLocation("hazmat_armor", "main");
    public static final ModelLayerLocation DIVING_ARMOR = createLocation("diving_armor", "main");
    public static final ModelLayerLocation DARKNESS_ARMOR = createLocation("darkness_armor", "main");
    public static final ModelLayerLocation RAINBOUNCE_ARMOR = createLocation("rainbounce_armor", "main");
    public static final ModelLayerLocation GINGERBREAD_ARMOR = createLocation("gingerbread_armor", "main");

    public static void registerFabric() {
        EntityModelLayerRegistry.registerModelLayer(PRIMORDIAL_ARMOR,
                () -> PrimordialArmorModel.createArmorLayer(new CubeDeformation(0.5F)));
        EntityModelLayerRegistry.registerModelLayer(HAZMAT_ARMOR,
                () -> HazmatArmorModel.createArmorLayer(new CubeDeformation(0.5F)));
        EntityModelLayerRegistry.registerModelLayer(DIVING_ARMOR,
                () -> DivingArmorModel.createArmorLayer(new CubeDeformation(0.5F)));
        EntityModelLayerRegistry.registerModelLayer(DARKNESS_ARMOR,
                () -> DarknessArmorModel.createArmorLayer(new CubeDeformation(0.5F)));
        EntityModelLayerRegistry.registerModelLayer(RAINBOUNCE_ARMOR,
                () -> RainbounceArmorModel.createArmorLayer(new CubeDeformation(0.75F)));
        EntityModelLayerRegistry.registerModelLayer(GINGERBREAD_ARMOR,
                () -> GingerbreadArmorModel.createArmorLayer(new CubeDeformation(0.5F)));
    }

    private static ModelLayerLocation createLocation(String model, String layer) {
        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, model), layer);
    }
}