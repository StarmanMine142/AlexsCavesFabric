package com.github.alexmodguy.alexscaves.server.enchantment;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

/**
 * Helper class for working with enchantments in 1.21.
 * Since enchantments are now data-driven, we need to look up Holders
 * from ResourceKeys via the registry access.
 */
public class ACEnchantmentHelper {

    /**
     * Gets the enchantment level for a given enchantment key on an item stack.
     * 
     * @param level The world level (for registry access)
     * @param enchantmentKey The ResourceKey of the enchantment
     * @param stack The item stack to check
     * @return The enchantment level, or 0 if not present or registry unavailable
     */
    public static int getEnchantmentLevel(Level level, ResourceKey<Enchantment> enchantmentKey, ItemStack stack) {
        if (level == null || enchantmentKey == null || stack.isEmpty()) {
            return 0;
        }
        return getEnchantmentLevel(level.registryAccess(), enchantmentKey, stack);
    }

    /**
     * Gets the enchantment level for a given enchantment key on an item stack.
     * 
     * @param registryAccess The registry access
     * @param enchantmentKey The ResourceKey of the enchantment
     * @param stack The item stack to check
     * @return The enchantment level, or 0 if not present or registry unavailable
     */
    public static int getEnchantmentLevel(RegistryAccess registryAccess, ResourceKey<Enchantment> enchantmentKey, ItemStack stack) {
        if (registryAccess == null || enchantmentKey == null || stack.isEmpty()) {
            return 0;
        }
        try {
            Holder<Enchantment> holder = registryAccess.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantmentKey);
            return EnchantmentHelper.getItemEnchantmentLevel(holder, stack);
        } catch (Exception e) {
            // Enchantment not registered or other error
            return 0;
        }
    }

    /**
     * Checks if an item has a specific enchantment (level > 0).
     * 
     * @param level The world level (for registry access)
     * @param enchantmentKey The ResourceKey of the enchantment
     * @param stack The item stack to check
     * @return true if the enchantment is present
     */
    public static boolean hasEnchantment(Level level, ResourceKey<Enchantment> enchantmentKey, ItemStack stack) {
        return getEnchantmentLevel(level, enchantmentKey, stack) > 0;
    }

    /**
     * Gets the enchantment level directly from the ItemStack without needing a Level.
     * This method iterates through the enchantments on the stack and matches by ResourceKey.
     * Useful for methods like getDefaultAttributeModifiers where Level is not available.
     * 
     * @param enchantmentKey The ResourceKey of the enchantment to check
     * @param stack The item stack to check
     * @return The enchantment level, or 0 if not present
     */
    public static int getEnchantmentLevelFromStack(ResourceKey<Enchantment> enchantmentKey, ItemStack stack) {
        if (enchantmentKey == null || stack.isEmpty()) {
            return 0;
        }
        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Holder<Enchantment> holder : enchantments.keySet()) {
            if (holder.is(enchantmentKey)) {
                return enchantments.getLevel(holder);
            }
        }
        return 0;
    }
}
