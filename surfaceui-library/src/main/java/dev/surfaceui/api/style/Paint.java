package dev.surfaceui.api.style;

public record Paint(int argb) {
    public static final Paint TRANSPARENT = new Paint(0x00000000);

    public static Paint argb(int argb) {
        return new Paint(argb);
    }

    public Paint withAlpha(float opacity) {
        int alpha = Math.max(0, Math.min(255, Math.round(((argb >>> 24) & 0xFF) * opacity)));
        return new Paint((argb & 0x00FFFFFF) | (alpha << 24));
    }

    public Paint blend(Paint other, float amount) {
        float t = Math.max(0.0F, Math.min(1.0F, amount));
        int a = lerp((argb >>> 24) & 0xFF, (other.argb >>> 24) & 0xFF, t);
        int r = lerp((argb >>> 16) & 0xFF, (other.argb >>> 16) & 0xFF, t);
        int g = lerp((argb >>> 8) & 0xFF, (other.argb >>> 8) & 0xFF, t);
        int b = lerp(argb & 0xFF, other.argb & 0xFF, t);
        return new Paint((a << 24) | (r << 16) | (g << 8) | b);
    }

    private static int lerp(int from, int to, float amount) {
        return Math.round(from + (to - from) * amount);
    }
}
