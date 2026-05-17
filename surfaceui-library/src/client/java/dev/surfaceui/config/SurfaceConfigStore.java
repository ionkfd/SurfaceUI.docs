package dev.surfaceui.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public final class SurfaceConfigStore {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Path path;
    private UserConfig config = new UserConfig();

    public SurfaceConfigStore() {
        this(FabricLoader.getInstance().getConfigDir().resolve("surfaceui").resolve("widgets.json"));
    }

    public SurfaceConfigStore(Path path) {
        this.path = path;
    }

    public UserConfig config() {
        return config;
    }

    public void load() {
        if (!Files.exists(path)) {
            return;
        }

        try {
            config = GSON.fromJson(Files.readString(path), UserConfig.class);
            if (config == null) {
                config = new UserConfig();
            }
        } catch (IOException ignored) {
            config = new UserConfig();
        }
    }

    public void save() {
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(config));
        } catch (IOException ignored) {
        }
    }
}
