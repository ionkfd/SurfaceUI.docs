package dev.surfaceui.api.style;

public record Radius(int topLeft, int topRight, int bottomRight, int bottomLeft) {
    public static final Radius ZERO = new Radius(0, 0, 0, 0);

    public static Radius all(int value) {
        return new Radius(value, value, value, value);
    }
}
