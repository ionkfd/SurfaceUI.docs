package dev.surfaceui.api;

import dev.surfaceui.runtime.HudGuiRuntime;

public final class HudGuiLibrary {
    private HudGuiLibrary() {
    }

    public static HudGui api() {
        return HudGuiApi.api();
    }

    public static HudGuiRuntime runtime() {
        return HudGuiRuntime.get();
    }

    public static void initialize() {
        HudGuiRuntime.get().initialize();
    }
}
