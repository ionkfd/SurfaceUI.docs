package dev.surfaceui.runtime;

import dev.surfaceui.api.style.Style;
import dev.surfaceui.api.style.Theme;
import dev.surfaceui.api.style.ThemeTokens;
import dev.surfaceui.api.widget.BoxWidget;
import dev.surfaceui.api.widget.ButtonWidget;
import dev.surfaceui.api.widget.CheckboxWidget;
import dev.surfaceui.api.widget.ContainerWidget;
import dev.surfaceui.api.widget.DropdownWidget;
import dev.surfaceui.api.widget.PanelWidget;
import dev.surfaceui.api.widget.ProgressWidget;
import dev.surfaceui.api.widget.SelectButtonWidget;
import dev.surfaceui.api.widget.SidebarItemWidget;
import dev.surfaceui.api.widget.SliderWidget;
import dev.surfaceui.api.widget.TextWidget;
import dev.surfaceui.api.widget.Widget;
import dev.surfaceui.api.widget.WindowWidget;

public final class StyleResolver {
    public Style resolve(Theme theme, Widget widget) {
        Style base = switch (widget) {
            case TextWidget ignored -> theme.get(ThemeTokens.TEXT).orElse(Style.create().build());
            case ButtonWidget button -> theme.get(button.active() ? ThemeTokens.BUTTON_ACTIVE : ThemeTokens.BUTTON).orElse(Style.create().build());
            case CheckboxWidget ignored -> theme.get(ThemeTokens.CHECKBOX).orElse(Style.create().build());
            case SliderWidget ignored -> theme.get(ThemeTokens.SLIDER).orElse(Style.create().build());
            case DropdownWidget ignored -> theme.get(ThemeTokens.SELECT).orElse(Style.create().build());
            case SelectButtonWidget ignored -> theme.get(ThemeTokens.SELECT).orElse(Style.create().build());
            case SidebarItemWidget sidebar -> theme.get(sidebar.active() ? ThemeTokens.SIDEBAR_ITEM_ACTIVE : ThemeTokens.SIDEBAR_ITEM).orElse(Style.create().build());
            case WindowWidget ignored -> theme.get(ThemeTokens.WINDOW).orElse(Style.create().build());
            case PanelWidget ignored -> theme.get(ThemeTokens.PANEL).orElse(Style.create().build());
            case ContainerWidget ignored -> Style.create().build();
            case BoxWidget ignored -> theme.get(ThemeTokens.PANEL).orElse(Style.create().build());
            case ProgressWidget ignored -> theme.get(ThemeTokens.PANEL).orElse(Style.create().build());
            default -> Style.create().build();
        };
        return base.merge(widget.style());
    }
}
