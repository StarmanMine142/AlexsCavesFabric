package com.github.alexmodguy.alexscaves.client.particle;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class ACParticleRegistry {

    public static final SimpleParticleType SCARLET_MAGNETIC_ORBIT = register("scarlet_magnetic_orbit", simpleParticleType());
    public static final SimpleParticleType AZURE_MAGNETIC_ORBIT = register("azure_magnetic_orbit", simpleParticleType());
    public static final SimpleParticleType SCARLET_MAGNETIC_FLOW = register("scarlet_magnetic_flow", simpleParticleType());
    public static final SimpleParticleType AZURE_MAGNETIC_FLOW = register("azure_magnetic_flow", simpleParticleType());
    public static final SimpleParticleType GALENA_DEBRIS = register("galena_debris", simpleParticleType());
    public static final SimpleParticleType TESLA_BULB_LIGHTNING = register("tesla_bulb_lightning", simpleParticleType());
    public static final SimpleParticleType MAGNET_LIGHTNING = register("magnet_lightning", simpleParticleType());
    public static final SimpleParticleType MAGNETIC_CAVES_AMBIENT = register("magnetic_caves_ambient", simpleParticleType());
    public static final SimpleParticleType FERROUSLIME = register("ferrouslime", simpleParticleType());
    public static final SimpleParticleType QUARRY_BORDER_LIGHTING = register("quarry_border_lightning", simpleParticleType());
    public static final SimpleParticleType SCARLET_SHIELD_LIGHTNING = register("scarlet_shield_lightning", simpleParticleType());
    public static final SimpleParticleType AZURE_SHIELD_LIGHTNING = register("azure_shield_lightning", simpleParticleType());
    public static final SimpleParticleType FLY = register("fly", simpleParticleType());
    public static final SimpleParticleType WATER_TREMOR = register("water_tremor", simpleParticleType());
    public static final SimpleParticleType AMBER_MONOLITH = register("amber_monolith", simpleParticleType());
    public static final SimpleParticleType AMBER_EXPLOSION = register("amber_explosion", simpleParticleType());
    public static final SimpleParticleType DINOSAUR_TRANSFORMATION_AMBER = register("dinosaur_transformation_amber", simpleParticleType());
    public static final SimpleParticleType DINOSAUR_TRANSFORMATION_TECTONIC = register("dinosaur_transformation_tectonic", simpleParticleType());
    public static final SimpleParticleType STUN_STAR = register("stun_star", simpleParticleType());
    public static final SimpleParticleType TEPHRA = register("tephra", simpleParticleType());
    public static final SimpleParticleType TEPHRA_SMALL = register("tephra_small", simpleParticleType());
    public static final SimpleParticleType TEPHRA_FLAME = register("tephra_flame", simpleParticleType());
    public static final SimpleParticleType LUXTRUCTOSAURUS_SPIT = register("luxtructosaurus_spit", simpleParticleType());
    public static final SimpleParticleType LUXTRUCTOSAURUS_ASH = register("luxtructosaurus_ash", simpleParticleType());
    public static final SimpleParticleType HAPPINESS = register("happiness", simpleParticleType());
    public static final SimpleParticleType ACID_BUBBLE = register("acid_bubble", simpleParticleType());
    public static final SimpleParticleType BLACK_VENT_SMOKE = register("black_vent_smoke", simpleParticleType());
    public static final SimpleParticleType WHITE_VENT_SMOKE = register("white_vent_smoke", simpleParticleType());
    public static final SimpleParticleType GREEN_VENT_SMOKE = register("green_vent_smoke", simpleParticleType());
    public static final SimpleParticleType RED_VENT_SMOKE = register("red_vent_smoke", simpleParticleType());
    public static final SimpleParticleType MUSHROOM_CLOUD = register("mushroom_cloud", simpleParticleType());
    public static final SimpleParticleType MUSHROOM_CLOUD_SMOKE = register("mushroom_cloud_smoke", simpleParticleType());
    public static final SimpleParticleType MUSHROOM_CLOUD_EXPLOSION = register("mushroom_cloud_explosion", simpleParticleType());
    public static final SimpleParticleType PROTON = register("proton", simpleParticleType());
    public static final SimpleParticleType FALLOUT = register("fallout", simpleParticleType());
    public static final SimpleParticleType GAMMAROACH = register("gammaroach", simpleParticleType());
    public static final SimpleParticleType HAZMAT_BREATHE = register("hazmat_breathe", simpleParticleType());
    public static final SimpleParticleType BLUE_HAZMAT_BREATHE = register("blue_hazmat_breathe", simpleParticleType());
    public static final SimpleParticleType RADGILL_SPLASH = register("radgill_splash", simpleParticleType());
    public static final SimpleParticleType ACID_DROP = register("acid_drop", simpleParticleType());
    public static final SimpleParticleType NUCLEAR_SIREN_SONAR = register("nuclear_siren_sonar", simpleParticleType());
    public static final SimpleParticleType RAYGUN_EXPLOSION = register("raygun_explosion", simpleParticleType());
    public static final SimpleParticleType BLUE_RAYGUN_EXPLOSION = register("blue_raygun_explosion", simpleParticleType());
    public static final SimpleParticleType RAYGUN_BLAST = register("raygun_blast", simpleParticleType());
    public static final SimpleParticleType TREMORZILLA_EXPLOSION = register("tremorzilla_explosion", simpleParticleType());
    public static final SimpleParticleType TREMORZILLA_RETRO_EXPLOSION = register("tremorzilla_retro_explosion", simpleParticleType());
    public static final SimpleParticleType TREMORZILLA_TECTONIC_EXPLOSION = register("tremorzilla_tectonic_explosion", simpleParticleType());
    public static final SimpleParticleType TREMORZILLA_PROTON = register("tremorzilla_proton", simpleParticleType());
    public static final SimpleParticleType TREMORZILLA_RETRO_PROTON = register("tremorzilla_retro_proton", simpleParticleType());
    public static final SimpleParticleType TREMORZILLA_TECTONIC_PROTON = register("tremorzilla_tectonic_proton", simpleParticleType());
    public static final SimpleParticleType TREMORZILLA_LIGHTNING = register("tremorzilla_lightning", simpleParticleType());
    public static final SimpleParticleType TREMORZILLA_RETRO_LIGHTNING = register("tremorzilla_retro_lightning", simpleParticleType());
    public static final SimpleParticleType TREMORZILLA_TECTONIC_LIGHTNING = register("tremorzilla_tectonic_lightning", simpleParticleType());
    public static final SimpleParticleType TREMORZILLA_BLAST = register("tremorzilla_blast", simpleParticleType());
    public static final SimpleParticleType TREMORZILLA_STEAM = register("tremorzilla_steam", simpleParticleType());
    public static final SimpleParticleType TUBE_WORM = register("tube_worm", simpleParticleType());
    public static final SimpleParticleType DEEP_ONE_MAGIC = register("deep_one_magic", simpleParticleType());
    public static final SimpleParticleType WATER_FOAM = register("water_foam", simpleParticleType());
    public static final SimpleParticleType BIG_SPLASH = register("big_splash", simpleParticleType());
    public static final SimpleParticleType BIG_SPLASH_EFFECT = register("big_splash_effect", simpleParticleType());
    public static final SimpleParticleType MINE_EXPLOSION = register("mine_explosion", simpleParticleType());
    public static final SimpleParticleType BIO_POP = register("bio_pop", simpleParticleType());
    public static final SimpleParticleType WATCHER_APPEARANCE = register("watcher_appearance", simpleParticleType());
    public static final SimpleParticleType VOID_BEING_CLOUD = register("void_being_cloud", simpleParticleType());
    public static final SimpleParticleType VOID_BEING_TENDRIL = register("void_being_tendril", simpleParticleType());
    public static final SimpleParticleType VOID_BEING_EYE = register("void_being_eye", simpleParticleType());
    public static final SimpleParticleType UNDERZEALOT_MAGIC = register("underzealot_magic", simpleParticleType());
    public static final SimpleParticleType UNDERZEALOT_EXPLOSION = register("underzealot_explosion", simpleParticleType());
    public static final SimpleParticleType FALLING_GUANO = register("falling_guano", simpleParticleType());
    public static final SimpleParticleType MOTH_DUST = register("moth_dust", simpleParticleType());
    public static final SimpleParticleType FORSAKEN_SPIT = register("forsaken_spit", simpleParticleType());
    public static final SimpleParticleType FORSAKEN_SONAR = register("forsaken_sonar", simpleParticleType());
    public static final SimpleParticleType FORSAKEN_SONAR_LARGE = register("forsaken_sonar_large", simpleParticleType());
    public static final SimpleParticleType TOTEM_EXPLOSION = register("totem_explosion", simpleParticleType());
    public static final SimpleParticleType ICE_CREAM_DRIP = register("ice_cream_drip", simpleParticleType());
    public static final SimpleParticleType ICE_CREAM_SPLASH = register("ice_cream_splash", simpleParticleType());
    public static final SimpleParticleType PURPLE_SODA_BUBBLE = register("purple_soda_bubble", simpleParticleType());
    public static final SimpleParticleType PURPLE_SODA_BUBBLE_EMITTER = register("purple_soda_bubble_emitter", simpleParticleType());
    public static final SimpleParticleType PURPLE_SODA_FIZZ = register("purple_soda_fizz", simpleParticleType());
    public static final SimpleParticleType SUNDROP = register("sundrop", simpleParticleType());
    public static final SimpleParticleType RAINBOW = register("rainbow", simpleParticleType());
    public static final SimpleParticleType PLAYER_RAINBOW = register("player_rainbow", simpleParticleType());
    public static final SimpleParticleType CANDICORN_CHARGE = register("candicorn_charge", simpleParticleType());
    public static final ParticleType<BlockParticleOption> BIG_BLOCK_DUST = register("big_block_dust", createBlockParticleType());
    public static final SimpleParticleType CARAMEL_DROP = register("caramel_drop", simpleParticleType());
    public static final ParticleType<ItemParticleOption> JELLY_BEAN_EAT = register("jelly_bean_eat", createItemParticleType());
    public static final SimpleParticleType SLEEP = register("sleep", simpleParticleType());
    public static final SimpleParticleType WITCH_COOKIE = register("witch_cookie", simpleParticleType());
    public static final SimpleParticleType PURPLE_WITCH_MAGIC = register("purple_witch_magic", simpleParticleType());
    public static final SimpleParticleType PURPLE_WITCH_EXPLOSION = register("purple_witch_explosion", simpleParticleType());
    public static final SimpleParticleType GOBTHUMPER = register("gobthumper", simpleParticleType());
    public static final SimpleParticleType COLORED_DUST = register("colored_dust", simpleParticleType());
    public static final SimpleParticleType SMALL_COLORED_DUST = register("small_colored_dust", simpleParticleType());
    public static final SimpleParticleType CONVERSION_CRUCIBLE_EXPLOSION = register("conversion_crucible_explosion", simpleParticleType());
    public static final SimpleParticleType FROSTMINT_EXPLOSION = register("frostmint_explosion", simpleParticleType());
    public static final SimpleParticleType SUGAR_FLAKE = register("sugar_flake", simpleParticleType());

    private static <T extends ParticleType<?>> T register(String name, T type) {
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, name), type);
        return type;
    }

    private static ParticleType<BlockParticleOption> createBlockParticleType() {
        return new ParticleType<>(false) {
            @Override
            public MapCodec<BlockParticleOption> codec() {
                return BlockParticleOption.codec(this);
            }
            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, BlockParticleOption> streamCodec() {
                return BlockParticleOption.streamCodec(this);
            }
        };
    }

    private static ParticleType<ItemParticleOption> createItemParticleType() {
        return new ParticleType<>(false) {
            @Override
            public MapCodec<ItemParticleOption> codec() {
                return ItemParticleOption.codec(this);
            }
            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, ItemParticleOption> streamCodec() {
                return ItemParticleOption.streamCodec(this);
            }
        };
    }

    private static SimpleParticleType simpleParticleType() {
        return new ACSimpleParticleType(false);
    }

    private static class ACSimpleParticleType extends SimpleParticleType {
        protected ACSimpleParticleType(boolean overrideLimiter) {
            super(overrideLimiter);
        }
    }
}