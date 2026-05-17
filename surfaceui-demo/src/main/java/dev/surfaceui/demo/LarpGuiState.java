package dev.surfaceui.demo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import org.lwjgl.glfw.GLFW;

public final class LarpGuiState {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("surfaceui-larp.json");
    private static final LarpGuiState INSTANCE = new LarpGuiState();

    private final long startedAt = System.nanoTime();
    private final Map<Integer, Boolean> previousKeyStates = new HashMap<>();
    private Tab selectedTab = Tab.DUNGEON;
    private Module listeningModule;
    private boolean searchFocused;
    private String searchText = "";
    private int dungeonScrollOffset;

    public final Module autoYangGlyph = new Module("auto_yang_glyph", "Auto Yang Glyph", GLFW.GLFW_KEY_F7, true, true)
            .slider("activation_delay", "Activation Delay", 220.0, 0.0, 400.0, "%.0fms")
            .slider("range", "Range", 6.8, 1.0, 10.0, "%.1f")
            .toggle("send_message", "Send Message", true);
    public final Module autoSoulcry = new Module("auto_soulcry", "Auto Soulcry", GLFW.GLFW_KEY_RIGHT, true, true)
            .slider("hp_threshold", "HP Threshold", 30.0, 0.0, 100.0, "%.0f%%")
            .slider("cooldown", "Cooldown", 5.5, 0.0, 10.0, "%.1fs")
            .toggle("party_only", "Party Only", true);
    public final Module autoFlux = new Module("auto_flux", "Auto Flux", -1, false, false);
    public final Module autoTuba = new Module("auto_tuba", "Auto Tuba", -1, false, false);
    public final Module silentChest = new Module("silent_chest", "Silent Chest", GLFW.GLFW_KEY_G, true, true)
            .slider("open_delay", "Open Delay", 90.0, 0.0, 250.0, "%.0fms")
            .toggle("auto_close", "Auto Close", true)
            .toggle("notify_chat", "Notify In Chat", false);

    private LarpGuiState() {
        load();
    }

    public static LarpGuiState get() {
        return INSTANCE;
    }

    public List<Module> dungeonModules() {
        return List.of(autoYangGlyph, autoSoulcry, autoFlux, autoTuba, silentChest);
    }

    public List<Module> visibleDungeonModules() {
        String query = normalizedSearch();
        if (query.isEmpty()) {
            return dungeonModules();
        }
        List<Module> modules = new ArrayList<>();
        for (Module module : dungeonModules()) {
            if (module.matches(query)) {
                modules.add(module);
            }
        }
        return modules;
    }

    public int activeModuleCount() {
        int count = 0;
        for (Module module : dungeonModules()) {
            if (module.enabled) {
                count++;
            }
        }
        return count;
    }

    public List<Module> activeModules() {
        List<Module> modules = new ArrayList<>();
        for (Module module : dungeonModules()) {
            if (module.enabled) {
                modules.add(module);
            }
        }
        return modules;
    }

    public Tab selectedTab() {
        return selectedTab;
    }

    public void selectedTab(Tab selectedTab) {
        this.selectedTab = selectedTab;
        cancelBinding();
        dungeonScrollOffset = 0;
        save();
    }

    public boolean searchFocused() {
        return searchFocused;
    }

    public void focusSearch() {
        searchFocused = true;
        cancelBinding();
    }

    public void blurSearch() {
        searchFocused = false;
    }

    public String searchText() {
        return searchText;
    }

    public String searchDisplayText() {
        if (!searchText.isEmpty()) {
            return searchText + (searchFocused ? "_" : "");
        }
        return searchFocused ? "_" : "Search modules...";
    }

    public void appendSearch(char character) {
        if (Character.isISOControl(character)) {
            return;
        }
        searchText += character;
        dungeonScrollOffset = 0;
    }

