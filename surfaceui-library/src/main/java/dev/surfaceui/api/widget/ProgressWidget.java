package dev.surfaceui.api.widget;

import java.util.function.DoubleSupplier;

public class ProgressWidget extends AbstractWidget<ProgressWidget> {
    private final DoubleSupplier progress;

    public ProgressWidget(DoubleSupplier progress) {
        this.progress = progress;
    }

    public double progress() {
        return Math.max(0.0, Math.min(1.0, progress.getAsDouble()));
    }
}
