package com.simpulator.engine;

import com.badlogic.gdx.InputAdapter;
import com.simpulator.engine.ButtonManager.ButtonBindType;
import java.util.Set;

/** Invokes actions from keyboard inputs. */
public class KeyboardManager extends InputAdapter {

    /** Represents a keyboard event. */
    public class KeyEvent {

        /** The relevant key. */
        public final int keycode;
        /** Whether the key was pressed, held, or released. */
        public final ButtonBindType type;
        /** Time since last update, in seconds. */
        public final float deltaTime;
        /** Time since some epoch, in seconds. */
        public final float timestamp;
        /** Whther left or right shift being held down. */
        public final boolean isShiftPressed;
        /** Whther left or right control being held down. */
        public final boolean isCtrlPressed;
        /** Whther left or right alt being held down. */
        public final boolean isAltPressed;

        public KeyEvent(
            int keycode,
            ButtonBindType type,
            float deltaTime,
            float timestamp,
            Set<Integer> pressedKeys
        ) {
            this.keycode = keycode;
            this.type = type;
            this.deltaTime = deltaTime;
            this.timestamp = timestamp;
            this.isShiftPressed =
                pressedKeys.contains(com.badlogic.gdx.Input.Keys.SHIFT_LEFT) ||
                pressedKeys.contains(com.badlogic.gdx.Input.Keys.SHIFT_RIGHT);
            this.isCtrlPressed =
                pressedKeys.contains(
                    com.badlogic.gdx.Input.Keys.CONTROL_LEFT
                ) ||
                pressedKeys.contains(com.badlogic.gdx.Input.Keys.CONTROL_RIGHT);
            this.isAltPressed =
                pressedKeys.contains(com.badlogic.gdx.Input.Keys.ALT_LEFT) ||
                pressedKeys.contains(com.badlogic.gdx.Input.Keys.ALT_RIGHT);
        }
    }

    private final ButtonManager<KeyEvent> buttonManager;

    public KeyboardManager() {
        buttonManager = new ButtonManager<>();
    }

    /**
     * Initialise with the state of the keys from the previous and current frames.
     * Useful for testing.
     */
    public KeyboardManager(
        Set<Integer> lastFrameKeys,
        Set<Integer> thisFrameKeys
    ) {
        buttonManager = new ButtonManager<>(lastFrameKeys, thisFrameKeys);
    }

    @Override
    public boolean keyDown(int keycode) {
        buttonManager.setButton(keycode, true);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        buttonManager.setButton(keycode, false);
        return true;
    }

    /** Bind the given action to a key and event type. */
    public void bind(
        ButtonBindType type,
        int keycode,
        Action<KeyEvent> action
    ) {
        buttonManager.bind(type, keycode, action);
    }

    /** Unbind all instances of the given action from a key and event type. */
    public void unbind(
        ButtonBindType type,
        int keycode,
        Action<KeyEvent> action
    ) {
        buttonManager.unbind(type, keycode, action);
    }

    /** Unbind all actions from the given event type */
    public void unbindAll(ButtonBindType type) {
        buttonManager.unbindAll(type);
    }

    /** Unbind all actions from the given key and event type. */
    public void unbindAll(ButtonBindType type, int keycode) {
        buttonManager.unbindAll(type, keycode);
    }

    /**
     * Processes key state updates and invokes the appropriate actions.
     *
     * @param deltaTime The time elapsed since the last update, in seconds.
     * @param timestamp The time since some epoch, in seconds.
     */
    public void update(float deltaTime, float timestamp) {
        buttonManager.update((keycode, bindType, thisFrameKeys) ->
            new KeyEvent(keycode, bindType, deltaTime, timestamp, thisFrameKeys)
        );
    }
}
