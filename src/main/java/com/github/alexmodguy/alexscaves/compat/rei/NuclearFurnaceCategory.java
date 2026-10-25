package com.github.alexmodguy.alexscaves.compat.rei;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.NuclearFurnaceBlockEntity;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class NuclearFurnaceCategory implements DisplayCategory<NuclearFurnaceDisplay> {

    public static final CategoryIdentifier<NuclearFurnaceDisplay> CATEGORY =
            CategoryIdentifier.of(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "nuclear_furnace"));

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/gui/nuclear_furnace.png");

    @Override
    public CategoryIdentifier<? extends NuclearFurnaceDisplay> getCategoryIdentifier() {
        return CATEGORY;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("alexscaves.container.nuclear_furnace_blasting");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ACBlockRegistry.NUCLEAR_FURNACE_COMPONENT);
    }

    @Override
    public int getDisplayHeight() {
        return 60;
    }

    @Override
    public int getDisplayWidth(NuclearFurnaceDisplay display) {
        return 150;
    }

    @Override
    public List<Widget> setupDisplay(NuclearFurnaceDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createTexturedWidget(TEXTURE, bounds.x, bounds.y, 5, 15, 150, 60));

        widgets.add(Widgets.createDrawableWidget((guiGraphics, mouseX, mouseY, delta) -> {
            if (Minecraft.getInstance().player == null) return;
            int ticks = Minecraft.getInstance().player.tickCount;
            int cookPixels = (int) Math.ceil(24 * ((ticks + 40) % 20 / 20F));
            int fillAnimateTime = ticks % 100;
            if (fillAnimateTime < 70) {
                int barrelPixels = (int) Math.ceil(14 * (fillAnimateTime / 70F));
                guiGraphics.blit(TEXTURE, bounds.x + 33, bounds.y + 21 + (14 - barrelPixels),
                        192, (14 - barrelPixels), 15, barrelPixels);
                int wastePixels = 5;
                guiGraphics.blit(TEXTURE, bounds.x + 8, bounds.y + 2 + (52 - wastePixels),
                        176, 32 + (52 - wastePixels), 16, wastePixels);
            }
            guiGraphics.blit(TEXTURE, bounds.x + 86, bounds.y + 20, 176, 14, cookPixels, 17);
            guiGraphics.blit(TEXTURE, bounds.x + 63, bounds.y + 21, 176, 0, 14, 14);

            if (display.getRecipe() != null) {
                Font font = Minecraft.getInstance().font;
                float experience = display.getRecipe().getExperience();
                if (experience > 0) {
                    Component exp = Component.translatable("gui.jei.category.smelting.experience", experience);
                    guiGraphics.drawString(font, exp, bounds.x + 150 - font.width(exp), bounds.y,
                            0xFF808080, false);
                }
                int cookTime = (int) Math.ceil(display.getRecipe().getCookingTime()
                        * NuclearFurnaceBlockEntity.getSpeedReduction());
                if (cookTime > 0) {
                    Component time = Component.translatable("gui.jei.category.smelting.time.seconds", cookTime / 20);
                    guiGraphics.drawString(font, time, bounds.x + 150 - font.width(time), bounds.y + 50,
                            0xFF808080, false);
                }
            }
        }));

        if (!display.getInputEntries().isEmpty()) {
            widgets.add(Widgets.createSlot(new Point(bounds.x + 62, bounds.y + 38))
                    .entries(display.getInputEntries().get(0)).markInput());
        }
        if (display.getInputEntries().size() > 1) {
            widgets.add(Widgets.createSlot(new Point(bounds.x + 32, bounds.y + 38))
                    .entries(display.getInputEntries().get(1)).markInput());
        }
        if (display.getInputEntries().size() > 2) {
            widgets.add(Widgets.createSlot(new Point(bounds.x + 62, bounds.y + 2))
                    .entries(display.getInputEntries().get(2)).markInput());
        }
        if (!display.getOutputEntries().isEmpty()) {
            widgets.add(Widgets.createSlot(new Point(bounds.x + 32, bounds.y + 2))
                    .entries(display.getOutputEntries().get(0)).markOutput());
        }
        if (display.getOutputEntries().size() > 1) {
            widgets.add(Widgets.createSlot(new Point(bounds.x + 122, bounds.y + 20))
                    .entries(display.getOutputEntries().get(1)).markOutput());
        }

        return widgets;
    }
}