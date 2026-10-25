package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class ACAdvancementTriggerRegistry {

    public static final ACAdvancementTrigger KILL_MOB_WITH_GALENA_GAUNTLET = register("kill_mob_with_galena_gauntlet", new ACAdvancementTrigger());
    public static final ACAdvancementTrigger FINISHED_QUARRY = register("finished_quarry", new ACAdvancementTrigger());
    public static final ACAdvancementTrigger DINOSAURS_MINECART = register("dinosaurs_minecart", new ACAdvancementTrigger());
    public static final ACAdvancementTrigger CAVE_PAINTING = register("cave_painting", new ACAdvancementTrigger());
    public static final ACAdvancementTrigger MYSTERY_CAVE_PAINTING = register("mystery_cave_painting", new ACAdvancementTrigger());
    public static final ACAdvancementTrigger SUMMON_LUXTRUCTOSAURUS = register("summon_luxtructosaurus", new ACAdvancementTrigger());
    public static final ACAdvancementTrigger ATLATITAN_STOMP = register("atlatitan_stomp", new ACAdvancementTrigger());
    public static final ACAdvancementTrigger ENTER_ACID_WITH_ARMOR = register("enter_acid_with_armor", new ACAdvancementTrigger());
    public static final ACAdvancementTrigger ACID_CREATE_RUST = register("acid_create_rust", new ACAdvancementTrigger());
    public static final ACAdvancementTrigger REMOTE_DETONATION = register("remote_detonation", new ACAdvancementTrigger());
    public static final ACAdvancementTrigger STOP_NUCLEAR_FURNACE_MELTDOWN = register("stop_nuclear_furnace_meltdown", new ACAdvancementTrigger());
    public static final ACAdvancementTrigger HATCH_TREMORZILLA_EGG = register("hatch_tremorzilla_egg", new ACAdvancementTrigger());
    public static final ACAdvancementTrigger TREMORZILLA_KILL_BEAM = register("tremorzilla_kill_beam", new ACAdvancementTrigger());
    public static final ACAdvancementTrigger STALKED_BY_DEEP_ONE = register("stalked_by_deep_one", new ACAdvancementTrigger());
    public static final ACAdvancementTrigger DEEP_ONE_TRADE = register("deep_one_trade", new ACAdvancementTrigger());
    public static final ACAdvancementTrigger DEEP_ONE_NEUTRAL = register("deep_one_neutral", new ACAdvancementTrigger());
    public static final ACAdvancementTrigger DEEP_ONE_HELPFUL = register("deep_one_helpful", new ACAdvancementTrigger());
    public static final ACAdvancementTrigger UNDERZEALOT_SACRIFICE = register("underzealot_sacrifice", new ACAdvancementTrigger());
    public static final ACAdvancementTrigger BEHOLDER_FAR_AWAY = register("beholder_far_away", new ACAdvancementTrigger());
    public static final ACAdvancementTrigger EAT_DARKENED_APPLE = register("eat_darkened_apple", new ACAdvancementTrigger());
    public static final ACAdvancementTrigger FROSTMINT_EXPLOSION = register("frostmint_explosion", new ACAdvancementTrigger());
    public static final ACAdvancementTrigger CONVERT_BIOME = register("convert_biome", new ACAdvancementTrigger());
    public static final ACAdvancementTrigger CONVERT_NETHER_BIOME = register("convert_nether_biome", new ACAdvancementTrigger());

    private static ACAdvancementTrigger register(String name, ACAdvancementTrigger trigger) {
        return Registry.register(BuiltInRegistries.TRIGGER_TYPES, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, name), trigger);
    }

    public static void init() {
    }
}