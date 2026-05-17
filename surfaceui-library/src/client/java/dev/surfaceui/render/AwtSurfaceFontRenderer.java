package dev.surfaceui.render;

import com.mojang.logging.LogUtils;
import dev.surfaceui.api.style.Style;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public final class AwtSurfaceFontRenderer implements SurfaceFontRenderer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Identifier DEFAULT_FONT = Identifier.of("surfaceui", "fonts/inter_regular.ttf");
    private static final int TEXTURE_SCALE = 2;
    private final Map<Identifier, Font> fonts = new HashMap<>();
    private final Map<TextKey, CachedText> textCache = new HashMap<>();
    private final BufferedImage metricsImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

    @Override
    public SurfaceTextMetrics measureText(String text, float size) {
        FontMetrics metrics = metrics(size);
        int width = Math.max(0, metrics.stringWidth(text == null ? "" : text));
        int height = Math.max(1, metrics.getHeight());
        return new SurfaceTextMetrics(width, height, metrics.getAscent());
    }

    @Override
    public int getTextHeight(float size) {
        return metrics(size).getHeight();
    }

    @Override
    public void drawText(DrawContext context, String text, int x, int y, Style style) {
        String value = text == null ? "" : text;
        if (value.isEmpty()) {
            return;
        }

        float size = Math.max(1.0F, style.fontSize());
        SurfaceTextMetrics metrics = measureText(value, size);
        TextKey key = new TextKey(value, Math.round(size * 10.0F), style.text().argb(), style.font() == null ? defaultFont() : style.font());
        CachedText texture = textCache.computeIfAbsent(key, ignored -> createTextTexture(value, style, metrics));
        context.drawTexturedQuad(texture.id(), x, y, x + metrics.width(), y + metrics.height(), 0.0F, 1.0F, 0.0F, 1.0F);
    }

    @Override
    public void drawTextCentered(DrawContext context, String text, int x, int y, int width, int height, Style style) {
        SurfaceTextMetrics metrics = measureText(text, style.fontSize());
        int textX = x + (width - metrics.width()) / 2;
        int textY = y + (height - metrics.height()) / 2;
        drawText(context, text, textX, textY, style);
    }

    @Override
    public Identifier defaultFont() {
        return DEFAULT_FONT;
    }

    private CachedText createTextTexture(String text, Style style, SurfaceTextMetrics metrics) {
        int width = Math.max(1, metrics.width() * TEXTURE_SCALE);
        int height = Math.max(1, metrics.height() * TEXTURE_SCALE);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        graphics.setFont(font(style.font()).deriveFont((float) style.fontSize() * TEXTURE_SCALE));
        graphics.setColor(new java.awt.Color(style.text().argb(), true));
        graphics.drawString(text, 0, metrics.baseline() * TEXTURE_SCALE);
        graphics.dispose();

        NativeImage nativeImage = new NativeImage(width, height, false);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                nativeImage.setColorArgb(x, y, image.getRGB(x, y));
            }
        }

        Identifier id = Identifier.of("surfaceui", "runtime/text/" + Integer.toHexString(new TextKey(text, Math.round(style.fontSize() * 10.0F), style.text().argb(), style.font() == null ? defaultFont() : style.font()).hashCode()));
        NativeImageBackedTexture texture = new NativeImageBackedTexture(() -> id.toString(), nativeImage);
        MinecraftClient.getInstance().getTextureManager().registerTexture(id, texture);
        texture.upload();
        return new CachedText(id, width, height);
    }

    private FontMetrics metrics(float size) {
        Graphics2D graphics = metricsImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        graphics.setFont(font(defaultFont()).deriveFont(size));
        FontMetrics metrics = graphics.getFontMetrics();
        graphics.dispose();
        return metrics;
    }

    private Font font(Identifier id) {
        Identifier fontId = id == null ? defaultFont() : id;
        return fonts.computeIfAbsent(fontId, this::loadFont);
    }

    private Font loadFont(Identifier id) {
        try {
            Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(id).orElse(null);
            if (resource != null) {
                try (InputStream stream = resource.getInputStream()) {
                    return Font.createFont(Font.TRUETYPE_FONT, stream);
                }
            }
            LOGGER.warn("SurfaceUI font {} was not found; falling back to SansSerif", id);
        } catch (FontFormatException | IOException | RuntimeException exception) {
            LOGGER.warn("SurfaceUI failed to load font {}; falling back to SansSerif", id, exception);
        }
        return new Font("SansSerif", Font.PLAIN, 14);
    }

    private record TextKey(String text, int size10, int color, Identifier font) {
    }

    private record CachedText(Identifier id, int textureWidth, int textureHeight) {
    }
}
