package com.simpulator.game.entities;

import com.badlogic.gdx.graphics.Camera;
import com.simpulator.engine.Entity;
import com.simpulator.engine.graphics.EntityRenderer;
import com.simpulator.engine.graphics.TextureBatch;

/** An EntityRenderer that does nothing. */
public class EmptyRenderer implements EntityRenderer {

    @Override
    public boolean isVisible(Camera camera, Entity entity) {
        return false;
    }

    @Override
    public void render(TextureBatch batch, Camera camera, Entity entity) {}
}
