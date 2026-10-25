package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Armor material helper class for Fabric 1.21.
 * In 1.21, ArmorMaterial is a record and must be registered directly.
 */
public class ACArmorMaterial {

    protected static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};

    private final String name;
    private final int durability;
    private final int[] damageReduction;
    private final int enchantability;
    private final Holder<SoundEvent> sound;
    private final float toughness;
    private final AtomicReference<Ingredient> repairIngredient = new AtomicReference<>(Ingredient.EMPTY);
    private final Supplier<Ingredient> ingredient = repairIngredient::get;
    public float knockbackResistance = 0.0F;
    private final Holder<ArmorMaterial> holder;

    public ACArmorMaterial(String name, int durability, int[] damageReduction, int enchantability, Holder<SoundEvent> sound, float toughness) {
        this.name = name;
        this.durability = durability;
        this.damageReduction = damageReduction;
        this.enchantability = enchantability;
        this.sound = sound;
        this.toughness = toughness;
        this.knockbackResistance = 0;
        this.holder = registerMaterial();
    }

    public ACArmorMaterial(String name, int durability, int[] damageReduction, int enchantability, Holder<SoundEvent> sound, float toughness, float knockbackResist) {
        this.name = name;
        this.durability = durability;
        this.damageReduction = damageReduction;
        this.enchantability = enchantability;
        this.sound = sound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResist;
        this.holder = registerMaterial();
    }

    private Holder<ArmorMaterial> registerMaterial() {
        EnumMap<ArmorItem.Type, Integer> defenseMap = Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
            map.put(ArmorItem.Type.BOOTS, this.damageReduction[3]);
            map.put(ArmorItem.Type.LEGGINGS, this.damageReduction[2]);
            map.put(ArmorItem.Type.CHESTPLATE, this.damageReduction[1]);
            map.put(ArmorItem.Type.HELMET, this.damageReduction[0]);
            map.put(ArmorItem.Type.BODY, this.damageReduction[1]);
        });
        ArmorMaterial material = new ArmorMaterial(
                defenseMap,
                this.enchantability,
                this.sound,
                this.ingredient,
                List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, this.name))),
                this.toughness,
                this.knockbackResistance
        );
        return Registry.registerForHolder(
                BuiltInRegistries.ARMOR_MATERIAL,
                ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, this.name),
                material
        );
    }

    public int getDurabilityForType(ArmorItem.Type type) {
        return MAX_DAMAGE_ARRAY[type.ordinal()] * this.durability;
    }

    public int getDefenseForType(ArmorItem.Type type) {
        return this.damageReduction[type.ordinal()];
    }

    public int getEnchantmentValue() {
        return this.enchantability;
    }

    public Holder<SoundEvent> getEquipSound() {
        return this.sound;
    }

    public Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }

    public void setRepairMaterial(Ingredient ingredient) {
        repairIngredient.set(ingredient);
    }

    public String getName() {
        return name;
    }

    public float getToughness() {
        return toughness;
    }

    public float getKnockbackResistance() {
        return knockbackResistance;
    }

    public Holder<ArmorMaterial> getHolder() {
        return holder;
    }
}