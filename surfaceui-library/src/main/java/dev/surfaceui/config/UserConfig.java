package dev.surfaceui.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserConfig {
    protected Map<String, WidgetOverride> overrides = new HashMap<>();

    public Optional<WidgetOverride> overrideFor(String widgetId) {
        return Optional.ofNullable(overrides.get(widgetId));
    }

    public void put(WidgetOverride override) {
        overrides.put(override.widgetId(), override);
    }

    public void reset(String widgetId) {
        overrides.remove(widgetId);
    }

    public Map<String, WidgetOverride> overrides() {
        return Map.copyOf(overrides);
    }
}
