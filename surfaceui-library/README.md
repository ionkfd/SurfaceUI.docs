# SurfaceUI

SurfaceUI is a Fabric client-side HUD and GUI API for Minecraft Java 1.21.11. It gives mod authors a Minecraft-native declarative widget model inspired by modern layout systems without implementing browser CSS, HTML, or a DOM.

The API separates widget definition, immutable style data, layout resolution, runtime state, user overrides, rendering, and editor behavior.

## Package Structure

```text
dev.surfaceui.api              Public registration and surface API
dev.surfaceui.api.input        Mouse/keyboard interaction contracts
dev.surfaceui.api.layout       Size, anchor, rectangle, and layout contracts
dev.surfaceui.api.style        Immutable style, theme, paint, border, shadow objects
dev.surfaceui.api.widget       Widget definitions and built-in widget factories
dev.surfaceui.client           Fabric client entrypoint and HUD hook integration
dev.surfaceui.client.screen    Screen subclasses, HUD editor, inspector panel
dev.surfaceui.config           User-edited placement and sizing persistence
dev.surfaceui.runtime          Layout/render/input pipeline internals
dev.surfaceui.example          Example third-party registrations
```

## Core API Types

SurfaceUI exposes four surface kinds:

- `HUD`: in-game HUD widgets layered through Fabric's HUD API.
- `SCREEN`: full GUI screens backed by Minecraft `Screen` subclasses.
- `OVERLAY`: transient non-blocking surfaces above HUD or screens.
- `POPUP`: focused transient UI with optional dismissal behavior.

Widgets are plain Java objects that can measure, receive a layout rectangle, render with `DrawContext`, and opt into interaction traits. Built-ins include text, box, image, bar, icon, stack, row, column, spacer, info HUD, and custom render widgets.

Styles are immutable value objects. They use SurfaceUI names such as `inset`, `gap`, `tone`, `stroke`, `corner`, and `lift` rather than copying CSS property names directly. Themes are reusable named style bundles that can be merged at registration time and overridden by user config.

Major classes:

- `SurfaceUiApi`: public registry for HUD, screen, overlay, and popup providers.
- `SurfaceProvider`: creates a widget tree for the current surface context.
- `Widget`: public widget contract; definitions stay separate from runtime layout state.
- `Widgets`: ergonomic factories for text, box, image, bar, icon, stack, row, column, spacer, info HUD, and custom render widgets.
- `Style`, `Paint`, `Border`, `Shadow`, `Insets`, `Radius`, `Theme`: immutable design-system values.
- `LayoutSpec`, `SizeSpec`, `SizeValue`, `LayoutRect`, `Anchor`: predictable Minecraft-native layout primitives.
- `InputTraits`: opt-in locked, draggable, resizable, clickable, hoverable, focusable, and keyboard behavior.
- `SurfaceManager`: client runtime that collects registered widgets and renders HUD, overlay, and popup surfaces.
- `LayoutEngine`: applies user overrides, measures, anchors, and flows widgets.
- `InputRouter`: handles HUD editor selection, drag, resize, snap, reset, and persistence.
- `SurfaceRenderer`: translates resolved widgets into `DrawContext` calls.
- `SurfaceConfigStore`: loads and saves user edits under `config/surfaceui/widgets.json`.
- `SurfaceScreen` and `HudEditorScreen`: Minecraft `Screen` subclasses for full GUI surfaces and HUD editing.

## Rendering And Layout Lifecycle

Every frame follows the same pipeline:

```text
1. collect widgets from registered surface providers
2. resolve style from defaults, theme, widget style, and runtime state
3. apply user config overrides for position, size, visibility, and lock state
4. measure and lay out the widget tree
5. route input when the current mode accepts it
6. render by z order using Minecraft DrawContext
```

Modes:

- `PLAY_MODE`: render only. Hover/click handlers may run for screens, but HUD editing is off.
- `HUD_EDIT_MODE`: HUD widgets can be selected, dragged, resized, snapped, reset, and inspected.
- `SCREEN_MODE`: screen surfaces capture mouse and keyboard through normal Minecraft `Screen` methods.

## Config And Editing

User edits are saved by `SurfaceConfigStore` as JSON under `config/surfaceui/widgets.json`. Each override is keyed by widget id and stores anchor, x/y offset, width, height, visibility, and locked state. Defaults remain in code; config only stores the user's delta.

`HudEditorScreen` provides selection outlines, drag handles, resize handles, grid snapping, an inspector panel, and a reset-to-default action. The editor modifies `WidgetOverride` records, then asks the config store to persist them.

## Fabric Integration Points

- `SurfaceUiClient` is the client entrypoint.
- HUD rendering uses Fabric's `HudElementRegistry` rather than deprecated HUD callbacks.
- Rendering uses Yarn-named Minecraft `DrawContext`.
- GUI and editor surfaces are `Screen` subclasses.
- Client-only code lives under `src/client/java`; public API and config value types live under `src/main/java`.
- Mixins are intentionally avoided. They may become useful only for optional deep integration such as replacing vanilla HUD sections, injecting custom debug overlays, or intercepting screen transitions.

## Third-Party Registration

```java
SurfaceUiApi.hud().register(Identifier.of("demo", "status_panel"), ctx -> Widgets.row()
    .id("demo:status_panel")
    .anchor(Anchor.TOP_RIGHT)
    .offset(-10, 10)
    .gap(4)
    .style(Style.create().tone(Paint.argb(0xAA101820)).corner(6).inset(6).build())
    .add(Widgets.icon(Identifier.of("demo", "textures/gui/star.png")).size(12, 12))
    .add(Widgets.text(() -> "Ready").color(0xFFE8F2FF))
);
```

## Example Surfaces

The `dev.surfaceui.example.ExampleRegistrations` class contains complete examples for:

- simple HUD widget
- draggable and resizable HUD widget
- full GUI screen
- popup overlay
- info HUD module

## Performance Notes

- Widget trees are collected into lightweight render plans each frame.
- Immutable style objects are safe to cache and share.
- Layout is single-pass for rows, columns, stacks, and anchored roots.
- Text measurement should be cached by widget id and value when used heavily.
- Rounded corners and shadows are approximated with fast `DrawContext` fills in the skeleton; a production renderer can replace `SurfaceRenderer` internals with textured nine-slices or specialized render pipelines.

## Limitations

SurfaceUI intentionally does not implement selectors, cascading CSS, HTML, browser layout, scriptable DOM mutation, or web fonts. Percent sizing is parent-relative and predictable; auto sizing depends on widget measurement. Custom fonts are represented as identifiers and resolved by a renderer hook, leaving actual font resource loading to Minecraft's resource system.
