package dev.surfaceui.example;

import dev.surfaceui.api.HudGui;
import dev.surfaceui.api.HudGuiLibrary;
import dev.surfaceui.api.input.InputTraits;
import dev.surfaceui.api.layout.Anchor;
import dev.surfaceui.api.style.Paint;
import dev.surfaceui.api.style.Shadow;
import dev.surfaceui.api.style.Style;
import dev.surfaceui.api.widget.Widgets;
import dev.surfaceui.client.screen.SurfaceScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class ExampleRegistrations {
    private ExampleRegistrations() {
    }

    public static void registerAll() {
        simpleHudWidget();
        draggableResizableHudWidget();
        popupOverlay();
        infoHudModule();
    }

    public static void simpleHudWidget() {
        HudGuiLibrary.api().registerHud(Identifier.of("example", "clock"), ctx -> Widgets.text(() -> {
                    if (MinecraftClient.getInstance().world == null) {
                        return "Day 0";
                    }
                    return "Day " + MinecraftClient.getInstance().world.getTimeOfDay() / 24000L;
                })
                .id("example:clock")
                .anchor(Anchor.TOP_LEFT)
                .offset(8, 8)
                .style(Style.create().tone(Paint.argb(0xAA101820)).text(Paint.argb(0xFFE8F2FF)).corner(4).inset(5).build()));
    }

    public static void draggableResizableHudWidget() {
        HudGuiLibrary.api().registerHud(Identifier.of("example", "resource_bar"), ctx -> Widgets.column()
                .id("example:resource_bar")
                .anchor(Anchor.TOP_RIGHT)
                .offset(-12, 12)
                .size(120, 34)
                .input(InputTraits.PASSIVE.withDraggable().withResizable())
                .style(Style.create().tone(Paint.argb(0xBB111A22)).stroke(1, Paint.argb(0x6644B8FF)).corner(6).inset(6).gap(4).build())
                .add(Widgets.text("Mana").id("example:resource_bar_label"))
                .add(Widgets.bar(() -> 0.72).id("example:resource_bar_fill").size(108, 8)));
    }

    public static SurfaceScreen fullGuiScreen() {
        return new SurfaceScreen(Text.literal("Example SurfaceUI Screen"), Widgets.column()
                .id("example:screen_root")
                .anchor(Anchor.CENTER)
                .size(220, 140)
                .style(Style.create().tone(Paint.argb(0xEE101820)).stroke(1, Paint.argb(0x6644B8FF)).corner(8).inset(10).gap(8).build())
                .add(Widgets.text("Machine Controller").id("example:screen_title").color(0xFFFFFFFF))
                .add(Widgets.row()
                        .id("example:screen_row")
                        .gap(8)
                        .add(Widgets.box().id("example:slot_a").size(32, 32).style(Style.create().tone(Paint.argb(0xFF1D2B36)).corner(4).build()))
                        .add(Widgets.box().id("example:slot_b").size(32, 32).style(Style.create().tone(Paint.argb(0xFF1D2B36)).corner(4).build())))
                .add(Widgets.bar(() -> 0.45).id("example:screen_progress").size(180, 10)));
    }

    public static void popupOverlay() {
        HudGuiLibrary.api().registerPopup(Identifier.of("example", "toast"), 100, ctx -> Widgets.row()
                .id("example:toast")
                .anchor(Anchor.BOTTOM_CENTER)
                .offset(0, -34)
                .style(Style.create().tone(Paint.argb(0xDD101820)).stroke(1, Paint.argb(0x5544B8FF)).corner(6).inset(8).gap(6).lift(new Shadow(2, 2, 0, Paint.argb(0x66000000))).build())
                .add(Widgets.icon(Identifier.of("example", "textures/gui/info.png")).id("example:toast_icon").size(12, 12))
                .add(Widgets.text("Objective updated").id("example:toast_text")));
    }

    public static void infoHudModule() {
        HudGuiLibrary.api().registerHud(Identifier.of("example", "biome_info"), ctx -> Widgets.infoHud("Biome", () -> {
                    if (MinecraftClient.getInstance().world == null || MinecraftClient.getInstance().player == null) {
                        return "Unknown";
                    }
                    return MinecraftClient.getInstance().world.getBiome(MinecraftClient.getInstance().player.getBlockPos()).getKey().map(key -> key.getValue().toString()).orElse("Unknown");
                })
                .id("example:biome_info")
                .anchor(Anchor.BOTTOM_LEFT)
                .offset(8, -8)
                .input(InputTraits.PASSIVE.withDraggable())
                .style(Style.create().tone(Paint.argb(0xAA101820)).corner(5).inset(6).gap(2).build()));
    }

    public static void registerFromAnotherMod(HudGui api) {
        api.registerOverlay(Identifier.of("othermod", "temperature_warning"), 75, ctx -> Widgets.row()
                .id("othermod:temperature_warning")
                .anchor(Anchor.TOP_CENTER)
                .offset(0, 18)
                .style(Style.create().tone(Paint.argb(0xCC2A1717)).stroke(1, Paint.argb(0x88FF6961)).corner(5).inset(6).gap(5).build())
                .add(Widgets.icon(Identifier.of("othermod", "textures/gui/heat.png")).id("othermod:temperature_icon").size(12, 12))
                .add(Widgets.text("Heat rising").id("othermod:temperature_text")));
    }
}
