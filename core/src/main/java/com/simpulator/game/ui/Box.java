package com.simpulator.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.simpulator.engine.graphics.TextureBatch;
import com.simpulator.engine.input.MouseManager.MouseMoveEvent;
import com.simpulator.engine.ui.Rect;
import com.simpulator.engine.ui.UIElement;
import com.simpulator.engine.ui.UILayout;

public class Box extends UIElement {

    private static final Texture texture = UiHelper.getWhiteTexture();

    private int borderWidth;
    private Color borderColor;
    private Color fillColor;

    public Box(
        int borderWidth,
        Color borderColor,
        Color fillColor,
        UILayout layout
    ) {
        super(layout);
        this.borderWidth = borderWidth;
        this.borderColor = borderColor;
        this.fillColor = fillColor;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public void addHoverColor(Color normalColor, Color hoverColor) {
        addListener(MouseMoveEvent.class, e -> {
            setFillColor(
                getBounds().contains(e.x, e.y) ? hoverColor : normalColor
            );
            return false;
        });
    }

    @Override
    public void render(TextureBatch batch) {
        Rect bounds = getBounds();
        int innerWidth = bounds.right - bounds.left;
        int innerHeight = bounds.bottom - bounds.top;
        int outerWidth = innerWidth + 2 * borderWidth;
        int outerHeight = innerHeight + 2 * borderWidth;

        if (borderWidth > 0 && outerWidth > 0 && outerHeight > 0) {
            batch.setColor(borderColor);
            batch.draw(
                texture,
                bounds.left - borderWidth,
                bounds.top - borderWidth,
                outerWidth,
                outerHeight
            );
        }
        if (innerWidth > 0 && innerHeight > 0) {
            batch.setColor(fillColor);
            batch.draw(
                texture,
                bounds.left,
                bounds.top,
                innerWidth,
                innerHeight
            );
        }
        batch.setColor(Color.WHITE);
    }
}
