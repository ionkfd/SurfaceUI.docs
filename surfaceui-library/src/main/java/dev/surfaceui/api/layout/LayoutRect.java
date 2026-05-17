package dev.surfaceui.api.layout;

public record LayoutRect(int x, int y, int width, int height) {
    public boolean contains(double mouseX, double mouseY) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    public LayoutRect inset(int left, int top, int right, int bottom) {
        return new LayoutRect(x + left, y + top, Math.max(0, width - left - right), Math.max(0, height - top - bottom));
    }

    public LayoutRect constrainedTo(LayoutRect parent) {
        int nextWidth = Math.min(width, Math.max(0, parent.width));
        int nextHeight = Math.min(height, Math.max(0, parent.height));
        int minX = parent.x;
        int minY = parent.y;
        int maxX = parent.x + parent.width - nextWidth;
        int maxY = parent.y + parent.height - nextHeight;
        int nextX = Math.max(minX, Math.min(maxX, x));
        int nextY = Math.max(minY, Math.min(maxY, y));
        return new LayoutRect(nextX, nextY, nextWidth, nextHeight);
    }

    public LayoutRect intersect(LayoutRect other) {
        int x1 = Math.max(x, other.x);
        int y1 = Math.max(y, other.y);
        int x2 = Math.min(x + width, other.x + other.width);
        int y2 = Math.min(y + height, other.y + other.height);
        return new LayoutRect(x1, y1, Math.max(0, x2 - x1), Math.max(0, y2 - y1));
    }
}
