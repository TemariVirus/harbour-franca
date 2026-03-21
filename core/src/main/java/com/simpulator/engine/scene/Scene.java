package com.simpulator.engine.scene;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.simpulator.engine.graphics.GraphicsManager;

/** A scene encapsulates the state and business logic of a section of the simulation. */
public abstract class Scene implements Disposable {

    protected SoundManager sounds;
    protected TextureCache textures;
    protected Viewport viewport;

    /** Create an empty scene. */
    public Scene(Viewport viewport) {
        sounds = new SoundManager();
        textures = new TextureCache();
        this.viewport = viewport;
    }

    /** Return the input processor for this scene, or null. */
    public abstract InputProcessor getInputProcessor();

    /** Called when the scene is loaded. */
    public void onLoad() {}

    /** Free all resources. Called when the scene is unloaded. */
    @Override
    public void dispose() {
        sounds.dispose();
        textures.dispose();
    }

    /** Update the scene's state by the given delta time in seconds. */
    public abstract void update(float deltaTime);

    /** Draw the scene to the screen. */
    public abstract void render(
        GraphicsManager graphics,
        int width,
        int height
    );
}
