package dev.surfaceui.api.widget;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public final class SidebarItemWidget extends AbstractWidget<SidebarItemWidget> {
    private final Supplier<String> icon;
    private final Supplier<String> label;
    private final BooleanSupplier active;
    private final Runnable action;
    private boolean armed;

    public SidebarItemWidget(Supplier<String> icon, Supplier<String> label, BooleanSupplier active, Runnable action) {
        this.icon = Objects.requireNonNull(icon, "icon");
        this.label = Objects.requireNonNull(label, "label");
        this.active = active == null ? () -> false : active;
        this.action = action == null ? () -> { } : action;
        this.input = this.input.withClickable().withFocusable();
    }

    public String icon() {
        return icon.get();
    }

    public String label() {
        return label.get();
    }

    public boolean active() {
        return active.getAsBoolean();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        armed = button == 0 && contains(mouseX, mouseY);
        return armed;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean activate = armed && button == 0 && contains(mouseX, mouseY);
        armed = false;
        if (activate) {
            action.run();
            super.mouseReleased(mouseX, mouseY, button);
            return true;
        }
        return false;
    }
}
