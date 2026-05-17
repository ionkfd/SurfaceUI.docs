package dev.surfaceui.api.widget;

import java.util.Optional;

public final class CustomRenderWidget extends AbstractWidget<CustomRenderWidget> {
    private final CustomRenderer renderer;

    public CustomRenderWidget(CustomRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public Optional<CustomRenderer> customRenderer() {
        return Optional.of(renderer);
    }
}
