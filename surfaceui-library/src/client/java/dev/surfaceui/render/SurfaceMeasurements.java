package dev.surfaceui.render;

public final class SurfaceMeasurements {
    private SurfaceMeasurements() {
    }

    public static int measureButtonWidth(String text, float fontSize, int horizontalPadding) {
        return SurfaceFonts.renderer().measureText(text, fontSize).width() + horizontalPadding * 2;
    }

    public static int measureButtonHeight(float fontSize, int verticalPadding, int minimumHeight) {
        return Math.max(SurfaceFonts.renderer().getTextHeight(fontSize) + verticalPadding * 2, minimumHeight);
    }

    public static int centerTextY(int buttonY, int buttonHeight, int textHeight) {
        return buttonY + (buttonHeight - textHeight) / 2;
    }
}
