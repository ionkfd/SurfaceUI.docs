package dev.surfaceui.runtime;

import dev.surfaceui.api.SurfaceKind;
import dev.surfaceui.api.SurfaceRegistration;
import dev.surfaceui.api.style.Theme;
import dev.surfaceui.api.widget.AbstractWidget;
import dev.surfaceui.api.widget.Widget;
import dev.surfaceui.config.HudGuiConfig;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public final class RenderPipeline {
    private final LayoutEngine layoutEngine = new LayoutEngine();
    private final SurfaceRenderer renderer = new SurfaceRenderer();
    private final ZIndexSorter sorter = new ZIndexSorter();

    public List<ResolvedWidget> buildPlan(HudGuiRuntime runtime, int width, int height) {
        List<Widget> roots = new ArrayList<>();
        for (SurfaceRegistration registration : runtime.surfaces().visibleHudLike()) {
            SurfaceFrameContext context = new SurfaceFrameContext(registration.kind(), runtime.mode(), width, height, runtime.activeTheme());
            Widget root = registration.provider().create(context);
            if (root instanceof AbstractWidget<?> abstractWidget) {
                abstractWidget.zIndex(registration.zIndex() + root.layout().zIndex());
            }
            roots.add(root);
        }

        HudGuiConfig config = runtime.config().config();
        SurfaceFrameContext layoutContext = new SurfaceFrameContext(SurfaceKind.HUD, runtime.mode(), width, height, runtime.activeTheme());
        return sorter.sort(layoutEngine.layout(layoutContext, roots, config));
    }

    public void render(DrawContext context, RenderTickCounter tickCounter, List<ResolvedWidget> plan) {
        renderer.render(context, tickCounter, plan);
    }
}
