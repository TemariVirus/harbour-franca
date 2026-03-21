package com.simpulator.engine;

import com.badlogic.gdx.InputProcessor;
import com.simpulator.engine.graphics.GraphicsManager;

public interface Widget {
    /** Returns the input processor for this widget, or null. */
    public InputProcessor getInputProcessor();

    /** Update the widget's state by the given delta time in seconds. */
    public default void update(float deltaTime) {}

    /** Render the widget at screen x and y, with width and height (in pixels). */
    public void render(
        GraphicsManager graphics,
        int x,
        int y,
        int width,
        int height
    );
}
