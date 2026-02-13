package com.simpulator.engine;

import com.badlogic.gdx.InputAdapter;
import com.simpulator.engine.ButtonManager.ButtonBindType;
import java.util.Set;

public class KeyboardManager extends InputAdapter {

    public class KeyEvent {

        public final int keycode;
        public final ButtonBindType type;
        public final float deltaTime;
        public final float timestamp;
        public final boolean isShiftPressed;
        public final boolean isCtrlPressed;
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

    public void bind(
        ButtonBindType type,
        int keycode,
        Action<KeyEvent> action
    ) {
        buttonManager.bind(type, keycode, action);
    }

    public void unbind(
        ButtonBindType type,
        int keycode,
        Action<KeyEvent> action
    ) {
        buttonManager.unbind(type, keycode, action);
    }

    public void unbindAll(ButtonBindType type) {
        buttonManager.unbindAll(type);
    }

    public void unbindAll(ButtonBindType type, int keycode) {
        buttonManager.unbindAll(type, keycode);
    }

    public void update(float deltaTime, float timestamp) {
        buttonManager.update((keycode, bindType, thisFrameKeys) ->
            new KeyEvent(keycode, bindType, deltaTime, timestamp, thisFrameKeys)
        );
    }
}
