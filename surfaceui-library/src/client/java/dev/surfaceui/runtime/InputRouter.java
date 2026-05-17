package dev.surfaceui.runtime;

import dev.surfaceui.api.SurfaceMode;
import dev.surfaceui.api.layout.Anchor;
import dev.surfaceui.config.SurfaceConfigStore;
import dev.surfaceui.config.WidgetOverride;
import java.util.Comparator;
import java.util.List;

public final class InputRouter {
    private static final int GRID = 4;
    private final SurfaceConfigStore store;
    private ResolvedWidget selected;
    private DragMode dragMode = DragMode.NONE;
    private double grabX;
    private double grabY;

    private enum DragMode {
        NONE,
        MOVE,
        RESIZE
    }

    public InputRouter(SurfaceConfigStore store) {
        this.store = store;
    }

    public ResolvedWidget selected() {
        return selected;
    }

    public boolean mouseClicked(SurfaceMode mode, List<ResolvedWidget> widgets, double mouseX, double mouseY, int button) {
        if (mode != SurfaceMode.HUD_EDIT_MODE) {
            return false;
        }

        selected = widgets.stream()
                .filter(widget -> widget.rect().contains(mouseX, mouseY))
                .max(Comparator.comparingInt(ResolvedWidget::zIndex))
                .orElse(null);
        if (selected != null) {
            grabX = mouseX - selected.rect().x();
            grabY = mouseY - selected.rect().y();
            dragMode = isResizeHandle(selected, mouseX, mouseY) && selected.widget().input().resizable() ? DragMode.RESIZE : DragMode.MOVE;
            return true;
        }
        return false;
    }

    public boolean mouseDragged(SurfaceMode mode, double mouseX, double mouseY) {
        if (mode != SurfaceMode.HUD_EDIT_MODE || selected == null || selected.widget().input().locked()) {
            return false;
        }

        if (dragMode == DragMode.RESIZE && selected.widget().input().resizable()) {
            int width = Math.max(8, snap((int) Math.round(mouseX - selected.rect().x())));
            int height = Math.max(8, snap((int) Math.round(mouseY - selected.rect().y())));
            store.config().put(new WidgetOverride(selected.widget().id(), Anchor.TOP_LEFT, selected.rect().x(), selected.rect().y(), width, height, true, false));
            return true;
        }

        if (dragMode == DragMode.MOVE && selected.widget().input().draggable()) {
            int x = snap((int) Math.round(mouseX - grabX));
            int y = snap((int) Math.round(mouseY - grabY));
            store.config().put(new WidgetOverride(selected.widget().id(), Anchor.TOP_LEFT, x, y, selected.rect().width(), selected.rect().height(), true, false));
            return true;
        }

        return false;
    }

    public void mouseReleased() {
        if (selected != null) {
            store.save();
        }
        dragMode = DragMode.NONE;
    }

    public void resetSelected() {
        if (selected != null) {
            store.config().reset(selected.widget().id());
            store.save();
        }
    }

    private int snap(int value) {
        return Math.round(value / (float) GRID) * GRID;
    }

    private boolean isResizeHandle(ResolvedWidget widget, double mouseX, double mouseY) {
        int handleSize = 8;
        return mouseX >= widget.rect().x() + widget.rect().width() - handleSize
                && mouseY >= widget.rect().y() + widget.rect().height() - handleSize;
    }
}
