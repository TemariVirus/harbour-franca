package com.simpulator.engine;

import com.badlogic.gdx.graphics.Camera;

/** Strategy based on Renderable for rendering entities */
public interface EntityRenderer {
    /** Returns whether the entity is currently in view of the given camera. */
    public boolean isVisible(Camera camera, Entity entity);

    /** The z distance of the entity from the camera's perspective. */
    public float getZOrder(Camera camera, Entity entity);

    /** Render the entity from the camera's perspective. */
    public void render(TextureBatch batch, Camera camera, Entity entity);
}
