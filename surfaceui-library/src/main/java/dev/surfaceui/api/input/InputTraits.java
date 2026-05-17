package dev.surfaceui.api.input;

public record InputTraits(
        boolean locked,
        boolean draggable,
        boolean resizable,
        boolean clickable,
        boolean hoverable,
        boolean focusable,
        boolean keyboard
) {
    public static final InputTraits PASSIVE = new InputTraits(false, false, false, false, false, false, false);

    public InputTraits withDraggable() {
        return new InputTraits(locked, true, resizable, clickable, true, focusable, keyboard);
    }

    public InputTraits withResizable() {
        return new InputTraits(locked, draggable, true, clickable, true, focusable, keyboard);
    }

    public InputTraits withClickable() {
        return new InputTraits(locked, draggable, resizable, true, true, focusable, keyboard);
    }

    public InputTraits withHoverable() {
        return new InputTraits(locked, draggable, resizable, clickable, true, focusable, keyboard);
    }

    public InputTraits withFocusable() {
        return new InputTraits(locked, draggable, resizable, clickable, true, true, true);
    }

    public InputTraits locked(boolean locked) {
        return new InputTraits(locked, draggable, resizable, clickable, hoverable, focusable, keyboard);
    }
}
