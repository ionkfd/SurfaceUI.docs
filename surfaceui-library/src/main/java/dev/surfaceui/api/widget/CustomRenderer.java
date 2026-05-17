package dev.surfaceui.api.widget;

import dev.surfaceui.api.layout.LayoutRect;
import dev.surfaceui.api.style.Style;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

@FunctionalInterface
public interface CustomRenderer {
    void render(DrawContext context, RenderTickCounter tickCounter, LayoutRect rect, Style style);
}
