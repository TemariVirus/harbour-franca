package com.simpulator.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.simpulator.engine.ButtonManager.ButtonBindType;
import java.util.ArrayList;

public class MouseManager extends InputAdapter {

    public class MouseMoveEvent {

        public final int x, y;
        public final int deltaX, deltaY;
        public final float timestamp;

        public MouseMoveEvent(
            int x,
            int y,
            int deltaX,
            int deltaY,
            float timestamp
        ) {
            this.x = x;
            this.y = y;
            this.deltaX = deltaX;
            this.deltaY = deltaY;
            this.timestamp = timestamp;
        }
    }

    public class MouseButtonEvent {

        public final int x, y;
        public final int pointer;
        public final int button;
        public final ButtonBindType type;
        public final float timestamp;

        public MouseButtonEvent(
            int x,
            int y,
            int pointer,
            int button,
            ButtonBindType type,
            float timestamp
        ) {
            this.x = x;
            this.y = y;
            this.pointer = pointer;
            this.button = button;
            this.type = type;
            this.timestamp = timestamp;
        }
    }

    public class MouseScrollEvent {

        public final float scrollX, scrollY;
        public final float timestamp;

        public MouseScrollEvent(float scrollX, float scrollY, float timestamp) {
            this.scrollX = scrollX;
            this.scrollY = scrollY;
            this.timestamp = timestamp;
        }
    }

    private int lastFrameX, lastFrameY;
    private int thisFrameX, thisFrameY;
    private float scrolledX, scrolledY;

    private final ButtonManager<MouseButtonEvent> buttonManager =
        new ButtonManager<>();

    private final ArrayList<Action<MouseMoveEvent>> moveBindings =
        new ArrayList<>();
    private final ArrayList<Action<MouseScrollEvent>> scrollBindings =
        new ArrayList<>();

    public MouseManager() {
        this.lastFrameX = Gdx.input.getX();
        this.lastFrameY = Gdx.input.getY();
        this.thisFrameX = this.lastFrameX;
        this.thisFrameY = this.lastFrameY;
        this.scrolledX = 0;
        this.scrolledY = 0;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        thisFrameX = screenX;
        thisFrameY = screenY;
        return true;
    }

    @Override
    public boolean touchDown(
        int screenX,
        int screenY,
        int pointer,
        int button
    ) {
        buttonManager.setButton(button, true);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        buttonManager.setButton(button, false);
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        this.scrolledX += amountX;
        this.scrolledY += amountY;
        return true;
    }

    public void bindButton(
        ButtonBindType type,
        int button,
        Action<MouseButtonEvent> action
    ) {
        buttonManager.bind(type, button, action);
    }

    public void unbindButton(
        ButtonBindType type,
        int button,
        Action<MouseButtonEvent> action
    ) {
        buttonManager.bind(type, button, action);
    }

    public void unbindAllButton(ButtonBindType type) {
        buttonManager.unbindAll(type);
    }

    public void unbindAllButton(ButtonBindType type, int button) {
        buttonManager.unbindAll(type, button);
    }

    public void bindMove(Action<MouseMoveEvent> action) {
        moveBindings.add(action);
    }

    public void unbindMove(Action<MouseMoveEvent> action) {
        moveBindings.remove(action);
    }

    public void unbindAllMove() {
        moveBindings.clear();
    }

    public void bindScroll(Action<MouseScrollEvent> action) {
        scrollBindings.add(action);
    }

    public void unbindScroll(Action<MouseScrollEvent> action) {
        scrollBindings.remove(action);
    }

    public void unbindAllScroll() {
        scrollBindings.clear();
    }

    public void update(float deltaTime, float timestamp) {
        buttonManager.update(deltaTime, (button, type, thisFrameButtons) ->
            new MouseButtonEvent(
                thisFrameX,
                thisFrameY,
                0,
                button,
                type,
                timestamp
            )
        );

        for (Action<MouseMoveEvent> action : moveBindings) {
            action.act(
                deltaTime,
                new MouseMoveEvent(
                    thisFrameX,
                    thisFrameY,
                    thisFrameX - lastFrameX,
                    thisFrameY - lastFrameY,
                    timestamp
                )
            );
        }

        for (Action<MouseScrollEvent> action : scrollBindings) {
            action.act(
                deltaTime,
                new MouseScrollEvent(scrolledX, scrolledY, timestamp)
            );
        }

        lastFrameX = thisFrameX;
        lastFrameY = thisFrameY;
        this.scrolledX = 0;
        this.scrolledY = 0;
    }
}
