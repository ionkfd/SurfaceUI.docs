package dev.surfaceui.runtime;

import java.util.HashMap;
import java.util.Map;

public final class WidgetStateStore {
    private static final WidgetStateStore INSTANCE = new WidgetStateStore();
    private final Map<String, WidgetState> states = new HashMap<>();

    private WidgetStateStore() {
    }

    public static WidgetStateStore get() {
        return INSTANCE;
    }

    public WidgetState state(String widgetId) {
        return states.getOrDefault(widgetId, WidgetState.IDLE);
    }

    public void hovered(String widgetId, boolean hovered) {
        update(widgetId, state(widgetId).hovered(hovered));
    }

    public void pressed(String widgetId, boolean pressed) {
        update(widgetId, state(widgetId).pressed(pressed));
    }

    public void focused(String widgetId, boolean focused) {
        update(widgetId, state(widgetId).focused(focused));
    }

    private void update(String widgetId, WidgetState state) {
        if (state.equals(WidgetState.IDLE)) {
            states.remove(widgetId);
        } else {
            states.put(widgetId, state);
        }
    }
}
