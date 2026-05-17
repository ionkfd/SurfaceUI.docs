package dev.surfaceui.api.widget;

import java.util.Objects;
import java.util.function.Supplier;

public final class SelectButtonWidget extends AbstractWidget<SelectButtonWidget> {
    private final Supplier<String> value;
    private final Runnable action;
    private boolean armed;

    public SelectButtonWidget(Supplier<String> value, Runnable action) {
        this.value = Objects.requireNonNull(value, "value");
        this.action = action == null ? () -> { } : action;
        this.input = this.input.withClickable().withFocusable();
    }

    public String value() {
        return value.get();
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
