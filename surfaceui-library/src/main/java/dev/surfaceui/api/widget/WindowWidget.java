package dev.surfaceui.api.widget;

import dev.surfaceui.api.layout.Axis;
import dev.surfaceui.api.layout.Align;
import dev.surfaceui.api.style.Paint;
import dev.surfaceui.api.style.Style;
import java.util.function.Supplier;

public final class WindowWidget extends ContainerWidget {
    public WindowWidget(Supplier<String> title, int titleHeight) {
        super(Axis.VERTICAL, false);
        add(Widgets.row()
                .id("surfaceui:window_title")
                .height(titleHeight)
                .style(Style.create().align(Align.START, Align.CENTER).build())
                .add(Widgets.text(title)
                        .id("surfaceui:window_title_text")
                        .height(titleHeight)
                        .style(Style.create().padding(8).color(Paint.argb(0xFFEAF2FA)).align(Align.START, Align.CENTER).build())));
    }
}
