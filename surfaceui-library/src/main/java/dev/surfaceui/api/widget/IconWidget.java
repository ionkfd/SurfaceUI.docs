package dev.surfaceui.api.widget;

import net.minecraft.util.Identifier;

public final class IconWidget extends AbstractWidget<IconWidget> {
    private final Identifier texture;

    public IconWidget(Identifier texture) {
        this.texture = texture;
    }

    public Identifier texture() {
        return texture;
    }
}
