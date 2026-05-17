package dev.surfaceui.runtime;

import dev.surfaceui.api.layout.LayoutRect;
import dev.surfaceui.api.layout.Align;
import dev.surfaceui.api.style.Paint;
import dev.surfaceui.api.style.Radius;
import dev.surfaceui.api.style.Style;
import dev.surfaceui.api.widget.ButtonWidget;
import dev.surfaceui.api.widget.CheckboxWidget;
import dev.surfaceui.api.widget.CustomRenderWidget;
import dev.surfaceui.api.widget.DropdownWidget;
import dev.surfaceui.api.widget.IconWidget;
import dev.surfaceui.api.widget.ImageWidget;
import dev.surfaceui.api.widget.ProgressWidget;
import dev.surfaceui.api.widget.SelectButtonWidget;
import dev.surfaceui.api.widget.SidebarItemWidget;
import dev.surfaceui.api.widget.SliderWidget;
import dev.surfaceui.api.widget.TextWidget;
import dev.surfaceui.api.widget.Widget;
import dev.surfaceui.render.DrawContextSurfaceCanvas;
import dev.surfaceui.render.SurfaceCanvas;
import dev.surfaceui.render.SurfaceFonts;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public final class SurfaceRenderer {
    private final WidgetStateStore states = WidgetStateStore.get();

    public void render(DrawContext context, RenderTickCounter tickCounter, List<ResolvedWidget> widgets) {
        SurfaceCanvas canvas = new DrawContextSurfaceCanvas(context);
        for (ResolvedWidget resolved : widgets) {
            renderWidget(canvas, context, tickCounter, resolved);
        }
    }

    private void renderWidget(SurfaceCanvas canvas, DrawContext context, RenderTickCounter tickCounter, ResolvedWidget resolved) {
        Widget widget = resolved.widget();
        LayoutRect rect = resolved.rect();
        Style style = resolved.style();
        LayoutRect clip = resolved.clip();
        if (rect.width() <= 0 || rect.height() <= 0 || clip.width() <= 0 || clip.height() <= 0) {
            return;
        }

        canvas.pushScissor(clip);
        drawShadow(canvas, rect, style);
        if (!paintsOwnChrome(widget)) {
            fill(canvas, rect, style);
        }

        if (widget instanceof TextWidget text) {
            drawStyledText(canvas, text.text(), rect, style);
        } else if (widget instanceof ButtonWidget button) {
            drawButton(canvas, button, rect, style);
        } else if (widget instanceof CheckboxWidget checkbox) {
            drawCheckbox(canvas, checkbox, rect, style);
        } else if (widget instanceof SliderWidget slider) {
            drawSlider(canvas, slider, rect, style);
        } else if (widget instanceof DropdownWidget dropdown) {
            drawDropdown(canvas, dropdown, rect, style);
        } else if (widget instanceof SelectButtonWidget select) {
            drawSelect(canvas, select, rect, style);
        } else if (widget instanceof SidebarItemWidget sidebar) {
            drawSidebarItem(canvas, sidebar, rect, style);
        } else if (widget instanceof ProgressWidget bar) {
            int fillWidth = (int) Math.round(rect.width() * bar.progress());
            Paint accent = accent(style);
            canvas.drawRoundedRect(new LayoutRect(rect.x(), rect.y(), fillWidth, rect.height()), style.corner(), accent, 1.0F);
        } else if (widget instanceof ImageWidget image) {
            canvas.drawImage(image.texture(), rect);
        } else if (widget instanceof IconWidget icon) {
            canvas.drawImage(icon.texture(), rect);
        } else if (widget instanceof CustomRenderWidget custom) {
            custom.customRenderer().ifPresent(renderer -> renderer.render(context, tickCounter, rect, style));
        } else {
            widget.customRenderer().ifPresent(renderer -> renderer.render(context, tickCounter, rect, style));
        }
        canvas.popScissor();
    }

    private void drawButton(SurfaceCanvas canvas, ButtonWidget button, LayoutRect rect, Style style) {
        WidgetState state = states.state(button.id());
        Paint top = style.tone();
        Paint bottom = bottomPaint(style);
        if (state.hovered()) {
            top = top.blend(Paint.argb(0xFF5D9FEA), 0.16F);
            bottom = bottom.blend(Paint.argb(0xFF2B77C9), 0.12F);
        }
        if (state.pressed()) {
            top = top.blend(Paint.argb(0xFF07111C), 0.18F);
            bottom = bottom.blend(Paint.argb(0xFF07111C), 0.24F);
        }
        canvas.drawRoundedGradientRect(rect, style.corner(), top, top, Math.max(1, style.stroke().width()), style.stroke().paint(), style.opacity());
        canvas.drawTextCentered(button.label(), rect, style);
    }

    private void drawCheckbox(SurfaceCanvas canvas, CheckboxWidget checkbox, LayoutRect rect, Style style) {
        WidgetState state = states.state(checkbox.id());
        int boxSize = Math.min(18, Math.max(14, rect.height() - 6));
        int boxY = rect.y() + (rect.height() - boxSize) / 2;
        LayoutRect box = new LayoutRect(rect.x(), boxY, boxSize, boxSize);
        Paint top = checkbox.checked() ? Paint.argb(0xFF3D8DE6) : Paint.argb(state.hovered() ? 0x991A2734 : 0x66101A24);
        Paint bottom = checkbox.checked() ? Paint.argb(0xFF286FC1) : Paint.argb(state.hovered() ? 0x99121E29 : 0x66101820);
        Paint border = checkbox.checked() ? Paint.argb(0xFF74BEFF) : Paint.argb(0xFF344251);
        canvas.drawRoundedRect(box, Radius.all(Math.min(6, boxSize / 3)), top, 1.0F);
        canvas.drawRoundedOutline(box, Radius.all(Math.min(6, boxSize / 3)), 1, border, 1.0F);

        canvas.drawText(checkbox.label(), rect.x() + boxSize + 12, rect.y() + (rect.height() - textHeight(style)) / 2, style);
    }

    private void drawSlider(SurfaceCanvas canvas, SliderWidget slider, LayoutRect rect, Style style) {
        canvas.drawText(slider.label(), rect.x(), rect.y() + (rect.height() - textHeight(style)) / 2, style);
        LayoutRect track = slider.trackRect();
        canvas.drawRoundedRect(track, Radius.all(Math.max(2, track.height() / 2)), Paint.argb(0xFF333846), 1.0F);
        int filled = Math.max(0, Math.min(track.width(), (int) Math.round(track.width() * slider.normalizedValue())));
        Paint accent = accent(style);
        canvas.drawRoundedRect(new LayoutRect(track.x(), track.y(), filled, track.height()), Radius.all(Math.max(2, track.height() / 2)), accent, 1.0F);
        int knobX = track.x() + filled;
        canvas.drawRoundedRect(new LayoutRect(knobX - 6, track.y() - 5, 12, 12), Radius.all(6), Paint.argb(0xFFFFE3A2), 1.0F);
        drawSliderValue(canvas, slider.formattedValue(), rect, style);
    }

    private void drawSliderValue(SurfaceCanvas canvas, String formattedValue, LayoutRect rect, Style style) {
        ValueParts value = splitValue(formattedValue);
        int textY = rect.y() + (rect.height() - textHeight(style)) / 2;
        int unitWidth = value.unit().isEmpty() ? 0 : SurfaceFonts.renderer().measureText(value.unit(), style.fontSize()).width();
        int numberWidth = SurfaceFonts.renderer().measureText(value.number(), style.fontSize()).width();
        int unitX = rect.x() + rect.width() - unitWidth;
        int numberX = unitX - numberWidth - (value.unit().isEmpty() ? 0 : 2);
        canvas.drawText(value.number(), numberX, textY, style);
        if (!value.unit().isEmpty()) {
            Style unitStyle = Style.create()
                    .fontSize(style.fontSize())
                    .color(style.text().withAlpha(0.72F))
                    .font(style.font())
                    .build();
            canvas.drawText(value.unit(), unitX, textY, unitStyle);
        }
    }

    private ValueParts splitValue(String value) {
        int split = value.length();
        while (split > 0 && Character.isLetter(value.charAt(split - 1))) {
            split--;
        }
        if (split == value.length() || split == 0) {
            return new ValueParts(value, "");
        }
        return new ValueParts(value.substring(0, split), value.substring(split));
    }

    private record ValueParts(String number, String unit) {
    }

    private void drawSelect(SurfaceCanvas canvas, SelectButtonWidget select, LayoutRect rect, Style style) {
        WidgetState state = states.state(select.id());
        Paint top = state.hovered() ? style.tone().blend(Paint.argb(0xFF5D9FEA), 0.12F) : style.tone();
        canvas.drawRoundedGradientRect(rect, style.corner(), top, top, Math.max(1, style.stroke().width()), style.stroke().paint(), style.opacity());
        canvas.drawText(select.value(), rect.x() + 10, rect.y() + (rect.height() - textHeight(style)) / 2, style);
        canvas.drawText("v", rect.x() + rect.width() - 16, rect.y() + (rect.height() - textHeight(style)) / 2, style);
    }

    private void drawDropdown(SurfaceCanvas canvas, DropdownWidget dropdown, LayoutRect rect, Style style) {
        WidgetState state = states.state(dropdown.id());
        LayoutRect head = new LayoutRect(rect.x(), rect.y(), rect.width(), dropdown.closedHeight());
        Paint top = state.hovered() ? style.tone().blend(Paint.argb(0xFF5D9FEA), 0.12F) : style.tone();
        canvas.drawRoundedGradientRect(head, style.corner(), top, top, Math.max(1, style.stroke().width()), style.stroke().paint(), style.opacity());
        canvas.drawText(dropdown.selected(), head.x() + 10, head.y() + (head.height() - textHeight(style)) / 2, style);
        canvas.drawText(dropdown.open() ? "^" : "v", head.x() + head.width() - 16, head.y() + (head.height() - textHeight(style)) / 2, style);

        if (!dropdown.open()) {
            return;
        }

        int y = rect.y() + dropdown.closedHeight();
        for (String option : dropdown.options()) {
            LayoutRect optionRect = new LayoutRect(rect.x(), y, rect.width(), dropdown.optionHeight());
            boolean selected = option.equals(dropdown.selected());
            Paint optionTop = selected ? Paint.argb(0xFF2F79D2) : Paint.argb(0xEE101820);
            canvas.drawRoundedRect(optionRect, Radius.all(2), optionTop, 1.0F);
            canvas.drawText(option, optionRect.x() + 10, optionRect.y() + (optionRect.height() - textHeight(style)) / 2, style);
            y += dropdown.optionHeight();
        }
    }

    private void drawSidebarItem(SurfaceCanvas canvas, SidebarItemWidget item, LayoutRect rect, Style style) {
        WidgetState state = states.state(item.id());
        if (item.active() || state.hovered()) {
            Paint top = item.active() ? style.tone() : Paint.argb(0x661A2734);
            canvas.drawRoundedGradientRect(rect, style.corner(), top, top, style.stroke().width(), style.stroke().paint(), style.opacity());
        }
        canvas.drawText(item.icon(), rect.x() + 12, rect.y() + (rect.height() - textHeight(style)) / 2, style);
        canvas.drawText(item.label(), rect.x() + 38, rect.y() + (rect.height() - textHeight(style)) / 2, style);
    }

    private void drawStyledText(SurfaceCanvas canvas, String text, LayoutRect rect, Style style) {
        int textWidth = SurfaceFonts.renderer().measureText(text, style.fontSize()).width();
        int textHeight = SurfaceFonts.renderer().measureText(text, style.fontSize()).height();
        int textX = alignedX(rect, style, textWidth);
        int textY = alignedY(rect, style, textHeight);
        canvas.drawText(text, textX, textY, style);
    }

    private void fill(SurfaceCanvas canvas, LayoutRect rect, Style style) {
        Paint paint = style.tone().withAlpha(style.opacity());
        Paint bottom = bottomPaint(style).withAlpha(style.opacity());
        Paint border = style.stroke().paint();
        if ((paint.argb() >>> 24) != 0 || (bottom.argb() >>> 24) != 0 || (border.argb() >>> 24) != 0) {
            canvas.drawRoundedGradientRect(rect, style.corner(), style.tone(), bottomPaint(style), style.stroke().width(), border, style.opacity());
        }
    }

    private boolean paintsOwnChrome(Widget widget) {
        return widget instanceof ButtonWidget
                || widget instanceof CheckboxWidget
                || widget instanceof SliderWidget
                || widget instanceof DropdownWidget
                || widget instanceof SelectButtonWidget
                || widget instanceof SidebarItemWidget
                || widget instanceof ProgressWidget;
    }

    private int textHeight(Style style) {
        return SurfaceFonts.renderer().getTextHeight(style.fontSize());
    }

    private void drawShadow(SurfaceCanvas canvas, LayoutRect rect, Style style) {
        if ((style.lift().paint().argb() >>> 24) != 0) {
            canvas.drawShadow(rect, style.corner(), style.lift().paint(), style.lift().blur(), style.lift().offsetX(), style.lift().offsetY(), 1.0F);
        }
    }

    private Paint bottomPaint(Style style) {
        return (style.gradient().argb() >>> 24) == 0 ? style.tone() : style.gradient();
    }

    private Paint accent(Style style) {
        return (style.tone().argb() >>> 24) == 0 ? Paint.argb(0xFF55A7FF) : style.tone();
    }

    private int alignedX(LayoutRect rect, Style style, int contentWidth) {
        return switch (style.horizontalAlign()) {
            case CENTER -> rect.x() + (rect.width() - contentWidth) / 2;
            case END -> rect.x() + rect.width() - style.inset().right() - contentWidth;
            case STRETCH, START -> rect.x() + style.inset().left();
        };
    }

    private int alignedY(LayoutRect rect, Style style, int contentHeight) {
        return switch (style.verticalAlign()) {
            case CENTER -> rect.y() + (rect.height() - contentHeight) / 2;
            case END -> rect.y() + rect.height() - style.inset().bottom() - contentHeight;
            case STRETCH, START -> rect.y() + style.inset().top();
        };
    }
}
