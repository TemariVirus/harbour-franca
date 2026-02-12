package com.simpulator.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ButtonManager<T> {

    public enum ButtonBindType {
        DOWN,
        HOLD,
        UP
    }
    
    // Attributes
    private HashSet<Integer> lastFrameButtons = new HashSet<>();
    private HashSet<Integer> thisFrameButtons = new HashSet<>();

    private final HashMap<Integer, ArrayList<Action<T>>> downBindings = new HashMap<>();
    private final HashMap<Integer, ArrayList<Action<T>>> holdBindings = new HashMap<>();
    private final HashMap<Integer, ArrayList<Action<T>>> upBindings = new HashMap<>();

    // Contructors
    public ButtonManager() {}

    public ButtonManager(HashSet<Integer> lastFrameButtons, HashSet<Integer> thisFrameButtons){
        this.lastFrameButtons.addAll(lastFrameButtons);
        this.thisFrameButtons.addAll(thisFrameButtons);
    }

    // ButtonBindType
    private HashMap<Integer, ArrayList<Action<T>>> getBindings(ButtonBindType type) {
        switch (type) {
            case DOWN:
                return downBindings;
            case HOLD:
                return holdBindings;
            case UP:
                return upBindings;
            default:
                throw new IllegalArgumentException("Unknowm ButtonBindType: " + type);
        }
    }

    public boolean getButtonDown(int button){
        return thisFrameButtons.contains(button) && !lastFrameButtons.contains(button);
    }
    public boolean getButtonUp(int button){
        return !thisFrameButtons.contains(button) && lastFrameButtons.contains(button);
    }

    public void setButtonDown(int button){
        thisFrameButtons.add(button);
    }
    public void setButtonUp(int button){
        thisFrameButtons.remove(button);
    }

    public void bind(ButtonBindType type, int button, Action<T> action) {
        getBindings(type).putIfAbsent(button, new ArrayList<>());
        getBindings(type).get(button).add(action);
    }

    public void unbind(ButtonBindType type,int button, Action<T> action){
        if (getBindings(type).containsKey(button)) {
            getBindings(type).get(button).removeIf(a -> a == action);
        }
    }

    public void unbindAll(ButtonBindType type) {
        getBindings(type).clear();
    }

    public void update(float deltaTime, float timestamp, T eventData) {
        for(int button : thisFrameButtons) {
            if(getButtonDown(button) && downBindings.containsKey(button)) {
                for (Action<T> action : downBindings.get(button)) {
                    action.act(deltaTime, eventData);
                }
            }
        }

        for (int button : thisFrameButtons) {
            if (holdBindings.containsKey(button)) {
                for(Action<T> action : holdBindings.get(button)) {
                    action.act(deltaTime, eventData);
                }
            }
        }

        for (int button : lastFrameButtons) {
            if(getButtonUp(button) && upBindings.containsKey(button)) {
                for (Action<T> action : upBindings.get(button)) {
                    action.act(deltaTime, eventData);
                }
            }
        }

        lastFrameButtons.clear();
        lastFrameButtons.addAll(thisFrameButtons);
    }
}
