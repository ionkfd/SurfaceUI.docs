package dev.surfaceui.runtime;

import dev.surfaceui.api.SurfaceMode;
import dev.surfaceui.api.style.Theme;
import dev.surfaceui.config.ConfigManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public final class HudGuiRuntime {
    private static final HudGuiRuntime INSTANCE = new HudGuiRuntime();

    private final SurfaceRegistry surfaces = new SurfaceRegistry();
    private final WidgetRegistry widgets = new WidgetRegistry();
    private final ConfigManager config = new ConfigManager();
    private final RenderPipeline renderPipeline = new RenderPipeline();
    private final InputPipeline inputPipeline = new InputPipeline(config);
    private final Map<String, Theme> themes = new HashMap<>();
    private SurfaceMode mode = SurfaceMode.PLAY_MODE;
    private Theme activeTheme = Theme.DEFAULT;
    private List<ResolvedWidget> lastPlan = List.of();
    private boolean initialized;

    private HudGuiRuntime() {
        themes.put(Theme.DEFAULT.id(), Theme.DEFAULT);
    }

    public static HudGuiRuntime get() {
        return INSTANCE;
    }

    public void initialize() {
        if (initialized) {
            return;
        }
        config.load();
        initialized = true;
    }

    public SurfaceRegistry surfaces() {
        return surfaces;
    }

    public WidgetRegistry widgets() {
        return widgets;
    }

    public ConfigManager config() {
        return config;
    }

    public InputPipeline input() {
        return inputPipeline;
    }

    public SurfaceMode mode() {
        return mode;
    }

    public void mode(SurfaceMode mode) {
        this.mode = mode;
    }

    public Theme activeTheme() {
        return activeTheme;
    }

    public void registerTheme(Theme theme) {
        themes.put(theme.id(), theme);
    }

    public Theme theme(String id) {
        return themes.getOrDefault(id, Theme.DEFAULT);
    }

    public void activeTheme(String id) {
        activeTheme = theme(id);
    }

    public List<ResolvedWidget> lastPlan() {
        return lastPlan;
    }

    public void renderHud(DrawContext context, RenderTickCounter tickCounter) {
        initialize();
        MinecraftClient client = MinecraftClient.getInstance();
        lastPlan = renderPipeline.buildPlan(this, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight());
        widgets.index(lastPlan);
        renderPipeline.render(context, tickCounter, lastPlan);
    }
}
