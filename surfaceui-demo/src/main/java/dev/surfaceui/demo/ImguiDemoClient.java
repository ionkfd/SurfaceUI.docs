package dev.surfaceui.demo;

import dev.surfaceui.api.HudGuiLibrary;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class ImguiDemoClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudGuiLibrary.initialize();

        KeyBinding openDemo = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.surfaceui_imgui_demo.open",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                KeyBinding.Category.create(Identifier.of("surfaceui_imgui_demo", "controls"))
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            LarpGuiState.get().tickKeybinds(client.getWindow().getHandle());
            while (openDemo.wasPressed()) {
                client.setScreen(new LarpGuiScreen());
            }
        });
    }
}
