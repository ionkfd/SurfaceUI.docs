package dev.surfaceui.runtime;

import dev.surfaceui.api.layout.LayoutRect;
import dev.surfaceui.api.style.Style;
import dev.surfaceui.api.widget.Widget;

public record ResolvedWidget(Widget widget, LayoutRect rect, Style style, int zIndex, LayoutRect clip) {
    public ResolvedWidget(Widget widget, LayoutRect rect, Style style, int zIndex) {
        this(widget, rect, style, zIndex, rect);
    }
}
