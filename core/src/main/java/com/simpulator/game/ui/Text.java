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

    public static class Builder {

        private Text text;

        public Builder(CharSequence text, BitmapFont font, UILayout layout) {
            this.text = new Text(
                text,
                font,
                Alignment.START,
                Color.WHITE,
                layout
            );
        }

        public Builder xAnchor(Alignment xAnchor) {
            text.setXAnchor(xAnchor);
            return this;
        }

        public Builder color(Color color) {
            text.setColor(color);
            return this;
        }

        public Text getText() {
            return text;
        }
    }

    private CharSequence text;
    private BitmapFont font;
    private Alignment xAnchor;
    private Color color;

    public Text(
        CharSequence text,
        BitmapFont font,
        Alignment xAnchor,
        Color color,
        UILayout layout
    ) {
        super(layout);
        setText(text);
        this.font = font;
        this.xAnchor = xAnchor;
        this.color = color;
    }

    public void setText(CharSequence text) {
        this.text = text;
    }

    public void setFont(BitmapFont font) {
        this.font = font;
    }

    public void setXAnchor(Alignment xAnchor) {
        this.xAnchor = xAnchor;
    }

    public void setColor(Color color) {
        this.color = color;
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
        GlyphLayout layout = new GlyphLayout(font, text);
        float x;
        switch (xAnchor) {
            case START:
                x = bounds.left;
                break;
            case CENTER:
                x =
                    bounds.left +
                    (bounds.right - bounds.left - layout.width) / 2f;
                break;
            case END:
                x = bounds.right - layout.width;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + xAnchor);
        }

        font.setColor(color);
        font.draw(batch, text, x, bounds.bottom);
    }
}
