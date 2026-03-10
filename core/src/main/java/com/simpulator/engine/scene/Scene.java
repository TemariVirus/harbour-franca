package com.simpulator.engine.scene;

import com.simpulator.engine.graphics.GraphicsManager;

/** A scene encapsulates the state and business logic of a section of the simulation. */
public abstract class Scene {

    protected SoundManager sounds;
    protected TextureCache textures;

    /** Create an empty scene. */
    public Scene() {
        sounds = new SoundManager();
        textures = new TextureCache();
    }

    /** Load the scene's resources into memory and start the scene. */
    public abstract void load();

    /** Stop the scene and free all resources. */
    public void unload() {
        // These fields can be safely reused in the next load()
        sounds.dispose();
        textures.dispose();
    }

    /** Update the scene's state by the given delta time in seconds. */
    public abstract void update(float deltaTime);

    /** Draw the scene to the screen. */
    public abstract void render(GraphicsManager graphics);
}
