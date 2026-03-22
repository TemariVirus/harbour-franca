package com.simpulator.engine.ui;

/** A pure function that computes the bounds of a UI element based on its parent's bounds. */
public interface UILayout {
    /** Transforms the inner bounds of the parent to the bounds of the child. */
    public Rect computeBounds(Rect bounds);
}
