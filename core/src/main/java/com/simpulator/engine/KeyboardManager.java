package com.simpulator.engine;

import com.badlogic.gdx.InputAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class KeyboardManager extends InputAdapter {

    public enum BindType {
        DOWN,
        HOLD,
        UP,
    }

    public class KeyEvent {

        public final int keycode;
        public final BindType type;
        public final float timestamp;
        public final boolean isShiftPressed;
        public final boolean isCtrlPressed;
        public final boolean isAltPressed;

        public KeyEvent(
            int keycode,
            BindType type,
            float timestamp,
            HashSet<Integer> pressedKeys
        ) {
            this.keycode = keycode;
            this.type = type;
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

    private HashSet<Integer> lastFrameKeys = new HashSet<>();
    private HashSet<Integer> thisFrameKeys = new HashSet<>();

    private final HashMap<Integer, ArrayList<Action<KeyEvent>>> downBindings =
        new HashMap<>();
    private final HashMap<Integer, ArrayList<Action<KeyEvent>>> holdBindings =
        new HashMap<>();
    private final HashMap<Integer, ArrayList<Action<KeyEvent>>> upBindings =
        new HashMap<>();

    public KeyboardManager() {}

    public KeyboardManager(
        HashSet<Integer> lastFrameKeys,
        HashSet<Integer> thisFrameKeys
    ) {
        this.lastFrameKeys.addAll(lastFrameKeys);
        this.thisFrameKeys.addAll(thisFrameKeys);
    }

    @Override
    public boolean keyDown(int keycode) {
        thisFrameKeys.add(keycode);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        thisFrameKeys.remove(keycode);
        return true;
    }

    private HashMap<Integer, ArrayList<Action<KeyEvent>>> getBindings(
        BindType type
    ) {
        switch (type) {
            case DOWN:
                return downBindings;
            case HOLD:
                return holdBindings;
            case UP:
                return upBindings;
            default:
                throw new IllegalArgumentException("Unknown BindType: " + type);
        }
    }

    public void bind(BindType type, int keycode, Action<KeyEvent> action) {
        getBindings(type).putIfAbsent(keycode, new ArrayList<>());
        getBindings(type).get(keycode).add(action);
    }

    public void unbind(BindType type, int keycode, Action<KeyEvent> action) {
        if (getBindings(type).containsKey(keycode)) {
            // Compare by reference instead of using equals()
            getBindings(type)
                .get(keycode)
                .removeIf(a -> a == action);
        }
    }

    public void unbindAll(BindType type) {
        getBindings(type).clear();
    }

    private HashSet<Integer> computeKeys(BindType type) {
        HashSet<Integer> keys = new HashSet<>();
        switch (type) {
            case DOWN:
                keys.addAll(thisFrameKeys);
                keys.removeAll(lastFrameKeys);
                break;
            case HOLD:
                keys.addAll(thisFrameKeys);
                break;
            case UP:
                keys.addAll(lastFrameKeys);
                keys.removeAll(thisFrameKeys);
                break;
            default:
                throw new IllegalArgumentException("Unknown BindType: " + type);
        }
        return keys;
    }

    public void update(float deltaTime, float timestamp) {
        for (BindType type : BindType.values()) {
            HashSet<Integer> keys = computeKeys(type);
            HashMap<Integer, ArrayList<Action<KeyEvent>>> bindings =
                getBindings(type);
            for (int key : keys) {
                if (!bindings.containsKey(key)) {
                    continue;
                }
                for (Action<KeyEvent> action : bindings.get(key)) {
                    action.act(
                        deltaTime,
                        new KeyEvent(key, type, timestamp, thisFrameKeys)
                    );
                }
            }
        }

        lastFrameKeys.clear();
        lastFrameKeys.addAll(thisFrameKeys);
    }
}
