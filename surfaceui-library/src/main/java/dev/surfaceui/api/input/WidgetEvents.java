package dev.surfaceui.api.input;

public final class WidgetEvents {
    public interface Click {
        void onClick(double mouseX, double mouseY, int button);
    }

    public interface Hover {
        void onHover(boolean hovered);
    }

    public interface Key {
        boolean onKey(int keyCode, int scanCode, int modifiers);
    }

    private WidgetEvents() {
    }
}
