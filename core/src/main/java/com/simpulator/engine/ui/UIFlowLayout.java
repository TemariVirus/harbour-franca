package com.simpulator.engine.ui;

public class UIFlowLayout implements UILayout {

    public enum Alignment {
        START,
        CENTER,
        END,
    }

    public static final float EXPAND = Float.POSITIVE_INFINITY;

    private Alignment xAlignment = Alignment.START;
    private Alignment yAlignment = Alignment.START;
    private float padLeft = 0;
    private float padRight = 0;
    private float padTop = 0;
    private float padBottom = 0;
    private float width = EXPAND;
    private float height = EXPAND;

    public UIFlowLayout() {}

    public UIFlowLayout setXAlignment(Alignment align) {
        this.xAlignment = align;
        return this;
    }

    public UIFlowLayout setYAlignment(Alignment align) {
        this.yAlignment = align;
        return this;
    }

    public UIFlowLayout setPadLeft(float pad) {
        this.padLeft = pad;
        return this;
    }

    public UIFlowLayout setPadRight(float pad) {
        this.padRight = pad;
        return this;
    }

    public UIFlowLayout setPadTop(float pad) {
        this.padTop = pad;
        return this;
    }

    public UIFlowLayout setPadBottom(float pad) {
        this.padBottom = pad;
        return this;
    }

    public UIFlowLayout setPadAll(float pad) {
        return setPadLeft(pad)
            .setPadRight(pad)
            .setPadTop(pad)
            .setPadBottom(pad);
    }

    public UIFlowLayout setWidth(float width) {
        this.width = width;
        return this;
    }

    public UIFlowLayout setHeight(float height) {
        this.height = height;
        return this;
    }

    private static float getSize(
        float size,
        float start,
        float end,
        float padStart,
        float padEnd
    ) {
        if (size == EXPAND) {
            return Math.max(0, end - start - padStart - padEnd);
        } else {
            return Math.max(0, size);
        }
    }

    private static float getOffset(
        Alignment align,
        float start,
        float end,
        float padStart,
        float padEnd,
        float size
    ) {
        switch (align) {
            case START:
                return Math.min(start + padStart, end);
            case CENTER:
                return (start + end - size) / 2;
            case END:
                return Math.max(end - padEnd - size, start);
            default:
                throw new IllegalStateException("Invalid alignment: " + align);
        }
    }

    @Override
    public Rect computeBounds(Rect bounds) {
        float w = getSize(width, bounds.left, bounds.right, padLeft, padRight);
        float h = getSize(height, bounds.top, bounds.bottom, padTop, padBottom);
        float x = getOffset(
            xAlignment,
            bounds.left,
            bounds.right,
            padLeft,
            padRight,
            w
        );
        float y = getOffset(
            yAlignment,
            bounds.top,
            bounds.bottom,
            padTop,
            padBottom,
            h
        );
        return new Rect(
            (int) x,
            (int) y,
            Math.min((int) (x + w), bounds.right),
            Math.min((int) (y + h), bounds.bottom)
        );
    }
}
