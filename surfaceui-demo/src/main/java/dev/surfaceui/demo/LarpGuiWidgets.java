package dev.surfaceui.demo;

import dev.surfaceui.api.layout.Align;
import dev.surfaceui.api.layout.Anchor;
import dev.surfaceui.api.layout.LayoutRect;
import dev.surfaceui.api.style.Paint;
import dev.surfaceui.api.style.Radius;
import dev.surfaceui.api.style.Style;
import dev.surfaceui.api.widget.CustomRenderWidget;
import dev.surfaceui.api.widget.Widget;
import dev.surfaceui.api.widget.Widgets;
import dev.surfaceui.render.DrawContextSurfaceCanvas;
import dev.surfaceui.render.SurfaceCanvas;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public final class LarpGuiWidgets {
    public static int WIDTH = 680;
    public static int HEIGHT = 420;

    private static int SIDEBAR_WIDTH = 150;
    private static int MAIN_WIDTH = 360;
    private static int STATUS_WIDTH = 170;
    private static int MAIN_CONTENT = MAIN_WIDTH - 20;
    private static int STATUS_CONTENT = STATUS_WIDTH - 24;
    private static final Paint AMBER = Paint.argb(0xFFFFB52C);
    private static final Paint GREEN = Paint.argb(0xFF31F08C);
    private static final Paint BG = Paint.argb(0xFF0A0B10);
    private static final Paint SIDE_BG = Paint.argb(0xFF090A0E);
    private static final Paint MAIN_BG = Paint.argb(0xFF111219);
    private static final Paint RIGHT_BG = Paint.argb(0xFF08090D);
    private static final Paint CARD_BG = Paint.argb(0xFF101116);
    private static final Paint CARD_ON_BG = Paint.argb(0xFF121008);
    private static final Paint ROW_BG = Paint.argb(0xFF151721);
    private static final Paint LINE = Paint.argb(0xFF242630);
    private static final Paint LINE_SOFT = Paint.argb(0x77303542);
    private static final Paint TEXT = Paint.argb(0xFFF1F3F7);
    private static final Paint TEXT_DIM = Paint.argb(0xFF9AA1B5);
    private static final Paint TEXT_MUTED = Paint.argb(0xFF737B91);
    private static final int CARD_PADDING = 5;
    private static final int CARD_GAP = 3;
    private static final int CONTROL_GAP = 6;
    private static final int HEADER_HEIGHT = 20;
    private static final int SETTING_HEIGHT = 18;
    private static final int COLLAPSED_CARD_HEIGHT = CARD_PADDING * 2 + HEADER_HEIGHT;
    private static final int KEY_WIDTH = 52;
    private static final int KEY_HEIGHT = 14;
    private static final int TOGGLE_WIDTH = 24;
    private static final int TOGGLE_HEIGHT = 13;

    private static final Style FRAME = Style.create().tone(BG).border(1, LINE).radius(8).build();
    private static final Style SIDEBAR = Style.create().tone(SIDE_BG).padding(14).gap(7).build();
    private static final Style MAIN = Style.create().tone(MAIN_BG).padding(10).gap(5).build();
    private static final Style RIGHT = Style.create().tone(RIGHT_BG).padding(12).gap(8).build();
    private static final Style CARD = flatPanel(CARD_BG, LINE, 5).padding(CARD_PADDING).gap(CARD_GAP).fontSize(10).color(TEXT).build();
    private static final Style CARD_ON = flatPanel(CARD_ON_BG, Paint.argb(0x8843320E), 5).padding(CARD_PADDING).gap(CARD_GAP).fontSize(10).color(TEXT).build();
    private static final Style CARD_CLOSED = Style.create().tone(ROW_BG).border(1, Paint.argb(0xFF252837)).radius(5).padding(5).fontSize(10).color(TEXT).build();
    private static final Style H1 = Style.create().fontSize(12).color(AMBER).build();
    private static final Style LABEL = Style.create().fontSize(8).color(TEXT_DIM).build();
    private static final Style VALUE = Style.create().fontSize(8).color(TEXT).align(Align.END, Align.CENTER).build();
    private static final Style VALUE_AMBER = Style.create().fontSize(8).color(AMBER).align(Align.END, Align.CENTER).build();
    private static final Style VALUE_GREEN = Style.create().fontSize(8).color(GREEN).align(Align.END, Align.CENTER).build();
    private static final Style SLIDER = Style.create().fontSize(8).color(Paint.argb(0xFFBBC2D4)).tone(AMBER).build();
    private static final Style TAB = Style.create().fontSize(10).color(TEXT_DIM).align(Align.START, Align.CENTER).build();
    private static final Style TAB_ACTIVE = Style.create().tone(Paint.argb(0xFF12100B)).border(1, Paint.argb(0x665C4210)).radius(5).fontSize(10).color(TEXT).align(Align.START, Align.CENTER).build();

    private LarpGuiWidgets() {
    }

    public static Widget create(LarpGuiState state) {
        return create(state, WIDTH, HEIGHT);
    }

    public static Widget create(LarpGuiState state, int viewportWidth, int viewportHeight) {
        configure(viewportWidth, viewportHeight);
        return Widgets.row()
                .id("larp:root")
                .anchor(Anchor.CENTER)
                .size(WIDTH, HEIGHT)
                .style(FRAME)
                .add(sidebar(state))
                .add(main(state))
                .add(status(state));
    }

    private static void configure(int viewportWidth, int viewportHeight) {
        WIDTH = clamp(Math.round(viewportWidth * 0.72F), 560, 760);
        HEIGHT = clamp(viewportHeight - 110, 330, 430);
        if (WIDTH > viewportWidth - 40) {
            WIDTH = Math.max(480, viewportWidth - 40);
        }
        if (HEIGHT > viewportHeight - 34) {
            HEIGHT = Math.max(300, viewportHeight - 34);
        }

        SIDEBAR_WIDTH = clamp(Math.round(WIDTH * 0.22F), 124, 156);
        STATUS_WIDTH = clamp(Math.round(WIDTH * 0.25F), 146, 184);
        MAIN_WIDTH = Math.max(280, WIDTH - SIDEBAR_WIDTH - STATUS_WIDTH);
        MAIN_CONTENT = MAIN_WIDTH - 20;
        STATUS_CONTENT = STATUS_WIDTH - 24;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static Style.Builder flatPanel(Paint background, Paint border, int radius) {
        return Style.create().tone(background).gradient(background).border(1, border).radius(radius);
    }

    private static Widget sidebar(LarpGuiState state) {
        return Widgets.column()
                .id("larp:sidebar")
                .size(SIDEBAR_WIDTH, HEIGHT)
                .style(SIDEBAR)
                .add(logo())
                .add(Widgets.spacer(1, 10))
                .add(tab(state, LarpGuiState.Tab.SETTINGS))
                .add(tab(state, LarpGuiState.Tab.DUNGEON))
                .add(tab(state, LarpGuiState.Tab.SLAYER))
                .add(tab(state, LarpGuiState.Tab.MISC))
                .add(tab(state, LarpGuiState.Tab.MINING))
                .add(tab(state, LarpGuiState.Tab.PATHFINDING))
                .add(Widgets.spacer(1, sidebarBottomSpacer()))
                .add(profile());
    }

    private static int sidebarBottomSpacer() {
        int fixed = 14 * 2 + 38 + 10 + 6 * 25 + 34 + 9 * 7;
        return Math.max(10, HEIGHT - fixed);
    }

    private static Widget logo() {
        return Widgets.text("LARP")
                .id("larp:logo")
                .size(SIDEBAR_WIDTH - 28, 38)
                .style(Style.create().fontSize(16).color(AMBER).align(Align.CENTER, Align.CENTER).build());
    }

    private static Widget tab(LarpGuiState state, LarpGuiState.Tab tab) {
        return Widgets.sidebarItem(tab.icon(), tab.title(), () -> state.selectedTab() == tab, () -> state.selectedTab(tab))
                .id("larp:tab_" + tab.name().toLowerCase())
                .size(SIDEBAR_WIDTH - 28, 25)
                .style(state.selectedTab() == tab ? TAB_ACTIVE : TAB);
    }

    private static Widget profile() {
        return Widgets.row()
                .id("larp:profile")
                .size(SIDEBAR_WIDTH - 28, 34)
                .style(Style.create().gap(7).build())
                .add(imageSlot())
                .add(Widgets.column()
                        .id("larp:profile_text")
                        .size(92, 34)
                        .style(Style.create().gap(0).build())
                        .add(Widgets.text("(not connected)").size(92, 16).style(Style.create().fontSize(8).color(TEXT).align(Align.START, Align.CENTER).build()))
                        .add(Widgets.text("v1.0.0").size(92, 12).style(Style.create().fontSize(7).color(TEXT_MUTED).align(Align.START, Align.CENTER).build())));
    }

    private static Widget imageSlot() {
        return Widgets.custom((context, tick, rect, style) -> {
            SurfaceCanvas canvas = new DrawContextSurfaceCanvas(context);
            canvas.drawRoundedRect(rect, Radius.all(4), Paint.argb(0xFF101219), 1.0F);
            canvas.drawRoundedOutline(rect, Radius.all(4), 1, Paint.argb(0xFF2A2D39), 1.0F);
            canvas.drawRoundedRect(new LayoutRect(rect.x() + 8, rect.y() + 8, rect.width() - 16, rect.height() - 16), Radius.all(2), Paint.argb(0xFF252A36), 1.0F);
        }).id("larp:image_slot").size(24, 24);
    }

    private static Widget main(LarpGuiState state) {
        return Widgets.column()
                .id("larp:main")
                .size(MAIN_WIDTH, HEIGHT)
                .style(MAIN)
                .add(mainHeader(state))
                .add(search(state))
                .add(state.selectedTab() == LarpGuiState.Tab.DUNGEON ? dungeonPage(state) : emptyPage(state));
    }

    private static Widget mainHeader(LarpGuiState state) {
        return Widgets.row()
                .id("larp:main_header")
                .size(MAIN_CONTENT, 18)
                .style(Style.create().gap(0).build())
                .add(Widgets.text("MODULES  /").size(62, 16).style(Style.create().fontSize(9).color(TEXT_MUTED).build()))
                .add(Widgets.text(() -> " " + state.selectedTab().title().toUpperCase()).size(Math.max(100, MAIN_CONTENT - 158), 16).style(Style.create().fontSize(9).color(AMBER).build()))
                .add(Widgets.text(() -> state.activeModuleCount() + " / 12 enabled").size(96, 16).style(VALUE_AMBER));
    }

    private static Widget search(LarpGuiState state) {
        return Widgets.custom((context, tick, rect, style) -> {
            SurfaceCanvas canvas = new DrawContextSurfaceCanvas(context);
            Paint border = state.searchFocused() ? Paint.argb(0xAAFFB52C) : Paint.argb(0xFF20222C);
            canvas.drawRoundedRect(rect, Radius.all(5), Paint.argb(0xFF0B0C11), 1.0F);
            canvas.drawRoundedOutline(rect, Radius.all(5), 1, border, 1.0F);
            Style icon = Style.create().fontSize(9).color(TEXT_MUTED).align(Align.CENTER, Align.CENTER).build();
            Style text = Style.create().fontSize(9).color(state.searchText().isEmpty() && !state.searchFocused() ? TEXT_MUTED : TEXT).build();
            canvas.drawTextCentered("o", new LayoutRect(rect.x() + 8, rect.y() + 5, 12, 12), icon);
            canvas.drawText(state.searchDisplayText(), rect.x() + 28, rect.y() + 6, text);
            LayoutRect keyRect = new LayoutRect(rect.x() + rect.width() - 56, rect.y() + 5, 44, 14);
            canvas.drawRoundedRect(keyRect, Radius.all(3), Paint.argb(0xFF15120A), 1.0F);
            canvas.drawRoundedOutline(keyRect, Radius.all(3), 1, Paint.argb(0x996B4809), 1.0F);
            canvas.drawTextCentered("CTRL+F", keyRect, Style.create().fontSize(8).color(AMBER).align(Align.CENTER, Align.CENTER).build());
        })
                .id("larp:search")
                .size(MAIN_CONTENT, 24)
                .clickable()
                .onClick((mouseX, mouseY, button) -> {
                    if (button == 0) {
                        state.focusSearch();
                    }
                });
    }

    private static Widget dungeonPage(LarpGuiState state) {
        var page = Widgets.stack()
                .id("larp:dungeon_page")
                .size(MAIN_CONTENT, pageHeight())
                .style(Style.create().build());
        List<LarpGuiState.Module> modules = state.visibleDungeonModules();
        if (modules.isEmpty()) {
            page.add(Widgets.text("No modules found").id("larp:no_results").size(MAIN_CONTENT, 24)
                    .style(Style.create().fontSize(9).color(TEXT_MUTED).align(Align.CENTER, Align.CENTER).build()));
            return page;
        }
        int totalHeight = totalModuleHeight(modules);
        int maxScroll = Math.max(0, totalHeight - pageHeight());
        state.clampDungeonScroll(maxScroll);
        int scrollOffset = state.dungeonScrollOffset();
        boolean overflow = maxScroll > 0;
        var list = Widgets.column()
                .id("larp:dungeon_list")
                .offset(0, -scrollOffset)
                .size(moduleListWidth(), totalHeight)
                .style(Style.create().gap(5).build());
        for (LarpGuiState.Module module : modules) {
            list.add(moduleCard("larp:" + module.id, state, module));
        }
        page.add(list);
        if (overflow) {
            page.add(scrollbar(scrollOffset, maxScroll));
        }
        return page;
    }

    private static Widget emptyPage(LarpGuiState state) {
        return Widgets.panel()
                .id("larp:empty_" + state.selectedTab().name().toLowerCase())
                .size(MAIN_CONTENT, pageHeight())
                .style(flatPanel(Paint.argb(0xFF0F1016), LINE, 5).padding(12).gap(5).build())
                .add(Widgets.text(state.selectedTab().title()).size(220, 18).style(H1))
                .add(Widgets.text("This tab is wired and ready for modules.").size(260, 14).style(LABEL));
    }

    private static Widget moduleCard(String id, LarpGuiState state, LarpGuiState.Module module) {
        double progress = module.expansionProgress();
        int height = moduleCardHeight(module);
        boolean showSettings = progress > 0.04;
        return Widgets.panel()
                .id(id)
                .size(moduleListWidth(), height)
                .style(module.enabled ? CARD_ON : CARD)
                .clickable()
                .onClick((mouseX, mouseY, button) -> {
                    if (button == 0) {
                        state.toggleEnabled(module);
                    } else if (button == 1) {
                        state.toggleExpanded(module);
                    }
                })
                .add(moduleHeader(id, state, module))
                .add(showSettings ? settings(id, state, module) : Widgets.spacer(1, 0));
    }

    private static int pageHeight() {
        return Math.max(250, HEIGHT - 72);
    }

    private static int moduleCardHeight(LarpGuiState.Module module) {
        int rows = module.sliders.size() + module.toggles.size();
        double progress = module.expansionProgress();
        int expandedHeight = COLLAPSED_CARD_HEIGHT + CARD_GAP + rows * SETTING_HEIGHT;
        return COLLAPSED_CARD_HEIGHT + (int) Math.round((expandedHeight - COLLAPSED_CARD_HEIGHT) * progress);
    }

    private static int totalModuleHeight(List<LarpGuiState.Module> modules) {
        int total = 0;
        for (int i = 0; i < modules.size(); i++) {
            total += moduleCardHeight(modules.get(i));
            if (i < modules.size() - 1) {
                total += 5;
            }
        }
        return total;
    }

    private static Widget scrollbar(int scrollOffset, int maxScroll) {
        return Widgets.custom((context, tick, rect, style) -> {
            SurfaceCanvas canvas = new DrawContextSurfaceCanvas(context);
            canvas.drawRoundedRect(rect, Radius.all(2), Paint.argb(0xFF171A22), 1.0F);
            int thumbHeight = Math.max(32, rect.height() - maxScroll);
            thumbHeight = Math.min(rect.height(), thumbHeight);
            int maxTravel = Math.max(1, rect.height() - thumbHeight);
            int thumbY = rect.y() + (int) Math.round(maxTravel * (scrollOffset / (double) Math.max(1, maxScroll)));
            canvas.drawRoundedRect(new LayoutRect(rect.x(), thumbY, rect.width(), thumbHeight), Radius.all(2), AMBER, 1.0F);
        }).id("larp:dungeon_scrollbar").offset(MAIN_CONTENT - 4, 0).size(3, pageHeight());
    }

    private static Widget moduleHeader(String id, LarpGuiState state, LarpGuiState.Module module) {
        var row = Widgets.row()
                .id(id + ":header")
                .size(cardInnerWidth(), HEADER_HEIGHT)
                .style(Style.create().gap(CONTROL_GAP).build());
        if (module.enabled) {
            row.add(accentRail(id + ":rail", () -> true));
        }
        row.add(Widgets.text(module.name).size(moduleNameWidth(module.enabled), HEADER_HEIGHT).style(Style.create().fontSize(9).color(TEXT).align(Align.START, Align.CENTER).build()));
        row.add(keyBind(id + ":bind_" + module.id, state, module, KEY_WIDTH));
        row.add(toggle(id + ":toggle", () -> module.enabled, value -> state.setEnabled(module, value)));
        return row;
    }

    private static Widget settings(String id, LarpGuiState state, LarpGuiState.Module module) {
        var column = Widgets.column()
                .id(id + ":settings")
                .size(cardInnerWidth(), Math.max(SETTING_HEIGHT, (module.sliders.size() + module.toggles.size()) * SETTING_HEIGHT))
                .style(Style.create().gap(0).build());
        for (LarpGuiState.SliderSetting setting : module.sliders) {
            column.add(Widgets.slider(setting.label, () -> setting.value, value -> state.setSlider(module, setting, value), setting.min, setting.max, setting::formatted)
                    .id(id + ":slider_" + setting.label.toLowerCase().replace(" ", "_"))
                    .size(cardInnerWidth(), SETTING_HEIGHT)
                    .style(SLIDER));
        }
        for (LarpGuiState.ToggleSetting setting : module.toggles) {
            column.add(toggleRow(id, state, module, setting));
        }
        return column;
    }

    private static Widget toggleRow(String id, LarpGuiState state, LarpGuiState.Module module, LarpGuiState.ToggleSetting setting) {
        return Widgets.row()
                .id(id + ":toggle_row_" + setting.label.toLowerCase().replace(" ", "_"))
                .size(cardInnerWidth(), SETTING_HEIGHT)
                .style(Style.create().gap(CONTROL_GAP).build())
                .add(Widgets.text(setting.label).size(settingLabelWidth(), SETTING_HEIGHT).style(Style.create().fontSize(8).color(Paint.argb(0xFFC5CBD8)).align(Align.START, Align.CENTER).build()))
                .add(toggle(id + ":setting_" + setting.label.toLowerCase().replace(" ", "_"), () -> setting.value, value -> state.setToggle(module, setting, value)));
    }

    private static int cardInnerWidth() {
        return moduleListWidth() - CARD_PADDING * 2;
    }

    private static int moduleListWidth() {
        return MAIN_CONTENT - 8;
    }

    private static int moduleNameWidth(boolean hasRail) {
        int railWidth = hasRail ? 3 : 0;
        int gaps = hasRail ? CONTROL_GAP * 3 : CONTROL_GAP * 2;
        return Math.max(120, cardInnerWidth() - railWidth - KEY_WIDTH - TOGGLE_WIDTH - gaps);
    }

    private static int settingLabelWidth() {
        return Math.max(90, cardInnerWidth() - TOGGLE_WIDTH - CONTROL_GAP);
    }

    private static Widget status(LarpGuiState state) {
        var status = Widgets.column()
                .id("larp:status")
                .size(STATUS_WIDTH, HEIGHT)
                .style(RIGHT)
                .add(sectionTitle("SESSION STATS"))
                .add(statLine("Session Time", state::sessionTime, VALUE_AMBER))
                .add(statLine("Modules Active", () -> Integer.toString(state.activeModuleCount()), VALUE))
                .add(statLine("Server", state::serverName, VALUE_GREEN))
                .add(statLine("FPS", () -> Integer.toString(state.fps()), VALUE_GREEN))
                .add(statLine("Ping", state::pingText, VALUE_GREEN))
                .add(separator("stats"))
                .add(sectionTitle("ACTIVE MODULES"));
        List<LarpGuiState.Module> active = state.activeModules();
        for (int i = 0; i < Math.min(3, active.size()); i++) {
            status.add(activeModuleLine(active.get(i), i));
        }
        status.add(separator("modules"))
                .add(sectionTitle("PERFORMANCE"))
                .add(metric("CPU", state::cpuLoad, state::cpuText))
                .add(metric("RAM", state::ramLoad, state::ramText))
                .add(metric("NET", state::networkLoad, state::networkText));
        return status;
    }

    private static Widget sectionTitle(String title) {
        return Widgets.row()
                .id("larp:section_" + title.toLowerCase().replace(" ", "_"))
                .size(STATUS_CONTENT, 14)
                .style(Style.create().gap(6).build())
                .add(Widgets.text(title).size(sectionTitleWidth(), 12).style(Style.create().fontSize(8).color(AMBER).build()))
                .add(rule("larp:rule_" + title.toLowerCase().replace(" ", "_"), Math.max(0, STATUS_CONTENT - sectionTitleWidth() - 6), 10));
    }

    private static Widget statLine(String label, Supplier<String> value, Style valueStyle) {
        return Widgets.row()
                .id("larp:stat_" + label.toLowerCase().replace(" ", "_"))
                .size(STATUS_CONTENT, 16)
                .style(Style.create().gap(0).build())
                .add(Widgets.text(label).size(statLabelWidth(), 13).style(LABEL))
                .add(Widgets.text(value).size(STATUS_CONTENT - statLabelWidth(), 13).style(valueStyle));
    }

    private static Widget activeModuleLine(LarpGuiState.Module module, int index) {
        return Widgets.row()
                .id("larp:active_" + index)
                .size(STATUS_CONTENT, 22)
                .style(Style.create().tone(Paint.argb(0xFF0C0D12)).border(1, Paint.argb(0x5520222C)).radius(4).padding(4).gap(5).build())
                .add(dot("larp:dot_" + index))
                .add(Widgets.text(shortModuleName(module.name)).size(Math.max(72, STATUS_CONTENT - 24), 14).style(Style.create().fontSize(8).color(TEXT).align(Align.START, Align.CENTER).build()));
    }

    private static int sectionTitleWidth() {
        return Math.min(110, Math.max(86, STATUS_CONTENT - 34));
    }

    private static int statLabelWidth() {
        return Math.min(80, Math.max(64, STATUS_CONTENT / 2));
    }

    private static String shortModuleName(String name) {
        return name.length() <= 14 ? name : name.substring(0, 13) + ".";
    }

    private static Widget metric(String label, DoubleSupplier value, Supplier<String> text) {
        return Widgets.custom((context, tick, rect, style) -> {
            SurfaceCanvas canvas = new DrawContextSurfaceCanvas(context);
            Style labelStyle = LABEL;
            Style valueStyle = Style.create().fontSize(8).color(TEXT_DIM).align(Align.END, Align.CENTER).build();
            canvas.drawText(label, rect.x(), rect.y() + 1, labelStyle);
            LayoutRect track = new LayoutRect(rect.x() + 30, rect.y() + 7, 78, 3);
            canvas.drawRoundedRect(track, Radius.all(2), Paint.argb(0xFF242733), 1.0F);
            int fill = (int) Math.round(track.width() * Math.max(0.0, Math.min(1.0, value.getAsDouble())));
            if (fill > 0) {
                canvas.drawRoundedRect(new LayoutRect(track.x(), track.y(), fill, track.height()), Radius.all(2), AMBER, 1.0F);
            }
            canvas.drawText(text.get(), rect.x() + 112, rect.y() + 1, valueStyle);
        }).id("larp:metric_" + label.toLowerCase()).size(STATUS_CONTENT, 17);
    }

    private static Widget keyTag(String label, int width) {
        return Widgets.custom((context, tick, rect, style) -> {
            SurfaceCanvas canvas = new DrawContextSurfaceCanvas(context);
            Style text = Style.create().fontSize(8).color(AMBER).align(Align.CENTER, Align.CENTER).build();
            canvas.drawRoundedRect(rect, Radius.all(3), Paint.argb(0xFF15120A), 1.0F);
            canvas.drawRoundedOutline(rect, Radius.all(3), 1, Paint.argb(0x996B4809), 1.0F);
            canvas.drawTextCentered(label, rect, text);
        }).id("larp:key_" + label.toLowerCase().replace("+", "_").replace(" ", "_")).size(width, KEY_HEIGHT);
    }

    private static Widget keyBind(String id, LarpGuiState state, LarpGuiState.Module module, int width) {
        return Widgets.custom((context, tick, rect, style) -> {
            SurfaceCanvas canvas = new DrawContextSurfaceCanvas(context);
            boolean listening = state.listeningFor(module);
            Style text = Style.create().fontSize(7).color(listening ? TEXT : AMBER).align(Align.CENTER, Align.CENTER).build();
            canvas.drawRoundedRect(rect, Radius.all(3), listening ? Paint.argb(0xFF6A4307) : Paint.argb(0xFF15120A), 1.0F);
            canvas.drawRoundedOutline(rect, Radius.all(3), 1, listening ? AMBER : Paint.argb(0x996B4809), 1.0F);
            canvas.drawTextCentered(listening ? "PRESS..." : module.keyName(), rect, text);
        }).id(id).size(width, KEY_HEIGHT).clickable().onClick((mouseX, mouseY, button) -> {
            if (button == 0) {
                state.beginBinding(module);
            }
        });
    }

    private static Widget toggle(String id, BooleanSupplier value, Consumer<Boolean> setter) {
        return Widgets.custom((context, tick, rect, style) -> {
            SurfaceCanvas canvas = new DrawContextSurfaceCanvas(context);
            boolean on = value.getAsBoolean();
            Paint track = on ? Paint.argb(0xFFD99A28) : Paint.argb(0xFF262A36);
            Paint knob = on ? Paint.argb(0xFFFFE5A8) : Paint.argb(0xFF6A7182);
            canvas.drawRoundedRect(rect, Radius.all(rect.height() / 2), track, 1.0F);
            canvas.drawRoundedOutline(rect, Radius.all(rect.height() / 2), 1, Paint.argb(on ? 0x99FFC96A : 0x66404957), 1.0F);
            int knobX = on ? rect.x() + rect.width() - rect.height() + 2 : rect.x() + 2;
            canvas.drawRoundedRect(new LayoutRect(knobX, rect.y() + 2, rect.height() - 4, rect.height() - 4), Radius.all((rect.height() - 4) / 2), knob, 1.0F);
        }).id(id).size(TOGGLE_WIDTH, TOGGLE_HEIGHT).clickable().onClick((mouseX, mouseY, button) -> {
            if (button == 0) {
                setter.accept(!value.getAsBoolean());
            }
        });
    }

    private static Widget accentRail(String id, BooleanSupplier visible) {
        return Widgets.custom((context, tick, rect, style) -> {
            if (visible.getAsBoolean()) {
                new DrawContextSurfaceCanvas(context).drawRoundedRect(rect, Radius.all(2), AMBER, 1.0F);
            }
        }).id(id).size(3, HEADER_HEIGHT);
    }

    private static Widget dot(String id) {
        return Widgets.custom((context, tick, rect, style) -> new DrawContextSurfaceCanvas(context)
                .drawRoundedRect(new LayoutRect(rect.x() + 3, rect.y() + 4, 5, 5), Radius.all(3), AMBER, 1.0F))
                .id(id)
                .size(11, 13);
    }

    private static CustomRenderWidget rule(String id, int width, int height) {
        return Widgets.custom((context, tick, rect, style) -> new DrawContextSurfaceCanvas(context)
                        .drawRect(new LayoutRect(rect.x(), rect.y() + rect.height() / 2, rect.width(), 1), LINE_SOFT, 1.0F))
                .id(id)
                .size(width, height);
    }

    private static Widget separator(String id) {
        return rule("larp:separator_" + id, STATUS_CONTENT, 8);
    }
}
