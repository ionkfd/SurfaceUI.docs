package dev.surfaceui.api;

import dev.surfaceui.api.widget.Widget;

@FunctionalInterface
public interface SurfaceProvider {
    Widget create(SurfaceContext context);
}
