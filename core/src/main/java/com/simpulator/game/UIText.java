package com.simpulator.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.simpulator.engine.Renderable;
import com.simpulator.engine.TextureBatch;

public class UIText implements Renderable {

    BitmapFont font;
    CharSequence text;
    float x, y;

    public UIText(BitmapFont font, CharSequence text, float x, float y) {
        this.font = font;
        this.text = text;
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean isVisible(Camera camera) {
        return true;
    }

    @Override
    public float getZOrder(Camera camera) {
        // Entities should always produce a positive value,
        // so using -1 ensures that UI text is always rendered on top of entities.
        return -1;
    }

    @Override
    public void render(TextureBatch batch, Camera camera) {
        font.draw(batch, text, x, y);
    }
}
