package com.simpulator.engine;

import com.badlogic.gdx.graphics.Camera;

public interface Renderable<T> {
    public void render(T renderer, Camera camera);
}
