package dev.surfaceui.api;

import dev.surfaceui.api.style.Style;
import dev.surfaceui.api.style.Theme;
import dev.surfaceui.api.widget.Widget;
import dev.surfaceui.client.screen.SurfaceScreen;
import dev.surfaceui.runtime.HudGuiRuntime;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class HudGuiApi implements HudGui {
    public static final HudGuiApi INSTANCE = new HudGuiApi();

    private HudGuiApi() {
    }

    public static HudGui api() {
        return INSTANCE;
    }

    @Override
    public SurfaceRegistration registerHud(Identifier id, SurfaceProvider provider) {
        return registerHud(id, 0, provider);
    }

    @Override
    public SurfaceRegistration registerHud(Identifier id, int zIndex, SurfaceProvider provider) {
        return HudGuiRuntime.get().surfaces().register(SurfaceKind.HUD, id, zIndex, provider);
    }

    @Override
    public SurfaceRegistration registerOverlay(Identifier id, SurfaceProvider provider) {
        return registerOverlay(id, 50, provider);
    }

    @Override
    public SurfaceRegistration registerOverlay(Identifier id, int zIndex, SurfaceProvider provider) {
        return HudGuiRuntime.get().surfaces().register(SurfaceKind.OVERLAY, id, zIndex, provider);
    }

    @Override
    public SurfaceRegistration registerPopup(Identifier id, SurfaceProvider provider) {
        return registerPopup(id, 100, provider);
    }

    @Override
    public SurfaceRegistration registerPopup(Identifier id, int zIndex, SurfaceProvider provider) {
        return HudGuiRuntime.get().surfaces().register(SurfaceKind.POPUP, id, zIndex, provider);
    }

    @Override
    public void openScreen(Screen screen) {
        MinecraftClient.getInstance().setScreen(screen);
    }

    @Override
    public void openScreen(Widget root) {
        openScreen(new SurfaceScreen(Text.literal(root.id()), root));
    }

    @Override
    public void registerTheme(Theme theme) {
        HudGuiRuntime.get().registerTheme(theme);
    }

    @Override
    public Theme theme(String id) {
        return HudGuiRuntime.get().theme(id);
    }

    @Override
    public Style style(String themeId, String styleName) {
        return theme(themeId).get(styleName).orElse(Style.create().build());
    }
}
