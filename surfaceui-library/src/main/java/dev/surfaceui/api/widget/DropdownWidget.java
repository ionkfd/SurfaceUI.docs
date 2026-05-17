package dev.surfaceui.api.widget;

import dev.surfaceui.api.layout.LayoutRect;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class DropdownWidget extends AbstractWidget<DropdownWidget> {
    private final Supplier<String> selected;
    private final List<String> options;
    private final Consumer<String> setter;
    private boolean open;
    private boolean armed;

    public DropdownWidget(Supplier<String> selected, List<String> options, Consumer<String> setter) {
        this.selected = Objects.requireNonNull(selected, "selected");
        this.options = List.copyOf(options);
        this.setter = setter == null ? value -> { } : setter;
        this.input = this.input.withClickable().withFocusable();
    }

    public String selected() {
        return selected.get();
    }

    public List<String> options() {
        return options;
    }

    public boolean open() {
        return open;
    }

    public int closedHeight() {
        return 24;
    }

    public int optionHeight() {
        return 20;
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
            LayoutRect rect = bounds();
            if (!open || mouseY < rect.y() + closedHeight()) {
                open = !open;
            } else {
                int index = (int) ((mouseY - rect.y() - closedHeight()) / optionHeight());
                if (index >= 0 && index < options.size()) {
                    setter.accept(options.get(index));
                }
                open = false;
            }
            super.mouseReleased(mouseX, mouseY, button);
            return true;
        }
        return false;
    }
}
