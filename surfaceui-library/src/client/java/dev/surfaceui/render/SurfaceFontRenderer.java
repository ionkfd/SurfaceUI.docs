package dev.surfaceui.render;

import dev.surfaceui.api.style.Style;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public interface SurfaceFontRenderer {
    SurfaceTextMetrics measureText(String text, float size);

    int getTextHeight(float size);

    void drawText(DrawContext context, String text, int x, int y, Style style);

    void drawTextCentered(DrawContext context, String text, int x, int y, int width, int height, Style style);

    Identifier defaultFont();
}
