package dev.surfaceui.config;

import dev.surfaceui.api.layout.Anchor;

public record WidgetOverride(
        String widgetId,
        Anchor anchor,
        int x,
        int y,
        Integer width,
        Integer height,
        boolean visible,
        boolean locked
) {
    public static WidgetOverride visible(String widgetId) {
        return new WidgetOverride(widgetId, null, 0, 0, null, null, true, false);
    }

    public static WidgetOverride edited(String widgetId, int x, int y, int width, int height) {
        return new WidgetOverride(widgetId, Anchor.TOP_LEFT, x, y, width, height, true, false);
    }
}
