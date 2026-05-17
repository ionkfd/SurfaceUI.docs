package dev.surfaceui.api.layout;

public record SizeValue(Kind kind, float value) {
    public enum Kind {
        AUTO,
        PIXELS,
        PERCENT
    }

    public static SizeValue auto() {
        return new SizeValue(Kind.AUTO, 0.0F);
    }

    public static SizeValue px(float pixels) {
        return new SizeValue(Kind.PIXELS, pixels);
    }

    public static SizeValue percent(float percent) {
        return new SizeValue(Kind.PERCENT, percent);
    }

    public int resolve(int parent, int measured) {
        return switch (kind) {
            case AUTO -> measured;
            case PIXELS -> Math.round(value);
            case PERCENT -> Math.round(parent * value);
        };
    }
}
