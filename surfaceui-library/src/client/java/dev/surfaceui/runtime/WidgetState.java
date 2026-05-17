package dev.surfaceui.runtime;

public record WidgetState(boolean hovered, boolean pressed, boolean focused) {
    public static final WidgetState IDLE = new WidgetState(false, false, false);

    public WidgetState hovered(boolean hovered) {
        return new WidgetState(hovered, pressed, focused);
    }

    public WidgetState pressed(boolean pressed) {
        return new WidgetState(hovered, pressed, focused);
    }

    public WidgetState focused(boolean focused) {
        return new WidgetState(hovered, pressed, focused);
    }
}
