package com.simpulator.engine;

import com.badlogic.gdx.graphics.Camera;

public interface Renderable<T> {
    void render(T renderer, Camera camera);
}
