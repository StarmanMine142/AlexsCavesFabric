package com.github.alexmodguy.alexscaves.compat.rei;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class SpelunkeryTableCategory implements DisplayCategory<SpelunkeryTableDisplay> {

    public static final CategoryIdentifier<SpelunkeryTableDisplay> CATEGORY =
            CategoryIdentifier.of(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "spelunkery_table"));

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/gui/spelunkery_table_jei.png");
    private static final ResourceLocation TEXTURE_WIDGETS =
            ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/gui/spelunkery_table_widgets.png");

    @Override
    public CategoryIdentifier<? extends SpelunkeryTableDisplay> getCategoryIdentifier() {
        return CATEGORY;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("alexscaves.container.spelunkery_table_translation");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ACBlockRegistry.SPELUNKERY_TABLE);
    }

    @Override
    public int getDisplayHeight() {
        return 27;
    }

    @Override
    public int getDisplayWidth(SpelunkeryTableDisplay display) {
        return 136;
    }

    @Override
    public List<Widget> setupDisplay(SpelunkeryTableDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createTexturedWidget(TEXTURE, bounds.x, bounds.y, 0, 0, 136, 27));

        widgets.add(Widgets.createDrawableWidget((guiGraphics, mouseX, mouseY, delta) -> {
            if (Minecraft.getInstance().player == null) return;
            int bulbs = Minecraft.getInstance().player.tickCount % 40 / 10;
            for (int i = 0; i < bulbs; i++) {
                guiGraphics.blit(TEXTURE_WIDGETS, bounds.x + 56 + i * 15, bounds.y + 7, 0, 0, 13, 14);
            }
        }));

        widgets.add(Widgets.createSlot(new Point(bounds.x + 14, bounds.y + 6))
                .entries(display.getInputEntries().get(0)).markInput());
        widgets.add(Widgets.createSlot(new Point(bounds.x + 34, bounds.y + 6))
                .entries(display.getInputEntries().get(1)).markInput());
        widgets.add(Widgets.createSlot(new Point(bounds.x + 106, bounds.y + 6))
                .entries(display.getOutputEntries().get(0)).markOutput());

        return widgets;
    }
}