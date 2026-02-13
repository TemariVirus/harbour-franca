package com.simpulator.engine;

import com.badlogic.gdx.graphics.Camera;

/** An object that can be rendered by a renderer of type T. */
public interface Renderable<T> {
    /** Render this object from the camera's perspective. */
    public void render(T renderer, Camera camera);
}
