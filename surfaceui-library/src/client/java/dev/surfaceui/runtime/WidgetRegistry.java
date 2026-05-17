package dev.surfaceui.runtime;

import dev.surfaceui.api.widget.Widget;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class WidgetRegistry {
    private final Map<String, Widget> widgets = new LinkedHashMap<>();

    public void index(Collection<ResolvedWidget> resolved) {
        widgets.clear();
        for (ResolvedWidget widget : resolved) {
            widgets.put(widget.widget().id(), widget.widget());
        }
    }

    public Optional<Widget> get(String id) {
        return Optional.ofNullable(widgets.get(id));
    }

    public Collection<Widget> all() {
        return List.copyOf(widgets.values());
    }
}
