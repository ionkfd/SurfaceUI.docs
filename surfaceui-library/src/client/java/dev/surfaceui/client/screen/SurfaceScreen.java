package dev.surfaceui.client.screen;

import dev.surfaceui.api.SurfaceMode;
import dev.surfaceui.api.widget.Widget;
import dev.surfaceui.config.ConfigManager;
import dev.surfaceui.runtime.HudGuiRuntime;
import dev.surfaceui.runtime.InputPipeline;
import dev.surfaceui.runtime.LayoutEngine;
import dev.surfaceui.runtime.ResolvedWidget;
import dev.surfaceui.runtime.SurfaceFrameContext;
import dev.surfaceui.api.SurfaceKind;
import dev.surfaceui.api.style.Theme;
import dev.surfaceui.runtime.SurfaceRenderer;
import java.util.List;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

public class SurfaceScreen extends Screen {
    private final Widget root;
    private final LayoutEngine layout = new LayoutEngine();
    private final SurfaceRenderer renderer = new SurfaceRenderer();
    private final ConfigManager config = new ConfigManager();
    private final InputPipeline input = new InputPipeline(config);
    private List<ResolvedWidget> lastPlan = List.of();

    public SurfaceScreen(Text title, Widget root) {
        super(title);
        this.root = root;
    }

    @Override
    protected void init() {
        super.init();
        config.load();
        HudGuiRuntime.get().mode(SurfaceMode.SCREEN_MODE);
    }

    @Override
    public void removed() {
        HudGuiRuntime.get().mode(SurfaceMode.PLAY_MODE);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        SurfaceFrameContext frame = new SurfaceFrameContext(SurfaceKind.SCREEN, SurfaceMode.SCREEN_MODE, width, height, Theme.DEFAULT);
        lastPlan = layout.layout(frame, List.of(root), config.config());
        input.mouseMoved(lastPlan, mouseX, mouseY);
        renderer.render(context, RenderTickCounter.ONE, lastPlan);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
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
    public boolean keyPressed(KeyInput inputEvent) {
        return input.keyPressed(inputEvent.key(), inputEvent.scancode(), inputEvent.modifiers())
                || super.keyPressed(inputEvent);
    }
}
