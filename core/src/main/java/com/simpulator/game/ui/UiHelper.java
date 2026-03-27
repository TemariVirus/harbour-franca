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
import com.simpulator.engine.scene.SoundManager;
import com.simpulator.engine.ui.UIListener;
import com.simpulator.engine.ui.UIRoot;
import com.simpulator.game.ui.UIRelativeLayout.Alignment;

public final class UiHelper {

    private UiHelper() {}

    public static class MouseHandlers {

        private final Action<MouseMoveEvent> moveHandler;
        private final Action<MouseButtonEvent> buttonHandler;
        private final Action<MouseScrollEvent> scrollHandler;

        private MouseHandlers(
            Action<MouseMoveEvent> moveHandler,
            Action<MouseButtonEvent> buttonHandler,
            Action<MouseScrollEvent> scrollHandler
        ) {
            this.moveHandler = moveHandler;
            this.buttonHandler = buttonHandler;
            this.scrollHandler = scrollHandler;
        }
    }

    public static Texture getWhiteTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture whiteTexture = new Texture(pixmap);
        pixmap.dispose();
        return whiteTexture;
    }

    public static MouseHandlers setupUiMouseHandlers(
        MouseManager mm,
        Viewport viewport,
        UIRoot root
    ) {
        Action<MouseMoveEvent> moveHandler = uiMouseMoveHandler(viewport, root);
        Action<MouseButtonEvent> buttonHandler = uiMouseButtonHandler(
            viewport,
            root
        );
        Action<MouseScrollEvent> scrollHandler = uiMouseScrollHandler(
            viewport,
            root
        );

        mm.bindMove(moveHandler);
        for (ButtonBindType type : ButtonBindType.values()) {
            for (MouseButton button : MouseButton.values()) {
                mm.bindButton(type, button.code(), buttonHandler);
            }
        }
        mm.bindScroll(scrollHandler);

        return new MouseHandlers(moveHandler, buttonHandler, scrollHandler);
    }

    public static void removeUiMouseHandlers(
        MouseManager mm,
        MouseHandlers handlers
    ) {
        mm.unbindMove(handlers.moveHandler);
        for (ButtonBindType type : ButtonBindType.values()) {
            for (MouseButton button : MouseButton.values()) {
                mm.unbindButton(type, button.code(), handlers.buttonHandler);
            }
        }
        mm.unbindScroll(handlers.scrollHandler);
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

    /**
     * Adds a listen that changes the fill color when the mouse hovers over the box.
     */
    public static Box addHoverColor(Box box, Color hoverColor) {
        Color normalColor = box.getFillColor();
        box.addListener(MouseMoveEvent.class, e -> {
            box.setFillColor(
                box.getBounds().contains(e.x, e.y) ? hoverColor : normalColor
            );
            return false;
        });
        return box;
    }

    public static Box createButton(
        Box box,
        Color hoverColor,
        Text text,
        float fontSize,
        SoundManager sounds,
        UIListener<MouseButtonEvent> onClick
    ) {
        text.setLayout(
            new UIRelativeLayout.Builder()
                .yAlignment(Alignment.CENTER)
                .height(fontSize)
                .getLayout()
        );
        box.addChild(text);
        addHoverColor(box, hoverColor);
        box.addListener(MouseButtonEvent.class, e -> {
            if (
                e.type != ButtonBindType.DOWN ||
                e.button != MouseButton.LEFT.code() ||
                !box.getBounds().contains(e.x, e.y)
            ) {
                return false;
            }
            sounds.play("sfx/click.ogg");
            return onClick.handle(e);
        });
        return box;
    }
}
