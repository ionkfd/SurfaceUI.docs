package dev.surfaceui.runtime;

import java.util.Comparator;
import java.util.List;

public final class ZIndexSorter {
    public List<ResolvedWidget> sort(List<ResolvedWidget> widgets) {
        return widgets.stream()
                .sorted(Comparator.comparingInt(ResolvedWidget::zIndex))
                .toList();
    }
}
