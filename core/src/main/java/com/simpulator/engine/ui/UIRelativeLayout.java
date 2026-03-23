package com.simpulator.engine.ui;

/** A layout that calculates bounds relative to its parent. */
public class UIRelativeLayout implements UILayout {

    public static class Builder {

        private UIRelativeLayout layout = new UIRelativeLayout();

        public Builder xAlignment(Alignment align) {
            layout.setXAlignment(align);
            return this;
        }

        public Builder yAlignment(Alignment align) {
            layout.setYAlignment(align);
            return this;
        }

        public Builder padLeft(float pad) {
            layout.setPadLeft(pad);
            return this;
        }

        public Builder padRight(float pad) {
            layout.setPadRight(pad);
            return this;
        }

        public Builder padTop(float pad) {
            layout.setPadTop(pad);
            return this;
        }

        public Builder padBottom(float pad) {
            layout.setPadBottom(pad);
            return this;
        }

        public Builder padAll(float pad) {
            layout.setPadAll(pad);
            return this;
        }

        public Builder width(float width) {
            layout.setWidth(width);
            return this;
        }

        public Builder height(float height) {
            layout.setHeight(height);
            return this;
        }

        public UIRelativeLayout getLayout() {
            return layout;
        }
    }

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

    public UIRelativeLayout() {}

    public void setXAlignment(Alignment align) {
        this.xAlignment = align;
    }

    public void setYAlignment(Alignment align) {
        this.yAlignment = align;
    }

    public void setPadLeft(float pad) {
        this.padLeft = pad;
    }

    public void setPadRight(float pad) {
        this.padRight = pad;
    }

    public void setPadTop(float pad) {
        this.padTop = pad;
    }

    public void setPadBottom(float pad) {
        this.padBottom = pad;
    }

    public void setPadAll(float pad) {
        setPadLeft(pad);
        setPadRight(pad);
        setPadTop(pad);
        setPadBottom(pad);
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
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
                return (start + padStart + end - padEnd - size) / 2;
            case END:
                return Math.max(end - padEnd - size, start);
            default:
                throw new IllegalStateException("Invalid alignment: " + align);
        }
    }

    private static Alignment alignmentOpposite(Alignment align) {
        switch (align) {
            case START:
                return Alignment.END;
            case CENTER:
                return Alignment.CENTER;
            case END:
                return Alignment.START;
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
            // Y=0 is the bottom of the screen, so we need to flip the alignment
            alignmentOpposite(yAlignment),
            bounds.top,
            bounds.bottom,
            padBottom,
            padTop,
            h
        );
        return new Rect(
            (int) x,
            (int) y,
            (int) Math.ceil(x + w),
            (int) Math.ceil(y + h)
        );
    }
}
