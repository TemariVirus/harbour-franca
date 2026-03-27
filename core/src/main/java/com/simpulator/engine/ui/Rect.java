package com.simpulator.engine.ui;

/** Represents a region on the screen in absolute coordinates. */
public final class Rect {

    /** The x-coordinate of the left edge in pixels. */
    public final int left;
    /** The y-coordinate of the top edge in pixels. */
    public final int top;
    /** The x-coordinate of the right edge in pixels. */
    public final int right;
    /** The y-coordinate of the bottom edge in pixels. */
    public final int bottom;

    public Rect(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    /** Returns whether the point is within this rect (including edges). */
    public boolean contains(int x, int y) {
        return left <= x && x <= right && top <= y && y <= bottom;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Rect other = (Rect) obj;
        return (
            left == other.left &&
            top == other.top &&
            right == other.right &&
            bottom == other.bottom
        );
    }
}