    public void appendSearch(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }
        for (int i = 0; i < text.length(); i++) {
            appendSearch(text.charAt(i));
        }
    }

    public void backspaceSearch() {
        if (!searchText.isEmpty()) {
            searchText = searchText.substring(0, searchText.length() - 1);
            dungeonScrollOffset = 0;
        }
    }

    public void clearSearch() {
        searchText = "";
        dungeonScrollOffset = 0;
    }

    public int dungeonScrollOffset() {
        return dungeonScrollOffset;
    }

    public void scrollDungeon(double verticalAmount) {
        int step = 24;
        if (verticalAmount > 0.0) {
            dungeonScrollOffset = Math.max(0, dungeonScrollOffset - step);
        } else if (verticalAmount < 0.0) {
            dungeonScrollOffset += step;
        }
    }

    public void clampDungeonScroll(int maxScrollOffset) {
        dungeonScrollOffset = Math.max(0, Math.min(dungeonScrollOffset, Math.max(0, maxScrollOffset)));
    }

    public String normalizedSearch() {
        return searchText.trim().toLowerCase(Locale.ROOT);
    }

    public Module listeningModule() {
        return listeningModule;
    }

    public boolean listeningFor(Module module) {
        return listeningModule == module;
    }

    public void beginBinding(Module module) {
        listeningModule = module;
        searchFocused = false;
    }

    public void cancelBinding() {
        listeningModule = null;
    }

    public void acceptBinding(int keyCode) {
        if (listeningModule == null) {
            return;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            listeningModule.keyCode = -1;
        } else if (keyCode != GLFW.GLFW_KEY_UNKNOWN) {
            listeningModule.keyCode = keyCode;
        }
        listeningModule = null;
        save();
    }

    public void setEnabled(Module module, boolean enabled) {
        module.enabled = enabled;
        if (enabled && module.enabledAtNanos == 0L) {
            module.enabledAtNanos = System.nanoTime();
        }
        save();
    }

    public void toggleEnabled(Module module) {
        setEnabled(module, !module.enabled);
        module.trigger();
    }

    public void setExpanded(Module module, boolean expanded) {
        if (module.expanded != expanded) {
            module.expanded = expanded;
            module.expansionChangedAtNanos = System.nanoTime();
            save();
        }
    }

    public void toggleExpanded(Module module) {
        setExpanded(module, !module.expanded);
    }

    public void setSlider(Module module, SliderSetting setting, double value) {
        setting.value = Math.max(setting.min, Math.min(setting.max, value));
        module.lastChangedAtNanos = System.nanoTime();
        save();
    }

    public void setToggle(Module module, ToggleSetting setting, boolean value) {
        setting.value = value;
        module.lastChangedAtNanos = System.nanoTime();
        save();
    }

    public void tickKeybinds(long windowHandle) {
        if (searchFocused || listeningModule != null) {
            return;
        }
        for (Module module : dungeonModules()) {
            if (module.keyCode < 0) {
                continue;
            }
            boolean down = GLFW.glfwGetKey(windowHandle, module.keyCode) == GLFW.GLFW_PRESS;
            boolean wasDown = previousKeyStates.getOrDefault(module.keyCode, false);
            previousKeyStates.put(module.keyCode, down);
            if (down && !wasDown) {
                toggleEnabled(module);
            }
        }
    }

    public String sessionTime() {
        long seconds = Math.max(0L, (System.nanoTime() - startedAt) / 1_000_000_000L);
        long hours = seconds / 3600L;
        long minutes = (seconds % 3600L) / 60L;
        long remainingSeconds = seconds % 60L;
        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }

    public String serverName() {
        MinecraftClient client = MinecraftClient.getInstance();
        ServerInfo info = client.getCurrentServerEntry();
        if (info != null) {
            String address = info.address == null ? "" : info.address;
            if (address.toLowerCase(Locale.ROOT).contains("hypixel")) {
                return "Hypixel";
            }
            return info.name == null || info.name.isBlank() ? address : info.name;
        }
        return client.isInSingleplayer() ? "Singleplayer" : "Unknown";
    }

    public int fps() {
        return Math.max(0, MinecraftClient.getInstance().getCurrentFps());
    }

    public int ping() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayNetworkHandler network = client.getNetworkHandler();
        if (client.player == null || network == null) {
            return 0;
        }
        PlayerListEntry entry = network.getPlayerListEntry(client.player.getUuid());
        return entry == null ? 0 : Math.max(0, entry.getLatency());
    }

    public String pingText() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.isInSingleplayer() || client.isConnectedToLocalServer()) {
            return "Local";
        }
        int ping = ping();
        return ping <= 0 ? "--" : ping + "ms";
    }

    public double cpuLoad() {
        java.lang.management.OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
        if (bean instanceof com.sun.management.OperatingSystemMXBean sunBean) {
            double load = sunBean.getCpuLoad();
            if (load >= 0.0) {
                return Math.max(0.0, Math.min(1.0, load));
            }
        }
        double load = bean.getSystemLoadAverage();
        if (load < 0.0) {
            return 0.0;
        }
        return Math.max(0.0, Math.min(1.0, load / Math.max(1, bean.getAvailableProcessors())));
    }

    public String cpuText() {
        int percent = (int) Math.round(cpuLoad() * 100.0);
        return percent <= 0 ? "<1%" : percent + "%";
    }

    public double ramLoad() {
        Runtime runtime = Runtime.getRuntime();
        long used = runtime.totalMemory() - runtime.freeMemory();
        return Math.max(0.0, Math.min(1.0, used / (double) runtime.maxMemory()));
    }

    public String ramText() {
        Runtime runtime = Runtime.getRuntime();
        long used = runtime.totalMemory() - runtime.freeMemory();
        return String.format("%.1fGB", used / 1024.0 / 1024.0 / 1024.0);
    }

    public double networkLoad() {
        int ping = ping();
        if (ping <= 0) {
            return 0.0;
        }
        return Math.max(0.04, Math.min(1.0, ping / 250.0));
    }

    public String networkText() {
        return ping() <= 0 ? "idle" : ping() + "ms";
    }

    private void load() {
        if (!Files.exists(CONFIG_PATH)) {
            return;
        }
        try {
            Config config = GSON.fromJson(Files.readString(CONFIG_PATH), Config.class);
            if (config == null) {
                return;
            }
            if (config.selectedTab != null) {
                selectedTab = Tab.valueOf(config.selectedTab);
            }
            for (Module module : dungeonModules()) {
                ModuleConfig moduleConfig = config.modules.get(module.id);
                if (moduleConfig != null) {
                    module.enabled = moduleConfig.enabled;
                    module.expanded = moduleConfig.expanded;
                    module.keyCode = moduleConfig.keyCode;
                    module.enabledAtNanos = module.enabled ? System.nanoTime() : 0L;
                    for (SliderSetting slider : module.sliders) {
                        if (moduleConfig.sliders.containsKey(slider.id)) {
                            slider.value = moduleConfig.sliders.get(slider.id);
                        }
                    }
                    for (ToggleSetting toggle : module.toggles) {
                        if (moduleConfig.toggles.containsKey(toggle.id)) {
                            toggle.value = moduleConfig.toggles.get(toggle.id);
                        }
                    }
                }
            }
        } catch (IOException | RuntimeException ignored) {
            // Bad user config should not stop the UI from opening.
        }
    }

    public void save() {
        Config config = new Config();
        config.selectedTab = selectedTab.name();
        for (Module module : dungeonModules()) {
            ModuleConfig moduleConfig = new ModuleConfig();
            moduleConfig.enabled = module.enabled;
            moduleConfig.expanded = module.expanded;
            moduleConfig.keyCode = module.keyCode;
            for (SliderSetting slider : module.sliders) {
                moduleConfig.sliders.put(slider.id, slider.value);
            }
            for (ToggleSetting toggle : module.toggles) {
                moduleConfig.toggles.put(toggle.id, toggle.value);
            }
            config.modules.put(module.id, moduleConfig);
        }
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(config));
        } catch (IOException ignored) {
            // The UI remains functional even if config persistence fails.
        }
    }

    public enum Tab {
        SETTINGS("Settings", "*"),
        DUNGEON("Dungeon", "^"),
        SLAYER("Slayer", "\\"),
        MISC("Misc", "..."),
        MINING("Mining", "/"),
        PATHFINDING("Pathfinding", "P");

        private final String title;
        private final String icon;

        Tab(String title, String icon) {
            this.title = title;
            this.icon = icon;
        }

        public String title() {
            return title;
        }

        public String icon() {
            return icon;
        }
    }

    public static final class Module {
        public final String id;
        public final String name;
        public boolean enabled;
        public boolean expanded;
        public int keyCode;
        public long enabledAtNanos;
        public long lastTriggeredAtNanos;
        public long lastChangedAtNanos;
        public long expansionChangedAtNanos = System.nanoTime();
        public int triggerCount;
        public final List<SliderSetting> sliders = new ArrayList<>();
        public final List<ToggleSetting> toggles = new ArrayList<>();

        private Module(String id, String name, int keyCode, boolean enabled, boolean expanded) {
            this.id = id;
            this.name = name;
            this.keyCode = keyCode;
            this.enabled = enabled;
            this.expanded = expanded;
            this.enabledAtNanos = enabled ? System.nanoTime() : 0L;
        }

        public Module slider(String id, String label, double value, double min, double max, String format) {
            sliders.add(new SliderSetting(id, label, value, min, max, format));
            return this;
        }

        public Module toggle(String id, String label, boolean value) {
            toggles.add(new ToggleSetting(id, label, value));
            return this;
        }

        public String keyName() {
            if (keyCode < 0) {
                return "NONE";
            }
            if (keyCode >= GLFW.GLFW_KEY_F1 && keyCode <= GLFW.GLFW_KEY_F25) {
                return "F" + (keyCode - GLFW.GLFW_KEY_F1 + 1);
            }
            String name = GLFW.glfwGetKeyName(keyCode, 0);
            if (name != null && !name.isBlank()) {
                return name.toUpperCase(Locale.ROOT);
            }
            return switch (keyCode) {
                case GLFW.GLFW_KEY_RIGHT -> "RIGHT";
                case GLFW.GLFW_KEY_LEFT -> "LEFT";
                case GLFW.GLFW_KEY_UP -> "UP";
                case GLFW.GLFW_KEY_DOWN -> "DOWN";
                case GLFW.GLFW_KEY_SPACE -> "SPACE";
                case GLFW.GLFW_KEY_ENTER -> "ENTER";
                case GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_RIGHT_SHIFT -> "SHIFT";
                case GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_RIGHT_CONTROL -> "CTRL";
                case GLFW.GLFW_KEY_LEFT_ALT, GLFW.GLFW_KEY_RIGHT_ALT -> "ALT";
                default -> "KEY " + keyCode;
            };
        }

        public void trigger() {
            lastTriggeredAtNanos = System.nanoTime();
            triggerCount++;
        }

        public String activeTime() {
            if (!enabled || enabledAtNanos == 0L) {
                return "";
            }
            long seconds = Math.max(0L, (System.nanoTime() - enabledAtNanos) / 1_000_000_000L);
            return String.format("%d:%02d", seconds / 60L, seconds % 60L);
        }

        public double expansionProgress() {
            double elapsed = (System.nanoTime() - expansionChangedAtNanos) / 170_000_000.0;
            double t = Math.max(0.0, Math.min(1.0, elapsed));
            double eased = t * t * (3.0 - 2.0 * t);
            return expanded ? eased : 1.0 - eased;
        }

        public boolean matches(String query) {
            if (name.toLowerCase(Locale.ROOT).contains(query)) {
                return true;
            }
            for (SliderSetting slider : sliders) {
                if (slider.label.toLowerCase(Locale.ROOT).contains(query)) {
                    return true;
                }
            }
            for (ToggleSetting toggle : toggles) {
                if (toggle.label.toLowerCase(Locale.ROOT).contains(query)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static final class SliderSetting {
        public final String id;
        public final String label;
        public final double min;
        public final double max;
        public final String format;
        public double value;

        private SliderSetting(String id, String label, double value, double min, double max, String format) {
            this.id = id;
            this.label = label;
            this.value = value;
            this.min = min;
            this.max = max;
            this.format = format;
        }

        public String formatted() {
            return String.format(format, value);
        }
    }

    public static final class ToggleSetting {
        public final String id;
        public final String label;
        public boolean value;

        private ToggleSetting(String id, String label, boolean value) {
            this.id = id;
            this.label = label;
            this.value = value;
        }
    }

    private static final class Config {
        String selectedTab = Tab.DUNGEON.name();
        Map<String, ModuleConfig> modules = new HashMap<>();
    }

    private static final class ModuleConfig {
        boolean enabled;
        boolean expanded;
        int keyCode = -1;
        Map<String, Double> sliders = new HashMap<>();
        Map<String, Boolean> toggles = new HashMap<>();
    }
}
