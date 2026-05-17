package dev.surfaceui.api;

import net.minecraft.util.Identifier;

public record SurfaceRegistration(
        Identifier id,
        SurfaceKind kind,
        int zIndex,
        SurfaceProvider provider
) {
}
