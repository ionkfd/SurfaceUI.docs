package dev.surfaceui.api.layout;

public record LayoutSpec(
        Anchor anchor,
        int offsetX,
        int offsetY,
        SizeSpec size,
        int zIndex
) {
    public static LayoutSpec defaults() {
        return new LayoutSpec(Anchor.TOP_LEFT, 0, 0, SizeSpec.auto(), 0);
    }

    public LayoutSpec withAnchor(Anchor anchor) {
        return new LayoutSpec(anchor, offsetX, offsetY, size, zIndex);
    }

    public LayoutSpec withOffset(int x, int y) {
        return new LayoutSpec(anchor, x, y, size, zIndex);
    }

    public LayoutSpec withSize(SizeSpec size) {
        return new LayoutSpec(anchor, offsetX, offsetY, size, zIndex);
    }

    public LayoutSpec withZIndex(int zIndex) {
        return new LayoutSpec(anchor, offsetX, offsetY, size, zIndex);
    }
}
