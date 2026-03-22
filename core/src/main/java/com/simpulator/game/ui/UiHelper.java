package com.simpulator.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.simpulator.engine.input.Action;
import com.simpulator.engine.input.ButtonManager.ButtonBindType;
import com.simpulator.engine.input.MouseManager;
import com.simpulator.engine.input.MouseManager.MouseButton;
import com.simpulator.engine.input.MouseManager.MouseButtonEvent;
import com.simpulator.engine.input.MouseManager.MouseMoveEvent;
import com.simpulator.engine.input.MouseManager.MouseScrollEvent;
import com.simpulator.engine.ui.UIRoot;

public final class UiHelper {

    private static Texture whiteTexture = null;

    public static Texture getWhiteTexture() {
        if (whiteTexture == null) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            whiteTexture = new Texture(pixmap);
            pixmap.dispose();
        }
        return whiteTexture;
    }

    public static void setupUiMouseHandlers(
        MouseManager mm,
        Viewport viewport,
        UIRoot root
    ) {
        mm.bindMove(uiMouseMoveHandler(viewport, root));
        for (ButtonBindType type : ButtonBindType.values()) {
            for (MouseButton button : MouseButton.values()) {
                mm.bindButton(
                    type,
                    button.getCode(),
                    uiMouseButtonHandler(viewport, root)
                );
            }
        }
        mm.bindScroll(uiMouseScrollHandler(viewport, root));
    }

    public static Action<MouseMoveEvent> uiMouseMoveHandler(
        Viewport viewport,
        UIRoot root
    ) {
        return e -> {
            Vector2 world = viewport.unproject(new Vector2(e.x, e.y));
            root.handleEvent(
                new MouseMoveEvent(
                    (int) world.x,
                    (int) world.y,
                    e.deltaX,
                    e.deltaY,
                    e.deltaTime,
                    e.timestamp
                )
            );
        };
    }

    public static Action<MouseButtonEvent> uiMouseButtonHandler(
        Viewport viewport,
        UIRoot root
    ) {
        return e -> {
            Vector2 world = viewport.unproject(new Vector2(e.x, e.y));
            root.handleEvent(
                new MouseButtonEvent(
                    (int) world.x,
                    (int) world.y,
                    e.button,
                    e.type,
                    e.deltaTime,
                    e.timestamp
                )
            );
        };
    }

    public static Action<MouseScrollEvent> uiMouseScrollHandler(
        Viewport viewport,
        UIRoot root
    ) {
        return e -> {
            Vector2 world = viewport.unproject(new Vector2(e.x, e.y));
            root.handleEvent(
                new MouseScrollEvent(
                    (int) world.x,
                    (int) world.y,
                    e.scrollX,
                    e.scrollY,
                    e.deltaTime,
                    e.timestamp
                )
            );
        };
    }
}
