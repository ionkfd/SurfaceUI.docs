package dev.surfaceui.render;

import dev.surfaceui.api.layout.LayoutRect;
import dev.surfaceui.api.style.Paint;
import dev.surfaceui.api.style.Radius;
import dev.surfaceui.api.style.Style;
import net.minecraft.util.Identifier;

public interface SurfaceCanvas {
    void drawRect(LayoutRect rect, Paint paint, float opacity);

    void drawRoundedRect(LayoutRect rect, Radius radius, Paint paint, float opacity);

    void drawRoundedOutline(LayoutRect rect, Radius radius, int width, Paint paint, float opacity);

    void drawGradientRect(LayoutRect rect, Paint top, Paint bottom, float opacity);

    void drawRoundedGradientRect(LayoutRect rect, Radius radius, Paint top, Paint bottom, int borderWidth, Paint border, float opacity);

    void drawShadow(LayoutRect rect, Radius radius, Paint paint, int blur, int offsetX, int offsetY, float opacity);

    void drawText(String text, int x, int y, Style style);

    void drawTextCentered(String text, LayoutRect rect, Style style);

    void drawImage(Identifier texture, LayoutRect rect);

    void pushScissor(LayoutRect rect);

    void popScissor();
}
