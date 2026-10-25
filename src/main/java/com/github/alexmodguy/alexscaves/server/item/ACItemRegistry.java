package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.citadel.server.block.LecternBooks;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.*;
import com.github.alexmodguy.alexscaves.server.entity.util.AlexsCavesBoat;
import com.github.alexmodguy.alexscaves.server.entity.util.GummyColors;
import com.github.alexmodguy.alexscaves.server.item.dispenser.FluidContainerDispenseItemBehavior;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACProjectileDispenseBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public class ACItemRegistry {
    private static Map<Item, ResourceKey<Biome>> creativeTabSpawnEggMap = new LinkedHashMap<>();

    // Custom item rarities - fallback to vanilla if custom enum values are unavailable.
    // Custom styling is applied at runtime via ACRarityEnumParams + ItemStack mixin.
    private static Rarity RARITY_DEMONIC_CACHE = null;
    private static Rarity RARITY_NUCLEAR_CACHE = null;
    private static Rarity RARITY_SWEET_CACHE = null;
    private static Rarity RARITY_RAINBOW_CACHE = null;

    private static Rarity resolveRarity(String name, Rarity fallback) {
        try {
            return Rarity.valueOf(name);
        } catch (IllegalArgumentException ignored) {
            return fallback;
        }
    }

    public static Rarity getRarityDemonic() {
        if (RARITY_DEMONIC_CACHE == null) {
            RARITY_DEMONIC_CACHE = resolveRarity("ALEXSCAVES_DEMONIC", Rarity.EPIC);
        }
        return RARITY_DEMONIC_CACHE;
    }

    public static Rarity getRarityNuclear() {
        if (RARITY_NUCLEAR_CACHE == null) {
            RARITY_NUCLEAR_CACHE = resolveRarity("ALEXSCAVES_NUCLEAR", Rarity.EPIC);
        }
        return RARITY_NUCLEAR_CACHE;
    }

    public static Rarity getRaritySweet() {
        if (RARITY_SWEET_CACHE == null) {
            RARITY_SWEET_CACHE = resolveRarity("ALEXSCAVES_SWEET", Rarity.RARE);
        }
        return RARITY_SWEET_CACHE;
    }

    public static Rarity getRarityRainbow() {
        if (RARITY_RAINBOW_CACHE == null) {
            RARITY_RAINBOW_CACHE = resolveRarity("ALEXSCAVES_RAINBOW", Rarity.EPIC);
        }
        return RARITY_RAINBOW_CACHE;
    }

    public static UnaryOperator<Style> getCustomRarityStyle(ItemStack stack) {
        Item item = stack.getItem();
        if (isDemonicRarityItem(item)) {
            return ACRarityEnumParams.getDemonicStyleModifier();
        }
        if (isNuclearRarityItem(item)) {
            return ACRarityEnumParams.getNuclearStyleModifier();
        }
        if (isSweetRarityItem(item)) {
            return ACRarityEnumParams.getSweetStyleModifier();
        }
        if (isRainbowRarityItem(item)) {
            return ACRarityEnumParams.getRainbowStyleModifier();
        }
        return null;
    }

    private static boolean isDemonicRarityItem(Item item) {
        return item == TECTONIC_SHARD
                || item == EXTINCTION_SPEAR
                || item == PURE_DARKNESS
                || item == DESOLATE_DAGGER
                || item == HOOD_OF_DARKNESS
                || item == CLOAK_OF_DARKNESS
                || item == DARKENED_APPLE
                || item == DREADBOW;
    }

    private static boolean isNuclearRarityItem(Item item) {
        return item == MUSIC_DISC_FUSION
                || item == ACBlockRegistry.TREMORZILLA_EGG.asItem();
    }

    private static boolean isSweetRarityItem(Item item) {
        return item == SUNDAE
                || item == SWEET_TOOTH
                || item == SACK_OF_SATING
                || item == MUSIC_DISC_TASTY;
    }

    private static boolean isRainbowRarityItem(Item item) {
        return item == RADIANT_ESSENCE
                || item == LICOWITCH_RADIANT_ESSENCE
                || item == ALEX_MEAL;
    }

    public static final ACArmorMaterial PRIMORDIAL_ARMOR_MATERIAL = new ACArmorMaterial("primordial", 20, new int[]{3, 4, 3, 2}, 25, SoundEvents.ARMOR_EQUIP_LEATHER, 0F);
    public static final ACArmorMaterial HAZMAT_SUIT_ARMOR_MATERIAL = new ACArmorMaterial("hazmat_suit", 20, new int[]{2, 4, 5, 2}, 25, SoundEvents.ARMOR_EQUIP_IRON, 0.5F);
    public static final ACArmorMaterial DIVING_SUIT_ARMOR_MATERIAL = new ACArmorMaterial("diving_suit", 20, new int[]{2, 6, 5, 2}, 25, SoundEvents.ARMOR_EQUIP_IRON, 0.0F);
    public static final ACArmorMaterial DARKNESS_ARMOR_MATERIAL = new ACArmorMaterial("darkness", 15, new int[]{4, 5, 1, 1}, 40, SoundEvents.ARMOR_EQUIP_LEATHER, 0.5F);
    public static final ACArmorMaterial RAINBOUNCE_ARMOR_MATERIAL = new ACArmorMaterial("rainbounce", 6, new int[]{2, 2, 1, 2}, 40, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F);
    public static final ACArmorMaterial GINGERBREAD_ARMOR_MATERIAL = new ACArmorMaterial("gingerbread", 10, new int[]{2, 4, 5, 2}, 25, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F);

    public static final Item ADVANCEMENT_TAB_ICON = registerItem("advancement_tab_icon", new Item(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final Item CAVE_TABLET = registerItem("cave_tablet", new CaveInfoItem(new Item.Properties(), true));
    public static final Item CAVE_CODEX = registerItem("cave_codex", new CaveInfoItem(new Item.Properties(), false));
    public static final Item CAVE_BOOK = registerItem("cave_book", new CaveBookItem());
    public static final Item CAVE_MAP = registerItem("cave_map", new CaveMapItem(new Item.Properties().stacksTo(1)));
    public static final Item CAVE_MAP_SPRITE = registerItem("cave_map_inventory", new Item(new Item.Properties()));
    public static final Item CAVE_MAP_LOADING_SPRITE = registerItem("cave_map_loading", new Item(new Item.Properties()));
    public static final Item CAVE_MAP_FILLED_SPRITE = registerItem("cave_map_filled", new Item(new Item.Properties()));
    public static final Item RAW_SCARLET_NEODYMIUM = registerItem("raw_scarlet_neodymium", new Item(new Item.Properties()));
    public static final Item RAW_AZURE_NEODYMIUM = registerItem("raw_azure_neodymium", new Item(new Item.Properties()));
    public static final Item SCARLET_NEODYMIUM_INGOT = registerItem("scarlet_neodymium_ingot", new Item(new Item.Properties()));
    public static final Item AZURE_NEODYMIUM_INGOT = registerItem("azure_neodymium_ingot", new Item(new Item.Properties()));
    public static final Item TELECORE = registerItem("telecore", new Item(new Item.Properties()));
    public static final Item NOTOR_COMPONENT = registerItem("notor_gizmo", new Item(new Item.Properties()));
    public static final Item HEAVYWEIGHT = registerItem("heavyweight", new Item(new Item.Properties()));
    public static final Item FERROUSLIME_BALL = registerItem("ferrouslime_ball", new Item(new Item.Properties()));
    public static final Item QUARRY_SMASHER = registerItem("quarry_smasher", new QuarrySmasherItem(new Item.Properties().stacksTo(1)));
    public static final Item HOLOCODER = registerItem("holocoder", new HolocoderItem(new Item.Properties().stacksTo(1)));
    public static final Item SEEKING_ARROW = registerItem("seeking_arrow", new SeekingArrowItem());
    public static final Item GALENA_GAUNTLET = registerItem("galena_gauntlet", new GalenaGauntletItem());
    public static final Item RESISTOR_SHIELD = registerItem("resistor_shield", new ResistorShieldItem());
    public static final Item POLARITY_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("polarity_armor_trim_smithing_template", SmithingTemplateItem.createArmorTrimTemplate(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "polarity")));
    public static final Item PEWEN_DOOR = registerItem("pewen_door", new DoubleHighBlockItem(ACBlockRegistry.PEWEN_DOOR, (new Item.Properties())));
    public static final Item PEWEN_SIGN = registerItem("pewen_sign", new SignItem((new Item.Properties()).stacksTo(16), ACBlockRegistry.PEWEN_SIGN, ACBlockRegistry.PEWEN_WALL_SIGN));
    public static final Item PEWEN_HANGING_SIGN = registerItem("pewen_hanging_sign", new HangingSignItem(ACBlockRegistry.PEWEN_HANGING_SIGN, ACBlockRegistry.PEWEN_WALL_HANGING_SIGN, (new Item.Properties()).stacksTo(16)));
    public static final Item PEWEN_BOAT = registerItem("pewen_boat", new CaveBoatItem(false, AlexsCavesBoat.Type.PEWEN, new Item.Properties().stacksTo(1)));
    public static final Item PEWEN_CHEST_BOAT = registerItem("pewen_chest_boat", new CaveBoatItem(true, AlexsCavesBoat.Type.PEWEN, new Item.Properties().stacksTo(1)));
    public static final Item TRILOCARIS_BUCKET = registerItem("trilocaris_bucket", new ModFishBucketItem(() -> ACEntityRegistry.TRILOCARIS, () -> Fluids.WATER, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final Item TRILOCARIS_TAIL = registerItem("trilocaris_tail", new Item(new Item.Properties().food(ACFoods.TRILOCARIS_TAIL)));
    public static final Item COOKED_TRILOCARIS_TAIL = registerItem("cooked_trilocaris_tail", new Item(new Item.Properties().food(ACFoods.TRILOCARIS_TAIL_COOKED)));
    public static final Item PINE_NUTS = registerItem("pine_nuts", new Item(new Item.Properties().food(ACFoods.PINE_NUTS)));
    public static final Item PEWEN_SAP = registerItem("pewen_sap", new Item(new Item.Properties()));
    public static final Item AMBER_CURIOSITY = registerItem("amber_curiosity", new Item(new Item.Properties()));
    public static final Item DINOSAUR_NUGGET = registerItem("dinosaur_nugget", new Item(new Item.Properties().food(ACFoods.DINOSAUR_NUGGETS)));
    public static final Item SERENE_SALAD = registerItem("serene_salad", new PrehistoricMixtureItem(new Item.Properties().stacksTo(1).food(ACFoods.SERENE_SALAD)));
    public static final Item SEETHING_STEW = registerItem("seething_stew", new PrehistoricMixtureItem(new Item.Properties().stacksTo(1).food(ACFoods.SEETHING_STEW)));
    public static final Item PRIMORDIAL_SOUP = registerItem("primordial_soup", new PrehistoricMixtureItem(new Item.Properties().stacksTo(1).food(ACFoods.PRIMORDIAL_SOUP)));
    public static final Item TOUGH_HIDE = registerItem("tough_hide", new Item(new Item.Properties()));
    public static final Item HEAVY_BONE = registerItem("heavy_bone", new Item(new Item.Properties().stacksTo(16)));
    public static final Item PRIMITIVE_CLUB = registerItem("primitive_club", new PrimitiveClubItem(new Item.Properties().durability(120)));
    public static final Item PRIMITIVE_CLUB_SPRITE = registerItem("primitive_club_inventory", new Item(new Item.Properties()));
    public static final Item PRIMORDIAL_HELMET = registerItem("primordial_helmet", new PrimordialArmorItem(PRIMORDIAL_ARMOR_MATERIAL, ArmorItem.Type.HELMET));
    public static final Item PRIMORDIAL_TUNIC = registerItem("primordial_tunic", new PrimordialArmorItem(PRIMORDIAL_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE));
    public static final Item PRIMORDIAL_PANTS = registerItem("primordial_pants", new PrimordialArmorItem(PRIMORDIAL_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS));
    public static final Item LIMESTONE_SPEAR = registerItem("limestone_spear", new LimestoneSpearItem(new Item.Properties().stacksTo(16)));
    public static final Item LIMESTONE_SPEAR_SPRITE = registerItem("limestone_spear_inventory", new Item(new Item.Properties()));
    public static final Item OMINOUS_CATALYST = registerItem("ominous_catalyst", new Item(new Item.Properties().rarity(Rarity.UNCOMMON).fireResistant()));
    public static final Item TECTONIC_SHARD = registerItem("tectonic_shard", new Item(new Item.Properties().rarity(getRarityDemonic()).fireResistant()));
    public static final Item EXTINCTION_SPEAR = registerItem("extinction_spear", new ExtinctionSpearItem(new Item.Properties().durability(1300).rarity(getRarityDemonic()).fireResistant()));
    public static final Item EXTINCTION_SPEAR_SPRITE = registerItem("extinction_spear_inventory", new Item(new Item.Properties()));
    public static final Item DINOSAUR_POTTERY_SHERD = registerItem("dinosaur_pottery_sherd", new Item(new Item.Properties()));
    public static final Item FOOTPRINT_POTTERY_SHERD = registerItem("footprint_pottery_sherd", new Item(new Item.Properties()));
    public static final Item DINOSAUR_TRAIN = registerItem("dinosaur_train", new Item(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final Item ACID_BUCKET = registerItem("acid_bucket", new BucketItem(ACFluidRegistry.ACID_FLUID_SOURCE, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final Item RADGILL_BUCKET = registerItem("radgill_bucket", new ModFishBucketItem(() -> ACEntityRegistry.RADGILL, () -> ACFluidRegistry.ACID_FLUID_SOURCE, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final Item RADGILL = registerItem("radgill", new Item(new Item.Properties().food(ACFoods.RADGILL)));
    public static final Item COOKED_RADGILL = registerItem("cooked_radgill", new Item(new Item.Properties().food(ACFoods.RADGILL_COOKED)));
    public static final Item URANIUM = registerItem("uranium", new RadioactiveItem(new Item.Properties(), 0.001F));
    public static final Item URANIUM_SHARD = registerItem("uranium_shard", new RadioactiveItem(new Item.Properties(), 0.001F));
    public static final Item SULFUR_DUST = registerItem("sulfur_dust", new Item(new Item.Properties()));
    public static final Item RADON_BOTTLE = registerItem("radon_bottle", new Item(new Item.Properties().craftRemainder(Items.GLASS_BOTTLE).stacksTo(16)));
    public static final Item CINDER_BRICK = registerItem("cinder_brick", new ThrownProjectileItem(new Item.Properties(), player -> new CinderBrickEntity(player.level(), player), -20.0F, 0.65F, 0.9F));
    public static final Item SPELUNKIE = registerItem("spelunkie", new RadiationRemovingFoodItem(new Item.Properties().food(ACFoods.SPELUNKIE)));
    public static final Item SLAM = registerItem("slam", new RadiationRemovingFoodItem(new Item.Properties().food(ACFoods.SLAM)));
    public static final Item GREEN_SOYLENT = registerItem("green_soylent", new RadiationRemovingFoodItem(new Item.Properties().food(ACFoods.SOYLENT_GREEN)));
    public static final Item TOXIC_PASTE = registerItem("toxic_paste", new Item(new Item.Properties()));
    public static final Item POLYMER_PLATE = registerItem("polymer_plate", new Item(new Item.Properties()));
    public static final Item HAZMAT_MASK = registerItem("hazmat_mask", new HazmatArmorItem(HAZMAT_SUIT_ARMOR_MATERIAL, ArmorItem.Type.HELMET));
    public static final Item HAZMAT_CHESTPLATE = registerItem("hazmat_chestplate", new HazmatArmorItem(HAZMAT_SUIT_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE));
    public static final Item HAZMAT_LEGGINGS = registerItem("hazmat_leggings", new HazmatArmorItem(HAZMAT_SUIT_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS));
    public static final Item HAZMAT_BOOTS = registerItem("hazmat_boots", new HazmatArmorItem(HAZMAT_SUIT_ARMOR_MATERIAL, ArmorItem.Type.BOOTS));
    public static final Item FISSILE_CORE = registerItem("fissile_core", new RadioactiveItem(new Item.Properties().rarity(Rarity.UNCOMMON), 0.001F));
    public static final Item CHARRED_REMNANT = registerItem("charred_remnant", new RadioactiveItem(new Item.Properties(), 0.0005F));
    public static final Item REMOTE_DETONATOR = registerItem("remote_detonator", new RemoteDetonatorItem());
    public static final Item RAYGUN = registerItem("raygun", new RaygunItem());
    public static final Item MUSIC_DISC_FUSION_FRAGMENT = registerItem("disc_fragment_fusion", new DiscFragmentItem(new Item.Properties()));
    // Music discs - in 1.21+, use jukeboxPlayable component in item properties
    // The jukebox songs are data-driven and defined in data/alexscaves/jukebox_song/
    public static final ResourceKey<JukeboxSong> JUKEBOX_SONG_FUSION = ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "fusion"));
    public static final Item MUSIC_DISC_FUSION = registerItem("music_disc_fusion", new Item(new Item.Properties().stacksTo(1).rarity(getRarityNuclear()).jukeboxPlayable(JUKEBOX_SONG_FUSION)));
    public static final Item LANTERNFISH_BUCKET = registerItem("lanternfish_bucket", new ModFishBucketItem(() -> ACEntityRegistry.LANTERNFISH, () -> Fluids.WATER, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final Item LANTERNFISH = registerItem("lanternfish", new Item(new Item.Properties().food(ACFoods.LANTERNFISH)));
    public static final Item COOKED_LANTERNFISH = registerItem("cooked_lanternfish", new Item(new Item.Properties().food(ACFoods.LANTERNFISH_COOKED)));
    public static final Item TRIPODFISH_BUCKET = registerItem("tripodfish_bucket", new ModFishBucketItem(() -> ACEntityRegistry.TRIPODFISH, () -> Fluids.WATER, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final Item TRIPODFISH = registerItem("tripodfish", new Item(new Item.Properties().food(ACFoods.TRIPODFISH)));
    public static final Item COOKED_TRIPODFISH = registerItem("cooked_tripodfish", new Item(new Item.Properties().food(ACFoods.TRIPODFISH_COOKED)));
    public static final Item SEA_PIG_BUCKET = registerItem("sea_pig_bucket", new ModFishBucketItem(() -> ACEntityRegistry.SEA_PIG, () -> Fluids.WATER, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final Item SEA_PIG = registerItem("sea_pig", new Item(new Item.Properties().food(ACFoods.SEA_PIG)));
    public static final Item MARINE_SNOW = registerItem("marine_snow", new MarineSnowItem());
    public static final Item GOSSAMER_WORM_BUCKET = registerItem("gossamer_worm_bucket", new ModFishBucketItem(() -> ACEntityRegistry.GOSSAMER_WORM, () -> Fluids.WATER, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final Item BIOLUMINESSCENCE = registerItem("bioluminesscence", new Item(new Item.Properties()));
    public static final Item PEARL = registerItem("pearl", new Item(new Item.Properties()));
    public static final Item COOKED_MUSSEL = registerItem("cooked_mussel", new Item(new Item.Properties().food(ACFoods.MUSSEL_COOKED)));
    public static final Item DEEP_SEA_SUSHI_ROLL = registerItem("deep_sea_sushi_roll", new Item(new Item.Properties().food(ACFoods.DEEP_SEA_SUSHI_ROLL)));
    public static final Item SEA_GLASS_SHARDS = registerItem("sea_glass_shards", new Item(new Item.Properties()));
    public static final Item SUBMARINE = registerItem("submarine", new SubmarineItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final Item DIVING_HELMET = registerItem("diving_helmet", new DivingArmorItem(DIVING_SUIT_ARMOR_MATERIAL, ArmorItem.Type.HELMET));
    public static final Item DIVING_CHESTPLATE = registerItem("diving_chestplate", new DivingArmorItem(DIVING_SUIT_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE));
    public static final Item DIVING_LEGGINGS = registerItem("diving_leggings", new DivingArmorItem(DIVING_SUIT_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS));
    public static final Item DIVING_BOOTS = registerItem("diving_boots", new DivingArmorItem(DIVING_SUIT_ARMOR_MATERIAL, ArmorItem.Type.BOOTS));
    public static final Item FLOATER = registerItem("floater", new FloaterItem());
    public static final Item GAZING_PEARL = registerItem("gazing_pearl", new GazingPearlItem());
    public static final Item INK_BOMB = registerItem("ink_bomb", new InkBombItem(new Item.Properties(), false));
    public static final Item GLOW_INK_BOMB = registerItem("glow_ink_bomb", new InkBombItem(new Item.Properties(), true));
    public static final Item MAGIC_CONCH = registerItem("magic_conch", new MagicConchItem(new Item.Properties().durability(5).rarity(Rarity.UNCOMMON)));
    public static final Item SEA_STAFF = registerItem("sea_staff", new SeaStaffItem(new Item.Properties().durability(850).rarity(Rarity.UNCOMMON)));
    public static final Item SEA_STAFF_SPRITE = registerItem("sea_staff_inventory", new Item(new Item.Properties()));
    public static final Item ORTHOLANCE = registerItem("ortholance", new OrtholanceItem(new Item.Properties().durability(340).rarity(Rarity.UNCOMMON)));
    public static final Item ORTHOLANCE_SPRITE = registerItem("ortholance_inventory", new Item(new Item.Properties()));
    public static final Item DEPTH_CHARGE = registerItem("depth_charge", new ThrownProjectileItem(new Item.Properties(), player -> new DepthChargeEntity(player.level(), player), -10.0F, 0.65F, 1.5F));
    public static final Item GUARDIAN_POTTERY_SHERD = registerItem("guardian_pottery_sherd", new Item(new Item.Properties()));
    public static final Item HERO_POTTERY_SHERD = registerItem("hero_pottery_sherd", new Item(new Item.Properties()));
    public static final Item BIOLUMINESCENT_TORCH = registerItem("bioluminescent_torch", new StandingAndWallBlockItem(ACBlockRegistry.BIOLUMINESCENT_TORCH, ACBlockRegistry.BIOLUMINESCENT_WALL_TORCH, new Item.Properties(), Direction.DOWN));
    public static final Item GAME_CONTROLLER = registerItem("game_controller", new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final Item STINKY_FISH = registerItem("stinky_fish", new Item(new Item.Properties().rarity(Rarity.RARE).food(ACFoods.STINKY_FISH)));
    public static final Item IMMORTAL_EMBRYO = registerItem("immortal_embryo", new Item(new Item.Properties().rarity(Rarity.EPIC)));
    public static final Item GUANO = registerItem("guano", new ThrownProjectileItem(new Item.Properties(), player -> new GuanoEntity(player.level(), player), 0.0F, 1.0F, 1.0F));
    public static final Item MOTH_DUST = registerItem("moth_dust", new MothDustItem());
    public static final Item FERTILIZER = registerItem("fertilizer", new FertilizerItem());
    public static final Item DARK_TATTERS = registerItem("dark_tatters", new Item(new Item.Properties()));
    public static final Item OCCULT_GEM = registerItem("occult_gem", new OccultGemItem());
    public static final Item TOTEM_OF_POSSESSION = registerItem("totem_of_possession", new TotemOfPossessionItem());
    public static final Item DESOLATE_DAGGER = registerItem("desolate_dagger", new DesolateDaggerItem());
    public static final Item CORRODENT_TEETH = registerItem("corrodent_teeth", new Item(new Item.Properties()));
    public static final Item BURROWING_ARROW = registerItem("burrowing_arrow", new BurrowingArrowItem());
    public static final Item VESPER_WING = registerItem("vesper_wing", new Item(new Item.Properties().food(ACFoods.VESPER_WING)));
    public static final Item VESPER_STEW = registerItem("vesper_stew", new Item(new Item.Properties().food(ACFoods.VESPER_SOUP).stacksTo(1)));
    public static final Item PURE_DARKNESS = registerItem("pure_darkness", new Item(new Item.Properties().rarity(getRarityDemonic())));
    public static final Item SHADOW_SILK = registerItem("shadow_silk", new Item(new Item.Properties()));
    public static final Item HOOD_OF_DARKNESS = registerItem("hood_of_darkness", new DarknessArmorItem(DARKNESS_ARMOR_MATERIAL, ArmorItem.Type.HELMET));
    public static final Item CLOAK_OF_DARKNESS = registerItem("cloak_of_darkness", new DarknessArmorItem(DARKNESS_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE));
    public static final Item DARKENED_APPLE = registerItem("darkened_apple", new DarkenedAppleItem());
    public static final Item DREADBOW = registerItem("dreadbow", new DreadbowItem());
    public static final Item DREADBOW_SPRITE = registerItem("dreadbow_inventory", new Item(new Item.Properties()));
    public static final Item DREADBOW_PULLING_0_SPRITE = registerItem("dreadbow_pulling_0_inventory", new Item(new Item.Properties()));
    public static final Item DREADBOW_PULLING_1_SPRITE = registerItem("dreadbow_pulling_1_inventory", new Item(new Item.Properties()));
    public static final Item DREADBOW_PULLING_2_SPRITE = registerItem("dreadbow_pulling_2_inventory", new Item(new Item.Properties()));
    public static final Item THORNWOOD_DOOR = registerItem("thornwood_door", new DoubleHighBlockItem(ACBlockRegistry.THORNWOOD_DOOR, (new Item.Properties())));
    public static final Item THORNWOOD_SIGN = registerItem("thornwood_sign", new SignItem((new Item.Properties()).stacksTo(16), ACBlockRegistry.THORNWOOD_SIGN, ACBlockRegistry.THORNWOOD_WALL_SIGN));
    public static final Item THORNWOOD_HANGING_SIGN = registerItem("thornwood_hanging_sign", new HangingSignItem(ACBlockRegistry.THORNWOOD_HANGING_SIGN, ACBlockRegistry.THORNWOOD_WALL_HANGING_SIGN, (new Item.Properties()).stacksTo(16)));
    public static final Item THORNWOOD_BOAT = registerItem("thornwood_boat", new CaveBoatItem(false, AlexsCavesBoat.Type.THORNWOOD, new Item.Properties().stacksTo(1)));
    public static final Item THORNWOOD_CHEST_BOAT = registerItem("thornwood_chest_boat", new CaveBoatItem(true, AlexsCavesBoat.Type.THORNWOOD, new Item.Properties().stacksTo(1)));
    public static final Item PURPLE_SODA_BUCKET = registerItem("purple_soda_bucket", new BucketItem(ACFluidRegistry.PURPLE_SODA_FLUID_SOURCE, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final Item PURPLE_SODA_BOTTLE = registerItem("purple_soda_bottle", new DrinkableBottledItem(new Item.Properties().stacksTo(16).food(ACFoods.PURPLE_SODA_BOTTLE)));
    public static final Item SWEETISH_FISH_RED_BUCKET = registerItem("sweetish_fish_red_bucket", new SweetishFishBucketItem(GummyColors.RED));
    public static final Item SWEETISH_FISH_GREEN_BUCKET = registerItem("sweetish_fish_green_bucket", new SweetishFishBucketItem(GummyColors.GREEN));
    public static final Item SWEETISH_FISH_BLUE_BUCKET = registerItem("sweetish_fish_blue_bucket", new SweetishFishBucketItem(GummyColors.BLUE));
    public static final Item SWEETISH_FISH_YELLOW_BUCKET = registerItem("sweetish_fish_yellow_bucket", new SweetishFishBucketItem(GummyColors.YELLOW));
    public static final Item SWEETISH_FISH_PINK_BUCKET = registerItem("sweetish_fish_pink_bucket", new SweetishFishBucketItem(GummyColors.PINK));
    public static final Item SWEETISH_FISH_RED = registerItem("sweetish_fish_red", new Item(new Item.Properties().food(ACFoods.SWEETISH_FISH)));
    public static final Item SWEETISH_FISH_GREEN = registerItem("sweetish_fish_green", new Item(new Item.Properties().food(ACFoods.SWEETISH_FISH)));
    public static final Item SWEETISH_FISH_BLUE = registerItem("sweetish_fish_blue", new Item(new Item.Properties().food(ACFoods.SWEETISH_FISH)));
    public static final Item SWEETISH_FISH_YELLOW = registerItem("sweetish_fish_yellow", new Item(new Item.Properties().food(ACFoods.SWEETISH_FISH)));
    public static final Item SWEETISH_FISH_PINK = registerItem("sweetish_fish_pink", new Item(new Item.Properties().food(ACFoods.SWEETISH_FISH)));
    public static final Item GELATIN_RED = registerItem("gelatin_red", new Item(new Item.Properties().food(ACFoods.GELATIN)));
    public static final Item GELATIN_GREEN = registerItem("gelatin_green", new Item(new Item.Properties().food(ACFoods.GELATIN)));
    public static final Item GELATIN_BLUE = registerItem("gelatin_blue", new Item(new Item.Properties().food(ACFoods.GELATIN)));
    public static final Item GELATIN_YELLOW = registerItem("gelatin_yellow", new Item(new Item.Properties().food(ACFoods.GELATIN)));
    public static final Item GELATIN_PINK = registerItem("gelatin_pink", new Item(new Item.Properties().food(ACFoods.GELATIN)));
    public static final Item HOT_CHOCOLATE_BOTTLE = registerItem("hot_chocolate_bottle", new HotChocolateBottleItem());
    public static final Item VANILLA_ICE_CREAM_SCOOP = registerItem("vanilla_ice_cream_scoop", new ThrownProjectileItem(new Item.Properties(), player -> new ThrownIceCreamScoopEntity(player.level(), player), -10.0F, 1.0F, 0.2F));
    public static final Item CHOCOLATE_ICE_CREAM_SCOOP = registerItem("chocolate_ice_cream_scoop", new ThrownProjectileItem(new Item.Properties(), player -> new ThrownIceCreamScoopEntity(player.level(), player), -10.0F, 1.0F, 0.2F));
    public static final Item SWEETBERRY_ICE_CREAM_SCOOP = registerItem("sweetberry_ice_cream_scoop", new ThrownProjectileItem(new Item.Properties(), player -> new ThrownIceCreamScoopEntity(player.level(), player), -10.0F, 1.0F, 0.2F));
    public static final Item SUNDAE = registerItem("sundae", new Item(new Item.Properties().food(ACFoods.SUNDAE).rarity(getRaritySweet()).stacksTo(1)));
    public static final Item SHARPENED_CANDY_CANE = registerItem("sharpened_candy_cane", new SharpenedCandyCaneItem(new Item.Properties().food(ACFoods.CANDY_CANE)));
    public static final Item PEPPERMINT_POWDER = registerItem("peppermint_powder", new Item(new Item.Properties().food(ACFoods.PEPPERMINT_POWDER)));
    public static final Item RAINBOUNCE_BOOTS = registerItem("rainbounce_boots", new RainbounceBootsItem(RAINBOUNCE_ARMOR_MATERIAL));
    public static final Item GUMBALL_PILE = registerItem("gumball_pile", new Item(new Item.Properties().food(ACFoods.GUMBALL_PILE)));
    public static final Item SHOT_GUM = registerItem("shot_gum", new ShotGumItem());
    public static final Item CARAMEL = registerItem("caramel", new Item(new Item.Properties().food(ACFoods.CARAMEL)));
    public static final Item CARAMEL_APPLE = registerItem("caramel_apple", new Item(new Item.Properties().food(ACFoods.CARAMEL_APPLE)));
    public static final Item CANDY_CANE_HOOK = registerItem("candy_cane_hook", new CandyCaneHookItem());
    public static final Item SWEET_TOOTH = registerItem("sweet_tooth", new Item(new Item.Properties().rarity(getRaritySweet())));
    public static final Item RADIANT_ESSENCE = registerItem("radiant_essence", new RadiantEssenceItem());
    public static final Item LICOWITCH_RADIANT_ESSENCE = registerItem("licowitch_radiant_essence", new RadiantEssenceItem());
    public static final Item SACK_OF_SATING = registerItem("sack_of_sating", new SackOfSatingItem());
    public static final Item SUGAR_STAFF = registerItem("sugar_staff", new SugarStaffItem(new Item.Properties().durability(100).rarity(Rarity.UNCOMMON)));
    public static final Item SUGAR_STAFF_SPRITE = registerItem("sugar_staff_inventory", new Item(new Item.Properties()));
    public static final Item GINGERBREAD_CRUMBS = registerItem("gingerbread_crumbs", new Item(new Item.Properties().food(ACFoods.GINGERBREAD_CRUMBS)));
    public static final Item GINGERBREAD_HELMET = registerItem("gingerbread_helmet", new GingerbreadArmorItem(GINGERBREAD_ARMOR_MATERIAL, ArmorItem.Type.HELMET));
    public static final Item GINGERBREAD_CHESTPLATE = registerItem("gingerbread_chestplate", new GingerbreadArmorItem(GINGERBREAD_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE));
    public static final Item GINGERBREAD_LEGGINGS = registerItem("gingerbread_leggings", new GingerbreadArmorItem(GINGERBREAD_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS));
    public static final Item GINGERBREAD_BOOTS = registerItem("gingerbread_boots", new GingerbreadArmorItem(GINGERBREAD_ARMOR_MATERIAL, ArmorItem.Type.BOOTS));
    public static final Item PURPLE_SODA_BOTTLE_ROCKET = registerItem("purple_soda_bottle_rocket", new SodaBottleRocketItem());
    public static final Item FROSTMINT_SPEAR = registerItem("frostmint_spear", new FrostmintSpearItem(new Item.Properties().stacksTo(16)));
    public static final Item FROSTMINT_SPEAR_SPRITE = registerItem("frostmint_spear_inventory", new Item(new Item.Properties()));
    public static final Item MUSIC_DISC_TASTY_FRAGMENT = registerItem("disc_fragment_tasty", new DiscFragmentItem(new Item.Properties()));
    public static final ResourceKey<JukeboxSong> JUKEBOX_SONG_TASTY = ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "tasty"));
    public static final Item MUSIC_DISC_TASTY = registerItem("music_disc_tasty", new Item(new Item.Properties().stacksTo(1).rarity(getRaritySweet()).jukeboxPlayable(JUKEBOX_SONG_TASTY)));
    public static final Item ALEX_MEAL = registerItem("alex_meal", new AlexMealItem());
    public static final Item BIOME_TREAT = registerItem("biome_treat", new BiomeTreatItem());
    public static final Item JELLY_BEAN = registerItem("jelly_bean", new JellyBeanItem());

    static {
        spawnEgg("teletor", ACEntityRegistry.TELETOR, 0X433B4A, 0X0060EF, ACBiomeRegistry.MAGNETIC_CAVES);
        spawnEgg("magnetron", ACEntityRegistry.MAGNETRON, 0XFF002A, 0X203070, ACBiomeRegistry.MAGNETIC_CAVES);
        spawnEgg("boundroid", ACEntityRegistry.BOUNDROID, 0XBB1919, 0XFFFFFF, ACBiomeRegistry.MAGNETIC_CAVES);
        spawnEgg("ferrouslime", ACEntityRegistry.FERROUSLIME, 0X26272D, 0X53556C, ACBiomeRegistry.MAGNETIC_CAVES);
        spawnEgg("notor", ACEntityRegistry.NOTOR, 0X5F5369, 0XC6C6C6, ACBiomeRegistry.MAGNETIC_CAVES);
        spawnEgg("subterranodon", ACEntityRegistry.SUBTERRANODON, 0X00B1B2, 0XFFF11C, ACBiomeRegistry.PRIMORDIAL_CAVES);
        spawnEgg("vallumraptor", ACEntityRegistry.VALLUMRAPTOR, 0X22389A, 0XEEE5AB, ACBiomeRegistry.PRIMORDIAL_CAVES);
        spawnEgg("grottoceratops", ACEntityRegistry.GROTTOCERATOPS, 0XAC3B03, 0XD39B4E, ACBiomeRegistry.PRIMORDIAL_CAVES);
        spawnEgg("trilocaris", ACEntityRegistry.TRILOCARIS, 0X713E0D, 0X8B2010, ACBiomeRegistry.PRIMORDIAL_CAVES);
        spawnEgg("tremorsaurus", ACEntityRegistry.TREMORSAURUS, 0X53780E, 0XDFA211, ACBiomeRegistry.PRIMORDIAL_CAVES);
        spawnEgg("relicheirus", ACEntityRegistry.RELICHEIRUS, 0X6AE4F9, 0X5B2152, ACBiomeRegistry.PRIMORDIAL_CAVES);
        spawnEgg("luxtructosaurus", ACEntityRegistry.LUXTRUCTOSAURUS, 0X1F0E15, 0XB30C03, ACBiomeRegistry.PRIMORDIAL_CAVES);
        spawnEgg("atlatitan", ACEntityRegistry.ATLATITAN, 0XB67000, 0XBFBAA4, ACBiomeRegistry.PRIMORDIAL_CAVES);
        spawnEgg("nucleeper", ACEntityRegistry.NUCLEEPER, 0X95A1A5, 0X00FF00, ACBiomeRegistry.TOXIC_CAVES);
        spawnEgg("radgill", ACEntityRegistry.RADGILL, 0X43302C, 0XE8E400, ACBiomeRegistry.TOXIC_CAVES);
        spawnEgg("brainiac", ACEntityRegistry.BRAINIAC, 0X3E5136, 0XE87C9E, ACBiomeRegistry.TOXIC_CAVES);
        spawnEgg("gammaroach", ACEntityRegistry.GAMMAROACH, 0X56682A, 0X2A2B19, ACBiomeRegistry.TOXIC_CAVES);
        spawnEgg("raycat", ACEntityRegistry.RAYCAT, 0X67FF00, 0X030A00, ACBiomeRegistry.TOXIC_CAVES);
        spawnEgg("tremorzilla", ACEntityRegistry.TREMORZILLA, 0X574D2F, 0X8CFF08, ACBiomeRegistry.TOXIC_CAVES);
        spawnEgg("lanternfish", ACEntityRegistry.LANTERNFISH, 0X182538, 0XECA500, ACBiomeRegistry.ABYSSAL_CHASM);
        spawnEgg("sea_pig", ACEntityRegistry.SEA_PIG, 0XFFA3B9, 0XF88672, ACBiomeRegistry.ABYSSAL_CHASM);
        spawnEgg("hullbreaker", ACEntityRegistry.HULLBREAKER, 0X182538, 0X76FFFD, ACBiomeRegistry.ABYSSAL_CHASM);
        spawnEgg("gossamer_worm", ACEntityRegistry.GOSSAMER_WORM, 0XC8F1FF, 0X96DEF6, ACBiomeRegistry.ABYSSAL_CHASM);
        spawnEgg("tripodfish", ACEntityRegistry.TRIPODFISH, 0X34529D, 0X81A1CF, ACBiomeRegistry.ABYSSAL_CHASM);
        spawnEgg("deep_one", ACEntityRegistry.DEEP_ONE, 0X0D2547, 0X0A843B, ACBiomeRegistry.ABYSSAL_CHASM);
        spawnEgg("deep_one_knight", ACEntityRegistry.DEEP_ONE_KNIGHT, 0X472C3B, 0XD4CCC3, ACBiomeRegistry.ABYSSAL_CHASM);
        spawnEgg("deep_one_mage", ACEntityRegistry.DEEP_ONE_MAGE, 0X96DEF6, 0XD1FF00, ACBiomeRegistry.ABYSSAL_CHASM);
        spawnEgg("mine_guardian", ACEntityRegistry.MINE_GUARDIAN, 0X404253, 0XE62008, ACBiomeRegistry.ABYSSAL_CHASM);
        spawnEgg("gloomoth", ACEntityRegistry.GLOOMOTH, 0X5E463D, 0XEBD3BE, ACBiomeRegistry.FORLORN_HOLLOWS);
        spawnEgg("underzealot", ACEntityRegistry.UNDERZEALOT, 0X291C17, 0XF27C68, ACBiomeRegistry.FORLORN_HOLLOWS);
        spawnEgg("watcher", ACEntityRegistry.WATCHER, 0X291C17, 0XEC1900, ACBiomeRegistry.FORLORN_HOLLOWS);
        spawnEgg("corrodent", ACEntityRegistry.CORRODENT, 0X351A14, 0X593B33, ACBiomeRegistry.FORLORN_HOLLOWS);
        spawnEgg("vesper", ACEntityRegistry.VESPER, 0X884E2A, 0XA54A6B, ACBiomeRegistry.FORLORN_HOLLOWS);
        spawnEgg("forsaken", ACEntityRegistry.FORSAKEN, 0X000000, 0X110909, ACBiomeRegistry.FORLORN_HOLLOWS);
        spawnEgg("sweetish_fish", ACEntityRegistry.SWEETISH_FISH, 0XE9132C, 0XFF364D, ACBiomeRegistry.CANDY_CAVITY);
        spawnEgg("caniac", ACEntityRegistry.CANIAC, 0XF9F0FF, 0XFF3F56, ACBiomeRegistry.CANDY_CAVITY);
        spawnEgg("gumbeeper", ACEntityRegistry.GUMBEEPER, 0XFF2B44, 0XE7BAFF, ACBiomeRegistry.CANDY_CAVITY);
        spawnEgg("candicorn", ACEntityRegistry.CANDICORN, 0XE86B00, 0XFFEF57, ACBiomeRegistry.CANDY_CAVITY);
        spawnEgg("gum_worm", ACEntityRegistry.GUM_WORM, 0X92FFD9, 0XFFA1DC, ACBiomeRegistry.CANDY_CAVITY);
        spawnEgg("caramel_cube", ACEntityRegistry.CARAMEL_CUBE, 0XCC8015, 0XB86A0D, ACBiomeRegistry.CANDY_CAVITY);
        spawnEgg("gummy_bear", ACEntityRegistry.GUMMY_BEAR, 0XFF463F, 0XFDA09E, ACBiomeRegistry.CANDY_CAVITY);
        spawnEgg("licowitch", ACEntityRegistry.LICOWITCH, 0X681182, 0XFF6CD7, ACBiomeRegistry.CANDY_CAVITY);
        spawnEgg("gingerbread_man", ACEntityRegistry.GINGERBREAD_MAN, 0XBB581D, 0XFFFFFF, ACBiomeRegistry.CANDY_CAVITY);
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, name), item);
    }

    private static void spawnEgg(String entityName, EntityType<? extends Mob> type, int color1, int color2, ResourceKey<Biome> biomeTab) {
        Item item = registerItem("spawn_egg_" + entityName, new SpawnEggItem(type, color1, color2, new Item.Properties()));
        creativeTabSpawnEggMap.put(item, biomeTab);
    }

    public static void setup() {
        PRIMORDIAL_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(TOUGH_HIDE));
        HAZMAT_SUIT_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(POLYMER_PLATE));
        DIVING_SUIT_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(Items.COPPER_INGOT));
        DARKNESS_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(DARK_TATTERS));
        GINGERBREAD_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(GINGERBREAD_CRUMBS));
        DispenserBlock.registerBehavior(SEEKING_ARROW, new ACProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                AbstractArrow abstractarrow = new SeekingArrowEntity(level, position.x(), position.y(), position.z());
                abstractarrow.pickup = AbstractArrow.Pickup.ALLOWED;
                return abstractarrow;
            }
        });
        DispenserBlock.registerBehavior(GALENA_GAUNTLET, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
        DispenserBlock.registerBehavior(TRILOCARIS_BUCKET, new FluidContainerDispenseItemBehavior());
        DispenserBlock.registerBehavior(ACID_BUCKET, new FluidContainerDispenseItemBehavior());
        DispenserBlock.registerBehavior(RADGILL_BUCKET, new FluidContainerDispenseItemBehavior());
        DispenserBlock.registerBehavior(CINDER_BRICK, new ACProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                return new CinderBrickEntity(level, position.x(), position.y(), position.z());
            }
        });
        DispenserBlock.registerBehavior(LANTERNFISH_BUCKET, new FluidContainerDispenseItemBehavior());
        DispenserBlock.registerBehavior(TRIPODFISH_BUCKET, new FluidContainerDispenseItemBehavior());
        DispenserBlock.registerBehavior(SEA_PIG_BUCKET, new FluidContainerDispenseItemBehavior());
        DispenserBlock.registerBehavior(GOSSAMER_WORM_BUCKET, new FluidContainerDispenseItemBehavior());
        DispenserBlock.registerBehavior(INK_BOMB, new ACProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                return new InkBombEntity(level, position.x(), position.y(), position.z());
            }
        });
        DispenserBlock.registerBehavior(GLOW_INK_BOMB, new ACProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                InkBombEntity inkBombEntity = new InkBombEntity(level, position.x(), position.y(), position.z());
                inkBombEntity.setGlowingBomb(true);
                inkBombEntity.setItem(itemStack);
                return inkBombEntity;
            }
        });
        DispenserBlock.registerBehavior(GUANO, new ACProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                return new GuanoEntity(level, position.x(), position.y(), position.z());
            }
        });
        DispenserBlock.registerBehavior(BURROWING_ARROW, new ACProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                AbstractArrow abstractarrow = new BurrowingArrowEntity(level, position.x(), position.y(), position.z());
                abstractarrow.pickup = AbstractArrow.Pickup.ALLOWED;
                return abstractarrow;
            }
        });
        DispenserBlock.registerBehavior(ACBlockRegistry.NUCLEAR_BOMB.asItem(), new DefaultDispenseItemBehavior() {
            protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                Level level = blockSource.level();
                BlockPos blockpos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
                NuclearBombEntity nuclearBomb = new NuclearBombEntity(level, (double)blockpos.getX() + 0.5D, (double)blockpos.getY(), (double)blockpos.getZ() + 0.5D);
                level.addFreshEntity(nuclearBomb);
                level.playSound((Player)null, nuclearBomb.getX(), nuclearBomb.getY(), nuclearBomb.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent((Entity)null, GameEvent.ENTITY_PLACE, blockpos);
                itemStack.shrink(1);
                return itemStack;
            }
        });
        DispenserBlock.registerBehavior(PURPLE_SODA_BUCKET, new FluidContainerDispenseItemBehavior());
        DispenserBlock.registerBehavior(SWEETISH_FISH_RED_BUCKET, new FluidContainerDispenseItemBehavior());
        DispenserBlock.registerBehavior(SWEETISH_FISH_GREEN_BUCKET, new FluidContainerDispenseItemBehavior());
        DispenserBlock.registerBehavior(SWEETISH_FISH_BLUE_BUCKET, new FluidContainerDispenseItemBehavior());
        DispenserBlock.registerBehavior(SWEETISH_FISH_YELLOW_BUCKET, new FluidContainerDispenseItemBehavior());
        DispenserBlock.registerBehavior(SWEETISH_FISH_PINK_BUCKET, new FluidContainerDispenseItemBehavior());
        LecternBooks.BOOKS.put(BuiltInRegistries.ITEM.getKey(CAVE_BOOK), new LecternBooks.BookData(0X81301C, 0XFDF8EC));
    }

    public static List<Item> getSpawnEggsForTab(ResourceKey<Biome> tabName) {
        List<Item> list = new ArrayList<>();
        for (Map.Entry<Item, ResourceKey<Biome>> entry : creativeTabSpawnEggMap.entrySet()) {
            if (entry.getValue().equals(tabName)) {
                list.add(entry.getKey());
            }
        }
        return list;
    }

    public static Item getSpawnEggFor(EntityType<?> type) {
        for (Map.Entry<Item, ResourceKey<Biome>> entry : creativeTabSpawnEggMap.entrySet()) {
            Item item = entry.getKey();
            if (item instanceof SpawnEggItem spawnEggItem && spawnEggItem.getType(null) == type) {
                return spawnEggItem;
            }
        }
        return Items.AIR;
    }
}