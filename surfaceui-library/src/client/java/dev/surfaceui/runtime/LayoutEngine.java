package dev.surfaceui.runtime;

import dev.surfaceui.api.layout.Axis;
import dev.surfaceui.api.layout.LayoutRect;
import dev.surfaceui.api.layout.LayoutSpec;
import dev.surfaceui.api.layout.SizeSpec;
import dev.surfaceui.api.layout.SizeValue;
import dev.surfaceui.api.style.Insets;
import dev.surfaceui.api.style.Style;
import dev.surfaceui.api.style.Theme;
import dev.surfaceui.api.widget.ButtonWidget;
import dev.surfaceui.api.widget.CheckboxWidget;
import dev.surfaceui.api.widget.ContainerWidget;
import dev.surfaceui.api.widget.DropdownWidget;
import dev.surfaceui.api.widget.ImageWidget;
import dev.surfaceui.api.widget.ProgressWidget;
import dev.surfaceui.api.widget.SelectButtonWidget;
import dev.surfaceui.api.widget.SidebarItemWidget;
import dev.surfaceui.api.widget.SliderWidget;
import dev.surfaceui.api.widget.SpacerWidget;
import dev.surfaceui.api.widget.TextWidget;
import dev.surfaceui.api.widget.Widget;
import dev.surfaceui.config.UserConfig;
import dev.surfaceui.config.WidgetOverride;
import dev.surfaceui.render.SurfaceFonts;
import dev.surfaceui.render.SurfaceMeasurements;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class LayoutEngine {
    private final StyleResolver styleResolver = new StyleResolver();

    public List<ResolvedWidget> layout(SurfaceFrameContext context, List<Widget> roots, UserConfig config) {
        List<ResolvedWidget> resolved = new ArrayList<>();
        LayoutRect viewport = new LayoutRect(0, 0, context.viewportWidth(), context.viewportHeight());

        for (Widget root : roots) {
            layoutWidget(context, root, viewport, viewport, config, resolved);
        }

        resolved.sort(Comparator.comparingInt(ResolvedWidget::zIndex));
        return resolved;
    }

    private LayoutRect layoutWidget(SurfaceFrameContext context, Widget widget, LayoutRect parent, LayoutRect clip, UserConfig config, List<ResolvedWidget> out) {
        Style style = styleResolver.resolve(context.theme(), widget);
        LayoutRect rect = place(widget, parent, style, config);
        widget.bounds(rect);
        LayoutRect nextClip = clip.intersect(rect);
        out.add(new ResolvedWidget(widget, rect, style, widget.layout().zIndex(), nextClip));

        if (widget instanceof ContainerWidget container) {
            layoutChildren(context, container, rect.inset(style.inset().left(), style.inset().top(), style.inset().right(), style.inset().bottom()), nextClip, config, out);
        }

        return rect;
    }

    private void layoutChildren(SurfaceFrameContext context, ContainerWidget container, LayoutRect content, LayoutRect clip, UserConfig config, List<ResolvedWidget> out) {
        int cursorX = content.x();
        int cursorY = content.y();
        int gap = styleResolver.resolve(context.theme(), container).gap();

        for (Widget child : container.children()) {
            Style childStyle = styleResolver.resolve(context.theme(), child);
            LayoutRect childRect = container.layered()
                    ? place(child, content, childStyle, config)
                    : flowPlace(container.axis(), child, content, cursorX, cursorY, childStyle);
            child.bounds(childRect);
            LayoutRect childClip = clip.intersect(content).intersect(childRect);
            out.add(new ResolvedWidget(child, childRect, childStyle, child.layout().zIndex(), childClip));

            if (child instanceof ContainerWidget nested) {
                layoutChildren(context, nested, childRect.inset(childStyle.inset().left(), childStyle.inset().top(), childStyle.inset().right(), childStyle.inset().bottom()), childClip, config, out);
            }

            if (!container.layered()) {
                if (container.axis() == Axis.HORIZONTAL) {
                    cursorX += childRect.width() + gap;
                } else {
                    cursorY += childRect.height() + gap;
                }
            }
        }
    }

    private LayoutRect flowPlace(Axis axis, Widget widget, LayoutRect parent, int x, int y, Style style) {
        Insets margin = style.margin();
        int width = measuredWidth(widget, parent, style);
        int height = measuredHeight(widget, parent, style);
        int nextX = x + margin.left();
        int nextY = y + margin.top();
        if (axis == Axis.HORIZONTAL) {
            nextY = parent.y() + margin.top() + Math.max(0, (parent.height() - height - margin.vertical()) / 2);
        }
        return new LayoutRect(nextX, nextY, width, height);
    }

    private LayoutRect place(Widget widget, LayoutRect parent, Style style, UserConfig config) {
        WidgetOverride override = config.overrideFor(widget.id()).orElse(null);
        LayoutSpec spec = widget.layout();
        if (override != null && !override.visible()) {
            return new LayoutRect(-100000, -100000, 0, 0);
        }
        int width = override != null && override.width() != null ? override.width() : measuredWidth(widget, parent, style);
        int height = override != null && override.height() != null ? override.height() : measuredHeight(widget, parent, style);
        int offsetX = override != null ? override.x() : spec.offsetX();
        int offsetY = override != null ? override.y() : spec.offsetY();

        int x = switch (override != null && override.anchor() != null ? override.anchor() : spec.anchor()) {
            case TOP_LEFT, CENTER_LEFT, BOTTOM_LEFT -> parent.x() + offsetX;
            case TOP_CENTER, CENTER, BOTTOM_CENTER -> parent.x() + (parent.width() - width) / 2 + offsetX;
            case TOP_RIGHT, CENTER_RIGHT, BOTTOM_RIGHT -> parent.x() + parent.width() - width + offsetX;
        };
        int y = switch (override != null && override.anchor() != null ? override.anchor() : spec.anchor()) {
            case TOP_LEFT, TOP_CENTER, TOP_RIGHT -> parent.y() + offsetY;
            case CENTER_LEFT, CENTER, CENTER_RIGHT -> parent.y() + (parent.height() - height) / 2 + offsetY;
            case BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT -> parent.y() + parent.height() - height + offsetY;
        };

        return new LayoutRect(x, y, width, height);
    }

    private int measuredWidth(Widget widget, LayoutRect parent, Style style) {
        int measured = switch (widget) {
            case TextWidget text -> SurfaceFonts.renderer().measureText(text.text(), style.fontSize()).width() + style.inset().horizontal();
            case SpacerWidget spacer -> spacer.layout().size().width().resolve(parent.width(), 0);
            case ProgressWidget ignored -> 80;
            case ImageWidget ignored -> 16;
            case ButtonWidget button -> SurfaceMeasurements.measureButtonWidth(button.label(), style.fontSize(), Math.max(10, style.inset().horizontal() / 2));
            case CheckboxWidget checkbox -> SurfaceFonts.renderer().measureText(checkbox.label(), style.fontSize()).width() + 34 + style.inset().horizontal();
            case SliderWidget ignored -> 260 + style.inset().horizontal();
            case SelectButtonWidget select -> SurfaceFonts.renderer().measureText(select.value(), style.fontSize()).width() + 34 + style.inset().horizontal();
            case DropdownWidget dropdown -> Math.max(70, SurfaceFonts.renderer().measureText(dropdown.selected(), style.fontSize()).width() + 34 + style.inset().horizontal());
            case SidebarItemWidget sidebar -> SurfaceFonts.renderer().measureText(sidebar.label(), style.fontSize()).width() + 48 + style.inset().horizontal();
            case ContainerWidget container -> measuredContainerWidth(container, parent, style);
            default -> 32 + style.inset().horizontal();
        };
        SizeSpec size = effectiveSize(widget, style);
        return size.clampWidth(size.width().resolve(parent.width(), measured));
    }

    private int measuredHeight(Widget widget, LayoutRect parent, Style style) {
        int measured = switch (widget) {
            case TextWidget ignored -> SurfaceFonts.renderer().getTextHeight(style.fontSize()) + style.inset().vertical();
            case SpacerWidget spacer -> spacer.layout().size().height().resolve(parent.height(), 0);
            case ProgressWidget ignored -> 8;
            case ImageWidget ignored -> 16;
            case ButtonWidget ignored -> SurfaceMeasurements.measureButtonHeight(style.fontSize(), Math.max(5, style.inset().vertical() / 2), 28);
            case CheckboxWidget ignored -> Math.max(22, SurfaceFonts.renderer().getTextHeight(style.fontSize()) + style.inset().vertical());
            case SliderWidget ignored -> Math.max(24, SurfaceFonts.renderer().getTextHeight(style.fontSize()) + style.inset().vertical());
            case SelectButtonWidget ignored -> SurfaceMeasurements.measureButtonHeight(style.fontSize(), Math.max(5, style.inset().vertical() / 2), 28);
            case DropdownWidget dropdown -> dropdown.closedHeight() + (dropdown.open() ? dropdown.options().size() * dropdown.optionHeight() : 0) + style.inset().vertical();
            case SidebarItemWidget ignored -> 27 + style.inset().vertical();
            case ContainerWidget container -> measuredContainerHeight(container, parent, style);
            default -> 24 + style.inset().vertical();
        };
        SizeSpec size = effectiveSize(widget, style);
        return size.clampHeight(size.height().resolve(parent.height(), measured));
    }

    private SizeSpec effectiveSize(Widget widget, Style style) {
        SizeSpec layoutSize = widget.layout().size();
        SizeSpec styleSize = style.size();
        SizeValue width = layoutSize.width().kind() == SizeValue.Kind.AUTO ? styleSize.width() : layoutSize.width();
        SizeValue height = layoutSize.height().kind() == SizeValue.Kind.AUTO ? styleSize.height() : layoutSize.height();
        int minWidth = Math.max(layoutSize.minWidth(), styleSize.minWidth());
        int minHeight = Math.max(layoutSize.minHeight(), styleSize.minHeight());
        int maxWidth = Math.min(layoutSize.maxWidth(), styleSize.maxWidth());
        int maxHeight = Math.min(layoutSize.maxHeight(), styleSize.maxHeight());
        return new SizeSpec(width, height, minWidth, minHeight, maxWidth, maxHeight);
    }

    private int measuredContainerWidth(ContainerWidget container, LayoutRect parent, Style style) {
        int width = 0;
        int gapCount = Math.max(0, container.children().size() - 1);
        for (Widget child : container.children()) {
            Style childStyle = styleResolver.resolve(Theme.DEFAULT, child);
            int childWidth = measuredWidth(child, parent, childStyle) + childStyle.margin().horizontal();
            if (container.layered()) {
                width = Math.max(width, childWidth);
            } else if (container.axis() == Axis.HORIZONTAL) {
                width += childWidth;
            } else {
                width = Math.max(width, childWidth);
            }
        }
        if (!container.layered() && container.axis() == Axis.HORIZONTAL) {
            width += style.gap() * gapCount;
        }
        return width + style.inset().horizontal();
    }

    private int measuredContainerHeight(ContainerWidget container, LayoutRect parent, Style style) {
        int height = 0;
        int gapCount = Math.max(0, container.children().size() - 1);
        for (Widget child : container.children()) {
            Style childStyle = styleResolver.resolve(Theme.DEFAULT, child);
            int childHeight = measuredHeight(child, parent, childStyle) + childStyle.margin().vertical();
            if (container.layered()) {
                height = Math.max(height, childHeight);
            } else if (container.axis() == Axis.VERTICAL) {
                height += childHeight;
            } else {
                height = Math.max(height, childHeight);
            }
        }
        if (!container.layered() && container.axis() == Axis.VERTICAL) {
            height += style.gap() * gapCount;
        }
        return height + style.inset().vertical();
    }
}
