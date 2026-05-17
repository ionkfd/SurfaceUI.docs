package dev.surfaceui.api.widget;

import net.minecraft.util.Identifier;

public final class ImageWidget extends AbstractWidget<ImageWidget> {
    private final Identifier texture;

    public ImageWidget(Identifier texture) {
        this.texture = texture;
    }

    public Identifier texture() {
        return texture;
    }
}
