package dev.surfaceui.api.widget;

import dev.surfaceui.api.layout.Axis;
import java.util.function.Supplier;

public final class InfoHudWidget extends ContainerWidget {
    public InfoHudWidget(String title, Supplier<String> value) {
        super(Axis.VERTICAL, false);
        add(new TextWidget(() -> title));
        add(new TextWidget(value));
    }
}
