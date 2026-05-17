package dev.surfaceui.runtime;

import dev.surfaceui.api.SurfaceKind;
import dev.surfaceui.api.SurfaceProvider;
import dev.surfaceui.api.SurfaceRegistration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.util.Identifier;

public final class SurfaceRegistry {
    private final Map<SurfaceKind, List<SurfaceRegistration>> registrations = new EnumMap<>(SurfaceKind.class);

    public synchronized SurfaceRegistration register(SurfaceKind kind, Identifier id, int zIndex, SurfaceProvider provider) {
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(provider, "provider");
        SurfaceRegistration registration = new SurfaceRegistration(id, kind, zIndex, provider);
        List<SurfaceRegistration> list = registrations.computeIfAbsent(kind, ignored -> new ArrayList<>());
        list.removeIf(existing -> existing.id().equals(id));
        list.add(registration);
        list.sort(Comparator.comparingInt(SurfaceRegistration::zIndex));
        return registration;
    }

    public synchronized List<SurfaceRegistration> all(SurfaceKind kind) {
        return List.copyOf(registrations.getOrDefault(kind, List.of()));
    }

    public synchronized List<SurfaceRegistration> visibleHudLike() {
        List<SurfaceRegistration> list = new ArrayList<>();
        list.addAll(all(SurfaceKind.HUD));
        list.addAll(all(SurfaceKind.OVERLAY));
        list.addAll(all(SurfaceKind.POPUP));
        list.sort(Comparator.comparingInt(SurfaceRegistration::zIndex));
        return list;
    }
}
