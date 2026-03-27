package com.simpulator.engine.scene;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Disposable;

/** A scene encapsulates the state and business logic of a section of the simulation. */
public interface Scene extends Disposable {
    /** Return the input processor for this scene, or null. */
    public InputProcessor getInputProcessor();

    /**
     * Called when the scene becomes the top scene.
     * Returns whether focus passes through this scene
     * (i.e., false if the previous scene in the stack should be focused instead).
     * This method must be idempotent, as it may be called on a Scene that is already focused.
     */
    public boolean onFocus();

    /** Update the scene's state by the given delta time in seconds. */
    public abstract void update(float deltaTime);

    /** Render the scene at screen x and y, with width and height (in pixels). */
    public void render(int x, int y, int width, int height);
}
