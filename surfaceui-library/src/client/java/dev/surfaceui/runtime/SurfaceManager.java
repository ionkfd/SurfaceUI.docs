package dev.surfaceui.runtime;

import dev.surfaceui.api.SurfaceMode;
import dev.surfaceui.config.SurfaceConfigStore;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public final class SurfaceManager {
    private final HudGuiRuntime runtime = HudGuiRuntime.get();

    public SurfaceManager(SurfaceConfigStore configStore) {
        runtime.initialize();
    }

    public SurfaceMode mode() {
        return runtime.mode();
    }

    public void mode(SurfaceMode mode) {
        runtime.mode(mode);
    }

    public InputPipeline input() {
        return runtime.input();
    }

    public void renderHud(DrawContext context, RenderTickCounter tickCounter) {
        runtime.renderHud(context, tickCounter);
    }

    public List<ResolvedWidget> lastHudPlan() {
        return runtime.lastPlan();
    }
}
