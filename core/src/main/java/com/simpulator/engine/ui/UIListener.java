package com.simpulator.engine.ui;

@FunctionalInterface
/** Handles UI events. */
public interface UIListener<T> {
    /**
     * Handles the event.
     * Returns false if the event should pass through to children.
     * Returns true if the event should be consumed and not passed to children.
     * All listeners of the element will run regardless.
     */
    public boolean handle(T data);
}
