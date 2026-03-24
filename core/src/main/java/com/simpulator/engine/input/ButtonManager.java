package com.simpulator.engine.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

/** Invokes actions from button inputs. */
public class ButtonManager<T> {

    /** When to invoke the action. */
    public enum ButtonBindType {
        /** When the button is just pressed. */
        DOWN,
        /** When the button is held down. */
        HOLD,
        /** When the button is just released. */
        UP,
    }

    @FunctionalInterface
    public interface ButtonEventConstructor<T> {
        public T apply(
            int button,
            ButtonBindType type,
            Set<Integer> thisFrameButtons
        );
    }

    private final class QueueNode {

        public final ButtonBindType type;
        public final int button;
        public final Action<T> action;

        public QueueNode(ButtonBindType type, int button, Action<T> action) {
            this.type = type;
            this.button = button;
            this.action = action;
        }

        public void bind(ButtonManager<T> manager) {
            manager
                .getBindings(type)
                .computeIfAbsent(button, k -> new ArrayList<>())
                .add(action);
        }

        public void unbind(ButtonManager<T> manager) {
            if (manager.getBindings(type).containsKey(button)) {
                manager
                    .getBindings(type)
                    .get(button)
                    // Check reference instead of using .equals()
                    .removeIf(a -> a == action);
            }
        }
    }

    private HashSet<Integer> lastFrameButtons = new HashSet<>();
    private HashSet<Integer> thisFrameButtons = new HashSet<>();
    private final ArrayList<QueueNode> queuedBindings = new ArrayList<>();
    private final ArrayList<QueueNode> queuedUnbindings = new ArrayList<>();

    private final HashMap<Integer, ArrayList<Action<T>>> downBindings =
        new HashMap<>();
    private final HashMap<Integer, ArrayList<Action<T>>> holdBindings =
        new HashMap<>();
    private final HashMap<Integer, ArrayList<Action<T>>> upBindings =
        new HashMap<>();

    public ButtonManager() {}

    /**
     * Initialise with the state of the buttons from the previous and current frames.
     * Useful for testing.
     */
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

    /** Returns whether the button is currently held down. */
    public boolean getButtonDown(int button) {
        return thisFrameButtons.contains(button);
    }

    /** Set the button's current state to held down or not. */
    public void setButton(int button, boolean down) {
        if (down) {
            thisFrameButtons.add(button);
        } else {
            thisFrameButtons.remove(button);
        }
    }

    /** Bind the given action to a button and event type. */
    public void bind(ButtonBindType type, int button, Action<T> action) {
        queuedBindings.add(new QueueNode(type, button, action));
    }

    /** Unbind all instances of the given action from a button and event type. */
    public void unbind(ButtonBindType type, int button, Action<T> action) {
        queuedUnbindings.add(new QueueNode(type, button, action));
    }

    /** Unbind all actions from the given event type */
    public void unbindAll(ButtonBindType type) {
        for (Entry<Integer, ArrayList<Action<T>>> kv : getBindings(
            type
        ).entrySet()) {
            for (Action<T> a : kv.getValue()) {
                queuedUnbindings.add(new QueueNode(type, kv.getKey(), a));
            }
        }
    }

    /** Unbind all actions from the given button and event type. */
    public void unbindAll(ButtonBindType type, int button) {
        for (Action<T> a : getBindings(type).getOrDefault(
            button,
            new ArrayList<>()
        )) {
            queuedUnbindings.add(new QueueNode(type, button, a));
        }
    }

    private HashSet<Integer> computeButtons(ButtonBindType type) {
        HashSet<Integer> buttons = new HashSet<>();
        switch (type) {
            case DOWN:
                // Just pressed
                buttons.addAll(thisFrameButtons);
                buttons.removeAll(lastFrameButtons);
                break;
            case HOLD:
                // Held down
                buttons.addAll(thisFrameButtons);
                break;
            case UP:
                // Just released
                buttons.addAll(lastFrameButtons);
                buttons.removeAll(thisFrameButtons);
                break;
            default:
                throw new IllegalArgumentException("Unknown BindType: " + type);
        }
        return buttons;
    }

    /**
     * Processes button state updates and invokes the appropriate actions.
     *
     * @param constructor Creates events from the button, bind type, and current button states.
     */
    public void update(ButtonEventConstructor<T> constructor) {
        for (QueueNode node : queuedBindings) {
            node.bind(this);
        }
        queuedBindings.clear();
        for (QueueNode node : queuedUnbindings) {
            node.unbind(this);
        }
        queuedUnbindings.clear();

        for (ButtonBindType type : ButtonBindType.values()) {
            HashSet<Integer> buttons = computeButtons(type);
            HashMap<Integer, ArrayList<Action<T>>> bindings = getBindings(type);
            for (int button : buttons) {
                if (!bindings.containsKey(button)) {
                    continue;
                }
                for (Action<T> action : bindings.get(button)) {
                    action.act(
                        constructor.apply(button, type, thisFrameButtons)
                    );
                }
            }
        }

        lastFrameButtons.clear();
        lastFrameButtons.addAll(thisFrameButtons);
    }
}
