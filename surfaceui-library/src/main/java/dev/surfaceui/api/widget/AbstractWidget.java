package dev.surfaceui.api.widget;

import dev.surfaceui.api.input.InputTraits;
import dev.surfaceui.api.input.WidgetEvents;
import dev.surfaceui.api.layout.Anchor;
import dev.surfaceui.api.layout.LayoutRect;
import dev.surfaceui.api.layout.LayoutSpec;
import dev.surfaceui.api.layout.SizeSpec;
import dev.surfaceui.api.layout.SizeValue;
import dev.surfaceui.api.style.Style;

public abstract class AbstractWidget<T extends AbstractWidget<T>> implements Widget {
    protected String id = "surfaceui:anonymous";
    protected LayoutSpec layout = LayoutSpec.defaults();
    protected LayoutRect bounds = new LayoutRect(0, 0, 0, 0);
    protected Style style = Style.create().build();
    protected InputTraits input = InputTraits.PASSIVE;
    protected WidgetEvents.Click clickHandler;
    protected WidgetEvents.Hover hoverHandler;
    protected WidgetEvents.Key keyHandler;

    @SuppressWarnings("unchecked")
    protected final T self() {
        return (T) this;
    }

    public T id(String id) {
        this.id = id;
        return self();
    }

    public T anchor(Anchor anchor) {
        this.layout = layout.withAnchor(anchor);
        return self();
    }

    public T offset(int x, int y) {
        this.layout = layout.withOffset(x, y);
        return self();
    }

    public T size(int width, int height) {
        this.layout = layout.withSize(SizeSpec.fixed(width, height));
        return self();
    }

    public T width(int width) {
        SizeSpec size = layout.size();
        this.layout = layout.withSize(new SizeSpec(SizeValue.px(width), size.height(), size.minWidth(), size.minHeight(), size.maxWidth(), size.maxHeight()));
        return self();
    }

    public T height(int height) {
        SizeSpec size = layout.size();
        this.layout = layout.withSize(new SizeSpec(size.width(), SizeValue.px(height), size.minWidth(), size.minHeight(), size.maxWidth(), size.maxHeight()));
        return self();
    }

    public T percentSize(float width, float height) {
        SizeSpec size = layout.size();
        this.layout = layout.withSize(new SizeSpec(SizeValue.percent(width), SizeValue.percent(height), size.minWidth(), size.minHeight(), size.maxWidth(), size.maxHeight()));
        return self();
    }

    public T minSize(int width, int height) {
        this.layout = layout.withSize(layout.size().withMin(width, height));
        return self();
    }

    public T maxSize(int width, int height) {
        this.layout = layout.withSize(layout.size().withMax(width, height));
        return self();
    }

    public T zIndex(int zIndex) {
        this.layout = layout.withZIndex(zIndex);
        return self();
    }

    public T style(Style style) {
        this.style = style;
        return self();
    }

    public T input(InputTraits input) {
        this.input = input;
        return self();
    }

    public T locked(boolean locked) {
        this.input = input.locked(locked);
        return self();
    }

    public T draggable() {
        this.input = input.withDraggable();
        return self();
    }

    public T resizable() {
        this.input = input.withResizable();
        return self();
    }

    public T clickable() {
        this.input = input.withClickable();
        return self();
    }

    public T hoverable() {
        this.input = input.withHoverable();
        return self();
    }

    public T focusable() {
        this.input = input.withFocusable();
        return self();
    }

    public T onClick(WidgetEvents.Click clickHandler) {
        this.clickHandler = clickHandler;
        this.input = this.input.withClickable();
        return self();
    }

    public T onHover(WidgetEvents.Hover hoverHandler) {
        this.hoverHandler = hoverHandler;
        return self();
    }

    public T onKey(WidgetEvents.Key keyHandler) {
        this.keyHandler = keyHandler;
        this.input = this.input.withFocusable();
        return self();
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public LayoutSpec layout() {
        return layout;
    }

    @Override
    public LayoutRect bounds() {
        return bounds;
    }

    @Override
    public void bounds(LayoutRect rect) {
        this.bounds = rect;
    }

    @Override
    public Style style() {
        return style;
    }

    @Override
    public InputTraits input() {
        return input;
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        if (clickHandler != null) {
            clickHandler.onClick(mouseX, mouseY, button);
        }
    }

    @Override
    public void onHover(boolean hovered) {
        if (hoverHandler != null) {
            hoverHandler.onHover(hovered);
        }
    }

    @Override
    public boolean onKey(int keyCode, int scanCode, int modifiers) {
        return keyHandler != null && keyHandler.onKey(keyCode, scanCode, modifiers);
    }
}
