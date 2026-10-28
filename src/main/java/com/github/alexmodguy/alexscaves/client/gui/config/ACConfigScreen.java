package com.github.alexmodguy.alexscaves.client.gui.config;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Environment(EnvType.CLIENT)
public final class ACConfigScreen {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CLIENT_FILE = "alexscaves-client";
    private static final String SERVER_FILE = "alexscaves-server";

    private ACConfigScreen() {
    }

    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("Alex's Caves Settings"))
                .setSavingRunnable(() -> AlexsCaves.LOGGER.info(
                        "[AC-CONFIG] Saved. Restart the game to apply changes."));

        ConfigEntryBuilder eb = builder.entryBuilder();

        ConfigCategory client = builder.getOrCreateCategory(Component.literal("Client"));

        addBool(client, eb, CLIENT_FILE, "visuals.cave_maps_visible_in_third_person", true, "Cave Maps In Third Person");
        addBool(client, eb, CLIENT_FILE, "visuals.screen_shaking", true, "Screen Shaking");
        addBool(client, eb, CLIENT_FILE, "visuals.emissive_block_models", true, "Emissive Block Models");
        addBool(client, eb, CLIENT_FILE, "visuals.nuclear_bomb_flash", true, "Nuclear Bomb Flash");
        addBool(client, eb, CLIENT_FILE, "visuals.biome_ambient_light", true, "Biome Ambient Light");
        addBool(client, eb, CLIENT_FILE, "visuals.biome_ambient_light_coloring", true, "Biome Ambient Light Coloring");
        addBool(client, eb, CLIENT_FILE, "visuals.biome_sky_overrides", true, "Biome Sky Overrides");
        addBool(client, eb, CLIENT_FILE, "visuals.biome_sky_fog_overrides", true, "Biome Sky Fog Overrides");
        addBool(client, eb, CLIENT_FILE, "visuals.biome_water_fog_overrides", true, "Biome Water Fog Overrides");
        addBool(client, eb, CLIENT_FILE, "visuals.ambersol_shines", true, "Ambersol Shines");
        addBool(client, eb, CLIENT_FILE, "visuals.radiation_glow_effect", true, "Radiation Glow Effect");
        addBool(client, eb, CLIENT_FILE, "visuals.sugar_rush_saturation_effect", true, "Sugar Rush Saturation Effect");
        addInt(client, eb, CLIENT_FILE, "visuals.subterranodon_indicator_x", 22, "Subterranodon Indicator X");
        addInt(client, eb, CLIENT_FILE, "visuals.subterranodon_indicator_y", 6, "Subterranodon Indicator Y");
        addBool(client, eb, CLIENT_FILE, "audio.nuclear_bomb_muffles_sounds", true, "Nuclear Bomb Muffles Sounds");

        ConfigCategory server = builder.getOrCreateCategory(Component.literal("Server"));

        addDouble(server, eb, SERVER_FILE, "generation.cave_biome_mean_width", 300D, "Cave Biome Mean Width");
        addInt(server, eb, SERVER_FILE, "generation.cave_biome_mean_separation", 900, "Cave Biome Mean Separation");
        addDouble(server, eb, SERVER_FILE, "generation.cave_biome_width_randomness", 0.15D, "Cave Biome Width Randomness");
        addDouble(server, eb, SERVER_FILE, "generation.cave_biome_spacing_randomness", 0.45D, "Cave Biome Spacing Randomness");
        addBool(server, eb, SERVER_FILE, "generation.warn_generation_incompatibility", true, "Warn Generation Incompatibility");

        addDouble(server, eb, SERVER_FILE, "mob-spawning.cave_creature_spawn_count_modifier", 1.75D, "Cave Creature Spawn Modifier");
        addDouble(server, eb, SERVER_FILE, "mob-spawning.drowned_diving_gear_spawn_chance", 0.2D, "Drowned Diving Gear Chance");

        addInt(server, eb, SERVER_FILE, "mob-behavior.pathfinding_threads", 5, "Pathfinding Threads");
        addDouble(server, eb, SERVER_FILE, "mob-behavior.luxtructosaurus_block_drop_chance", 0.75D, "Luxtructosaurus Block Drop Chance");
        addInt(server, eb, SERVER_FILE, "mob-behavior.atlatitan_max_block_explosion_resistance", 10, "Atlatitan Max Explosion Resistance");
        addInt(server, eb, SERVER_FILE, "mob-behavior.nucleeper_fuse_time", 300, "Nucleeper Fuse Time");
        addBool(server, eb, SERVER_FILE, "mob-behavior.devastating_tremorzilla_beam", true, "Devastating Tremorzilla Beam");
        addBool(server, eb, SERVER_FILE, "mob-behavior.watcher_possession", true, "Watcher Possession");
        addInt(server, eb, SERVER_FILE, "mob-behavior.watcher_possession_cooldown", 300, "Watcher Possession Cooldown");

        addBool(server, eb, SERVER_FILE, "block-behavior.walking_on_magnets", true, "Walking on Magnets");
        addInt(server, eb, SERVER_FILE, "block-behavior.amber_monolith_mean_time", 32000, "Amber Monolith Mean Time");
        addBool(server, eb, SERVER_FILE, "block-behavior.nuclear_furnace_blasting_only", true, "Nuclear Furnace Blasting Only");
        addBool(server, eb, SERVER_FILE, "block-behavior.nuclear_furnace_custom_type", false, "Nuclear Furnace Custom Type");

        addBool(server, eb, SERVER_FILE, "item-behavior.only_one_research_needed", false, "Only One Research Needed");
        addInt(server, eb, SERVER_FILE, "item-behavior.cave_map_search_attempts", 128000, "Cave Map Search Attempts");
        addInt(server, eb, SERVER_FILE, "item-behavior.cave_map_search_width", 64, "Cave Map Search Width");
        addInt(server, eb, SERVER_FILE, "item-behavior.nuke_max_block_explosion_resistance", 1000, "Nuke Max Explosion Resistance");
        addBool(server, eb, SERVER_FILE, "item-behavior.nuke_spawn_item_drops", true, "Nuke Spawn Item Drops");
        addDouble(server, eb, SERVER_FILE, "item-behavior.nuclear_explosion_size_modifier", 3.0D, "Nuclear Explosion Size Modifier");
        addBool(server, eb, SERVER_FILE, "item-behavior.totem_of_possession_works_on_players", true, "Totem Works On Players");
        addInt(server, eb, SERVER_FILE, "item-behavior.darkness_cloak_charge_time", 1000, "Darkness Cloak Charge Time");
        addInt(server, eb, SERVER_FILE, "item-behavior.darkness_cloak_fly_time", 200, "Darkness Cloak Fly Time");

        addBool(server, eb, SERVER_FILE, "potion-behavior.sugar_rush_slows_time", true, "Sugar Rush Slows Time");

        addDouble(server, eb, SERVER_FILE, "vanilla-changes.magnetic_tablet_loot_chance", 0.45D, "Magnetic Tablet Loot Chance");
        addDouble(server, eb, SERVER_FILE, "vanilla-changes.primordial_tablet_loot_chance", 0.15D, "Primordial Tablet Loot Chance");
        addDouble(server, eb, SERVER_FILE, "vanilla-changes.toxic_tablet_loot_chance", 0.5D, "Toxic Tablet Loot Chance");
        addDouble(server, eb, SERVER_FILE, "vanilla-changes.abyssal_tablet_loot_chance", 0.4D, "Abyssal Tablet Loot Chance");
        addDouble(server, eb, SERVER_FILE, "vanilla-changes.forlorn_tablet_loot_chance", 0.75D, "Forlorn Tablet Loot Chance");
        addDouble(server, eb, SERVER_FILE, "vanilla-changes.candy_cavity_loot_chance", 0.9D, "Candy Cavity Loot Chance");
        addDouble(server, eb, SERVER_FILE, "vanilla-changes.cabin_map_loot_chance", 0.15D, "Cabin Map Loot Chance");
        addBool(server, eb, SERVER_FILE, "vanilla-changes.cartographers_sell_cabin_maps", true, "Cartographers Sell Cabin Maps");
        addBool(server, eb, SERVER_FILE, "vanilla-changes.wandering_traders_sell_cabin_maps", true, "Wandering Traders Sell Cabin Maps");
        addBool(server, eb, SERVER_FILE, "vanilla-changes.loot_chest_in_witch_huts", true, "Loot Chest In Witch Huts");
        addBool(server, eb, SERVER_FILE, "vanilla-changes.enchantments_in_loot", false, "Enchantments In Loot");

        return builder.build();
    }

    private static void addBool(ConfigCategory cat, ConfigEntryBuilder eb, String file, String key, boolean def, String label) {
        cat.addEntry(eb.startBooleanToggle(Component.literal(label), readBool(file, key, def))
                .setDefaultValue(def)
                .setTooltip(Component.literal(key))
                .setSaveConsumer(v -> writeValue(file, key, new JsonPrimitive(v)))
                .build());
    }

    private static void addInt(ConfigCategory cat, ConfigEntryBuilder eb, String file, String key, int def, String label) {
        cat.addEntry(eb.startIntField(Component.literal(label), readInt(file, key, def))
                .setDefaultValue(def)
                .setTooltip(Component.literal(key))
                .setSaveConsumer(v -> writeValue(file, key, new JsonPrimitive(v)))
                .build());
    }

    private static void addDouble(ConfigCategory cat, ConfigEntryBuilder eb, String file, String key, double def, String label) {
        cat.addEntry(eb.startDoubleField(Component.literal(label), readDouble(file, key, def))
                .setDefaultValue(def)
                .setTooltip(Component.literal(key))
                .setSaveConsumer(v -> writeValue(file, key, new JsonPrimitive(v)))
                .build());
    }

    private static JsonObject loadFile(String name) {
        Path p = FabricLoader.getInstance().getConfigDir().resolve(name + ".json");
        if (!Files.exists(p)) {
            return new JsonObject();
        }
        try {
            JsonElement el = JsonParser.parseString(Files.readString(p));
            return el.isJsonObject() ? el.getAsJsonObject() : new JsonObject();
        } catch (IOException e) {
            AlexsCaves.LOGGER.error("[AC-CONFIG] failed to read " + name, e);
            return new JsonObject();
        }
    }

    private static void saveFile(String name, JsonObject obj) {
        try {
            Path p = FabricLoader.getInstance().getConfigDir().resolve(name + ".json");
            Files.createDirectories(p.getParent());
            Files.writeString(p, GSON.toJson(obj));
        } catch (IOException e) {
            AlexsCaves.LOGGER.error("[AC-CONFIG] failed to write " + name, e);
        }
    }

    private static JsonElement getNested(JsonObject root, String path) {
        String[] parts = path.split("\\.");
        JsonObject cur = root;
        for (int i = 0; i < parts.length - 1; i++) {
            if (!cur.has(parts[i]) || !cur.get(parts[i]).isJsonObject()) {
                return null;
            }
            cur = cur.getAsJsonObject(parts[i]);
        }
        return cur.get(parts[parts.length - 1]);
    }

    private static void setNested(JsonObject root, String path, JsonElement value) {
        String[] parts = path.split("\\.");
        JsonObject cur = root;
        for (int i = 0; i < parts.length - 1; i++) {
            if (!cur.has(parts[i]) || !cur.get(parts[i]).isJsonObject()) {
                cur.add(parts[i], new JsonObject());
            }
            cur = cur.getAsJsonObject(parts[i]);
        }
        cur.add(parts[parts.length - 1], value);
    }

    private static boolean readBool(String file, String key, boolean def) {
        JsonElement el = getNested(loadFile(file), key);
        return el != null && el.isJsonPrimitive() ? el.getAsBoolean() : def;
    }

    private static int readInt(String file, String key, int def) {
        JsonElement el = getNested(loadFile(file), key);
        return el != null && el.isJsonPrimitive() ? el.getAsInt() : def;
    }

    private static double readDouble(String file, String key, double def) {
        JsonElement el = getNested(loadFile(file), key);
        return el != null && el.isJsonPrimitive() ? el.getAsDouble() : def;
    }

    private static void writeValue(String file, String key, JsonElement value) {
        JsonObject root = loadFile(file);
        setNested(root, key, value);
        saveFile(file, root);
    }
}