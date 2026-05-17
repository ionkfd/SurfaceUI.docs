package dev.surfaceui.api.layout;

public record SizeSpec(SizeValue width, SizeValue height, int minWidth, int minHeight, int maxWidth, int maxHeight) {
    public static SizeSpec auto() {
        return new SizeSpec(SizeValue.auto(), SizeValue.auto(), 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public static SizeSpec fixed(int width, int height) {
        return new SizeSpec(SizeValue.px(width), SizeValue.px(height), 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public SizeSpec withMin(int width, int height) {
        return new SizeSpec(this.width, this.height, width, height, maxWidth, maxHeight);
    }

    public SizeSpec withMax(int width, int height) {
        return new SizeSpec(this.width, this.height, minWidth, minHeight, width, height);
    }

    public int clampWidth(int value) {
        return Math.max(minWidth, Math.min(maxWidth, value));
    }

    public int clampHeight(int value) {
        return Math.max(minHeight, Math.min(maxHeight, value));
    }
}
