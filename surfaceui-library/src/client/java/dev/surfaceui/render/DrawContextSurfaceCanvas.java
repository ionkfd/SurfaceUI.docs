package dev.surfaceui.render;

import dev.surfaceui.api.layout.LayoutRect;
import dev.surfaceui.api.style.Paint;
import dev.surfaceui.api.style.Radius;
import dev.surfaceui.api.style.Shadow;
import dev.surfaceui.api.style.Style;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public final class DrawContextSurfaceCanvas implements SurfaceCanvas {
    private static final SmoothShapeCache SHAPES = new SmoothShapeCache();
    private final DrawContext context;
    private final Deque<LayoutRect> scissors = new ArrayDeque<>();

    public DrawContextSurfaceCanvas(DrawContext context) {
        this.context = context;
    }

    @Override
    public void drawRect(LayoutRect rect, Paint paint, float opacity) {
        if (visible(rect, paint.withAlpha(opacity))) {
            drawRoundedGradientRect(rect, Radius.ZERO, paint, paint, 0, Paint.TRANSPARENT, opacity);
        }
    }

    @Override
    public void drawRoundedRect(LayoutRect rect, Radius radius, Paint paint, float opacity) {
        drawRoundedGradientRect(rect, radius, paint, paint, 0, Paint.TRANSPARENT, opacity);
    }

    @Override
    public void drawRoundedOutline(LayoutRect rect, Radius radius, int width, Paint paint, float opacity) {
        drawRoundedGradientRect(rect, radius, Paint.TRANSPARENT, Paint.TRANSPARENT, width, paint, opacity);
    }

    @Override
    public void drawGradientRect(LayoutRect rect, Paint top, Paint bottom, float opacity) {
        if (rect.width() > 0 && rect.height() > 0) {
            drawRoundedGradientRect(rect, Radius.ZERO, top, bottom, 0, Paint.TRANSPARENT, opacity);
        }
    }

    @Override
    public void drawRoundedGradientRect(LayoutRect rect, Radius radius, Paint top, Paint bottom, int borderWidth, Paint border, float opacity) {
        if (rect.width() <= 0 || rect.height() <= 0) {
            return;
        }
        if ((top.withAlpha(opacity).argb() >>> 24) == 0 && (bottom.withAlpha(opacity).argb() >>> 24) == 0 && (border.withAlpha(opacity).argb() >>> 24) == 0) {
            return;
        }

        SmoothShapeCache.CachedShape texture = SHAPES.rounded(rect, radius, top, bottom, borderWidth, border, opacity);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, texture.id(), rect.x(), rect.y(), 0.0F, 0.0F, rect.width(), rect.height(), texture.textureWidth(), texture.textureHeight(), texture.textureWidth(), texture.textureHeight());
    }

    @Override
    public void drawShadow(LayoutRect rect, Radius radius, Paint paint, int blur, int offsetX, int offsetY, float opacity) {
        Shadow shadow = new Shadow(offsetX, offsetY, blur, paint);
        if ((shadow.paint().withAlpha(opacity).argb() >>> 24) == 0) {
            return;
        }
        int grow = Math.max(0, blur / 2);
        LayoutRect shadowRect = new LayoutRect(rect.x() + offsetX - grow, rect.y() + offsetY - grow, rect.width() + grow * 2, rect.height() + grow * 2);
        drawRoundedGradientRect(shadowRect, Radius.all(Math.max(0, radius.topLeft() + grow)), paint, paint, 0, Paint.TRANSPARENT, opacity);
    }

    @Override
    public void drawText(String text, int x, int y, Style style) {
        SurfaceFonts.renderer().drawText(context, text, x, y, style);
    }

    @Override
    public void drawTextCentered(String text, LayoutRect rect, Style style) {
        SurfaceFonts.renderer().drawTextCentered(context, text, rect.x(), rect.y(), rect.width(), rect.height(), style);
    }

    @Override
    public void drawImage(Identifier texture, LayoutRect rect) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, rect.x(), rect.y(), 0.0F, 0.0F, rect.width(), rect.height(), rect.width(), rect.height());
    }

    @Override
    public void pushScissor(LayoutRect rect) {
        LayoutRect next = scissors.isEmpty() ? rect : scissors.peek().intersect(rect);
        scissors.push(next);
        context.enableScissor(next.x(), next.y(), next.x() + next.width(), next.y() + next.height());
    }

    @Override
    public void popScissor() {
        if (!scissors.isEmpty()) {
            scissors.pop();
        }
        context.disableScissor();
        if (!scissors.isEmpty()) {
            LayoutRect next = scissors.peek();
            context.enableScissor(next.x(), next.y(), next.x() + next.width(), next.y() + next.height());
        }
    }

    private boolean visible(LayoutRect rect, Paint paint) {
        return rect.width() > 0 && rect.height() > 0 && (paint.argb() >>> 24) != 0;
    }

}
