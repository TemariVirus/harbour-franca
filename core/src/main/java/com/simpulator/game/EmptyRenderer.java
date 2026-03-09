package com.simpulator.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.simpulator.engine.Entity;
import com.simpulator.engine.EntityRenderer;

/** An EntityRenderer that does nothing. */
public class EmptyRenderer implements EntityRenderer {

    @Override
    public boolean isVisible(Camera camera, Entity entity) {
        return false;
    }

    @Override
    public float getZOrder(Camera camera, Entity entity) {
        return Float.MIN_VALUE;
    }

    @Override
    public void render(SpriteBatch batch, Camera camera, Entity entity) {}
}
