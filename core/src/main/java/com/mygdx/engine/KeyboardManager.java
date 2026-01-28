package com.mygdx.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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

    private HashSet<Integer> lastFrameKeys = new HashSet<>();
    private HashSet<Integer> thisFrameKeys = new HashSet<>();

    private final HashMap<Integer, ArrayList<IAction>> downBindings =
        new HashMap<>();
    private final HashMap<Integer, ArrayList<IAction>> holdBindings =
        new HashMap<>();
    private final HashMap<Integer, ArrayList<IAction>> upBindings =
        new HashMap<>();

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

    private HashMap<Integer, ArrayList<IAction>> getBindings(BindType type) {
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

    public void bind(BindType type, int keycode, IAction action) {
        getBindings(type).putIfAbsent(keycode, new ArrayList<>());
        getBindings(type).get(keycode).add(action);
    }

    public void unbind(BindType type, int keycode, IAction action) {
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

    public void update(float deltaTime) {
        for (BindType type : BindType.values()) {
            HashSet<Integer> keys = computeKeys(type);
            HashMap<Integer, ArrayList<IAction>> bindings = getBindings(type);
            for (int key : keys) {
                if (!bindings.containsKey(key)) {
                    continue;
                }
                for (IAction action : bindings.get(key)) {
                    action.act(deltaTime);
                }
            }
        }

        lastFrameKeys.clear();
        lastFrameKeys.addAll(thisFrameKeys);
    }
}
