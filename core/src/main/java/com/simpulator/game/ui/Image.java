package com.simpulator.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.simpulator.engine.graphics.TextureBatch;
import com.simpulator.engine.ui.Rect;
import com.simpulator.engine.ui.UIElement;
import com.simpulator.engine.ui.UILayout;

public class Image extends UIElement {

    private Texture texture;
    private Color tint;

    public Image(Texture texture, UILayout layout) {
        this(texture, Color.WHITE, layout);
    }

    public Image(Texture texture, Color tint, UILayout layout) {
        super(layout);
        this.texture = texture;
        this.tint = tint;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public void setTint(Color tint) {
        this.tint = tint;
    }

    @Override
    public void render(TextureBatch batch) {
        if (texture == null) return;

        Rect bounds = getBounds();
        float width = bounds.right - bounds.left;
        float height = bounds.bottom - bounds.top;

        if (width <= 0 || height <= 0) return;

        batch.setColor(tint);
        batch.draw(texture, bounds.left, bounds.top, width, height);
        batch.setColor(Color.WHITE);
    }
}
