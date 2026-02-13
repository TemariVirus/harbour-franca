package com.simpulator.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ButtonManager<T> {

    public enum ButtonBindType {
        DOWN,
        HOLD,
        UP,
    }

    @FunctionalInterface
    public interface EventConstructor<T> {
        public T apply(
            int button,
            ButtonBindType type,
            Set<Integer> thiFrameButtons
        );
    }

    private HashSet<Integer> lastFrameButtons = new HashSet<>();
    private HashSet<Integer> thisFrameButtons = new HashSet<>();

    private final HashMap<Integer, ArrayList<Action<T>>> downBindings =
        new HashMap<>();
    private final HashMap<Integer, ArrayList<Action<T>>> holdBindings =
        new HashMap<>();
    private final HashMap<Integer, ArrayList<Action<T>>> upBindings =
        new HashMap<>();

    public ButtonManager() {}

    public ButtonManager(
        Set<Integer> lastFrameButtons,
        Set<Integer> thisFrameButtons
    ) {
        this.lastFrameButtons.addAll(lastFrameButtons);
        this.thisFrameButtons.addAll(thisFrameButtons);
    }

    private HashMap<Integer, ArrayList<Action<T>>> getBindings(
        ButtonBindType type
    ) {
        switch (type) {
            case DOWN:
                return downBindings;
            case HOLD:
                return holdBindings;
            case UP:
                return upBindings;
            default:
                throw new IllegalArgumentException(
                    "Unknown ButtonBindType: " + type
                );
        }
    }

    public boolean getButtonDown(int button) {
        return thisFrameButtons.contains(button);
    }

    public void setButton(int button, boolean down) {
        if (down) {
            thisFrameButtons.add(button);
        } else {
            thisFrameButtons.remove(button);
        }
    }

    public void bind(ButtonBindType type, int button, Action<T> action) {
        getBindings(type).putIfAbsent(button, new ArrayList<>());
        getBindings(type).get(button).add(action);
    }

    public void unbind(ButtonBindType type, int button, Action<T> action) {
        if (getBindings(type).containsKey(button)) {
            getBindings(type)
                .get(button)
                // Check reference instead of using .equals()
                .removeIf(a -> a == action);
        }
    }

    public void unbindAll(ButtonBindType type) {
        getBindings(type).clear();
    }

    public void unbindAll(ButtonBindType type, int button) {
        getBindings(type).remove(button);
    }

    private HashSet<Integer> computeButtons(ButtonBindType type) {
        HashSet<Integer> buttons = new HashSet<>();
        switch (type) {
            case DOWN:
                buttons.addAll(thisFrameButtons);
                buttons.removeAll(lastFrameButtons);
                break;
            case HOLD:
                buttons.addAll(thisFrameButtons);
                break;
            case UP:
                buttons.addAll(lastFrameButtons);
                buttons.removeAll(thisFrameButtons);
                break;
            default:
                throw new IllegalArgumentException("Unknown BindType: " + type);
        }
        return buttons;
    }

    public void update(float deltaTime, EventConstructor<T> constructor) {
        for (ButtonBindType type : ButtonBindType.values()) {
            HashSet<Integer> buttons = computeButtons(type);
            HashMap<Integer, ArrayList<Action<T>>> bindings = getBindings(type);
            for (int button : buttons) {
                if (!bindings.containsKey(button)) {
                    continue;
                }
                for (Action<T> action : bindings.get(button)) {
                    action.act(
                        deltaTime,
                        constructor.apply(button, type, thisFrameButtons)
                    );
                }
            }
        }

        lastFrameButtons.clear();
        lastFrameButtons.addAll(thisFrameButtons);
    }
}
