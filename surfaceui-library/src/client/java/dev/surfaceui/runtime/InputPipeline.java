package dev.surfaceui.runtime;

import dev.surfaceui.api.SurfaceMode;
import dev.surfaceui.config.ConfigManager;
import dev.surfaceui.config.WidgetOverride;
import java.util.List;
import org.lwjgl.glfw.GLFW;

public final class InputPipeline {
    private static final int GRID = 4;
    private final ConfigManager config;
    private ResolvedWidget hovered;
    private ResolvedWidget focused;
    private ResolvedWidget selected;
    private ResolvedWidget pressed;
    private final WidgetStateStore states = WidgetStateStore.get();
    private DragMode dragMode = DragMode.NONE;
    private double grabX;
    private double grabY;

    private enum DragMode {
        NONE,
        MOVE,
        RESIZE
    }

    public InputPipeline(ConfigManager config) {
        this.config = config;
    }

    public ResolvedWidget hovered() {
        return hovered;
    }

    public ResolvedWidget focused() {
        return focused;
    }

    public ResolvedWidget selected() {
        return selected;
    }

    public void mouseMoved(List<ResolvedWidget> plan, double mouseX, double mouseY) {
        ResolvedWidget next = topHit(plan, mouseX, mouseY);
        if (hovered != next) {
            if (hovered != null) {
                states.hovered(hovered.widget().id(), false);
                hovered.widget().onHover(false);
            }
            if (next != null && next.widget().input().hoverable()) {
                states.hovered(next.widget().id(), true);
                next.widget().onHover(true);
            }
        }
        hovered = next;
    }

    public boolean mouseClicked(SurfaceMode mode, List<ResolvedWidget> plan, double mouseX, double mouseY, int button) {
        hovered = topHit(plan, mouseX, mouseY);
        if (hovered == null) {
            selected = null;
            return false;
        }

        if (hovered.widget().input().focusable()) {
            if (focused != null) {
                states.focused(focused.widget().id(), false);
            }
            focused = hovered;
            states.focused(focused.widget().id(), true);
        }

        if (mode == SurfaceMode.HUD_EDIT_MODE) {
            selected = topEditableHit(plan, mouseX, mouseY);
            if (selected == null) {
                return false;
            }
            grabX = mouseX - selected.rect().x();
            grabY = mouseY - selected.rect().y();
            dragMode = isResizeHandle(selected, mouseX, mouseY) && selected.widget().input().resizable() ? DragMode.RESIZE : DragMode.MOVE;
            return true;
        }

        if (hovered.widget().input().clickable()) {
            pressed = hovered;
            boolean consumed = hovered.widget().mouseClicked(mouseX, mouseY, button);
            if (consumed) {
                states.pressed(pressed.widget().id(), true);
            } else {
                pressed = null;
            }
            return consumed;
        }

        return false;
    }

    public boolean mouseDragged(SurfaceMode mode, double mouseX, double mouseY) {
        if (mode != SurfaceMode.HUD_EDIT_MODE) {
            return pressed != null && pressed.widget().mouseDragged(mouseX, mouseY, 0, 0.0, 0.0);
        }

        if (mode != SurfaceMode.HUD_EDIT_MODE || selected == null || isLocked(selected)) {
            return false;
        }

        if (dragMode == DragMode.RESIZE && selected.widget().input().resizable()) {
            int width = Math.max(8, snap((int) Math.round(mouseX - selected.rect().x())));
            int height = Math.max(8, snap((int) Math.round(mouseY - selected.rect().y())));
            config.put(WidgetOverride.edited(selected.widget().id(), selected.rect().x(), selected.rect().y(), width, height));
            return true;
        }

        if (dragMode == DragMode.MOVE && selected.widget().input().draggable()) {
            int x = snap((int) Math.round(mouseX - grabX));
            int y = snap((int) Math.round(mouseY - grabY));
            config.put(WidgetOverride.edited(selected.widget().id(), x, y, selected.rect().width(), selected.rect().height()));
            return true;
        }

        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_R && selected != null) {
            config.reset(selected.widget().id());
            return true;
        }

        return focused != null && focused.widget().input().keyboard() && focused.widget().onKey(keyCode, scanCode, modifiers);
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragMode = DragMode.NONE;
        boolean consumed = false;
        if (pressed != null) {
            consumed = pressed.widget().mouseReleased(mouseX, mouseY, button);
            states.pressed(pressed.widget().id(), false);
        }
        pressed = null;
        config.save();
        return consumed;
    }

    public void resetSelected() {
        if (selected != null) {
            config.reset(selected.widget().id());
        }
    }

    private ResolvedWidget topHit(List<ResolvedWidget> plan, double mouseX, double mouseY) {
        for (int i = plan.size() - 1; i >= 0; i--) {
            ResolvedWidget widget = plan.get(i);
            if (isInputTarget(widget)
                    && widget.rect().contains(mouseX, mouseY)
                    && widget.clip().contains(mouseX, mouseY)) {
                return widget;
            }
        }
        return null;
    }

    private ResolvedWidget topEditableHit(List<ResolvedWidget> plan, double mouseX, double mouseY) {
        for (int i = plan.size() - 1; i >= 0; i--) {
            ResolvedWidget widget = plan.get(i);
            if ((widget.widget().input().draggable() || widget.widget().input().resizable())
                    && widget.rect().contains(mouseX, mouseY)
                    && widget.clip().contains(mouseX, mouseY)) {
                return widget;
            }
        }
        return null;
    }

    private boolean isInputTarget(ResolvedWidget widget) {
        return widget.widget().input().clickable()
                || widget.widget().input().hoverable()
                || widget.widget().input().focusable()
                || widget.widget().input().draggable()
                || widget.widget().input().resizable();
    }

    private boolean isResizeHandle(ResolvedWidget widget, double mouseX, double mouseY) {
        return mouseX >= widget.rect().x() + widget.rect().width() - 8
                && mouseY >= widget.rect().y() + widget.rect().height() - 8;
    }

    private int snap(int value) {
        return Math.round(value / (float) GRID) * GRID;
    }

    private boolean isLocked(ResolvedWidget widget) {
        return config.config().overrideFor(widget.widget().id()).map(WidgetOverride::locked).orElse(widget.widget().input().locked());
    }
}
