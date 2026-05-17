package dev.surfaceui.api.widget;

import java.util.function.DoubleSupplier;

public final class ProgressBarWidget extends ProgressWidget {
    public ProgressBarWidget(DoubleSupplier progress) {
        super(progress);
    }
}
