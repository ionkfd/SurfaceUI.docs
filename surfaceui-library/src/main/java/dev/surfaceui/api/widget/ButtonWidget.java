package dev.surfaceui.api.widget;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public final class ButtonWidget extends AbstractWidget<ButtonWidget> {
    private final Supplier<String> label;
    private final Runnable action;
    private final BooleanSupplier active;
    private boolean armed;

    public ButtonWidget(Supplier<String> label, Runnable action, BooleanSupplier active) {
        this.label = Objects.requireNonNull(label, "label");
        this.action = action == null ? () -> { } : action;
        this.active = active == null ? () -> false : active;
        this.input = this.input.withClickable().withFocusable();
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
