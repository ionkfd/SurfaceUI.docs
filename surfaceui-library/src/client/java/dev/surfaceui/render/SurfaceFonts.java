package dev.surfaceui.render;

public final class SurfaceFonts {
    private static final SurfaceFontRenderer RENDERER = new AwtSurfaceFontRenderer();

    private SurfaceFonts() {
    }

    public static SurfaceFontRenderer renderer() {
        return RENDERER;
    }
}
