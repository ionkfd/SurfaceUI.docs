package dev.surfaceui.api.style;

public record Border(int width, Paint paint) {
    public static final Border NONE = new Border(0, Paint.TRANSPARENT);
}
