package dev.surfaceui.api;

import dev.surfaceui.api.style.Theme;

public interface SurfaceContext {
    SurfaceKind kind();

    SurfaceMode mode();

    int viewportWidth();

    int viewportHeight();

    Theme theme();
}
