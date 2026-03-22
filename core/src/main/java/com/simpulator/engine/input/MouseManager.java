package com.simpulator.engine.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.simpulator.engine.input.ButtonManager.ButtonBindType;
import java.util.ArrayList;

/** Invokes actions from mouse inputs. */
public class MouseManager extends InputAdapter {

    /** Represents a mouse movement event. */
    public static final class MouseMoveEvent {

        /** The x position of the mouse, in pixels */
        public final int x;
        /** The y position of the mouse, in pixels */
        public final int y;
        /** The change in x position since last update, in pixels */
        public final int deltaX;
        /** The change in y position since last update, in pixels */
        public final int deltaY;
        /** Time since last update, in seconds. */
        public final float deltaTime;
        /** Time since some epoch, in seconds. */
        public final float timestamp;

        public MouseMoveEvent(
            int x,
            int y,
            int deltaX,
            int deltaY,
            float deltaTime,
            float timestamp
        ) {
            this.x = x;
            this.y = y;
            this.deltaX = deltaX;
            this.deltaY = deltaY;
            this.deltaTime = deltaTime;
            this.timestamp = timestamp;
        }
    }

    /** Represents a mouse button event. */
    public static final class MouseButtonEvent {

        /** The x position of the mouse, in pixels */
        public final int x;
        /** The y position of the mouse, in pixels */
        public final int y;
        /** The relevant button. */
        public final int button;
        /** Whether the button was pressed, held, or released. */
        public final ButtonBindType type;
        /** Time since last update, in seconds. */
        public final float deltaTime;
        /** Time since some epoch, in seconds. */
        public final float timestamp;

        public MouseButtonEvent(
            int x,
            int y,
            int button,
            ButtonBindType type,
            float deltaTime,
            float timestamp
        ) {
            this.x = x;
            this.y = y;
            this.button = button;
            this.type = type;
            this.deltaTime = deltaTime;
            this.timestamp = timestamp;
        }
    }

    /** Represents a mouse scroll event. */
    public static final class MouseScrollEvent {

        /** The x position of the mouse, in pixels */
        public final int x;
        /** The y position of the mouse, in pixels */
        public final int y;
        /** The amount scrolled in the x direction. */
        public final float scrollX;
        /** The amount scrolled in the y direction. */
        public final float scrollY;
        /** Time since last update, in seconds. */
        public final float deltaTime;
        /** Time since some epoch, in seconds. */
        public final float timestamp;

        public MouseScrollEvent(
            int x,
            int y,
            float scrollX,
            float scrollY,
            float deltaTime,
            float timestamp
        ) {
            this.x = x;
            this.y = y;
            this.scrollX = scrollX;
            this.scrollY = scrollY;
            this.deltaTime = deltaTime;
            this.timestamp = timestamp;
        }
    }

    public static final class MouseButton {

        public static final int LEFT = 0;
        public static final int RIGHT = 1;
        public static final int MIDDLE = 2;
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
        resetMousePosition();
        resetMouseScroll();
    }

    public void resetMousePosition() {
        this.lastFrameX = Gdx.input.getX();
        this.lastFrameY = Gdx.input.getY();
        this.thisFrameX = this.lastFrameX;
        this.thisFrameY = this.lastFrameY;
    }

    public void resetMouseScroll() {
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
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        thisFrameX = screenX;
        thisFrameY = screenY;
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        this.scrolledX += amountX;
        this.scrolledY += amountY;
        return true;
    }

    /** Bind the given action to a button and event type. */
    public void bindButton(
        ButtonBindType type,
        int button,
        Action<MouseButtonEvent> action
    ) {
        buttonManager.bind(type, button, action);
    }

    /** Unbind all instances of the given action from a button and event type. */
    public void unbindButton(
        ButtonBindType type,
        int button,
        Action<MouseButtonEvent> action
    ) {
        buttonManager.bind(type, button, action);
    }

    /** Unbind all actions from the given button event type */
    public void unbindAllButton(ButtonBindType type) {
        buttonManager.unbindAll(type);
    }

    /** Unbind all actions from the given button and event type. */
    public void unbindAllButton(ButtonBindType type, int button) {
        buttonManager.unbindAll(type, button);
    }

    /** Bind the given action to mouse movement events. */
    public void bindMove(Action<MouseMoveEvent> action) {
        moveBindings.add(action);
    }

    /** Unbind all instances of the given action from mouse movement events. */
    public void unbindMove(Action<MouseMoveEvent> action) {
        moveBindings.remove(action);
    }

    /** Unbind all actions from mouse movement events. */
    public void unbindAllMove() {
        moveBindings.clear();
    }

    /** Bind the given action to mouse scroll events. */
    public void bindScroll(Action<MouseScrollEvent> action) {
        scrollBindings.add(action);
    }

    /** Unbind all instances of the given action from mouse scroll events. */
    public void unbindScroll(Action<MouseScrollEvent> action) {
        scrollBindings.remove(action);
    }

    /** Unbind all actions from mouse scroll events. */
    public void unbindAllScroll() {
        scrollBindings.clear();
    }

    /**
     * Processes mouse state updates and invokes the appropriate actions.
     *
     * @param deltaTime The time elapsed since the last update, in seconds.
     * @param timestamp The time since some epoch, in seconds.
     */
    public void update(float deltaTime, float timestamp) {
        buttonManager.update((button, type, thisFrameButtons) ->
            new MouseButtonEvent(
                thisFrameX,
                thisFrameY,
                button,
                type,
                deltaTime,
                timestamp
            )
        );

        for (Action<MouseMoveEvent> action : moveBindings) {
            action.act(
                new MouseMoveEvent(
                    thisFrameX,
                    thisFrameY,
                    thisFrameX - lastFrameX,
                    thisFrameY - lastFrameY,
                    deltaTime,
                    timestamp
                )
            );
        }

        for (Action<MouseScrollEvent> action : scrollBindings) {
            action.act(
                new MouseScrollEvent(
                    thisFrameX,
                    thisFrameY,
                    scrolledX,
                    scrolledY,
                    deltaTime,
                    timestamp
                )
            );
        }

        lastFrameX = thisFrameX;
        lastFrameY = thisFrameY;
        this.scrolledX = 0;
        this.scrolledY = 0;
    }
}
