package com.simpulator.engine;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

/** A scene encapsulates the state and business logic of a section of the simulation. */
public abstract class Scene implements Disposable {

    protected EntityManager entityManager;
    protected SoundManager sounds;
    protected TextureManager textures;

    /** Create an empty scene. */
    public Scene() {
        entityManager = new EntityManager();
        sounds = new SoundManager();
        textures = new TextureManager();
    }

    /** Get a reference to the scene's EntityManager. */
    public EntityManager getEntityManager() {
        return entityManager;
    }

    /** Load the given sound. */
    public Sound getSound(String path) {
        return sounds.get(path);
    }

    /** Load the given texture. */
    public Texture getTexture(String path) {
        return textures.get(path);
    }

    /** Load the scene's resources into memory and start the scene. */
    public abstract void load();

    /** Stop the scene and free all resources. */
    public void unload() {
        dispose();
    }

    /** Update the scene's state by the given delta time in seconds. */
    public void update(float deltaTime) {
        entityManager.update(deltaTime);
    }

    /** Play the given sound. */
    public long playSound(String path) {
        return sounds.play(path);
    }

    /** Draw the scene to the screen. */
    public abstract void render(GraphicsManager graphics);

    @Override
    public void dispose() {
        // These fields can be safely reused in the next load()
        entityManager.removeAll();
        sounds.dispose();
        textures.dispose();
    }
}
