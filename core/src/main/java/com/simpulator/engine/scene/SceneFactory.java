package com.simpulator.engine.scene;

/** Creates a Scene. */
@FunctionalInterface
public interface SceneFactory {
    /** Create a new scene and return it. */
    public Scene create();
}
