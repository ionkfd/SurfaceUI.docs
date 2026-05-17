package dev.surfaceui.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Path path;
    private HudGuiConfig config = new HudGuiConfig();

    public ConfigManager() {
        this(FabricLoader.getInstance().getConfigDir().resolve("surfaceui").resolve("widgets.json"));
    }

    public ConfigManager(Path path) {
        this.path = path;
    }

    public HudGuiConfig config() {
        return config;
    }

    public void load() {
        if (!Files.exists(path)) {
            return;
        }

        try {
            HudGuiConfig loaded = GSON.fromJson(Files.readString(path), HudGuiConfig.class);
            config = loaded == null ? new HudGuiConfig() : loaded;
        } catch (IOException ignored) {
            config = new HudGuiConfig();
        }
    }

    public void save() {
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(config));
        } catch (IOException ignored) {
        }
    }

    public void put(WidgetOverride override) {
        config.put(override);
    }

    public void reset(String widgetId) {
        config.reset(widgetId);
        save();
    }
}
