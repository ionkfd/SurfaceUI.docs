package dev.surfaceui.client.screen;

import dev.surfaceui.api.layout.Align;
import dev.surfaceui.api.layout.LayoutRect;
import dev.surfaceui.api.style.Paint;
import dev.surfaceui.api.style.Radius;
import dev.surfaceui.api.style.Style;
import dev.surfaceui.render.DrawContextSurfaceCanvas;
import dev.surfaceui.render.SurfaceCanvas;
import dev.surfaceui.runtime.ResolvedWidget;
import net.minecraft.client.gui.DrawContext;

public final class InspectorPanel {
    private static final Style TITLE = Style.create().fontSize(13).color(Paint.argb(0xFFE8F2FF)).build();
    private static final Style MUTED = Style.create().fontSize(11).color(Paint.argb(0xFF9DB1C4)).build();
    private static final Style BUTTON = Style.create().fontSize(12).color(Paint.argb(0xFFE8F2FF)).align(Align.CENTER, Align.CENTER).build();

    public void render(DrawContext context, int screenWidth, int screenHeight, ResolvedWidget selected) {
        int x = screenWidth - 150;
        SurfaceCanvas canvas = new DrawContextSurfaceCanvas(context);
        canvas.drawRoundedGradientRect(new LayoutRect(x, 0, 150, screenHeight), Radius.all(0), Paint.argb(0xD2111821), Paint.argb(0xD20B1118), 1, Paint.argb(0x663A5068), 1.0F);
        canvas.drawText("SurfaceUI", x + 8, 8, TITLE);
        canvas.drawText("R: reset selected", x + 8, 24, MUTED);
        LayoutRect reset = new LayoutRect(x + 8, screenHeight - 32, 134, 22);
        canvas.drawRoundedGradientRect(reset, Radius.all(7), Paint.argb(0xCC1D2B36), Paint.argb(0xCC15202B), 1, Paint.argb(0x6644B8FF), 1.0F);
        canvas.drawTextCentered("Reset to default", reset, BUTTON);

        if (selected != null) {
            canvas.drawText(selected.widget().id(), x + 8, 48, TITLE);
            canvas.drawText("x " + selected.rect().x() + " y " + selected.rect().y(), x + 8, 64, MUTED);
            canvas.drawText("w " + selected.rect().width() + " h " + selected.rect().height(), x + 8, 78, MUTED);
        }
    }

    public boolean isResetButton(int screenWidth, int screenHeight, double mouseX, double mouseY) {
        int x = screenWidth - 150;
        return mouseX >= x + 8 && mouseX <= screenWidth - 8 && mouseY >= screenHeight - 32 && mouseY <= screenHeight - 10;
    }
}
