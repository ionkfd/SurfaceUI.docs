package dev.surfaceui.client;

import dev.surfaceui.api.SurfaceMode;
import dev.surfaceui.api.HudGuiLibrary;
import dev.surfaceui.config.SurfaceConfigStore;
import dev.surfaceui.runtime.SurfaceManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class SurfaceUiClient implements ClientModInitializer {
    public static final String MOD_ID = "surfaceui";
    private static SurfaceManager manager;

    public static SurfaceManager manager() {
        return manager;
    }

    @Override
    public void onInitializeClient() {
        HudGuiLibrary.initialize();
        SurfaceConfigStore configStore = new SurfaceConfigStore();
        configStore.load();
        manager = new SurfaceManager(configStore);

        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.of(MOD_ID, "hud_surface"),
                (context, tickCounter) -> manager.renderHud(context, tickCounter)
        );

        KeyBinding editHud = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.surfaceui.edit_hud",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_BRACKET,
                KeyBinding.Category.create(Identifier.of(MOD_ID, "controls"))
        ));

        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (editHud.wasPressed()) {
                if (manager.mode() == SurfaceMode.HUD_EDIT_MODE) {
                    manager.mode(SurfaceMode.PLAY_MODE);
                    MinecraftClient.getInstance().setScreen(null);
                } else {
                    manager.mode(SurfaceMode.HUD_EDIT_MODE);
                    MinecraftClient.getInstance().setScreen(new dev.surfaceui.client.screen.HudEditorScreen(manager));
                }
            }
        });
    }
}
