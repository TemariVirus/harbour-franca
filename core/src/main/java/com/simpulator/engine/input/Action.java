package com.simpulator.engine.input;

@FunctionalInterface
/** Capable of performing an action. */
public interface Action<T> {
    /** Perform the action, parameterised by the given data. */
    public void act(T data);
}
