package com.github.alexmodguy.alexscaves.mixin;

import net.minecraft.locale.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.BiConsumer;

@Mixin(Language.class)
public class LanguageMixin {
    // Automatically redirect fml.menu.mods.info.description.alexscaves to modmenu.descriptionTranslation.alexscaves
    @ModifyVariable(method = "loadFromJson", at = @At("HEAD"), argsOnly = true)
    private static BiConsumer<String, String> wrapTranslationOutput(BiConsumer<String, String> output) {
        return (key, text) -> {
            output.accept(key, text);

            if ("fml.menu.mods.info.description.alexscaves".equals(key)) {
                output.accept("modmenu.descriptionTranslation.alexscaves", text);
            }
        };
    }
}