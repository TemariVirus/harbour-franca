package com.simpulator.engine;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** An object that can be rendered by a SpriteBatch. */
public interface Renderable {
    /** Returns whether the object is currently in view of the given camera. */
    public boolean isVisible(Camera camera);

    /** The z distance of this object from the camera's perspective. */
    public float getZOrder(Camera camera);

    /** Render this object from the camera's perspective. */
    public void render(SpriteBatch batch, Camera camera);
}
