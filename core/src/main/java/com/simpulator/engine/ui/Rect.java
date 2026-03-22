package com.simpulator.engine.ui;

public final class Rect {

    public final int left;
    public final int top;
    public final int right;
    public final int bottom;

    public Rect(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

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
