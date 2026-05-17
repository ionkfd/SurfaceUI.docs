package dev.surfaceui.api;

import dev.surfaceui.api.style.Style;
import dev.surfaceui.api.style.Theme;
import dev.surfaceui.api.widget.Widget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

public interface HudGui {
    SurfaceRegistration registerHud(Identifier id, SurfaceProvider provider);

    SurfaceRegistration registerHud(Identifier id, int zIndex, SurfaceProvider provider);

    SurfaceRegistration registerOverlay(Identifier id, SurfaceProvider provider);

    SurfaceRegistration registerOverlay(Identifier id, int zIndex, SurfaceProvider provider);

    SurfaceRegistration registerPopup(Identifier id, SurfaceProvider provider);

    SurfaceRegistration registerPopup(Identifier id, int zIndex, SurfaceProvider provider);

    void openScreen(Screen screen);

    void openScreen(Widget root);

    void registerTheme(Theme theme);

    Theme theme(String id);

    Style style(String themeId, String styleName);
}
