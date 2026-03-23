package com.simpulator.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.simpulator.engine.graphics.TextureBatch;
import com.simpulator.engine.ui.Rect;
import com.simpulator.engine.ui.UIElement;
import com.simpulator.engine.ui.UILayout;

public class Text extends UIElement {

    public enum Alignment {
        START,
        CENTER,
        END,
    }

    private CharSequence text;
    private BitmapFont font;
    private Alignment xAnchor;
    private Color color;

    private float width;

    public Text(
        CharSequence text,
        BitmapFont font,
        Alignment xAnchor,
        Color color,
        UILayout layout
    ) {
        super(layout);
        this.text = text;
        this.font = font;
        this.xAnchor = xAnchor;
        this.color = color;

        invalidateWidth();
    }

    private void invalidateWidth() {
        Rect bounds = getBounds();
        float height = bounds.bottom - bounds.top;
        if (height <= 0) {
            width = 0;
            return;
        }

        font.getData().setScale(1);
        font.getData().setScale(height / font.getCapHeight());
        GlyphLayout layout = new GlyphLayout(font, text);
        width = layout.width;
    }

    @Override
    public void updateBounds(Rect parentBounds) {
        float oldHeight = getBounds().bottom - getBounds().top;
        super.updateBounds(parentBounds);
        float newHeight = getBounds().bottom - getBounds().top;
        if (newHeight != oldHeight) {
            invalidateWidth();
        }
    }

    public void setText(CharSequence text) {
        this.text = text;
        invalidateWidth();
    }

    public void setFont(BitmapFont font) {
        this.font = font;
        invalidateWidth();
    }

    public Alignment getXAnchor() {
        return xAnchor;
    }

    public void setXAnchor(Alignment xAnchor) {
        this.xAnchor = xAnchor;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public float getTextWidth() {
        return width;
    }

    @Override
    public void render(TextureBatch batch) {
        Rect bounds = getBounds();
        float height = bounds.bottom - bounds.top;
        if (height <= 0) {
            return;
        }

        font.getData().setScale(1);
        font.getData().setScale(height / font.getCapHeight());
        float x;
        switch (xAnchor) {
            case START:
                x = bounds.left;
                break;
            case CENTER:
                x = bounds.left + (bounds.right - bounds.left - width) / 2f;
                break;
            case END:
                x = bounds.right - width;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + xAnchor);
        }

        font.setColor(color);
        font.draw(batch, text, x, bounds.bottom);
    }
}
