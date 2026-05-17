package dev.surfaceui.api.style;

public record Shadow(int offsetX, int offsetY, int blur, Paint paint) {
    public static final Shadow NONE = new Shadow(0, 0, 0, Paint.TRANSPARENT);
}
