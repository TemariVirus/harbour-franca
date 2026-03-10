package com.simpulator.engine;

import com.badlogic.gdx.graphics.Camera;

/** An object that can be rendered by a TextureBatch. */
public interface Renderable {
    /** Returns whether the object is currently in view of the given camera. */
    public boolean isVisible(Camera camera);

    /**
     * The z order for resolving overlapping 2D objects.
     * 3D objects will be resolved with the depth buffer instead.
     */
    public default float getZOrder(Camera camera) {
        return 1;
    }

    /** Render this object from the camera's perspective. */
    public void render(TextureBatch batch, Camera camera);
}
