package dev.surfaceui.api.style;

import dev.surfaceui.api.layout.Align;
import dev.surfaceui.api.layout.SizeSpec;
import dev.surfaceui.api.layout.SizeValue;
import net.minecraft.util.Identifier;

public record Style(
        Insets margin,
        Insets inset,
        int gap,
        Radius corner,
        Paint tone,
        Paint gradient,
        Border stroke,
        float opacity,
        Shadow lift,
        int fontSize,
        Paint text,
        Align horizontalAlign,
        Align verticalAlign,
        Identifier font,
        SizeSpec size
) {
    private static final Style DEFAULTS = new Builder().build();

    public static Builder create() {
        return new Builder();
    }

    public Style merge(Style other) {
        if (other == null) {
            return this;
        }

        return new Style(
                other.margin.equals(DEFAULTS.margin) ? margin : other.margin,
                other.inset.equals(DEFAULTS.inset) ? inset : other.inset,
                other.gap == DEFAULTS.gap ? gap : other.gap,
                other.corner.equals(DEFAULTS.corner) ? corner : other.corner,
                other.tone.equals(DEFAULTS.tone) ? tone : other.tone,
                other.gradient.equals(DEFAULTS.gradient) ? gradient : other.gradient,
                other.stroke.equals(DEFAULTS.stroke) ? stroke : other.stroke,
                other.opacity == DEFAULTS.opacity ? opacity : other.opacity,
                other.lift.equals(DEFAULTS.lift) ? lift : other.lift,
                other.fontSize == DEFAULTS.fontSize ? fontSize : other.fontSize,
                other.text.equals(DEFAULTS.text) ? text : other.text,
                other.horizontalAlign == DEFAULTS.horizontalAlign ? horizontalAlign : other.horizontalAlign,
                other.verticalAlign == DEFAULTS.verticalAlign ? verticalAlign : other.verticalAlign,
                other.font == DEFAULTS.font ? font : other.font,
                other.size.equals(DEFAULTS.size) ? size : other.size
        );
    }

    public static final class Builder {
        private Insets margin = Insets.ZERO;
        private Insets inset = Insets.ZERO;
        private int gap;
        private Radius corner = Radius.ZERO;
        private Paint tone = Paint.TRANSPARENT;
        private Paint gradient = Paint.TRANSPARENT;
        private Border stroke = Border.NONE;
        private float opacity = 1.0F;
        private Shadow lift = Shadow.NONE;
        private int fontSize = 9;
        private Paint text = Paint.argb(0xFFFFFFFF);
        private Align horizontalAlign = Align.START;
        private Align verticalAlign = Align.START;
        private Identifier font;
        private SizeSpec size = SizeSpec.auto();

        public Builder margin(int value) {
            this.margin = Insets.all(value);
            return this;
        }

        public Builder margin(Insets margin) {
            this.margin = margin;
            return this;
        }

        public Builder inset(int value) {
            this.inset = Insets.all(value);
            return this;
        }

        public Builder padding(int value) {
            return inset(value);
        }

        public Builder padding(Insets padding) {
            this.inset = padding;
            return this;
        }

        public Builder gap(int value) {
            this.gap = value;
            return this;
        }

        public Builder corner(int value) {
            this.corner = Radius.all(value);
            return this;
        }

        public Builder radius(int value) {
            return corner(value);
        }

        public Builder radius(Radius radius) {
            this.corner = radius;
            return this;
        }

        public Builder tone(Paint paint) {
            this.tone = paint;
            return this;
        }

        public Builder background(Paint paint) {
            return tone(paint);
        }

        public Builder gradient(Paint to) {
            this.gradient = to;
            return this;
        }

        public Builder verticalGradient(Paint from, Paint to) {
            this.tone = from;
            this.gradient = to;
            return this;
        }

        public Builder stroke(int width, Paint paint) {
            this.stroke = new Border(width, paint);
            return this;
        }

        public Builder border(int width, Paint paint) {
            return stroke(width, paint);
        }

        public Builder opacity(float opacity) {
            this.opacity = opacity;
            return this;
        }

        public Builder lift(Shadow lift) {
            this.lift = lift;
            return this;
        }

        public Builder shadow(Shadow shadow) {
            return lift(shadow);
        }

        public Builder glow(Paint paint, int radius) {
            this.lift = new Shadow(0, 0, radius, paint);
            return this;
        }

        public Builder fontSize(int fontSize) {
            this.fontSize = fontSize;
            return this;
        }

        public Builder fontScale(float scale) {
            this.fontSize = Math.max(1, Math.round(9 * scale));
            return this;
        }

        public Builder text(Paint text) {
            this.text = text;
            return this;
        }

        public Builder color(Paint text) {
            return text(text);
        }

        public Builder align(Align horizontal, Align vertical) {
            this.horizontalAlign = horizontal;
            this.verticalAlign = vertical;
            return this;
        }

        public Builder font(Identifier font) {
            this.font = font;
            return this;
        }

        public Builder width(int width) {
            this.size = new SizeSpec(SizeValue.px(width), size.height(), size.minWidth(), size.minHeight(), size.maxWidth(), size.maxHeight());
            return this;
        }

        public Builder height(int height) {
            this.size = new SizeSpec(size.width(), SizeValue.px(height), size.minWidth(), size.minHeight(), size.maxWidth(), size.maxHeight());
            return this;
        }

        public Builder percentWidth(float percent) {
            this.size = new SizeSpec(SizeValue.percent(percent), size.height(), size.minWidth(), size.minHeight(), size.maxWidth(), size.maxHeight());
            return this;
        }

        public Builder percentHeight(float percent) {
            this.size = new SizeSpec(size.width(), SizeValue.percent(percent), size.minWidth(), size.minHeight(), size.maxWidth(), size.maxHeight());
            return this;
        }

        public Builder minSize(int width, int height) {
            this.size = new SizeSpec(size.width(), size.height(), width, height, size.maxWidth(), size.maxHeight());
            return this;
        }

        public Builder maxSize(int width, int height) {
            this.size = new SizeSpec(size.width(), size.height(), size.minWidth(), size.minHeight(), width, height);
            return this;
        }

        public Builder size(SizeSpec size) {
            this.size = size;
            return this;
        }

        public Style build() {
            return new Style(margin, inset, gap, corner, tone, gradient, stroke, opacity, lift, fontSize, text, horizontalAlign, verticalAlign, font, size);
        }
    }
}
