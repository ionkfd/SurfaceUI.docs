package dev.surfaceui.demo;

import dev.surfaceui.api.SurfaceKind;
import dev.surfaceui.api.SurfaceMode;
import dev.surfaceui.api.layout.LayoutRect;
import dev.surfaceui.api.style.Paint;
import dev.surfaceui.api.style.Theme;
import dev.surfaceui.api.widget.Widget;
import dev.surfaceui.config.UserConfig;
import dev.surfaceui.render.DrawContextSurfaceCanvas;
import dev.surfaceui.render.SurfaceCanvas;
import dev.surfaceui.runtime.InputPipeline;
import dev.surfaceui.runtime.LayoutEngine;
import dev.surfaceui.runtime.ResolvedWidget;
import dev.surfaceui.runtime.SurfaceFrameContext;
import dev.surfaceui.runtime.SurfaceRenderer;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public final class LarpGuiScreen extends Screen {
    private final LayoutEngine layoutEngine = new LayoutEngine();
    private final SurfaceRenderer renderer = new SurfaceRenderer();
    private final InputPipeline input = new InputPipeline(new dev.surfaceui.config.ConfigManager());
    private final LarpGuiState state = LarpGuiState.get();
    private List<ResolvedWidget> lastPlan = List.of();

    public LarpGuiScreen() {
        super(Text.literal("LARP"));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackdrop(context);
        Widget root = LarpGuiWidgets.create(state, width, height);
        SurfaceFrameContext frame = new SurfaceFrameContext(SurfaceKind.SCREEN, SurfaceMode.SCREEN_MODE, width, height, Theme.DEFAULT);
        lastPlan = layoutEngine.layout(frame, List.of(root), new UserConfig());
        input.mouseMoved(lastPlan, mouseX, mouseY);
        renderer.render(context, RenderTickCounter.ONE, lastPlan);
        super.render(context, mouseX, mouseY, delta);
    }

    private void renderBackdrop(DrawContext context) {
        SurfaceCanvas canvas = new DrawContextSurfaceCanvas(context);
        canvas.drawRect(new LayoutRect(0, 0, width, height), Paint.argb(0xF0050609), 1.0F);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (state.listeningModule() != null && !hitWidget(click.x(), click.y(), ":bind_")) {
            state.cancelBinding();
            return true;
        }
        if (state.searchFocused() && !hitWidget(click.x(), click.y(), "larp:search")) {
            state.blurSearch();
        }
        return input.mouseClicked(SurfaceMode.SCREEN_MODE, lastPlan, click.x(), click.y(), click.button())
                || super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        return input.mouseDragged(SurfaceMode.SCREEN_MODE, click.x(), click.y())
                || super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        return input.mouseReleased(click.x(), click.y(), click.button())
                || super.mouseReleased(click);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (state.selectedTab() == LarpGuiState.Tab.DUNGEON && hitWidget(mouseX, mouseY, "larp:dungeon_page")) {
            state.scrollDungeon(verticalAmount);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (state.listeningModule() != null) {
            state.acceptBinding(input.key());
            return true;
        }
        if ((input.modifiers() & GLFW.GLFW_MOD_CONTROL) != 0 && input.key() == GLFW.GLFW_KEY_F) {
            state.focusSearch();
            return true;
        }
        if (state.searchFocused()) {
            if (input.key() == GLFW.GLFW_KEY_ESCAPE) {
                state.blurSearch();
                return true;
            }
            if (input.key() == GLFW.GLFW_KEY_BACKSPACE) {
                state.backspaceSearch();
                return true;
            }
            if (input.key() == GLFW.GLFW_KEY_DELETE) {
                state.clearSearch();
                return true;
            }
            return true;
        }
        if (MinecraftClient.getInstance().options.inventoryKey.matchesKey(input)) {
            state.save();
            close();
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean charTyped(CharInput input) {
        if (state.searchFocused()) {
            if (input.isValidChar()) {
                state.appendSearch(input.asString());
            }
            return true;
        }
        return super.charTyped(input);
    }

    private boolean hitWidget(double mouseX, double mouseY, String idPart) {
        for (int i = lastPlan.size() - 1; i >= 0; i--) {
            ResolvedWidget widget = lastPlan.get(i);
            if (widget.widget().id().contains(idPart)
                    && widget.rect().contains(mouseX, mouseY)
                    && widget.clip().contains(mouseX, mouseY)) {
                return true;
            }
        }
        return false;
    }
}
