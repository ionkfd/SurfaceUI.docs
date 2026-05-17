package dev.surfaceui.runtime;

import dev.surfaceui.api.SurfaceContext;
import dev.surfaceui.api.SurfaceKind;
import dev.surfaceui.api.SurfaceMode;
import dev.surfaceui.api.style.Theme;

public record SurfaceFrameContext(
        SurfaceKind kind,
        SurfaceMode mode,
        int viewportWidth,
        int viewportHeight,
        Theme theme
) implements SurfaceContext {
}
