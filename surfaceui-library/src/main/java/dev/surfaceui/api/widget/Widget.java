package dev.surfaceui.api.widget;

import dev.surfaceui.api.input.InputTraits;
import dev.surfaceui.api.layout.LayoutRect;
import dev.surfaceui.api.layout.LayoutSpec;
import dev.surfaceui.api.style.Style;
import dev.surfaceui.api.input.WidgetEvents;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public interface Widget {
    String id();

    LayoutSpec layout();

    Style style();

    InputTraits input();

    default void onClick(double mouseX, double mouseY, int button) {
    }

    default boolean mouseClicked(double mouseX, double mouseY, int button) {
        return input().clickable() && contains(mouseX, mouseY);
    }

    default boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (input().clickable() && contains(mouseX, mouseY)) {
            onClick(mouseX, mouseY, button);
            return true;
        }
        return false;
    }

    default boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    default void onHover(boolean hovered) {
    }

    default boolean onKey(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    default LayoutRect bounds() {
        return new LayoutRect(0, 0, 0, 0);
    }

    default boolean contains(double mouseX, double mouseY) {
        return bounds().contains(mouseX, mouseY);
    }

    default void bounds(LayoutRect rect) {
    }

    default List<Widget> children() {
        return List.of();
    }

    default Optional<CustomRenderer> customRenderer() {
        return Optional.empty();
    }

    default void render(DrawContext context, RenderTickCounter tickCounter, LayoutRect rect, Style resolvedStyle) {
        customRenderer().ifPresent(renderer -> renderer.render(context, tickCounter, rect, resolvedStyle));
    }
}
