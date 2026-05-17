package dev.surfaceui.render;

import dev.surfaceui.api.layout.LayoutRect;
import dev.surfaceui.api.style.Paint;
import dev.surfaceui.api.style.Radius;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

final class SmoothShapeCache {
    private static final int MAX_CACHE_ENTRIES = 768;
    private static final int TEXTURE_SCALE = 2;
    private final Map<Key, CachedShape> rounded = new LinkedHashMap<>(128, 0.75F, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Key, CachedShape> eldest) {
            if (size() <= MAX_CACHE_ENTRIES) {
                return false;
            }
            MinecraftClient.getInstance().getTextureManager().destroyTexture(eldest.getValue().id());
            return true;
        }
    };

    CachedShape rounded(LayoutRect rect, Radius radius, Paint top, Paint bottom, int borderWidth, Paint border, float opacity) {
        Paint topPaint = top.withAlpha(opacity);
        Paint bottomPaint = bottom.withAlpha(opacity);
        Paint borderPaint = border.withAlpha(opacity);
        int roundedRadius = uniformRadius(radius, rect);
        Key key = new Key(rect.width(), rect.height(), roundedRadius, Math.max(0, borderWidth), topPaint.argb(), bottomPaint.argb(), borderPaint.argb());
        return rounded.computeIfAbsent(key, SmoothShapeCache::createRoundedTexture);
    }

    private static CachedShape createRoundedTexture(Key key) {
        int textureWidth = Math.max(1, key.width * TEXTURE_SCALE);
        int textureHeight = Math.max(1, key.height * TEXTURE_SCALE);
        int radius = key.radius * TEXTURE_SCALE;
        int borderWidth = key.borderWidth * TEXTURE_SCALE;
        NativeImage image = new NativeImage(textureWidth, textureHeight, false);
        int innerRadius = Math.max(0, radius - borderWidth);
        for (int y = 0; y < textureHeight; y++) {
            float vertical = textureHeight <= 1 ? 0.0F : y / (float) (textureHeight - 1);
            int fillColor = blend(key.top, key.bottom, vertical);
            for (int x = 0; x < textureWidth; x++) {
                float outerCoverage = coverage(x, y, textureWidth, textureHeight, radius);
                if (outerCoverage <= 0.0F) {
                    image.setColorArgb(x, y, 0);
                    continue;
                }

                float innerCoverage = borderWidth <= 0
                        ? outerCoverage
                        : coverage(x - borderWidth, y - borderWidth, Math.max(0, textureWidth - borderWidth * 2), Math.max(0, textureHeight - borderWidth * 2), innerRadius);
                boolean borderPixel = borderWidth > 0 && outerCoverage > innerCoverage;
                int color = borderPixel ? key.border : fillColor;
                float alpha = borderPixel ? outerCoverage : innerCoverage;
                image.setColorArgb(x, y, multiplyAlpha(color, alpha));
            }
        }

        Identifier id = Identifier.of("surfaceui", "runtime/rounded/" + Integer.toHexString(key.hashCode()));
        NativeImageBackedTexture texture = new NativeImageBackedTexture(() -> id.toString(), image);
        MinecraftClient.getInstance().getTextureManager().registerTexture(id, texture);
        texture.upload();
        return new CachedShape(id, textureWidth, textureHeight);
    }

    private static float coverage(int x, int y, int width, int height, int radius) {
        if (width <= 0 || height <= 0) {
            return 0.0F;
        }

        int samples = 8;
        int inside = 0;
        for (int sy = 0; sy < samples; sy++) {
            for (int sx = 0; sx < samples; sx++) {
                float px = x + (sx + 0.5F) / samples;
                float py = y + (sy + 0.5F) / samples;
                if (insideRounded(px, py, width, height, radius)) {
                    inside++;
                }
            }
        }
        return inside / (float) (samples * samples);
    }

    private static boolean insideRounded(float x, float y, int width, int height, int radius) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return false;
        }
        if (radius <= 0) {
            return true;
        }

        float left = radius;
        float right = width - radius;
        float top = radius;
        float bottom = height - radius;
        float cx = Math.max(left, Math.min(right, x));
        float cy = Math.max(top, Math.min(bottom, y));
        float dx = x - cx;
        float dy = y - cy;
        return dx * dx + dy * dy <= radius * radius;
    }

    private static int uniformRadius(Radius radius, LayoutRect rect) {
        int requested = Math.max(Math.max(radius.topLeft(), radius.topRight()), Math.max(radius.bottomLeft(), radius.bottomRight()));
        return Math.max(0, Math.min(Math.min(rect.width(), rect.height()) / 2, requested));
    }

    private static int blend(int from, int to, float amount) {
        int a = lerp((from >>> 24) & 0xFF, (to >>> 24) & 0xFF, amount);
        int r = lerp((from >>> 16) & 0xFF, (to >>> 16) & 0xFF, amount);
        int g = lerp((from >>> 8) & 0xFF, (to >>> 8) & 0xFF, amount);
        int b = lerp(from & 0xFF, to & 0xFF, amount);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int lerp(int from, int to, float amount) {
        return Math.round(from + (to - from) * amount);
    }

    private static int multiplyAlpha(int argb, float coverage) {
        int alpha = Math.max(0, Math.min(255, Math.round(((argb >>> 24) & 0xFF) * coverage)));
        return (argb & 0x00FFFFFF) | (alpha << 24);
    }

    private record Key(int width, int height, int radius, int borderWidth, int top, int bottom, int border) {
    }

    record CachedShape(Identifier id, int textureWidth, int textureHeight) {
    }
}
