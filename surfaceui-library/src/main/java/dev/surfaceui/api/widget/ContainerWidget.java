package dev.surfaceui.api.widget;

import dev.surfaceui.api.layout.Axis;
import dev.surfaceui.api.style.Style;
import java.util.ArrayList;
import java.util.List;

public class ContainerWidget extends AbstractWidget<ContainerWidget> {
    private final Axis axis;
    private final boolean layered;
    private final List<Widget> children = new ArrayList<>();

    public ContainerWidget(Axis axis, boolean layered) {
        this.axis = axis;
        this.layered = layered;
    }

    public Axis axis() {
        return axis;
    }

    public boolean layered() {
        return layered;
    }

    public ContainerWidget add(Widget widget) {
        children.add(widget);
        return this;
    }

    public ContainerWidget gap(int value) {
        this.style = Style.create().gap(value).build();
        return this;
    }

    @Override
    public List<Widget> children() {
        return List.copyOf(children);
    }
}
