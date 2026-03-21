package com.simpulator.engine.ui;

public interface UILayout {
    /** Transforms the inner bounds of the parent to the bounds of the child. */
    public Rect computeBounds(Rect bounds);
}
