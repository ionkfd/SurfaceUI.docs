package dev.surfaceui.api.style;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class Theme {
    public static final Theme DEFAULT = new Theme("surfaceui:default")
            .put(ThemeTokens.WINDOW, Style.create().verticalGradient(Paint.argb(0xF216202B), Paint.argb(0xF2111821)).stroke(1, Paint.argb(0xFF354251)).corner(14).inset(0).fontSize(14).build())
            .put(ThemeTokens.PANEL, Style.create().verticalGradient(Paint.argb(0xD91A2632), Paint.argb(0xD9141D27)).stroke(1, Paint.argb(0x663A5068)).corner(10).inset(10).fontSize(14).build())
            .put(ThemeTokens.BUTTON, Style.create().verticalGradient(Paint.argb(0xCC1D2A36), Paint.argb(0xCC15202B)).stroke(1, Paint.argb(0xFF354554)).corner(8).padding(6).fontSize(14).text(Paint.argb(0xFFEAF2FA)).build())
            .put(ThemeTokens.BUTTON_HOVER, Style.create().verticalGradient(Paint.argb(0xDD253545), Paint.argb(0xDD1A2835)).stroke(1, Paint.argb(0xFF466176)).corner(8).padding(6).fontSize(14).text(Paint.argb(0xFFFFFFFF)).build())
            .put(ThemeTokens.BUTTON_ACTIVE, Style.create().verticalGradient(Paint.argb(0xFF3D88DE), Paint.argb(0xFF276ABD)).stroke(1, Paint.argb(0xFF67B4FF)).corner(8).padding(6).fontSize(14).text(Paint.argb(0xFFFFFFFF)).build())
            .put(ThemeTokens.CHECKBOX, Style.create().fontSize(14).text(Paint.argb(0xFFEAF2FA)).build())
            .put(ThemeTokens.SLIDER, Style.create().fontSize(14).text(Paint.argb(0xFFEAF2FA)).build())
            .put(ThemeTokens.SELECT, Style.create().verticalGradient(Paint.argb(0x99131D28), Paint.argb(0x99101820)).stroke(1, Paint.argb(0xFF354554)).corner(8).padding(6).fontSize(14).text(Paint.argb(0xFFEAF2FA)).build())
            .put(ThemeTokens.SIDEBAR_ITEM, Style.create().fontSize(14).text(Paint.argb(0xFFEAF2FA)).build())
            .put(ThemeTokens.SIDEBAR_ITEM_ACTIVE, Style.create().verticalGradient(Paint.argb(0xFF3D88DE), Paint.argb(0xFF276ABD)).corner(8).fontSize(14).text(Paint.argb(0xFFFFFFFF)).build())
            .put(ThemeTokens.TEXT, Style.create().fontSize(14).text(Paint.argb(0xFFE8F2FF)).build())
            .put(ThemeTokens.TEXT_MUTED, Style.create().fontSize(12).text(Paint.argb(0xFFB9C5D1)).build())
            .put(ThemeTokens.ACCENT, Style.create().tone(Paint.argb(0xFF44B8FF)).text(Paint.argb(0xFFFFFFFF)).build());

    private final String id;
    private final Map<String, Style> styles = new HashMap<>();

    public Theme(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public Theme put(String name, Style style) {
        styles.put(name, style);
        return this;
    }

    public Optional<Style> get(String name) {
        return Optional.ofNullable(styles.get(name));
    }
}
