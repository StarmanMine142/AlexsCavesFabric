package com.github.alexmodguy.alexscaves.server.inventory;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public class ACMenuRegistry {

    public static final MenuType<SpelunkeryTableMenu> SPELUNKERY_TABLE_MENU = registerMenu("spelunkery_table_menu",
            new MenuType<>(SpelunkeryTableMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final MenuType<NuclearFurnaceMenu> NUCLEAR_FURNACE_MENU = registerMenu("nuclear_furnace_menu",
            new MenuType<>(NuclearFurnaceMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static void register() {
    }

    private static <T extends MenuType<?>> T registerMenu(String name, T menuType) {
        return Registry.register(BuiltInRegistries.MENU, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, name), menuType);
    }
}