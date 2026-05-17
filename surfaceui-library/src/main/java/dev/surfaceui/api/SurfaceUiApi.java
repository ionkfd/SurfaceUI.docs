package dev.surfaceui.api;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import net.minecraft.util.Identifier;

public final class SurfaceUiApi {
    private static final Registry HUD = new Registry(SurfaceKind.HUD);
    private static final Registry SCREEN = new Registry(SurfaceKind.SCREEN);
    private static final Registry OVERLAY = new Registry(SurfaceKind.OVERLAY);
    private static final Registry POPUP = new Registry(SurfaceKind.POPUP);

    private SurfaceUiApi() {
    }

    public static Registry hud() {
        return HUD;
    }

    public static Registry screens() {
        return SCREEN;
    }

    public static Registry overlays() {
        return OVERLAY;
    }

    public static Registry popups() {
        return POPUP;
    }

    public static final class Registry {
        private final SurfaceKind kind;
        private final List<SurfaceRegistration> registrations = new ArrayList<>();

        private Registry(SurfaceKind kind) {
            this.kind = kind;
        }

        public synchronized SurfaceRegistration register(Identifier id, SurfaceProvider provider) {
            return register(id, 0, provider);
        }

        public synchronized SurfaceRegistration register(Identifier id, int zIndex, SurfaceProvider provider) {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(provider, "provider");
            SurfaceRegistration registration = new SurfaceRegistration(id, kind, zIndex, provider);
            registrations.removeIf(existing -> existing.id().equals(id));
            registrations.add(registration);
            registrations.sort(Comparator.comparingInt(SurfaceRegistration::zIndex));
            HudGuiLibrary.runtime().surfaces().register(kind, id, zIndex, provider);
            return registration;
        }

        public synchronized List<SurfaceRegistration> all() {
            return List.copyOf(registrations);
        }
    }
}
