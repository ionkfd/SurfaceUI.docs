package dev.surfaceui.api.widget;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class CheckboxWidget extends AbstractWidget<CheckboxWidget> {
    private final Supplier<String> label;
    private final BooleanSupplier checked;
    private final Consumer<Boolean> setter;
    private boolean armed;

    public CheckboxWidget(Supplier<String> label, BooleanSupplier checked, Consumer<Boolean> setter) {
        this.label = Objects.requireNonNull(label, "label");
        this.checked = Objects.requireNonNull(checked, "checked");
        this.setter = setter == null ? value -> { } : setter;
        this.input = this.input.withClickable().withFocusable();
    }

    public String label() {
        return label.get();
    }

    public boolean checked() {
        return checked.getAsBoolean();
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
            setter.accept(!checked());
            super.mouseReleased(mouseX, mouseY, button);
            return true;
        }
        return false;
    }
}
