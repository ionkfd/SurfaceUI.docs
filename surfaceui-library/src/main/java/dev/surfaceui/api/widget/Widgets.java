package dev.surfaceui.api.widget;

import dev.surfaceui.api.layout.Axis;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.util.Identifier;

public final class Widgets {
    private Widgets() {
    }

    public static TextWidget text(String text) {
        return new TextWidget(() -> text);
    }

    public static TextWidget text(Supplier<String> text) {
        return new TextWidget(text);
    }

    public static BoxWidget box() {
        return new BoxWidget();
    }

    public static PanelWidget panel() {
        return new PanelWidget();
    }

    public static WindowWidget window(String title, int titleHeight) {
        return new WindowWidget(() -> title, titleHeight);
    }

    public static WindowWidget window(Supplier<String> title, int titleHeight) {
        return new WindowWidget(title, titleHeight);
    }

    public static ImageWidget image(Identifier texture) {
        return new ImageWidget(texture);
    }

    public static IconWidget icon(Identifier texture) {
        return new IconWidget(texture);
    }

    public static ProgressBarWidget bar(DoubleSupplier progress) {
        return new ProgressBarWidget(progress);
    }

    public static ButtonWidget button(String label, Runnable action) {
        return button(() -> label, action);
    }

    public static ButtonWidget button(Supplier<String> label, Runnable action) {
        return new ButtonWidget(label, action, () -> false);
    }

    public static ButtonWidget button(Supplier<String> label, Runnable action, BooleanSupplier active) {
        return new ButtonWidget(label, action, active);
    }

    public static CheckboxWidget checkbox(String label, BooleanSupplier checked, Consumer<Boolean> setter) {
        return new CheckboxWidget(() -> label, checked, setter);
    }

    public static CheckboxWidget checkbox(Supplier<String> label, BooleanSupplier checked, Consumer<Boolean> setter) {
        return new CheckboxWidget(label, checked, setter);
    }

    public static SliderWidget slider(String label, DoubleSupplier value, DoubleConsumer setter, double min, double max) {
        return new SliderWidget(() -> label, value, setter, min, max, null);
    }

    public static SliderWidget slider(String label, DoubleSupplier value, DoubleConsumer setter, double min, double max, Supplier<String> formattedValue) {
        return new SliderWidget(() -> label, value, setter, min, max, formattedValue);
    }

    public static SelectButtonWidget select(Supplier<String> value, Runnable action) {
        return new SelectButtonWidget(value, action);
    }

    public static DropdownWidget dropdown(Supplier<String> selected, List<String> options, Consumer<String> setter) {
        return new DropdownWidget(selected, options, setter);
    }

    public static SidebarItemWidget sidebarItem(String icon, String label, BooleanSupplier active, Runnable action) {
        return new SidebarItemWidget(() -> icon, () -> label, active, action);
    }

    public static RowWidget row() {
        return new RowWidget();
    }

    public static ColumnWidget column() {
        return new ColumnWidget();
    }

    public static StackWidget stack() {
        return new StackWidget();
    }

    public static SpacerWidget spacer(int width, int height) {
        return new SpacerWidget(width, height);
    }

    public static InfoHudWidget infoHud(String title, Supplier<String> value) {
        return new InfoHudWidget(title, value);
    }

    public static CustomRenderWidget custom(CustomRenderer renderer) {
        return new CustomRenderWidget(renderer);
    }
}
