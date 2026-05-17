package dev.surfaceui.api.widget;

import dev.surfaceui.api.style.Paint;
import dev.surfaceui.api.style.Style;
import java.util.function.Supplier;

public final class TextWidget extends AbstractWidget<TextWidget> {
    private final Supplier<String> text;

    public TextWidget(Supplier<String> text) {
        this.text = text;
    }

    public String text() {
        return text.get();
    }

    public TextWidget color(int argb) {
        this.style = Style.create().text(Paint.argb(argb)).build();
        return this;
    }
}
