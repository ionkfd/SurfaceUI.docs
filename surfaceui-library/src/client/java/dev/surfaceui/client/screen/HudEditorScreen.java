package dev.surfaceui.client.screen;

import dev.surfaceui.api.SurfaceMode;
import dev.surfaceui.api.HudGuiLibrary;
import dev.surfaceui.api.layout.LayoutRect;
import dev.surfaceui.api.style.Paint;
import dev.surfaceui.api.style.Radius;
import dev.surfaceui.render.DrawContextSurfaceCanvas;
import dev.surfaceui.render.SurfaceCanvas;
import dev.surfaceui.runtime.ResolvedWidget;
import dev.surfaceui.runtime.SurfaceManager;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public final class HudEditorScreen extends Screen {
    private final SurfaceManager manager;
    private final InspectorPanel inspector = new InspectorPanel();

    public HudEditorScreen(SurfaceManager manager) {
        super(Text.translatable("screen.surfaceui.hud_editor"));
        this.manager = manager;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void removed() {
        HudGuiLibrary.runtime().config().save();
        manager.mode(SurfaceMode.PLAY_MODE);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        drawGrid(context);
        for (ResolvedWidget widget : manager.lastHudPlan()) {
            drawSelectionChrome(context, widget, widget == manager.input().selected());
        }
        inspector.render(context, width, height, manager.input().selected());
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        if (inspector.isResetButton(width, height, mouseX, mouseY)) {
            manager.input().resetSelected();
            return true;
        }
        return manager.input().mouseClicked(SurfaceMode.HUD_EDIT_MODE, manager.lastHudPlan(), mouseX, mouseY, click.button()) || super.mouseClicked(click, doubled);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        manager.input().mouseMoved(manager.lastHudPlan(), mouseX, mouseY);
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        return manager.input().mouseDragged(SurfaceMode.HUD_EDIT_MODE, click.x(), click.y()) || super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        return manager.input().mouseReleased(click.x(), click.y(), click.button()) || super.mouseReleased(click);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.key() == GLFW.GLFW_KEY_R) {
            manager.input().resetSelected();
            return true;
        }
        return super.keyPressed(input);
    }

    private void drawGrid(DrawContext context) {
        SurfaceCanvas canvas = new DrawContextSurfaceCanvas(context);
        Paint line = Paint.argb(0x223A5068);
        for (int x = 0; x < width; x += 16) {
            canvas.drawRect(new LayoutRect(x, 0, 1, height), line, 1.0F);
        }
        for (int y = 0; y < height; y += 16) {
            canvas.drawRect(new LayoutRect(0, y, width, 1), line, 1.0F);
        }
    }

    private void drawSelectionChrome(DrawContext context, ResolvedWidget widget, boolean selected) {
        SurfaceCanvas canvas = new DrawContextSurfaceCanvas(context);
        Paint color = Paint.argb(selected ? 0xFF44B8FF : 0x775A7892);
        int x = widget.rect().x();
        int y = widget.rect().y();
        int w = widget.rect().width();
        int h = widget.rect().height();
        canvas.drawRoundedOutline(new LayoutRect(x, y, w, h), Radius.all(4), 1, color, 1.0F);
        if (selected) {
            canvas.drawRoundedRect(new LayoutRect(x - 3, y - 3, 6, 6), Radius.all(3), color, 1.0F);
            canvas.drawRoundedRect(new LayoutRect(x + w - 3, y - 3, 6, 6), Radius.all(3), color, 1.0F);
            canvas.drawRoundedRect(new LayoutRect(x - 3, y + h - 3, 6, 6), Radius.all(3), color, 1.0F);
            canvas.drawRoundedRect(new LayoutRect(x + w - 3, y + h - 3, 6, 6), Radius.all(3), color, 1.0F);
        }
    }
}
