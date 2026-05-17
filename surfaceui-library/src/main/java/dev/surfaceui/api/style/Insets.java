package dev.surfaceui.api.style;

public record Insets(int left, int top, int right, int bottom) {
    public static final Insets ZERO = new Insets(0, 0, 0, 0);

    public static Insets all(int value) {
        return new Insets(value, value, value, value);
    }

    public int horizontal() {
        return left + right;
    }

    public int vertical() {
        return top + bottom;
    }
}
