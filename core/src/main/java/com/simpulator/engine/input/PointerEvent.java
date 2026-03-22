package com.simpulator.engine.input;

public class PointerEvent {

    /** The x position of the pointer, in pixels */
    public final int x;
    /** The y position of the pointer, in pixels */
    public final int y;

    public PointerEvent(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
