package dev.surfaceui.api.widget;

import dev.surfaceui.api.layout.LayoutRect;
import java.util.Objects;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public final class SliderWidget extends AbstractWidget<SliderWidget> {
    private final Supplier<String> label;
    private final DoubleSupplier value;
    private final DoubleConsumer setter;
    private final double min;
    private final double max;
    private final Supplier<String> formattedValue;
    private boolean dragging;

    public SliderWidget(Supplier<String> label, DoubleSupplier value, DoubleConsumer setter, double min, double max, Supplier<String> formattedValue) {
        this.label = Objects.requireNonNull(label, "label");
        this.value = Objects.requireNonNull(value, "value");
        this.setter = setter == null ? ignored -> { } : setter;
        this.min = Math.min(min, max);
        this.max = Math.max(min, max);
        this.formattedValue = formattedValue == null ? () -> String.format("%.2f", value()) : formattedValue;
        this.input = this.input.withClickable().withFocusable();
    }

    public String label() {
        return label.get();
    }

    public double value() {
        return value.getAsDouble();
    }

    public double normalizedValue() {
        if (max <= min) {
            return 0.0;
        }
        return Math.max(0.0, Math.min(1.0, (value() - min) / (max - min)));
    }

    public String formattedValue() {
        return formattedValue.get();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            dragging = true;
            setFromMouse(mouseX);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!dragging) {
            return false;
        }
        setFromMouse(mouseX);
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!dragging) {
            return false;
        }
        setFromMouse(mouseX);
        dragging = false;
        return true;
    }

    private void setFromMouse(double mouseX) {
        LayoutRect track = trackRect();
        double normalized = Math.max(0.0, Math.min(1.0, (mouseX - track.x()) / Math.max(1.0, track.width())));
        setter.accept(min + normalized * (max - min));
    }

    public LayoutRect trackRect() {
        LayoutRect rect = bounds();
        int labelWidth = Math.min(108, Math.max(68, rect.width() / 3));
        int valueWidth = 64;
        int trackX = rect.x() + labelWidth + 10;
        int trackWidth = Math.max(24, rect.width() - labelWidth - valueWidth - 16);
        int trackY = rect.y() + rect.height() / 2 - 2;
        return new LayoutRect(trackX, trackY, trackWidth, 4);
    }
}
